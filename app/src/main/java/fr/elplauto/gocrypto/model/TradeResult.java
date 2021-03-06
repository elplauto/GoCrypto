package fr.elplauto.gocrypto.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TradeResult {

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
