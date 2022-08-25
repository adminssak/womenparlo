package com.smartwebarts.rogrows.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("landmark")
    @Expose
    private String landmark;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAddress() {
        return address!=null?address:"";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city!=null?city:"";
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode!=null?pincode:"";
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark!=null?landmark:"";
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getState() {
        return state!=null?state:"";
    }

    public void setState(String state) {
        this.state = state;
    }
}
