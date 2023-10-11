package sn.ept.git.mobile.api.models.categorie;

public class CategorieRequest {
    private String nom;

    public CategorieRequest(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
