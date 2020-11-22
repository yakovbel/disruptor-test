package io.sevenbit;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        AppEventFactory eventFactory = new AppEventFactory();
        int bufferSize = 1024;
        Disruptor<AppEvent> disruptor = new Disruptor<>(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new AppEventHandler());
        disruptor.start();

        RingBuffer<AppEvent> ringBuffer = disruptor.getRingBuffer();
        AppEventProducer producer = new AppEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);

        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            producer.onData(bb);
            Thread.sleep(1000);
        }
    }
}
