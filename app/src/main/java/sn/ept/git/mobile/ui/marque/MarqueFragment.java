package sn.ept.git.mobile.ui.marque;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sn.ept.git.mobile.MainActivity;
import sn.ept.git.mobile.R;
import sn.ept.git.mobile.adapter.CategoryAdapter;
import sn.ept.git.mobile.adapter.MarqueAdapter;
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.marque.Marque;
import sn.ept.git.mobile.databinding.FragmentMarqueBinding;
import sn.ept.git.mobile.db.Handler;
import sn.ept.git.mobile.ui.category.CategoryViewModel;

public class MarqueFragment extends Fragment {

    private FragmentMarqueBinding binding;
    private RecyclerView recyclerView;
    private MarqueAdapter adapter;
    private List<Marque> marqueList = new ArrayList<>();
    private FloatingActionButton addMarqueButton;
    private MarqueViewModel marqueViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        marqueViewModel =
                new ViewModelProvider(this).get(MarqueViewModel.class);

        binding = FragmentMarqueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.category_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MarqueAdapter(marqueList, this);
        recyclerView.setAdapter(adapter);

        marqueViewModel.getMarques();
        marqueViewModel.getMarqueList().observe(getViewLifecycleOwner(), marques -> {
            Handler dbHandler = new Handler(requireContext());
            dbHandler.initMarque(marques);
            dbHandler.close();

            marqueList.clear();
            marqueList.addAll(marques);
            adapter.notifyDataSetChanged();
        });

        marqueViewModel.getGetMarquesError().observe(getViewLifecycleOwner(), getMarquesError -> {
            if (getMarquesError) {
                Toast.makeText(requireContext(), "Error retrieving marques", Toast.LENGTH_LONG).show();
            }
        });

        marqueViewModel.getPostMarqueError().observe(getViewLifecycleOwner(), postMarqueError -> {
            if (postMarqueError) {
                Toast.makeText(requireContext(), marqueViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        marqueViewModel.getUpdateMarqueError().observe(getViewLifecycleOwner(), updateMarqueError -> {
            if (updateMarqueError) {
                Toast.makeText(requireContext(), marqueViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        marqueViewModel.getDeleteMarqueError().observe(getViewLifecycleOwner(), deleteMarqueError -> {
            if (deleteMarqueError) {
                Toast.makeText(requireContext(), marqueViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addMarqueButton = ((MainActivity) requireActivity()).getFab();

        if (addMarqueButton != null) {
            addMarqueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        }

        return root;
    }

    public void showDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.categorie_dialog);

        EditText marqueNameEditText = dialog.findViewById(R.id.input_category_name);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        marqueNameEditText.setHint("Nom de la nouvelle marque");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the cancel button is clicked
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle saving the category here
                String marqueName = marqueNameEditText.getText().toString().trim();
                if (!marqueName.isEmpty()) {
                    marqueViewModel.createNewMarque(marqueName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Marque name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    public void showEditDialog(Marque marque) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.categorie_dialog);

        EditText marqueNameEditText = dialog.findViewById(R.id.input_category_name);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        // Set the existing category name in the EditText
        marqueNameEditText.setHint("Nom de la nouvelle marque");
        marqueNameEditText.setText(marque.getNom());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the cancel button is clicked
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle saving the edited category here
                String updatedCategoryName = marqueNameEditText.getText().toString().trim();
                if (!updatedCategoryName.isEmpty()) {
                    marqueViewModel.updateMarque(marque.getId(), updatedCategoryName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Marque name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void showDeleteDialog(Marque marque) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.yes_or_no_dialog);

        TextView marqueDeleteText = dialog.findViewById(R.id.delete_message);
        Button noButton = dialog.findViewById(R.id.no_button);
        Button yesButton = dialog.findViewById(R.id.yes_button);

        marqueDeleteText.setText("Etes-vous sur de vouloir supprimer " + marque.getNom() + " ?");

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the cancel button is clicked
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marqueViewModel.deleteMarque(marque.getId());
                dialog.dismiss();
            }
        });

        dialog.show();
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
                List<Marque> filteredList = filterList(marqueList, s);
                adapter.setMarqueList(filteredList);
                return true;
            }
        });
    }

    private List<Marque> filterList(List<Marque> originalList, String query) {
        List<Marque> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Marque marque : originalList) {
                if (marque.getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(marque);
                }
            }
        }

        return filteredList;
    }
}