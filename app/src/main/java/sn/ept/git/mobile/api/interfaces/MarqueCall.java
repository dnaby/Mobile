package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.categorie.CategorieRequest;
import sn.ept.git.mobile.api.models.categorie.CategorieResponse;
import sn.ept.git.mobile.api.models.marque.Marque;
import sn.ept.git.mobile.api.models.marque.MarqueRequest;
import sn.ept.git.mobile.api.models.marque.MarqueResponse;

public interface MarqueCall {
    @GET("marques/")
    Call<List<Marque>> getMarques();

    @POST("marques/")
    Call<MarqueResponse> createNewMarque(@Body MarqueRequest marqueRequest);

    @PUT("marques/{id}")
    Call<MarqueResponse> updateMarque(@Path("id") int id, @Body MarqueRequest marqueRequest);

    @DELETE("marques/{id}")
    Call<MarqueResponse> deleteMarque(@Path("id") int id);
}
