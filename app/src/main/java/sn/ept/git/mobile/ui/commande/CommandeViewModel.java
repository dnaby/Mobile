package sn.ept.git.mobile.ui.commande;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.CommandeCall;
import sn.ept.git.mobile.api.interfaces.MagasinCall;
import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.api.models.magasin.Magasin;

public class CommandeViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData=new MutableLiveData<>();
    private final MutableLiveData<List<Commande>> commandeList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getCommandesError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postCommandeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateCommandeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteCommandeError = new MutableLiveData<>();
    private String errorMessage;

    public CommandeViewModel() {  }

    public void getCommandes() {
        CommandeCall commandeCall = ClientAPI.getCommandeCall();
        Call<List<Commande>> call = commandeCall.getCommandes();
        call.enqueue(new Callback<List<Commande>>() {
            @Override
            public void onResponse(@NonNull Call<List<Commande>> call, @NonNull Response<List<Commande>> response) {
                if (response.isSuccessful()) {
                    List<Commande> commandes = response.body();
                    commandeList.setValue(commandes);
                } else {
                    getCommandesError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Commande>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getCommandesError.setValue(true);
            }
        });
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<Commande>> getCommandeList() {
        return commandeList;
    }

    public MutableLiveData<Boolean> getGetCommandesError() {
        return getCommandesError;
    }

    public MutableLiveData<Boolean> getPostCommandeError() {
        return postCommandeError;
    }

    public MutableLiveData<Boolean> getUpdateCommandeError() {
        return updateCommandeError;
    }

    public MutableLiveData<Boolean> getDeleteCommandeError() {
        return deleteCommandeError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) { mutableLiveData.setValue(s); }

    public MutableLiveData<String> getText() { return mutableLiveData; }
}