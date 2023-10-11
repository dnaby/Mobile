package sn.ept.git.mobile.api.models.produit;

import androidx.annotation.NonNull;

import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.marque.Marque;

public class Produit {
    private int id;
    private String nom;
    private Marque marque;
    private Categorie categorie;
    private int marqueId;
    private int categorieId;
    private int anneeModel;
    private double prixDepart;

    public Produit() {  }

    public Produit(int id, String nom, Marque marque,
                   Categorie categorie, int marqueId,
                   int categorieId, int anneeModel, double prixDepart) {
        this.id = id;
        this.nom = nom;
        this.marque = marque;
        this.categorie = categorie;
        this.marqueId = marqueId;
        this.categorieId = categorieId;
        this.anneeModel = anneeModel;
        this.prixDepart = prixDepart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public int getMarqueId() {
        return marqueId;
    }

    public void setMarqueId(int marqueId) {
        this.marqueId = marqueId;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public int getAnneeModel() {
        return anneeModel;
    }

    public void setAnneeModel(int anneeModel) {
        this.anneeModel = anneeModel;
    }

    public double getPrixDepart() {
        return prixDepart;
    }

    public void setPrixDepart(double prixDepart) {
        this.prixDepart = prixDepart;
    }

    @NonNull
    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", marque=" + marque +
                ", categorie=" + categorie +
                ", marqueId=" + marqueId +
                ", categorieId=" + categorieId +
                ", anneeModel=" + anneeModel +
                ", prixDepart=" + prixDepart +
                '}';
    }
}
