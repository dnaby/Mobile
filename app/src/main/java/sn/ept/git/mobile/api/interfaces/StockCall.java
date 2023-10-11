package sn.ept.git.mobile.api.interfaces;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import sn.ept.git.mobile.api.models.categorie.CategorieRequest;
import sn.ept.git.mobile.api.models.categorie.CategorieResponse;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.api.models.stock.NewStockRequest;
import sn.ept.git.mobile.api.models.stock.Stock;
import sn.ept.git.mobile.api.models.stock.StockResponse;
import sn.ept.git.mobile.api.models.stock.SyncStock;
import sn.ept.git.mobile.api.models.stock.UpdateStockRequest;

public interface StockCall {
    @GET("stocks/")
    Call<List<Stock>> getStocks();

    @POST("stocks/")
    Call<StockResponse> createNewStock(@Body NewStockRequest newStockRequest);

    @PUT("stocks/{produitId}/{magasinId}")
    Call<StockResponse> updateStock(@Path("produitId") int produitId, @Path("magasinId") int magasinId, @Body UpdateStockRequest updateStockRequest);

    @DELETE("categories/{produitId}/{magasinId}")
    Call<StockResponse> deleteStock(@Path("produitId") int produitId, @Path("magasinId") int magasinId);

    @POST("stocks/sync/")
    Call<Object> syncData(@Body SyncStock syncStock);
}
