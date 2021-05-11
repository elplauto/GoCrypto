package fr.elplauto.gocrypto.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallet {

    @SerializedName("USD")
    @Expose
    private Double usd;
    @SerializedName("crypto")
    @Expose
    private List<CryptoInWallet> crypto = null;
    @SerializedName("history")
    @Expose
    private List<History> history = null;

    public Wallet() {
        crypto = new ArrayList<>();
        history = new ArrayList<>();
    }

    public Wallet(Double usd) {
        this();
        this.usd = usd;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public List<CryptoInWallet> getCrypto() {
        return crypto;
    }

    public void setCrypto(List<CryptoInWallet> crypto) {
        this.crypto = crypto;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }
}