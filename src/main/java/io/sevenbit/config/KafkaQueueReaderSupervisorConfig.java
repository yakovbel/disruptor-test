package io.sevenbit.config;

public class KafkaQueueReaderSupervisorConfig {
    public final int workerCount = 4;
    public final double defaultRateLimit = 5_000;
}
