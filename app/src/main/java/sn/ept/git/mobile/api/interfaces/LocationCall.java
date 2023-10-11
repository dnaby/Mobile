package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sn.ept.git.mobile.api.models.location.Location;
import sn.ept.git.mobile.api.models.location.LocationResponse;

public interface LocationCall {
    @POST("locations/")
    Call<LocationResponse> postLocation(@Body Location location);
}
