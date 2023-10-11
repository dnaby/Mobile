package sn.ept.git.mobile.ui.stock;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.CategorieCall;
import sn.ept.git.mobile.api.interfaces.StockCall;
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.categorie.CategorieRequest;
import sn.ept.git.mobile.api.models.categorie.CategorieResponse;
import sn.ept.git.mobile.api.models.stock.NewStockRequest;
import sn.ept.git.mobile.api.models.stock.Stock;
import sn.ept.git.mobile.api.models.stock.StockID;
import sn.ept.git.mobile.api.models.stock.StockResponse;
import sn.ept.git.mobile.api.models.stock.UpdateStockRequest;
import sn.ept.git.mobile.connectivity.ConnectivityReceiver;

public class StockViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData=new MutableLiveData<>();
    private final MutableLiveData<List<Stock>> stockList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getStocksError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postStockError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStockError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteStockError = new MutableLiveData<>();
    private String errorMessage;

    public StockViewModel() {}

    public void getStocks() {
        StockCall stockCall = ClientAPI.getStockCall();
        Call<List<Stock>> call = stockCall.getStocks();
        call.enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(@NonNull Call<List<Stock>> call, @NonNull Response<List<Stock>> response) {
                if (response.isSuccessful()) {
                    List<Stock> stocks = response.body();
                    stockList.setValue(stocks);
                } else {
                    getStocksError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Stock>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getStocksError.setValue(true);
            }
        });
    }

    public void createNewStock(String produitName, String magasinName, int quantite) {
        NewStockRequest newStockRequest = new NewStockRequest();
        newStockRequest.setProductName(produitName);
        newStockRequest.setMagasinName(magasinName);
        newStockRequest.setQuantite(quantite);

        StockCall stockCall = ClientAPI.getStockCall();
        Call<StockResponse> call = stockCall.createNewStock(newStockRequest);
        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(@NonNull Call<StockResponse> call, @NonNull Response<StockResponse> response) {
                Log.d("RESPONSE", response.toString());
                if (response.isSuccessful()) {
                    StockResponse stockResponse = response.body();
                    List<Stock> stocks = stockList.getValue();
                    if (stockResponse != null && stocks != null) {
                        stocks.add(stockResponse.getStock());
                    }
                    stockList.setValue(stocks);
                } else {
                    if (response.code() == 409) {
                        errorMessage = "Stock with given Produit and Magasin already Exist";
                    } else if (response.code() == 404) {
                        errorMessage = "Stock with given Produit or Magasin do not Exist";
                    }
                    postStockError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la création du stock !";
                postStockError.setValue(true);
            }
        });
    }

    public void updateStock(Stock stock) {
        UpdateStockRequest updateStockRequest = new UpdateStockRequest();
        updateStockRequest.setQuantite(stock.getQuantite());

        StockCall stockCall = ClientAPI.getStockCall();
        Call<StockResponse> call = stockCall.updateStock(stock.getId().getProduitId(), stock.getId().getMagasinId(), updateStockRequest);
        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(@NonNull Call<StockResponse> call, @NonNull Response<StockResponse> response) {
                if (response.isSuccessful()) {
                    StockResponse stockResponse = response.body();
                    Stock updatedStock = stockResponse.getStock();
                    List<Stock> stocks = stockList.getValue();
                    for (int i = 0; i < stocks.size(); i++) {
                        if (stocks.get(i).getId() == updatedStock.getId()) {
                            stocks.set(i, updatedStock);
                            stockList.setValue(stocks);
                            break;
                        }
                    }
                } else {
                    if (response.code() == 404) {
                        errorMessage = "Stock with the given ID no longer exists in the database";
                    } else {
                        errorMessage = "Erreur lors de la MAJ du Stock !";
                    }
                    updateStockError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la MAJ du Stock !";
                updateStockError.setValue(true);
            }
        });
    }

    public void deleteStock(StockID stockID) {
        StockCall stockCall = ClientAPI.getStockCall();
        Call<StockResponse> call = stockCall.deleteStock(stockID.getProduitId(), stockID.getMagasinId());
        call.enqueue(new Callback<StockResponse>() {
            @Override
            public void onResponse(@NonNull Call<StockResponse> call, @NonNull Response<StockResponse> response) {
                if (response.isSuccessful()) {
                    StockResponse stockResponse = response.body();
                    List<Stock> stocks = stockList.getValue();
                    for (int i = 0; i < stocks.size(); i++) {
                        if (stocks.get(i).getId() == stockResponse.getStock().getId()) {
                            stocks.remove(i);
                            stockList.setValue(stocks);
                            break;
                        }
                    }
                } else {
                    if (response.code() == 404) {
                        errorMessage = "Stock with the given ID no longer exists in the database !";
                    } else {
                        errorMessage = "Erreur lors de la MAJ du stock !";
                    }
                    deleteStockError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                errorMessage = "Erreur lors de la MAJ du stock !";
                deleteStockError.setValue(true);
            }
        });
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<Stock>> getStockList() {
        return stockList;
    }

    public MutableLiveData<Boolean> getGetStocksError() {
        return getStocksError;
    }

    public MutableLiveData<Boolean> getPostStockError() {
        return postStockError;
    }

    public MutableLiveData<Boolean> getUpdateStockError() {
        return updateStockError;
    }

    public MutableLiveData<Boolean> getDeleteStockError() {
        return deleteStockError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) { mutableLiveData.setValue(s); }

    public MutableLiveData<String> getText() { return mutableLiveData; }
}