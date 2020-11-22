package io.sevenbit;

import com.lmax.disruptor.EventFactory;

/**
 * Pre-allocates AppEvent instances
 */
public class AppEventFactory implements EventFactory<AppEvent> {

    public AppEvent newInstance() {
        return new AppEvent();
    }
}
