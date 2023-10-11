package sn.ept.git.mobile.api.models.marque;

public class MarqueRequest {
    private String nom;

    public MarqueRequest(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
