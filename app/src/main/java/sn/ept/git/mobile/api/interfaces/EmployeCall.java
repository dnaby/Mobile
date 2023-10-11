package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sn.ept.git.mobile.api.models.employe.Employe;
import sn.ept.git.mobile.api.models.stock.Stock;

public interface EmployeCall {
    @GET("employes/")
    Call<List<Employe>> getEmployes();
}
