package sn.ept.git.mobile.ui.stock;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import sn.ept.git.mobile.MainActivity;
import sn.ept.git.mobile.R;
import sn.ept.git.mobile.adapter.ProduitAdapter;
import sn.ept.git.mobile.adapter.StockAdapter;
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.api.models.stock.NewStockRequest;
import sn.ept.git.mobile.api.models.stock.Stock;
import sn.ept.git.mobile.api.models.stock.StockID;
import sn.ept.git.mobile.databinding.FragmentProduitBinding;
import sn.ept.git.mobile.databinding.FragmentStockBinding;
import sn.ept.git.mobile.db.Handler;
import sn.ept.git.mobile.ui.produit.ProduitViewModel;

public class StockFragment extends Fragment {
    private FragmentStockBinding binding;
    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private List<Stock> stockList = new ArrayList<>();
    private FloatingActionButton addStockButton;
    private StockViewModel stockViewModel;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        stockViewModel =
                new ViewModelProvider(this).get(StockViewModel.class);

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.stock_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(adapter);

        if (isOnline()) {
            stockViewModel.getStocks();
            stockViewModel.getStockList().observe(getViewLifecycleOwner(), stocks -> {
                Handler dbHandler = new Handler(requireContext());
                dbHandler.initStock(stocks);
                dbHandler.close();

                for (Stock stock: stocks) {
                    checkStockForNotification(stock);
                }

                stockList.clear();
                stockList.addAll(stocks);
                adapter.notifyDataSetChanged();
            });
        } else {
            List<Stock> stocks = getStocksFromLocalDatabase();

            for (Stock stock: stocks) {
                checkStockForNotification(stock);
            }

            stockList.clear();
            stockList.addAll(stocks);
            adapter.notifyDataSetChanged();
        }

