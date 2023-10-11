package sn.ept.git.mobile.ui.client;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.ClientCall;
import sn.ept.git.mobile.api.interfaces.MagasinCall;
import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.magasin.Magasin;

public class ClientViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Client>> clientList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getClientsError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postClientError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateClientError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteClientError = new MutableLiveData<>();
    private String errorMessage;

    public ClientViewModel() {
    }

    public void getClients() {
        ClientCall clientCall = ClientAPI.getClientCall();
        Call<List<Client>> call = clientCall.getClients();
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(@NonNull Call<List<Client>> call, @NonNull Response<List<Client>> response) {
                if (response.isSuccessful()) {
                    List<Client> clients = response.body();
                    clientList.setValue(clients);
                } else {
                    getClientsError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Client>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getClientsError.setValue(true);
            }
        });
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<Client>> getClientList() {
        return clientList;
    }

    public MutableLiveData<Boolean> getGetClientsError() {
        return getClientsError;
    }

    public MutableLiveData<Boolean> getPostClientError() {
        return postClientError;
    }

    public MutableLiveData<Boolean> getUpdateClientError() {
        return updateClientError;
    }

    public MutableLiveData<Boolean> getDeleteClientError() {
        return deleteClientError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) {
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getText() {
        return mutableLiveData;
    }
}