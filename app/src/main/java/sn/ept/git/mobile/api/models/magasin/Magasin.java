package sn.ept.git.mobile.api.models.magasin;

import androidx.annotation.NonNull;

public class Magasin {
    private int id;
    private String nom;
    private String adresse;
    private String codeZip;
    private String email;
    private String etat;
    private String telephone;
    private String ville;

    public Magasin() {
    }

    public Magasin(int id, String nom, String adresse, String codeZip, String email, String etat, String telephone, String ville) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.codeZip = codeZip;
        this.email = email;
        this.etat = etat;
        this.telephone = telephone;
        this.ville = ville;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodeZip() {
        return codeZip;
    }

    public void setCodeZip(String codeZip) {
        this.codeZip = codeZip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    @NonNull
    @Override
    public String toString() {
        return "Magasin{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", codeZip='" + codeZip + '\'' +
                ", email='" + email + '\'' +
                ", etat='" + etat + '\'' +
                ", telephone='" + telephone + '\'' +
                ", ville='" + ville + '\'' +
                '}';
    }
}