        stockViewModel.getGetStocksError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving stocks", Toast.LENGTH_LONG).show();
            }
        });

        stockViewModel.getPostStockError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), stockViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        stockViewModel.getUpdateStockError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), stockViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        stockViewModel.getDeleteStockError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), stockViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addStockButton = ((MainActivity) requireActivity()).getFab();

        if (addStockButton != null) {
            addStockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Stock> filteredList = filterList(stockList, s);
                adapter.setStockList(filteredList);
                return true;
            }
        });
    }

    private List<Stock> filterList(List<Stock> originalList, String query) {
        List<Stock> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Stock stock : originalList) {
                if (stock.getProduit().getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(stock);
                }
            }
        }

        return filteredList;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo netInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfoMobile != null && netInfoMobile.isConnectedOrConnecting() ||
                netInfoWifi != null && netInfoWifi.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public List<Stock> getStocksFromLocalDatabase() {
        Handler dbHandler = new Handler(getContext());
        List<Stock> stockList = dbHandler.getAllStocks();
        dbHandler.close();
        return stockList;
    }

    public void showDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.stock_dialog);

        final EditText[] quantiteInput = {dialog.findViewById(R.id.input_quantite)};
        final String[] produitName = {""};
        final String[] magasinName = {""};
        final int[] quantite = new int[1];

        AutoCompleteTextView produitAutoComplete = dialog.findViewById(R.id.produit);
        ArrayAdapter<String> produitAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, stockList.stream().map(Stock::getProduit).map(Produit::getNom).distinct().collect(Collectors.toList()));
        produitAutoComplete.setAdapter(produitAdapter);
        produitAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                produitName[0] = adapterView.getItemAtPosition(i).toString();
            }
        });

        AutoCompleteTextView magasinAutoComplete = dialog.findViewById(R.id.magasin);
        ArrayAdapter<String> magasinAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, stockList.stream().map(Stock::getMagasin).map(Magasin::getNom).distinct().collect(Collectors.toList()));
        magasinAutoComplete.setAdapter(magasinAdapter);
        magasinAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                magasinName[0] = adapterView.getItemAtPosition(i).toString();
            }
        });

        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (magasinName[0].isEmpty()) {
                    Toast.makeText(requireContext(), "La catégorie est un champ obligatoire.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (produitName[0].isEmpty()) {
                    Toast.makeText(requireContext(), "Le produit est un champ obligatoire.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    quantite[0] = Integer.parseInt(quantiteInput[0].getText().toString().trim());
                    if (quantite[0] < 0) {
                        Toast.makeText(requireContext(), "La quantité ne peut-être négative.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "La quantité doit être un entier.", Toast.LENGTH_SHORT).show();
                }
                Stock stock = new Stock();
                stock.setQuantite(quantite[0]);
                if (isOnline()) {
                    stockViewModel.createNewStock(produitName[0], magasinName[0], quantite[0]);
                    dialog.dismiss();
                } else {
                    Handler dbHandler = new Handler(getContext());
                    NewStockRequest newStockRequest = new NewStockRequest();
                    newStockRequest.setProductName(produitName[0]);
                    newStockRequest.setMagasinName(magasinName[0]);
                    newStockRequest.setQuantite(quantite[0]);
                    Object response = dbHandler.addStock(newStockRequest);
                    dbHandler.close();
                    if (response.getClass() == String.class) {
                        Toast.makeText(requireContext(), (String) response, Toast.LENGTH_SHORT).show();
                    } else if (response.getClass() == Stock.class) {
                        stockList.add((Stock) response);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }

    public void showEditDialog(Stock stock) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.stock_dialog);

        final EditText[] quantiteInput = {dialog.findViewById(R.id.input_quantite)};
        final int[] quantite = {stock.getQuantite()};

        AutoCompleteTextView produitAutoComplete = dialog.findViewById(R.id.produit);
        produitAutoComplete.setText(stock.getProduit().getNom());
        produitAutoComplete.setInputType(0);

        AutoCompleteTextView magasinAutoComplete = dialog.findViewById(R.id.magasin);
        magasinAutoComplete.setText(stock.getMagasin().getNom());
        magasinAutoComplete.setInputType(0);

        quantiteInput[0].setText(String.valueOf(quantite[0]));

        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                try {
                    quantite[0] = Integer.parseInt(quantiteInput[0].getText().toString().trim());
                    if (quantite[0] < 0) {
                        Toast.makeText(requireContext(), "La quantité ne peut-être négative.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "La quantité doit être un entier.", Toast.LENGTH_SHORT).show();
                }
                stock.setQuantite(quantite[0]);

                Log.d("STOCK UPDATED", "ALREADY");
                if (isOnline()) {
                    Log.d("UPDATE ONLINE", "ENTERED");
                    stockViewModel.updateStock(stock);
                    dialog.dismiss();
                } else {
                    Log.d("UPDATE OFFLINE", "ENTERED");
                    Handler dbHandler = new Handler(getContext());
                    dbHandler.updateStock(stock);
                    dbHandler.close();

                    for (int i = 0; i < stockList.size(); i++) {
                        if (stockList.get(i).getId() == stock.getId()) {
                            stockList.set(i, stock);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void showDeleteDialog(Stock stock) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.yes_or_no_dialog);

        TextView deleteText = dialog.findViewById(R.id.delete_message);
        Button noButton = dialog.findViewById(R.id.no_button);
        Button yesButton = dialog.findViewById(R.id.yes_button);

        deleteText.setText("Etes-vous sur de vouloir supprimer ce stock du produit " + stock.getProduit().getNom() + " ?");

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    stockViewModel.deleteStock(new StockID(stock.getMagasin().getId(), stock.getProduit().getId()));
                    dialog.dismiss();
                } else {
                    Handler dbHandler = new Handler(getContext());
                    dbHandler.deleteStock(stock);
                    dbHandler.close();

                    for (int i = 0; i < stockList.size(); i++) {
                        if (stockList.get(i).getId() == stock.getId()) {
                            stockList.remove(i);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void checkStockForNotification(Stock stock) {
        int seuilMinimum = 2; // Seuil minimum de stock

        if (stock.getQuantite() < seuilMinimum) {
            if (stock.getProduit() != null && stock.getMagasin() != null) {
                String produitName = stock.getProduit().getNom();
                String magasinName = stock.getMagasin().getNom();
                int stockActuel = stock.getQuantite();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "channel_id")
                        .setSmallIcon(R.drawable.baseline_notifications)
                        .setContentTitle("Stock Faible")
                        .setContentText("Le stock de " + produitName + " du  magasin " + magasinName + " est faible. Stock actuel : " + stockActuel)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // Affichez la notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                    return;
                }
                notificationManager.notify(stock.hashCode(), builder.build());
            }
        }
    }
}