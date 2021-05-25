package fr.elplauto.gocrypto.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CryptoInWallet implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("purchasingPrice")
    @Expose
    private Double purchasingPrice;

    public CryptoInWallet() {}

    public CryptoInWallet(Integer id, Double amount, Double purchasingPrice) {
        this.id = id;
        this.amount = amount;
        this.purchasingPrice = purchasingPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPurchasingPrice() {
        return purchasingPrice;
    }

    public void setPurchasingPrice(Double purchasingPrice) {
        this.purchasingPrice = purchasingPrice;
    }

}
