package sn.ept.git.mobile.api.models.stock;

import java.util.Objects;

import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.api.models.produit.Produit;

public class Stock {
    private StockID id;
    private Magasin magasin;
    private Produit produit;
    private int quantite;

    public Stock() {
    }

    public StockID getId() {
        return id;
    }

    public void setId(StockID id) {
        this.id = id;
    }

    public Magasin getMagasin() {
        return magasin;
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return id.equals(stock.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", magasin=" + magasin +
                ", produit=" + produit +
                ", quantite=" + quantite +
                '}';
    }
}
