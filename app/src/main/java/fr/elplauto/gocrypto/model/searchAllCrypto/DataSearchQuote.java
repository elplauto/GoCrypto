package fr.elplauto.gocrypto.model.searchAllCrypto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataSearchQuote {

    @SerializedName("USD")
    @Expose
    private DataSearchUsd dataSearchUsd;

    public DataSearchUsd getDataSearchUsd() {
        return dataSearchUsd;
    }

    public void setDataSearchUsd(DataSearchUsd dataSearchUsd) {
        this.dataSearchUsd = dataSearchUsd;
    }

}
