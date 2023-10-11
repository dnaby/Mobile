package sn.ept.git.mobile.api.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import sn.ept.git.mobile.api.models.article_commande.ArticleCommande;
import sn.ept.git.mobile.api.models.commande.Commande;

public interface ArticleCommandeCall {
    @GET("article_commandes/")
    Call<List<ArticleCommande>> getArticleCommandes();
}
