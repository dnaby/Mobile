package sn.ept.git.mobile.adapter;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sn.ept.git.mobile.R;
import sn.ept.git.mobile.api.models.article_commande.ArticleCommande;
import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.ui.article_commande.ArticleCommandeFragment;
import sn.ept.git.mobile.ui.article_commande.ArticleCommandeViewModel;
import sn.ept.git.mobile.ui.commande.CommandeFragment;
import sn.ept.git.mobile.ui.commande.CommandeViewModel;

public class ArticleCommandeAdapter extends RecyclerView.Adapter<ArticleCommandeAdapter.ViewHolder> {
    private List<ArticleCommande> articleCommandeList;
    private ArticleCommandeFragment articleCommandeFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<ArticleCommande> selectedArticleCommandes =new ArrayList<>();
    ArticleCommandeViewModel articleCommandeViewModel;

    public ArticleCommandeAdapter(List<ArticleCommande> articleCommandeList, ArticleCommandeFragment articleCommandeFragment) {
        this.articleCommandeList = articleCommandeList;
        this.articleCommandeFragment = articleCommandeFragment;
        articleCommandeViewModel = new ArticleCommandeViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_commande_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleCommande articleCommande = articleCommandeList.get(position);
        holder.quantity.setText(String.valueOf(articleCommande.getQuantite()));
        holder.produit.setText(articleCommande.getProduit().getNom());
        holder.prixDepart.setText(String.valueOf(articleCommande.getPrixDepart()));
        holder.remise.setText(String.valueOf( (int) (articleCommande.getRemise() * 100)));

        holder.expandCollaspseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.prixDepartSection.getVisibility() == View.GONE) {
                    //TransitionManager.beginDelayedTransition(, new AutoTransition());
                    holder.expandCollaspseButton.setRotation(90);
                    holder.prixDepartSection.setVisibility(View.VISIBLE);
                    holder.remiseSection.setVisibility(View.VISIBLE);
                } else {
                    holder.expandCollaspseButton.setRotation(0);
                    holder.prixDepartSection.setVisibility(View.GONE);
                    holder.remiseSection.setVisibility(View.GONE);
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
                            articleCommandeViewModel.getText().observe(
                                (LifecycleOwner) articleCommandeFragment, new Observer<String>() {
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
                                for(ArticleCommande a:selectedArticleCommandes)
                                {
                                    // TODO MAKE API CALL TO DELETE PRODUCT
                                    articleCommandeList.remove(a);
                                }
                                if(articleCommandeList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedArticleCommandes.size()==articleCommandeList.size()) {
                                    isSelectAll = false;
                                    selectedArticleCommandes.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedArticleCommandes.clear();
                                    selectedArticleCommandes.addAll(articleCommandeList);
                                }
                                articleCommandeViewModel.setText(String.valueOf(selectedArticleCommandes.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedArticleCommandes.clear();
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
            holder.articleCommandeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.articleCommandeIcon.setVisibility(View.VISIBLE);
        }
    }

    private void ClickItem(ViewHolder holder) {
        ArticleCommande a = articleCommandeList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.articleCommandeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedArticleCommandes.add(a);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.articleCommandeIcon.setVisibility(View.VISIBLE);
            selectedArticleCommandes.remove(a);
        }
        articleCommandeViewModel.setText(String.valueOf(selectedArticleCommandes.size()));
    }

    @Override
    public int getItemCount() {
        return articleCommandeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quantity;
        public TextView produit;
        public TextView prixDepart;
        public TextView remise;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageButton expandCollaspseButton;
        public LinearLayout prixDepartSection;
        public LinearLayout remiseSection;
        public ImageView select;
        public ImageView articleCommandeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.quantity);
            produit = itemView.findViewById(R.id.produit_text);
            prixDepart = itemView.findViewById(R.id.prix_depart_text);
            remise = itemView.findViewById(R.id.remise_text);
            editButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
            expandCollaspseButton = itemView.findViewById(R.id.expand_collapse);
            prixDepartSection = itemView.findViewById(R.id.prix_depart_section);
            remiseSection = itemView.findViewById(R.id.remise_section);
            select = itemView.findViewById(R.id.checkbox_icon);
            articleCommandeIcon = itemView.findViewById(R.id.article_commande_icon);
        }
    }

    public void setArticleCommandeList(List<ArticleCommande> articleCommandeList) {
        this.articleCommandeList = articleCommandeList;
    }
}
