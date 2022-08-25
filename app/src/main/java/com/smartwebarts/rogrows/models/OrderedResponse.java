package com.smartwebarts.rogrows.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderedResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("orderid")
    @Expose
    private String orderid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
}
