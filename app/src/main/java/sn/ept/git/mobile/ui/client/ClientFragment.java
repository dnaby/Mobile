package sn.ept.git.mobile.ui.client;

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
import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.databinding.FragmentClientBinding;

public class ClientFragment extends Fragment {
    private FragmentClientBinding binding;
    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private List<Client> clientList = new ArrayList<>();
    private FloatingActionButton addClientButton;
    private ClientViewModel clientViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        clientViewModel =
                new ViewModelProvider(this).get(ClientViewModel.class);

        binding = FragmentClientBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.client_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ClientAdapter(clientList, this);
        recyclerView.setAdapter(adapter);

        clientViewModel.getClients();
        clientViewModel.getClientList().observe(getViewLifecycleOwner(), clients -> {
            clientList.clear();
            clientList.addAll(clients);
            adapter.notifyDataSetChanged();
        });

        clientViewModel.getGetClientsError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving clients", Toast.LENGTH_LONG).show();
            }
        });

        clientViewModel.getPostClientError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), clientViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        clientViewModel.getUpdateClientError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), clientViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        clientViewModel.getDeleteClientError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), clientViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addClientButton = ((MainActivity) requireActivity()).getFab();

        if (addClientButton != null) {
            addClientButton.setOnClickListener(new View.OnClickListener() {
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
                List<Client> filteredList = filterList(clientList, s);
                adapter.setClientList(filteredList);
                return true;
            }
        });
    }

    private List<Client> filterList(List<Client> originalList, String query) {
        List<Client> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Client client : originalList) {
                if (client.getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(client);
                } else if ((client.getPrenom().toLowerCase().contains(lowercaseQuery))) {
                    filteredList.add(client);
                }
            }
        }

        return filteredList;
    }
}