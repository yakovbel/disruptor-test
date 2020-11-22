package io.sevenbit;

import com.lmax.disruptor.EventHandler;

/**
 * Handles upcoming events
 */
public class AppEventHandler implements EventHandler<AppEvent> {

    public void onEvent(AppEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(event);

    }
}
