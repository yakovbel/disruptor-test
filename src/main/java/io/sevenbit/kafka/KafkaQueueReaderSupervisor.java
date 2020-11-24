package io.sevenbit.kafka;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.sevenbit.AppEventPublisher;
import io.sevenbit.config.KafkaQueueReaderSupervisorConfig;
import io.sevenbit.config.KafkaQueueReaderConfig;
import io.sevenbit.monitor.KafkaQueueReaderMonitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class KafkaQueueReaderSupervisor {
    private final KafkaQueueReaderSupervisorConfig config;
    private final KafkaQueueReaderConfig workerConfig;
    private final ExecutorService executorService;
    private final KafkaQueueReaderMonitor monitor;
    private final RateLimiter rateLimiter;
    private final AppEventPublisher publisher;

    public KafkaQueueReaderSupervisor(
            KafkaQueueReaderSupervisorConfig config,
            KafkaQueueReaderConfig workerConfig,
            KafkaQueueReaderMonitor monitor,
            AppEventPublisher publisher) {
        this.config = config;
        this.workerConfig = workerConfig;
        this.executorService = Executors.newFixedThreadPool(
                config.workerCount,
                new ThreadFactoryBuilder().setNameFormat("QueueReader-%d").build());
        this.monitor = monitor;
        this.rateLimiter = RateLimiter.create(config.defaultRateLimit);
        this.publisher = publisher;
    }

    public void start() {
        for(int i = 0; i < config.workerCount; i++) {
            SimpleKafkaQueueReader worker = new SimpleKafkaQueueReader(
                    workerConfig,
                    monitor,
                    rateLimiter,
                    publisher,
                    "worker_" + i);
            executorService.execute(worker);
        }
    }

    public void stop() {
        executorService.shutdown();
    }

    public void updateRate(long newRate) {
        rateLimiter.setRate(newRate);
    }
}
