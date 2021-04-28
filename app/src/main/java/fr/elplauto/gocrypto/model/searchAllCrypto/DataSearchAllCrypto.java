package fr.elplauto.gocrypto.model.searchAllCrypto;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataSearchAllCrypto {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private List<Crypto> cryptos = null;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Crypto> getCryptos() {
        return cryptos;
    }

    public void setCryptos(List<Crypto> cryptos) {
        this.cryptos = cryptos;
    }

}








