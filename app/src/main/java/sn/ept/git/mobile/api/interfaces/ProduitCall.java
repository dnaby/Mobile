package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sn.ept.git.mobile.api.models.produit.Produit;

public interface ProduitCall {
    @GET("produits/")
    Call<List<Produit>> getProducts();
}
