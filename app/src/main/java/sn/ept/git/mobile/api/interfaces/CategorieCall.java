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
import sn.ept.git.mobile.api.models.categorie.CategorieResponse;
import sn.ept.git.mobile.api.models.categorie.CategorieRequest;

public interface CategorieCall {
    @GET("categories/")
    Call<List<Categorie>> getCategories();

    @POST("categories/")
    Call<CategorieResponse> createNewCategorie(@Body CategorieRequest createCategorieRequest);

    @PUT("categories/{id}")
    Call<CategorieResponse> updateCategorie(@Path("id") int id, @Body CategorieRequest createCategorieRequest);

    @DELETE("categories/{id}")
    Call<CategorieResponse> deleteCategorie(@Path("id") int id);
}
