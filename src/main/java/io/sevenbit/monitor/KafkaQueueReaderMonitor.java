package io.sevenbit.monitor;

import io.sevenbit.kafka.SimpleKafkaQueueReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaQueueReaderMonitor {
    private static final Logger log = LoggerFactory.getLogger(KafkaQueueReaderMonitor.class);

    public void pollException(Throwable ex) {
        log.error("cause: {} message: {}", ex.getCause(), ex.getMessage());
    }

    public long pollStart() {
        return System.currentTimeMillis();
    }

    public void pollSuccess(long startTime) {

    }

    public void errCycleException(SimpleKafkaQueueReader simpleKafkaQueueReader, Exception ex) {
        log.error("cause: {} message: {}", ex.getCause(), ex.getMessage());
        ex.printStackTrace();
    }

    public void errSubscribe(Exception ex) {
        log.error("Can't perform subscribe, cause: {} message: {}", ex.getCause(), ex.getMessage());
    }
}
