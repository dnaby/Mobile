package sn.ept.git.mobile.api.models.article_commande;

import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.api.models.produit.Produit;

public class ArticleCommande {
    private ArticleCommandeId id;
    private int produitId;
    private int quantite;
    private double prixDepart;
    private double remise;
    private Produit produit;
    private Commande commande;

    public ArticleCommande() {
    }

    public ArticleCommandeId getId() {
        return id;
    }

    public void setId(ArticleCommandeId id) {
        this.id = id;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixDepart() {
        return prixDepart;
    }

    public void setPrixDepart(double prixDepart) {
        this.prixDepart = prixDepart;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }
}
