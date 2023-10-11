package sn.ept.git.mobile.ui.marque;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.MarqueCall;
import sn.ept.git.mobile.api.models.marque.Marque;
import sn.ept.git.mobile.api.models.marque.MarqueRequest;
import sn.ept.git.mobile.api.models.marque.MarqueResponse;
import sn.ept.git.mobile.db.Handler;

public class MarqueViewModel extends ViewModel {
    private final MutableLiveData<List<Marque>> marqueList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getMarquesError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postMarqueError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateMarqueError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteMarqueError = new MutableLiveData<>();
    private String errorMessage;

    public MarqueViewModel() {  }

    public void getMarques() {
        MarqueCall marqueCall = ClientAPI.getMarqueCall();
        Call<List<Marque>> call = marqueCall.getMarques();
        call.enqueue(new Callback<List<Marque>>() {
            @Override
            public void onResponse(@NonNull Call<List<Marque>> call, @NonNull Response<List<Marque>> response) {
                if (response.isSuccessful()) {
                    List<Marque> marques = response.body();
                    marqueList.setValue(marques);
                } else {
                    getMarquesError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Marque>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getMarquesError.setValue(true);
            }
        });
    }


    public void createNewMarque(String nom) {
        MarqueCall marqueCall = ClientAPI.getMarqueCall();
        Call<MarqueResponse> call = marqueCall.createNewMarque(new MarqueRequest(nom));
        call.enqueue(new Callback<MarqueResponse>() {
            @Override
            public void onResponse(@NonNull Call<MarqueResponse> call, @NonNull Response<MarqueResponse> response) {
                if (response.isSuccessful()) {
                    MarqueResponse marqueResponse = response.body();
                    List<Marque> marques = marqueList.getValue();
                    marques.add(marqueResponse.getMarque());
                    marqueList.setValue(marques);
                } else {
                    if (response.code() == 409) {
                        errorMessage = nom + " existe déjà dans la base !";
                    } else {
                        errorMessage = "Erreur lors de la création de la marque de nom " + nom + " !";
                    }
                    postMarqueError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarqueResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la création de la marque de nom " + nom + " !";
                postMarqueError.setValue(true);
            }
        });
    }

    public void updateMarque(int id, String nom) {
        MarqueCall marqueCall = ClientAPI.getMarqueCall();
        Call<MarqueResponse> call = marqueCall.updateMarque(id, new MarqueRequest(nom));
        call.enqueue(new Callback<MarqueResponse>() {
            @Override
            public void onResponse(@NonNull Call<MarqueResponse> call, @NonNull Response<MarqueResponse> response) {
                if (response.isSuccessful()) {
                    MarqueResponse marqueResponse = response.body();
                    // TODO FIND AND UPDATE CATEGORIE INSIDE THE CATEGORIES LIST
                    List<Marque> marques = marqueList.getValue();
                    for (int i = 0; i < marques.size(); i++) {
                        Marque marque = marques.get(i);
                        if (marque.getId() == id) {
                            marque.setNom(nom);
                            marques.set(i, marque);
                            marqueList.setValue(marques);
                            break;
                        }
                    }
                } else {
                    if (response.code() == 404) {
                        errorMessage = "Une marque avec l' " + id + " n'existe pas dans la base !";
                    } else if (response.code() == 409) {
                        errorMessage = "Une marque avec le nom " + nom + " existe déjà dans la base !";
                    } else {
                        errorMessage = "Erreur lors de la MAJ de la marque d'id=" + id + " !";
                    }
                    updateMarqueError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarqueResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la MAJ de la marque d'id=" + id + " !";
                updateMarqueError.setValue(true);
            }
        });
    }

    public void deleteMarque(int id) {
        MarqueCall marqueCall = ClientAPI.getMarqueCall();
        Call<MarqueResponse> call = marqueCall.deleteMarque(id);
        call.enqueue(new Callback<MarqueResponse>() {
            @Override
            public void onResponse(@NonNull Call<MarqueResponse> call, @NonNull Response<MarqueResponse> response) {
                if (response.isSuccessful()) {
                    MarqueResponse marqueResponse = response.body();
                    // TODO FIND AND REMOVE CATEGORIE INSIDE THE CATEGORIES LIST
                    List<Marque> marques = marqueList.getValue();
                    for (int i = 0; i < marques.size(); i++) {
                        Marque marque = marques.get(i);
                        if (marque.getId() == id) {
                            marques.remove(i);
                            marqueList.setValue(marques);
                            break;
                        }
                    }
                } else {
                    if (response.code() == 404) {
                        errorMessage = "Une marque avec l' " + id + " n'existe pas dans la base !";
                    } else {
                        errorMessage = "Erreur lors de la MAJ de la marque d'id=" + id + " !";
                    }
                    deleteMarqueError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarqueResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la MAJ de la marque d'id=" + id + " !";
                deleteMarqueError.setValue(true);
            }
        });
    }

    public MutableLiveData<List<Marque>> getMarqueList() {
        return marqueList;
    }

    public MutableLiveData<Boolean> getGetMarquesError() {
        return getMarquesError;
    }

    public MutableLiveData<Boolean> getPostMarqueError() {
        return postMarqueError;
    }

    public MutableLiveData<Boolean> getUpdateMarqueError() {
        return updateMarqueError;
    }

    public MutableLiveData<Boolean> getDeleteMarqueError() {
        return deleteMarqueError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}