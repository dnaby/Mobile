package sn.ept.git.mobile.api.models.commande;

import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.employe.Employe;
import sn.ept.git.mobile.api.models.magasin.Magasin;

public class Commande {
    private int id;
    private int clientId;
    private int statut;
    private String dateCommande;
    private String dateLivraison;
    private String dateLivraisonVoulue;
    private int magasinId;
    private int vendeurId;
    private Client client;
    private Magasin magasin;
    private Employe vendeur;

    public Commande() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public String getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(String dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public String getDateLivraisonVoulue() {
        return dateLivraisonVoulue;
    }

    public void setDateLivraisonVoulue(String dateLivraisonVoulue) {
        this.dateLivraisonVoulue = dateLivraisonVoulue;
    }

    public int getMagasinId() {
        return magasinId;
    }

    public void setMagasinId(int magasinId) {
        this.magasinId = magasinId;
    }

    public int getVendeurId() {
        return vendeurId;
    }

    public void setVendeurId(int vendeurId) {
        this.vendeurId = vendeurId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Magasin getMagasin() {
        return magasin;
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    public Employe getVendeur() {
        return vendeur;
    }

    public void setVendeur(Employe vendeur) {
        this.vendeur = vendeur;
    }
}
