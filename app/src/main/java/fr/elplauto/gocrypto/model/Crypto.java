package fr.elplauto.gocrypto.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Crypto {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("priceHistory")
    @Expose
    private List<History> priceHistory = null;
    @SerializedName("percentChange1h")
    @Expose
    private Double percentChange1h;
    @SerializedName("percentChange24h")
    @Expose
    private Double percentChange24h;
    @SerializedName("percentChange7d")
    @Expose
    private Double percentChange7d;
    @SerializedName("percentChange30d")
    @Expose
    private Double percentChange30d;
    @SerializedName("percentChange60d")
    @Expose
    private Double percentChange60d;
    @SerializedName("percentChange90d")
    @Expose
    private Double percentChange90d;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<History> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<History> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public Double getPercentChange1h() {
        return percentChange1h;
    }

    public void setPercentChange1h(Double percentChange1h) {
        this.percentChange1h = percentChange1h;
    }

    public Double getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(Double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public Double getPercentChange7d() {
        return percentChange7d;
    }

    public void setPercentChange7d(Double percentChange7d) {
        this.percentChange7d = percentChange7d;
    }

    public Double getPercentChange30d() {
        return percentChange30d;
    }

    public void setPercentChange30d(Double percentChange30d) {
        this.percentChange30d = percentChange30d;
    }

    public Double getPercentChange60d() {
        return percentChange60d;
    }

    public void setPercentChange60d(Double percentChange60d) {
        this.percentChange60d = percentChange60d;
    }

    public Double getPercentChange90d() {
        return percentChange90d;
    }

    public void setPercentChange90d(Double percentChange90d) {
        this.percentChange90d = percentChange90d;
    }

    public Double getPercentChange(String time) {
        switch (time) {
            case "1h":
                return this.getPercentChange1h();
            case "24h":
                return this.getPercentChange24h();
            case "7d":
                return this.getPercentChange7d();
            case "30d":
                return this.getPercentChange30d();
            case "60d":
                return this.getPercentChange60d();
            case "90d":
                return this.getPercentChange90d();
            default:
                return 0d;
        }
    }
}
