package sn.ept.git.mobile.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sn.ept.git.mobile.api.interfaces.ArticleCommandeCall;
import sn.ept.git.mobile.api.interfaces.CategorieCall;
import sn.ept.git.mobile.api.interfaces.ClientCall;
import sn.ept.git.mobile.api.interfaces.CommandeCall;
import sn.ept.git.mobile.api.interfaces.EmployeCall;
import sn.ept.git.mobile.api.interfaces.LocationCall;
import sn.ept.git.mobile.api.interfaces.MagasinCall;
import sn.ept.git.mobile.api.interfaces.MarqueCall;
import sn.ept.git.mobile.api.interfaces.ProduitCall;
import sn.ept.git.mobile.api.interfaces.StockCall;

public class ClientAPI {
    private static final String API_BASE_URL = "http://192.168.1.10:8080/projet_matiere-1.0-SNAPSHOT/api/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static CategorieCall getCategorieCall() {
        return ClientAPI.getClient().create(CategorieCall.class);
    }

    public static MarqueCall getMarqueCall() {
        return ClientAPI.getClient().create(MarqueCall.class);
    }

    public static ProduitCall getProduitCall() {
        return ClientAPI.getClient().create(ProduitCall.class);
    }

    public static MagasinCall getMagasinCall() {
        return ClientAPI.getClient().create(MagasinCall.class);
    }

    public static StockCall getStockCall() {
        return ClientAPI.getClient().create(StockCall.class);
    }

    public static ClientCall getClientCall() {
        return ClientAPI.getClient().create(ClientCall.class);
    }

    public static EmployeCall getEmployeCall() {
        return ClientAPI.getClient().create(EmployeCall.class);
    }

    public static CommandeCall getCommandeCall() {
        return ClientAPI.getClient().create(CommandeCall.class);
    }

    public static ArticleCommandeCall getArticleCommandeCall() {
        return ClientAPI.getClient().create(ArticleCommandeCall.class);
    }

    public static LocationCall getLocationCall() {
        return ClientAPI.getClient().create(LocationCall.class);
    }
}
