package sn.ept.git.mobile.ui.article_commande;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.ArticleCommandeCall;
import sn.ept.git.mobile.api.models.article_commande.ArticleCommande;

public class ArticleCommandeViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData=new MutableLiveData<>();
    private final MutableLiveData<List<ArticleCommande>> articleCommandeList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getArticleCommandesError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postArticleCommandeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateArticleCommandeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteArticleCommandeError = new MutableLiveData<>();
    private String errorMessage;

    public ArticleCommandeViewModel() {  }

    public void getArticleCommandes() {
        ArticleCommandeCall articleCommandeCall = ClientAPI.getArticleCommandeCall();
        Call<List<ArticleCommande>> call = articleCommandeCall.getArticleCommandes();
        call.enqueue(new Callback<List<ArticleCommande>>() {
            @Override
            public void onResponse(@NonNull Call<List<ArticleCommande>> call, @NonNull Response<List<ArticleCommande>> response) {
                if (response.isSuccessful()) {
                    List<ArticleCommande> articleCommandes = response.body();
                    articleCommandeList.setValue(articleCommandes);
                } else {
                    getArticleCommandesError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ArticleCommande>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getArticleCommandesError.setValue(true);
            }
        });
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<ArticleCommande>> getArticleCommandeList() {
        return articleCommandeList;
    }

    public MutableLiveData<Boolean> getGetArticleCommandesError() {
        return getArticleCommandesError;
    }

    public MutableLiveData<Boolean> getPostArticleCommandeError() {
        return postArticleCommandeError;
    }

    public MutableLiveData<Boolean> getUpdateArticleCommandeError() {
        return updateArticleCommandeError;
    }

    public MutableLiveData<Boolean> getDeleteArticleCommandeError() {
        return deleteArticleCommandeError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) { mutableLiveData.setValue(s); }

    public MutableLiveData<String> getText() { return mutableLiveData; }
}