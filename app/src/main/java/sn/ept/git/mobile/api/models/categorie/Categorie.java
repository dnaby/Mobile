package sn.ept.git.mobile.api.models.categorie;

public class Categorie {
    private int id;
    private String nom;

    public Categorie() { }

    public Categorie(int id) { this.id = id; }

    public Categorie(int id, String categorie) {
        this.id = id;
        this.nom = categorie;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }

    public void setNom(String categorie) { this.nom = categorie; }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
