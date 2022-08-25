package com.smartwebarts.rogrows.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {

        /*<color name="defaultcolor">#000000</color>
            <color name="success">#008000</color>
            <color name="warning">#FFFF00</color>
            <color name="danger">#FF0000</color>
            <color name="info">#EE82EE</color>*/

        String resColor = "#000000";

        if (color.equalsIgnoreCase("text-success")) {
            resColor = "#008000";
        } else if (color.equalsIgnoreCase("text-warning")) {
            resColor = "#FFFF00";
        } else if (color.equalsIgnoreCase("text-Danger")) {
            resColor = "#FF0000";
        } else if (color.equalsIgnoreCase("text-Info")) {
            resColor = "#EE82EE";
        }

        return resColor;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNote() {

        note = "<span style=\"color:"+getColor()+";\">"+"           "+"</span>"+
                "<span style=\"color:"+getColor()+";\">"+note+"</span>";
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
