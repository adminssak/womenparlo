package com.smartwebarts.rogrows.utils;

/**
 * Created by Sunny on 01-02-2017.
 */


public class ActivityActivityMessage {
    private String message;
    private String from;

    public ActivityActivityMessage(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {return from;}
}


