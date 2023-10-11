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
import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.ui.client.ClientFragment;
import sn.ept.git.mobile.ui.client.ClientViewModel;
import sn.ept.git.mobile.ui.magasin.MagasinFragment;
import sn.ept.git.mobile.ui.magasin.MagasinViewModel;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {
    private List<Client> clientList;
    private ClientFragment clientFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<Client> selectedClients =new ArrayList<>();
    ClientViewModel clientViewModel;

    public ClientAdapter(List<Client> clientList, ClientFragment clientFragment) {
        this.clientList = clientList;
        this.clientFragment = clientFragment;
        clientViewModel = new ClientViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.id.setText(String.valueOf(client.getId()));
        holder.nom.setText(client.getPrenom() + " " + client.getNom());
        holder.telephone.setText(client.getTelephone());
        holder.email.setText(client.getEmail());
        holder.adresse.setText(client.getAdresse());
        holder.ville.setText(client.getVille());
        holder.etat.setText(client.getEtat());
        holder.codeZip.setText(client.getCodeZip());

        holder.expandCollaspseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.telephoneSection.getVisibility() == View.GONE) {
                    //TransitionManager.beginDelayedTransition(, new AutoTransition());
                    holder.expandCollaspseButton.setRotation(90);
                    holder.telephoneSection.setVisibility(View.VISIBLE);
                    holder.emailSection.setVisibility(View.VISIBLE);
                    holder.adresseSection.setVisibility(View.VISIBLE);
                    holder.villeSection.setVisibility(View.VISIBLE);
                    holder.etatSection.setVisibility(View.VISIBLE);
                    holder.codeZipSection.setVisibility(View.VISIBLE);
                } else {
                    holder.expandCollaspseButton.setRotation(0);
                    holder.telephoneSection.setVisibility(View.GONE);
                    holder.emailSection.setVisibility(View.GONE);
                    holder.adresseSection.setVisibility(View.GONE);
                    holder.villeSection.setVisibility(View.GONE);
                    holder.etatSection.setVisibility(View.GONE);
                    holder.codeZipSection.setVisibility(View.GONE);
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
                            clientViewModel.getText().observe(
                                (LifecycleOwner) clientFragment, new Observer<String>() {
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
                                for(Client c:selectedClients)
                                {
                                    // TODO MAKE API CALL TO DELETE PRODUCT
                                    clientList.remove(c);
                                }
                                if(clientList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedClients.size()==clientList.size()) {
                                    isSelectAll = false;
                                    selectedClients.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedClients.clear();
                                    selectedClients.addAll(clientList);
                                }
                                clientViewModel.setText(String.valueOf(selectedClients.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedClients.clear();
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
            holder.storeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.storeIcon.setVisibility(View.VISIBLE);
        }
    }

    private void ClickItem(ViewHolder holder) {
        Client c = clientList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.storeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedClients.add(c);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.storeIcon.setVisibility(View.VISIBLE);
            selectedClients.remove(c);
        }
        clientViewModel.setText(String.valueOf(selectedClients.size()));
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView nom;
        public TextView telephone;
        public TextView email;
        public TextView adresse;
        public TextView ville;
        public TextView etat;
        public TextView codeZip;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageButton expandCollaspseButton;
        public LinearLayout telephoneSection;
        public LinearLayout emailSection;
        public LinearLayout adresseSection;
        public LinearLayout villeSection;
        public LinearLayout etatSection;
        public LinearLayout codeZipSection;
        public ImageView select;
        public ImageView storeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.magasin_id);
            nom = itemView.findViewById(R.id.magasin_text);
            telephone = itemView.findViewById(R.id.phone_text);
            email = itemView.findViewById(R.id.email_text);
            adresse = itemView.findViewById(R.id.adresse_text);
            ville = itemView.findViewById(R.id.ville_text);
            etat = itemView.findViewById(R.id.etat_text);
            codeZip = itemView.findViewById(R.id.zipCode_text);
            editButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
            expandCollaspseButton = itemView.findViewById(R.id.expand_collapse);
            telephoneSection = itemView.findViewById(R.id.phone_section);
            emailSection = itemView.findViewById(R.id.email_section);
            adresseSection = itemView.findViewById(R.id.adresse_section);
            villeSection = itemView.findViewById(R.id.ville_section);
            etatSection = itemView.findViewById(R.id.etat_section);
            codeZipSection = itemView.findViewById(R.id.zipCode_section);
            select = itemView.findViewById(R.id.checkbox_icon);
            storeIcon = itemView.findViewById(R.id.store_icon);
        }
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }
}
