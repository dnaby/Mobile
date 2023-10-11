package sn.ept.git.mobile.api.models.stock;

import java.util.List;

public class SyncStock {
    private List<Stock> stocks;

    public SyncStock() {
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }
}
