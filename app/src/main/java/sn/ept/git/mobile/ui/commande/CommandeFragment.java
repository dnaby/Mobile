package sn.ept.git.mobile.ui.commande;

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

import sn.ept.git.mobile.MainActivity;
import sn.ept.git.mobile.R;
import sn.ept.git.mobile.adapter.ClientAdapter;
import sn.ept.git.mobile.adapter.CommandeAdapter;
import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.databinding.FragmentClientBinding;
import sn.ept.git.mobile.databinding.FragmentCommandeBinding;
import sn.ept.git.mobile.ui.client.ClientViewModel;

public class CommandeFragment extends Fragment {
    private FragmentCommandeBinding binding;
    private RecyclerView recyclerView;
    private CommandeAdapter adapter;
    private List<Commande> commandeList = new ArrayList<>();
    private FloatingActionButton addCommandeButton;
    private CommandeViewModel commandeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        commandeViewModel =
                new ViewModelProvider(this).get(CommandeViewModel.class);

        binding = FragmentCommandeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.commande_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CommandeAdapter(commandeList, this);
        recyclerView.setAdapter(adapter);

        commandeViewModel.getCommandes();
        commandeViewModel.getCommandeList().observe(getViewLifecycleOwner(), commandes -> {
            commandeList.clear();
            commandeList.addAll(commandes);
            adapter.notifyDataSetChanged();
        });

        commandeViewModel.getGetCommandesError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving commandes", Toast.LENGTH_LONG).show();
            }
        });

        commandeViewModel.getPostCommandeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), commandeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        commandeViewModel.getUpdateCommandeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), commandeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        commandeViewModel.getDeleteCommandeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), commandeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addCommandeButton = ((MainActivity) requireActivity()).getFab();

        if (addCommandeButton != null) {
            addCommandeButton.setOnClickListener(new View.OnClickListener() {
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
                List<Commande> filteredList = filterList(commandeList, s);
                adapter.setCommandeList(filteredList);
                return true;
            }
        });
    }

    private List<Commande> filterList(List<Commande> originalList, String query) {
        List<Commande> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Commande commande : originalList) {
                if (commande.getClient().getNom().toLowerCase().contains(lowercaseQuery) ||
                    commande.getClient().getPrenom().toLowerCase().contains(lowercaseQuery) ||
                    commande.getMagasin().getNom().toLowerCase().contains(lowercaseQuery) ||
                    commande.getVendeur().getNom().toLowerCase().contains(lowercaseQuery) ||
                    commande.getVendeur().getPrenom().toLowerCase().contains(lowercaseQuery)
                ) {
                    filteredList.add(commande);
                }
            }
        }

        return filteredList;
    }
}