package sn.ept.git.mobile.ui.article_commande;

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
import sn.ept.git.mobile.adapter.ArticleCommandeAdapter;
import sn.ept.git.mobile.adapter.CommandeAdapter;
import sn.ept.git.mobile.api.models.article_commande.ArticleCommande;
import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.databinding.FragmentArticleCommandeBinding;
import sn.ept.git.mobile.databinding.FragmentCommandeBinding;
import sn.ept.git.mobile.ui.commande.CommandeViewModel;

public class ArticleCommandeFragment extends Fragment {
    private FragmentArticleCommandeBinding binding;
    private RecyclerView recyclerView;
    private ArticleCommandeAdapter adapter;
    private List<ArticleCommande> articleCommandeList = new ArrayList<>();
    private FloatingActionButton addArticleCommandeButton;
    private ArticleCommandeViewModel articleCommandeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        articleCommandeViewModel =
                new ViewModelProvider(this).get(ArticleCommandeViewModel.class);

        binding = FragmentArticleCommandeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.article_commande_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ArticleCommandeAdapter(articleCommandeList, this);
        recyclerView.setAdapter(adapter);

        articleCommandeViewModel.getArticleCommandes();
        articleCommandeViewModel.getArticleCommandeList().observe(getViewLifecycleOwner(), articleCommandes -> {
            articleCommandeList.clear();
            articleCommandeList.addAll(articleCommandes);
            adapter.notifyDataSetChanged();
        });

        articleCommandeViewModel.getGetArticleCommandesError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), "Error retrieving articles commandes", Toast.LENGTH_LONG).show();
            }
        });

        articleCommandeViewModel.getPostArticleCommandeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), articleCommandeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        articleCommandeViewModel.getUpdateArticleCommandeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), articleCommandeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        articleCommandeViewModel.getDeleteArticleCommandeError().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                Toast.makeText(requireContext(), articleCommandeViewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

        addArticleCommandeButton = ((MainActivity) requireActivity()).getFab();

        if (addArticleCommandeButton != null) {
            addArticleCommandeButton.setOnClickListener(new View.OnClickListener() {
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
                List<ArticleCommande> filteredList = filterList(articleCommandeList, s);
                adapter.setArticleCommandeList(filteredList);
                return true;
            }
        });
    }

    private List<ArticleCommande> filterList(List<ArticleCommande> originalList, String query) {
        List<ArticleCommande> filteredList = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            // If the query is empty, return the original list
            filteredList.addAll(originalList);
        } else {
            // Convert the query to lowercase for case-insensitive search
            String lowercaseQuery = query.toLowerCase();

            // Iterate through the original list and add items that contain the query
            for (ArticleCommande articleCommande : originalList) {
                if (articleCommande.getProduit().getNom().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(articleCommande);
                }
            }
        }

        return filteredList;
    }
}