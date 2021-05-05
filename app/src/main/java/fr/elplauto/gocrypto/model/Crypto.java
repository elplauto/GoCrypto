package fr.elplauto.gocrypto.model;

public class Crypto {

    private String id;
    private String name;
    private String symbol;
    private Double price;
    private Double percentChange1h;
    private Double percentChange24h;
    private Double percentChange7d;
    private Double percentChange30d;
    private Double percentChange60d;
    private Double percentChange90d;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
