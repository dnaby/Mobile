package sn.ept.git.mobile.api.models.marque;

public class Marque {
    private int id;
    private String nom;

    public Marque() { }

    public Marque(int id) { this.id = id; }

    public Marque(int id, String categorie) {
        this.id = id;
        this.nom = categorie;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }

    public void setNom(String categorie) { this.nom = categorie; }

    @Override
    public String toString() {
        return "Marque{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
