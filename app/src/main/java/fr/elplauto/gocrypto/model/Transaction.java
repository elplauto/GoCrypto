package fr.elplauto.gocrypto.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable {

    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("USD")
    @Expose
    private Double usd;
    @SerializedName("cryptoId")
    @Expose
    private Integer cryptoId;
    @SerializedName("amount")
    @Expose
    private Double amount;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Integer getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(Integer cryptoId) {
        this.cryptoId = cryptoId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}