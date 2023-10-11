package sn.ept.git.mobile.api.models.stock;

public class StockResponse {
    private String msg;
    private Stock stock;

    public StockResponse() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
