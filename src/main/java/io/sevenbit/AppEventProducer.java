package io.sevenbit;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

public class AppEventProducer {
    private final RingBuffer<AppEvent> ringBuffer;

    public AppEventProducer(RingBuffer<AppEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<AppEvent, ByteBuffer> translator =
            (event, sequence, buffer) -> event.setValue(Long.toString(buffer.getLong(0)));

    public void onData(ByteBuffer bb) {
        ringBuffer.publishEvent(translator, bb);
    }
}
