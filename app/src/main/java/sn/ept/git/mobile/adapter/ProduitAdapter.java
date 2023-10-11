package sn.ept.git.mobile.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sn.ept.git.mobile.R;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.ui.produit.ProduitFragment;
import sn.ept.git.mobile.ui.produit.ProduitViewModel;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {
    private List<Produit> produitList;
    private ProduitFragment produitFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<Produit> selectedProduits =new ArrayList<>();
    ProduitViewModel produitViewModel;

    public ProduitAdapter(List<Produit> produitList, ProduitFragment produitFragment) {
        this.produitList = produitList;
        this.produitFragment = produitFragment;
        produitViewModel = new ProduitViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.produit_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produit produit = produitList.get(position);
        holder.produitId.setText(String.valueOf(produit.getId()));
        holder.produitName.setText(produit.getNom());
        holder.marqueName.setText(produit.getMarque().getNom());
        holder.categoryName.setText(produit.getCategorie().getNom());
        holder.yearModel.setText(String.valueOf(produit.getAnneeModel()));
        holder.price.setText(String.valueOf(produit.getPrixDepart()));

        holder.expandCollaspseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.marqueSection.getVisibility() == View.GONE) {
                    //TransitionManager.beginDelayedTransition(, new AutoTransition());
                    holder.expandCollaspseButton.setRotation(90);
                    holder.marqueSection.setVisibility(View.VISIBLE);
                    holder.categorySection.setVisibility(View.VISIBLE);
                    holder.yearSection.setVisibility(View.VISIBLE);
                    holder.priceSection.setVisibility(View.VISIBLE);
                } else {
                    holder.expandCollaspseButton.setRotation(0);
                    holder.marqueSection.setVisibility(View.GONE);
                    holder.categorySection.setVisibility(View.GONE);
                    holder.yearSection.setVisibility(View.GONE);
                    holder.priceSection.setVisibility(View.GONE);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEnable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater= actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.multi_select, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isEnable=true;
                            ClickItem(holder);
                            produitViewModel.getText().observe(
                                (LifecycleOwner) produitFragment, new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        actionMode.setTitle(String.format("%s Selected",s));
                                    }
                                });
                            return true;
                        }

                        @SuppressLint("NonConstantResourceId")
                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            if (id == R.id.menu_delete) {
                                for(Produit p:selectedProduits)
                                {
                                    // TODO MAKE API CALL TO DELETE PRODUCT
                                    produitList.remove(p);
                                }
                                if(produitList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedProduits.size()==produitList.size()) {
                                    isSelectAll = false;
                                    selectedProduits.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedProduits.clear();
                                    selectedProduits.addAll(produitList);
                                }
                                produitViewModel.setText(String.valueOf(selectedProduits.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedProduits.clear();
                            notifyDataSetChanged();
                        }
                    };
                    ((FragmentActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnable) {
                    ClickItem(holder);
                }
            }
        });

        if (isSelectAll) {
            holder.bikeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.bikeIcon.setVisibility(View.VISIBLE);
        }
    }

    private void ClickItem(ViewHolder holder) {
        Produit p = produitList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.bikeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedProduits.add(p);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.bikeIcon.setVisibility(View.VISIBLE);
            selectedProduits.remove(p);
        }
        produitViewModel.setText(String.valueOf(selectedProduits.size()));
    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView produitId;
        public TextView produitName;
        public TextView marqueName;
        public TextView categoryName;
        public TextView yearModel;
        public TextView price;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageButton expandCollaspseButton;
        public LinearLayout marqueSection;
        public LinearLayout categorySection;
        public LinearLayout yearSection;
        public LinearLayout priceSection;
        public ImageView select;
        public ImageView bikeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            produitId = itemView.findViewById(R.id.produit_id);
            produitName = itemView.findViewById(R.id.produit_text);
            marqueName = itemView.findViewById(R.id.marque_text);
            categoryName = itemView.findViewById(R.id.category_text);
            yearModel = itemView.findViewById(R.id.annee_model_text);
            price = itemView.findViewById(R.id.prix_depart_text);
            editButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
            expandCollaspseButton = itemView.findViewById(R.id.expand_collapse);
            marqueSection = itemView.findViewById(R.id.marque_section);
            categorySection = itemView.findViewById(R.id.category_section);
            yearSection = itemView.findViewById(R.id.year_section);
            priceSection = itemView.findViewById(R.id.price_section);
            select = itemView.findViewById(R.id.checkbox_icon);
            bikeIcon = itemView.findViewById(R.id.bike_icon);
        }
    }

    public void setProduitList(List<Produit> produitList) {
        this.produitList = produitList;
    }
}
