package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.api.models.stock.Stock;

public interface CommandeCall {
    @GET("commandes/")
    Call<List<Commande>> getCommandes();
}
