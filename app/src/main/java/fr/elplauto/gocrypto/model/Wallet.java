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
    @SerializedName("history_1h")
    @Expose
    private List<History> history1h = null;
    @SerializedName("history_7d")
    @Expose
    private List<History> history7d = null;

    public Wallet() {
        this.history1h = new ArrayList<>();
        this.history7d = new ArrayList<>();
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

    public List<History> getHistory1h() {
        return history1h;
    }

    public void setHistory1h(List<History> history1h) {
        this.history1h = history1h;
    }

    public List<History> getHistory7d() {
        return history7d;
    }

    public void setHistory7d(List<History> history7d) {
        this.history7d = history7d;
    }
}