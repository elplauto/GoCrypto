package fr.elplauto.gocrypto.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataSearchAllCrypto {

    @SerializedName("data")
    @Expose
    private List<Crypto> data = null;

    public List<Crypto> getData() {
        return data;
    }

    public void setData(List<Crypto> data) {
        this.data = data;
    }
}








