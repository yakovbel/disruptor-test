package io.sevenbit.config;

import java.util.Properties;

public class KafkaQueueReaderConfig {
    public volatile long pollTimeoutMs;
    public volatile long pauseOnLoopCycleExceptionMs;
    public volatile boolean enabled = true;
    public final String topic;
    public final Properties consumerProperties;

    public KafkaQueueReaderConfig(String topic, long pollTimeoutMs, long pauseOnLoopCycleExceptionMs, Properties properties) {
        this.topic = topic;
        this.pollTimeoutMs = pollTimeoutMs;
        this.consumerProperties = properties;
        this.pauseOnLoopCycleExceptionMs = pauseOnLoopCycleExceptionMs;
    }
}
