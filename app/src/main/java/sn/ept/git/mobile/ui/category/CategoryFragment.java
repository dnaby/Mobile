package sn.ept.git.mobile.ui.category;

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
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.databinding.FragmentCategorieBinding;
import sn.ept.git.mobile.db.Handler;

public class CategoryFragment extends Fragment {
    private FragmentCategorieBinding binding;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Categorie> categorieList = new ArrayList<>();
    private FloatingActionButton addCategorieButton;
    private CategoryViewModel categoryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        categoryViewModel =
                new ViewModelProvider(this).get(CategoryViewModel.class);

        binding = FragmentCategorieBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.category_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CategoryAdapter(categorieList, this);
        recyclerView.setAdapter(adapter);

        categoryViewModel.getCategories();
        categoryViewModel.getCategorieList().observe(getViewLifecycleOwner(), categories -> {
            Handler dbHandler = new Handler(requireContext());
            dbHandler.initCategorie(categories);
            dbHandler.close();

            categorieList.clear();
            categorieList.addAll(categories);
            adapter.notifyDataSetChanged();
        });

        categoryViewModel.getGetCategoriesError().observe(getViewLifecycleOwner(), getCategoriesError -> {
            if (getCategoriesError) {
                Toast.makeText(requireContext(), "Error retrieving categories", Toast.LENGTH_LONG).show();
            }
        });

        categoryViewModel.getPostCategorieError().observe(getViewLifecycleOwner(), postCategorieError -> {
            if (postCategorieError) {
                Toast.makeText(requireContext(), categoryViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        categoryViewModel.getUpdateCategorieError().observe(getViewLifecycleOwner(), updateCategorieError -> {
            if (updateCategorieError) {
                Toast.makeText(requireContext(), categoryViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        categoryViewModel.getDeleteCategorieError().observe(getViewLifecycleOwner(), deleteCategorieError -> {
            if (deleteCategorieError) {
                Toast.makeText(requireContext(), categoryViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addCategorieButton = ((MainActivity) requireActivity()).getFab();

        if (addCategorieButton != null) {
            addCategorieButton.setOnClickListener(new View.OnClickListener() {
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

        EditText categoryNameEditText = dialog.findViewById(R.id.input_category_name);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = categoryNameEditText.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    categoryViewModel.createNewCategorie(categoryName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    public void showEditDialog(Categorie categorie) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.categorie_dialog);

        EditText categoryNameEditText = dialog.findViewById(R.id.input_category_name);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        // Set the existing category name in the EditText
        categoryNameEditText.setText(categorie.getNom());

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
                String updatedCategoryName = categoryNameEditText.getText().toString().trim();
                if (!updatedCategoryName.isEmpty()) {
                    categoryViewModel.updateCategorie(categorie.getId(), updatedCategoryName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void showDeleteDialog(Categorie categorie) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.yes_or_no_dialog);

        TextView categoryDeleteText = dialog.findViewById(R.id.delete_message);
        Button noButton = dialog.findViewById(R.id.no_button);
        Button yesButton = dialog.findViewById(R.id.yes_button);

        categoryDeleteText.setText("Etes-vous sur de vouloir supprimer " + categorie.getNom() + " ?");

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
                categoryViewModel.deleteCategorie(categorie.getId());
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
                List<Categorie> filteredList = filterList(categorieList, s);
                adapter.setCategorieList(filteredList);
                return true;
            }
        });
    }

    private List<Categorie> filterList(List<Categorie> originalList, String query) {
        List<Categorie> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (Categorie categorie : originalList) {
                if (categorie.getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(categorie);
                }
            }
        }

        return filteredList;
    }
}