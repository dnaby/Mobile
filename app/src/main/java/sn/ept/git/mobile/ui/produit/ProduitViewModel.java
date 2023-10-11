package sn.ept.git.mobile.ui.produit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.ProduitCall;
import sn.ept.git.mobile.api.models.produit.Produit;

public class ProduitViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData=new MutableLiveData<>();
    private final MutableLiveData<List<Produit>> produitList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getProduitsError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postProduitError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateProduitError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteProduitError = new MutableLiveData<>();
    private String errorMessage;

    public ProduitViewModel() {  }

    public void getProducts() {
        ProduitCall produitCall = ClientAPI.getProduitCall();
        Call<List<Produit>> call = produitCall.getProducts();
        call.enqueue(new Callback<List<Produit>>() {
            @Override
            public void onResponse(@NonNull Call<List<Produit>> call, @NonNull Response<List<Produit>> response) {
                if (response.isSuccessful()) {
                    List<Produit> produits = response.body();
                    produitList.setValue(produits);
                } else {
                    getProduitsError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Produit>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getProduitsError.setValue(true);
            }
        });
    }

    public MutableLiveData<List<Produit>> getProduitList() {
        return produitList;
    }

    public MutableLiveData<Boolean> getGetProduitsError() {
        return getProduitsError;
    }

    public MutableLiveData<Boolean> getPostProduitError() {
        return postProduitError;
    }

    public MutableLiveData<Boolean> getUpdateProduitError() {
        return updateProduitError;
    }

    public MutableLiveData<Boolean> getDeleteProduitError() {
        return deleteProduitError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) { mutableLiveData.setValue(s); }

    public MutableLiveData<String> getText() { return mutableLiveData; }
}
