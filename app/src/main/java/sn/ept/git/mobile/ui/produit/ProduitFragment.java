package sn.ept.git.mobile.ui.produit;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sn.ept.git.mobile.MainActivity;
import sn.ept.git.mobile.R;
import sn.ept.git.mobile.adapter.MarqueAdapter;
import sn.ept.git.mobile.adapter.ProduitAdapter;
import sn.ept.git.mobile.api.models.marque.Marque;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.databinding.FragmentMarqueBinding;
import sn.ept.git.mobile.databinding.FragmentProduitBinding;
import sn.ept.git.mobile.db.Handler;
import sn.ept.git.mobile.ui.marque.MarqueViewModel;

public class ProduitFragment extends Fragment {
    private FragmentProduitBinding binding;
    private RecyclerView recyclerView;
    private ProduitAdapter adapter;
    private List<Produit> produitList = new ArrayList<>();
    private FloatingActionButton addProduitButton;
    private ProduitViewModel produitViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        produitViewModel =
                new ViewModelProvider(this).get(ProduitViewModel.class);

        binding = FragmentProduitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.produit_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProduitAdapter(produitList, this);
        recyclerView.setAdapter(adapter);

        produitViewModel.getProducts();
        produitViewModel.getProduitList().observe(getViewLifecycleOwner(), produits -> {
            Handler dbHandler = new Handler(requireContext());
            dbHandler.initProduit(produits);
            dbHandler.close();

            produitList.clear();
            produitList.addAll(produits);
            adapter.notifyDataSetChanged();
        });

        produitViewModel.getGetProduitsError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving products", Toast.LENGTH_LONG).show();
            }
        });

        produitViewModel.getPostProduitError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), produitViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        produitViewModel.getUpdateProduitError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), produitViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        produitViewModel.getDeleteProduitError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), produitViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addProduitButton = ((MainActivity) requireActivity()).getFab();

        if (addProduitButton != null) {
            addProduitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //showDialog();
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
        searchView.setQueryHint("Type here to search" );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Produit> filteredList = filterList(produitList, s);
                adapter.setProduitList(filteredList);
                return true;
            }
        });
    }

    private List<Produit> filterList(List<Produit> originalList, String query) {
        List<Produit> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Produit produit : originalList) {
                if (produit.getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(produit);
                }
            }
        }

        return filteredList;
    }
}