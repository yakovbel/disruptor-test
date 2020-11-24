package io.sevenbit;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.sevenbit.config.KafkaQueueReaderConfig;
import io.sevenbit.config.KafkaQueueReaderSupervisorConfig;
import io.sevenbit.kafka.KafkaQueueReaderSupervisor;
import io.sevenbit.monitor.KafkaQueueReaderMonitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {
    private final static int bufferSize = 1024;
    private final static String topic = "app_topic";

    public static void main(String[] args) throws InterruptedException {
        init();
    }

    public static void handleEvent(AppEvent event, long sequence, boolean endOfBatch) {
        System.out.println(event);
    }

    public static void init()  {
        Disruptor<AppEvent> disruptor = new Disruptor<>(AppEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(Main::handleEvent);
        disruptor.start();

        KafkaQueueReaderSupervisorConfig supervisorConfig = new KafkaQueueReaderSupervisorConfig();
        KafkaQueueReaderConfig workerConfig = new KafkaQueueReaderConfig(
                topic,
                2000,
                30_000,
                getKafkaConsumerProperties());
        KafkaQueueReaderSupervisor kafkaQueueReaderSupervisor = new KafkaQueueReaderSupervisor(
                supervisorConfig,
                workerConfig,
                new KafkaQueueReaderMonitor(), new AppEventPublisher(disruptor.getRingBuffer()));

        kafkaQueueReaderSupervisor.start();
    }

    public static Properties getKafkaConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return properties;
    }

}
