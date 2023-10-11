package sn.ept.git.mobile.ui.employe;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import sn.ept.git.mobile.adapter.EmployeAdapter;
import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.employe.Employe;
import sn.ept.git.mobile.databinding.FragmentClientBinding;
import sn.ept.git.mobile.databinding.FragmentEmployeBinding;
import sn.ept.git.mobile.ui.client.ClientViewModel;

public class EmployeFragment extends Fragment {
    private FragmentEmployeBinding binding;
    private RecyclerView recyclerView;
    private EmployeAdapter adapter;
    private List<Employe> employeList = new ArrayList<>();
    private FloatingActionButton addEmployeButton;
    private EmployeViewModel employeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        employeViewModel =
                new ViewModelProvider(this).get(EmployeViewModel.class);

        binding = FragmentEmployeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.employe_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new EmployeAdapter(employeList, this);
        recyclerView.setAdapter(adapter);

        employeViewModel.getEmployes();
        employeViewModel.getEmployeList().observe(getViewLifecycleOwner(), employes -> {
            employeList.clear();
            employeList.addAll(employes);
            adapter.notifyDataSetChanged();
        });

        employeViewModel.getGetEmployesError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving employes", Toast.LENGTH_LONG).show();
            }
        });

        employeViewModel.getPostEmployeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), employeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        employeViewModel.getUpdateEmployeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), employeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        employeViewModel.getDeleteEmployeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), employeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addEmployeButton = ((MainActivity) requireActivity()).getFab();

        if (addEmployeButton != null) {
            addEmployeButton.setOnClickListener(new View.OnClickListener() {
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
                List<Employe> filteredList = filterList(employeList, s);
                adapter.setEmployeList(filteredList);
                return true;
            }
        });
    }

    private List<Employe> filterList(List<Employe> originalList, String query) {
        List<Employe> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Employe employe : originalList) {
                if (employe.getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(employe);
                } else if ((employe.getPrenom().toLowerCase().contains(lowercaseQuery))) {
                    filteredList.add(employe);
                }
            }
        }

        return filteredList;
    }
}