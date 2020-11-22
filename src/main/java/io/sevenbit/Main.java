package io.sevenbit;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

public class Main {


    public static void main(String[] args) throws InterruptedException {
        int bufferSize = 1024;
        Disruptor<AppEvent> disruptor = new Disruptor<>(AppEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(Main::handleEvent);
        disruptor.start();
        RingBuffer<AppEvent> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            ringBuffer.publishEvent(Main::translate, bb);
            Thread.sleep(1000);
        }
    }

    public static void handleEvent(AppEvent event, long sequence, boolean endOfBatch) {
        System.out.println(event);
    }

    public static void translate(AppEvent event, long sequence, ByteBuffer buffer) {
        event.setValue(Long.toString(buffer.getLong(0)));
    }
}
