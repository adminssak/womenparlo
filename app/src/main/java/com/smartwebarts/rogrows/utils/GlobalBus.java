package com.smartwebarts.rogrows.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Sunny on 01-02-2017.
 */

public class GlobalBus {

    private static EventBus sBus;

    public static EventBus getBus() {
        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }

}
