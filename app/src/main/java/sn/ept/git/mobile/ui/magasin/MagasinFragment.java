package sn.ept.git.mobile.ui.magasin;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sn.ept.git.mobile.MainActivity;
import sn.ept.git.mobile.R;
import sn.ept.git.mobile.adapter.MagasinAdapter;
import sn.ept.git.mobile.adapter.ProduitAdapter;
import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.databinding.FragmentMagasinBinding;
import sn.ept.git.mobile.databinding.FragmentProduitBinding;
import sn.ept.git.mobile.db.Handler;
import sn.ept.git.mobile.ui.produit.ProduitViewModel;

public class MagasinFragment extends Fragment {
    private FragmentMagasinBinding binding;
    private RecyclerView recyclerView;
    private MagasinAdapter adapter;
    private List<Magasin> magasinList = new ArrayList<>();
    private FloatingActionButton addMagasinButton;
    private MagasinViewModel magasinViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        magasinViewModel =
                new ViewModelProvider(this).get(MagasinViewModel.class);

        binding = FragmentMagasinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.magasin_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MagasinAdapter(magasinList, this);
        recyclerView.setAdapter(adapter);

        magasinViewModel.getMagasins();
        magasinViewModel.getMagasinList().observe(getViewLifecycleOwner(), magasins -> {
            Handler dbHandler = new Handler(requireContext());
            dbHandler.initMagasin(magasins);
            dbHandler.close();

            magasinList.clear();
            magasinList.addAll(magasins);
            adapter.notifyDataSetChanged();
        });

        magasinViewModel.getGetMagasinsError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving magasins", Toast.LENGTH_LONG).show();
            }
        });

        magasinViewModel.getPostMagasinError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), magasinViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        magasinViewModel.getUpdateMagasinError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), magasinViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        magasinViewModel.getDeleteMagasinError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), magasinViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addMagasinButton = ((MainActivity) requireActivity()).getFab();

        if (addMagasinButton != null) {
            addMagasinButton.setOnClickListener(new View.OnClickListener() {
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
                List<Magasin> filteredList = filterList(magasinList, s);
                adapter.setMagasinList(filteredList);
                return true;
            }
        });
    }

    private List<Magasin> filterList(List<Magasin> originalList, String query) {
        List<Magasin> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Magasin magasin : originalList) {
                if (magasin.getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(magasin);
                }
            }
        }

        return filteredList;
    }
}