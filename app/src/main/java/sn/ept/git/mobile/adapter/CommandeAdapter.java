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
import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.commande.Commande;
import sn.ept.git.mobile.ui.client.ClientFragment;
import sn.ept.git.mobile.ui.client.ClientViewModel;
import sn.ept.git.mobile.ui.commande.CommandeFragment;
import sn.ept.git.mobile.ui.commande.CommandeViewModel;

public class CommandeAdapter extends RecyclerView.Adapter<CommandeAdapter.ViewHolder> {
    private List<Commande> commandeList;
    private CommandeFragment commandeFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<Commande> selectedCommandes =new ArrayList<>();
    CommandeViewModel commandeViewModel;

    public CommandeAdapter(List<Commande> commandeList, CommandeFragment commandeFragment) {
        this.commandeList = commandeList;
        this.commandeFragment = commandeFragment;
        commandeViewModel = new CommandeViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commande_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Commande commande = commandeList.get(position);
        holder.id.setText(String.valueOf(commande.getId()));
        holder.client.setText(commande.getClient().getPrenom() + " " + commande.getClient().getNom());
        holder.status.setText(String.valueOf(commande.getStatut()));
        holder.dateCommande.setText(commande.getDateCommande());
        holder.livraisonVoulue.setText(commande.getDateLivraisonVoulue());
        holder.livraison.setText(commande.getDateLivraison());
        holder.magasin.setText(commande.getMagasin().getNom());
        holder.vendeur.setText(commande.getVendeur().getPrenom() + " " + commande.getVendeur().getNom());

        holder.expandCollaspseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.clientSection.getVisibility() == View.GONE) {
                    //TransitionManager.beginDelayedTransition(, new AutoTransition());
                    holder.expandCollaspseButton.setRotation(90);
                    holder.clientSection.setVisibility(View.VISIBLE);
                    holder.statusSection.setVisibility(View.VISIBLE);
                    holder.dateCommandeSection.setVisibility(View.VISIBLE);
                    holder.livraisonVoulueSection.setVisibility(View.VISIBLE);
                    holder.livraisonSection.setVisibility(View.VISIBLE);
                    holder.magasinSection.setVisibility(View.VISIBLE);
                    holder.vendeurSection.setVisibility(View.VISIBLE);
                } else {
                    holder.expandCollaspseButton.setRotation(0);
                    holder.clientSection.setVisibility(View.GONE);
                    holder.statusSection.setVisibility(View.GONE);
                    holder.dateCommandeSection.setVisibility(View.GONE);
                    holder.livraisonVoulueSection.setVisibility(View.GONE);
                    holder.livraisonSection.setVisibility(View.GONE);
                    holder.magasinSection.setVisibility(View.GONE);
                    holder.vendeurSection.setVisibility(View.GONE);
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
                            commandeViewModel.getText().observe(
                                (LifecycleOwner) commandeFragment, new Observer<String>() {
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
                                for(Commande c:selectedCommandes)
                                {
                                    // TODO MAKE API CALL TO DELETE PRODUCT
                                    commandeList.remove(c);
                                }
                                if(commandeList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedCommandes.size()==commandeList.size()) {
                                    isSelectAll = false;
                                    selectedCommandes.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedCommandes.clear();
                                    selectedCommandes.addAll(commandeList);
                                }
                                commandeViewModel.setText(String.valueOf(selectedCommandes.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedCommandes.clear();
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
            holder.commandeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.commandeIcon.setVisibility(View.VISIBLE);
        }
    }

    private void ClickItem(ViewHolder holder) {
        Commande c = commandeList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.commandeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedCommandes.add(c);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.commandeIcon.setVisibility(View.VISIBLE);
            selectedCommandes.remove(c);
        }
        commandeViewModel.setText(String.valueOf(selectedCommandes.size()));
    }

    @Override
    public int getItemCount() {
        return commandeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView client;
        public TextView status;
        public TextView dateCommande;
        public TextView livraisonVoulue;
        public TextView livraison;
        public TextView magasin;
        public TextView vendeur;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageButton expandCollaspseButton;
        public LinearLayout clientSection;
        public LinearLayout statusSection;
        public LinearLayout dateCommandeSection;
        public LinearLayout livraisonVoulueSection;
        public LinearLayout livraisonSection;
        public LinearLayout magasinSection;
        public LinearLayout vendeurSection;
        public ImageView select;
        public ImageView commandeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.commande_id);
            client = itemView.findViewById(R.id.client_text);
            status = itemView.findViewById(R.id.status_text);
            dateCommande = itemView.findViewById(R.id.date_commande_text);
            livraisonVoulue = itemView.findViewById(R.id.livraison_voulue_text);
            livraison = itemView.findViewById(R.id.livraison_text);
            magasin = itemView.findViewById(R.id.magasin_text);
            vendeur= itemView.findViewById(R.id.vendeur_text);
            editButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
            expandCollaspseButton = itemView.findViewById(R.id.expand_collapse);
            clientSection = itemView.findViewById(R.id.client_section);
            statusSection = itemView.findViewById(R.id.status_section);
            dateCommandeSection = itemView.findViewById(R.id.date_commande_section);
            livraisonVoulueSection = itemView.findViewById(R.id.livraison_voulue_section);
            livraisonSection = itemView.findViewById(R.id.livraison_section);
            magasinSection = itemView.findViewById(R.id.magasin_section);
            vendeurSection = itemView.findViewById(R.id.vendeur_section);
            select = itemView.findViewById(R.id.checkbox_icon);
            commandeIcon = itemView.findViewById(R.id.commande_icon);
        }
    }

    public void setCommandeList(List<Commande> commandeList) {
        this.commandeList = commandeList;
    }
}
