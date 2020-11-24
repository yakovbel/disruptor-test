package io.sevenbit;

import com.lmax.disruptor.RingBuffer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class AppEventPublisher {
    private final RingBuffer<AppEvent> ringBuffer;

    public AppEventPublisher(RingBuffer<AppEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }


    public void publish(ConsumerRecord<String, byte[]> record) {
        ringBuffer.publishEvent(AppEventPublisher::translate, record);
    }

    public void publish(ConsumerRecords<String, byte[]> records) {
        for (var record : records) {
            publish(record);
        }
    }

    static void translate(AppEvent event, long sequence, ConsumerRecord<String, byte[]> consumerRecord) {
        event.setValue(new String(consumerRecord.value()));
    }
}
