package io.sevenbit.kafka;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.Uninterruptibles;
import io.sevenbit.AppEventPublisher;
import io.sevenbit.config.KafkaQueueReaderConfig;
import io.sevenbit.monitor.KafkaQueueReaderMonitor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SimpleKafkaQueueReader implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SimpleKafkaQueueReader.class);
    private final Consumer<String, byte[]> consumer;
    private final KafkaQueueReaderMonitor monitor;
    private final KafkaQueueReaderConfig config;
    private final RateLimiter rateLimiter;
    private final AppEventPublisher publisher;
    private volatile boolean active = true;
    private volatile boolean shutdownRequested = false;
    private final String name;

    public SimpleKafkaQueueReader(KafkaQueueReaderConfig config,
                                  KafkaQueueReaderMonitor monitor,
                                  RateLimiter rateLimiter,
                                  AppEventPublisher publisher,
                                  String name) {
        this.consumer = new KafkaConsumer<>(config.consumerProperties);
        this.monitor = monitor;
        this.config = config;
        this.rateLimiter = rateLimiter;
        this.publisher = publisher;
        this.name = name;
    }

    public void doLoopCycle() {
        //Poll kafka for new messages
        ConsumerRecords<String, byte[]> records = null;
        try {
            long startTime = monitor.pollStart();
            records = consumer.poll(Duration.ofMillis(config.pollTimeoutMs));
            monitor.pollSuccess(startTime);
        } catch (WakeupException ex) {
            //do nothing
        }
        catch (Throwable th) {
            monitor.pollException(th);
            throw th;
        }

        //Publish message for processing
        if (!records.isEmpty()) {
            rateLimiter.acquire(records.count());
            publisher.publish(records);
        }
    }

    @Override
    public void run() {
        active = true;
        try {
            this.consumer.subscribe(Arrays.asList(config.topic));
        } catch (Exception ex) {
            monitor.errSubscribe(ex);
            return;
        }

        log.info("Worker {} started", name);
        while (true) {
            if (shutdownRequested) {
                log.info("Worker {}: Shutdown requested", name);
                shutdownRequested = false;
                break;
            }

            if (!this.config.enabled) {
                log.info("Worker {} disabled", name);
                while (!this.config.enabled) {
                    Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
                    if (shutdownRequested) {
                        log.debug("Shutdown requested");
                        shutdownRequested = false;
                        break;
                    }
                }
                log.info("Worker {} enabled", name);
            }

            //
            try {
                doLoopCycle();
            } catch (Exception ex) {
                monitor.errCycleException(this, ex);
                Uninterruptibles.sleepUninterruptibly(config.pauseOnLoopCycleExceptionMs, TimeUnit.MILLISECONDS);
            }

        }

        // close queue reader
        try {
            consumer.close();
        } catch (Exception ex) {
            log.error("Error closing queue reader", ex);
        } finally {
            active = false;
        }
    }
}
