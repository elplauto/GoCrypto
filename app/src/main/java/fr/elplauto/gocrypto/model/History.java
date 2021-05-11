package fr.elplauto.gocrypto.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("value")
    @Expose
    private Double value;

    public History() {}

    public History(String timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
