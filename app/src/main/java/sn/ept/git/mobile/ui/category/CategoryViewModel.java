package sn.ept.git.mobile.ui.category;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.CategorieCall;
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.categorie.CategorieResponse;
import sn.ept.git.mobile.api.models.categorie.CategorieRequest;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<Categorie>> categorieList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getCategoriesError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postCategorieError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateCategorieError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteCategorieError = new MutableLiveData<>();
    private String errorMessage;

    public CategoryViewModel() {  }

    public void getCategories() {
        CategorieCall categorieCall = ClientAPI.getCategorieCall();
        Call<List<Categorie>> call = categorieCall.getCategories();
        call.enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Categorie>> call, @NonNull Response<List<Categorie>> response) {
                if (response.isSuccessful()) {
                    List<Categorie> categories = response.body();
                    categorieList.setValue(categories);
                } else {
                    getCategoriesError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Categorie>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getCategoriesError.setValue(true);
            }
        });
    }

    public void createNewCategorie(String nom) {
        CategorieCall categorieCall = ClientAPI.getCategorieCall();
        Call<CategorieResponse> call = categorieCall.createNewCategorie(new CategorieRequest(nom));
        call.enqueue(new Callback<CategorieResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategorieResponse> call, @NonNull Response<CategorieResponse> response) {
                if (response.isSuccessful()) {
                    CategorieResponse categorieResponse = response.body();
                    List<Categorie> categories = categorieList.getValue();
                    categories.add(categorieResponse.getCategorie());
                    categorieList.setValue(categories);
                } else {
                    if (response.code() == 409) {
                        errorMessage = nom + " existe déjà dans la base !";
                    } else {
                        errorMessage = "Erreur lors de la création de la catégorie de nom " + nom + " !";
                    }
                    postCategorieError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategorieResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la création de la catégorie de nom " + nom + " !";
                postCategorieError.setValue(true);
            }
        });
    }

    public void updateCategorie(int id, String nom) {
        CategorieCall categorieCall = ClientAPI.getCategorieCall();
        Call<CategorieResponse> call = categorieCall.updateCategorie(id, new CategorieRequest(nom));
        call.enqueue(new Callback<CategorieResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategorieResponse> call, @NonNull Response<CategorieResponse> response) {
                if (response.isSuccessful()) {
                    CategorieResponse categorieResponse = response.body();
                    List<Categorie> categories = categorieList.getValue();
                    for (int i = 0; i < categories.size(); i++) {
                        Categorie category = categories.get(i);
                        if (category.getId() == id) {
                            category.setNom(nom);
                            categories.set(i, category);
                            categorieList.setValue(categories);
                            break;
                        }
                    }
                } else {
                    if (response.code() == 404) {
                        errorMessage = "Une catégorie avec l' " + id + " n'existe pas dans la base !";
                    } else if (response.code() == 409) {
                        errorMessage = "Une catégorie avec le nom " + nom + " existe déjà dans la base !";
                    } else {
                        errorMessage = "Erreur lors de la MAJ de la catégorie d'id=" + id + " !";
                    }
                    updateCategorieError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategorieResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la MAJ de la catégorie d'id=" + id + " !";
                updateCategorieError.setValue(true);
            }
        });
    }

    public void deleteCategorie(int id) {
        CategorieCall categorieCall = ClientAPI.getCategorieCall();
        Call<CategorieResponse> call = categorieCall.deleteCategorie(id);
        call.enqueue(new Callback<CategorieResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategorieResponse> call, @NonNull Response<CategorieResponse> response) {
                if (response.isSuccessful()) {
                    CategorieResponse categorieResponse = response.body();
                    // TODO FIND AND REMOVE CATEGORIE INSIDE THE CATEGORIES LIST
                    List<Categorie> categories = categorieList.getValue();
                    for (int i = 0; i < categories.size(); i++) {
                        Categorie category = categories.get(i);
                        if (category.getId() == id) {
                            categories.remove(i);
                            categorieList.setValue(categories);
                            break;
                        }
                    }
                } else {
                    if (response.code() == 404) {
                        errorMessage = "Une catégorie avec l' " + id + " n'existe pas dans la base !";
                    } else {
                        errorMessage = "Erreur lors de la MAJ de la catégorie d'id=" + id + " !";
                    }
                    deleteCategorieError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategorieResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la MAJ de la catégorie d'id=" + id + " !";
                deleteCategorieError.setValue(true);
            }
        });
    }

    public LiveData<List<Categorie>> getCategorieList() {
        return categorieList;
    }

    public MutableLiveData<Boolean> getGetCategoriesError() {
        return getCategoriesError;
    }

    public MutableLiveData<Boolean> getPostCategorieError() {
        return postCategorieError;
    }

    public MutableLiveData<Boolean> getUpdateCategorieError() {
        return updateCategorieError;
    }

    public MutableLiveData<Boolean> getDeleteCategorieError() {
        return deleteCategorieError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}