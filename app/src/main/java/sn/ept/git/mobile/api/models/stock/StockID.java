package sn.ept.git.mobile.api.models.stock;

import java.util.Objects;

public class StockID {
    private int magasinId;
    private int produitId;

    public StockID() {
    }

    public StockID(int magasinId, int produitId) {
        this.magasinId = magasinId;
        this.produitId = produitId;
    }

    public int getMagasinId() {
        return magasinId;
    }

    public void setMagasinId(int magasinId) {
        this.magasinId = magasinId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockID stockID = (StockID) o;
        return magasinId == stockID.magasinId && produitId == stockID.produitId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(magasinId, produitId);
    }

    @Override
    public String toString() {
        return "StockID{" +
                "magasinId=" + magasinId +
                ", produitId=" + produitId +
                '}';
    }
}
