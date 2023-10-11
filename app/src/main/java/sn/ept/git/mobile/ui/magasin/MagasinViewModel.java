package sn.ept.git.mobile.ui.magasin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.MagasinCall;
import sn.ept.git.mobile.api.models.magasin.Magasin;
public class MagasinViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData=new MutableLiveData<>();
    private final MutableLiveData<List<Magasin>> magasinList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getMagasinsError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postMagasinError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateMagasinError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteMagasinError = new MutableLiveData<>();
    private String errorMessage;

    public MagasinViewModel() {  }

    public void getMagasins() {
        MagasinCall magasinCall = ClientAPI.getMagasinCall();
        Call<List<Magasin>> call = magasinCall.getMagasins();
        call.enqueue(new Callback<List<Magasin>>() {
            @Override
            public void onResponse(@NonNull Call<List<Magasin>> call, @NonNull Response<List<Magasin>> response) {
                if (response.isSuccessful()) {
                    List<Magasin> magasins = response.body();
                    magasinList.setValue(magasins);
                } else {
                    getMagasinsError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Magasin>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getMagasinsError.setValue(true);
            }
        });
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<Magasin>> getMagasinList() {
        return magasinList;
    }

    public MutableLiveData<Boolean> getGetMagasinsError() {
        return getMagasinsError;
    }

    public MutableLiveData<Boolean> getPostMagasinError() {
        return postMagasinError;
    }

    public MutableLiveData<Boolean> getUpdateMagasinError() {
        return updateMagasinError;
    }

    public MutableLiveData<Boolean> getDeleteMagasinError() {
        return deleteMagasinError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) { mutableLiveData.setValue(s); }

    public MutableLiveData<String> getText() { return mutableLiveData; }
}