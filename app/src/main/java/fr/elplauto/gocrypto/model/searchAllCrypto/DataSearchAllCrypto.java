package fr.elplauto.gocrypto.model.searchAllCrypto;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataSearchAllCrypto {

    @SerializedName("status")
    @Expose
    private DataSearchStatus dataSearchStatus;
    @SerializedName("data")
    @Expose
    private List<DataSearchCrypto> dataSearchCryptos = null;

    public DataSearchStatus getDataSearchStatus() {
        return dataSearchStatus;
    }

    public void setDataSearchStatus(DataSearchStatus dataSearchStatus) {
        this.dataSearchStatus = dataSearchStatus;
    }

    public List<DataSearchCrypto> getDataSearchCryptos() {
        return dataSearchCryptos;
    }

    public void setDataSearchCryptos(List<DataSearchCrypto> dataSearchCryptos) {
        this.dataSearchCryptos = dataSearchCryptos;
    }

}








