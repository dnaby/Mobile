package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sn.ept.git.mobile.api.models.magasin.Magasin;

public interface MagasinCall {
    @GET("magasins/")
    Call<List<Magasin>> getMagasins();
}
