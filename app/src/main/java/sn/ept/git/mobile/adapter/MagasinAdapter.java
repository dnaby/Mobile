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
import sn.ept.git.mobile.api.models.magasin.Magasin;
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.ui.magasin.MagasinFragment;
import sn.ept.git.mobile.ui.magasin.MagasinViewModel;
import sn.ept.git.mobile.ui.produit.ProduitFragment;
import sn.ept.git.mobile.ui.produit.ProduitViewModel;

public class MagasinAdapter extends RecyclerView.Adapter<MagasinAdapter.ViewHolder> {
    private List<Magasin> magasinList;
    private MagasinFragment magasinFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<Magasin> selectedMagasins =new ArrayList<>();
    MagasinViewModel magasinViewModel;

    public MagasinAdapter(List<Magasin> magasinList, MagasinFragment magasinFragment) {
        this.magasinList = magasinList;
        this.magasinFragment = magasinFragment;
        magasinViewModel = new MagasinViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.magasin_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Magasin magasin = magasinList.get(position);
        holder.id.setText(String.valueOf(magasin.getId()));
        holder.nom.setText(magasin.getNom());
        holder.telephone.setText(magasin.getTelephone());
        holder.email.setText(magasin.getEmail());
        holder.adresse.setText(magasin.getAdresse());
        holder.ville.setText(magasin.getVille());
        holder.etat.setText(magasin.getEtat());
        holder.codeZip.setText(magasin.getCodeZip());

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
                            magasinViewModel.getText().observe(
                                (LifecycleOwner) magasinFragment, new Observer<String>() {
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
                                for(Magasin m:selectedMagasins)
                                {
                                    // TODO MAKE API CALL TO DELETE PRODUCT
                                    magasinList.remove(m);
                                }
                                if(magasinList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedMagasins.size()==magasinList.size()) {
                                    isSelectAll = false;
                                    selectedMagasins.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedMagasins.clear();
                                    selectedMagasins.addAll(magasinList);
                                }
                                magasinViewModel.setText(String.valueOf(selectedMagasins.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedMagasins.clear();
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
        Magasin m = magasinList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.storeIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedMagasins.add(m);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.storeIcon.setVisibility(View.VISIBLE);
            selectedMagasins.remove(m);
        }
        magasinViewModel.setText(String.valueOf(selectedMagasins.size()));
    }

    @Override
    public int getItemCount() {
        return magasinList.size();
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

    public void setMagasinList(List<Magasin> magasinList) {
        this.magasinList = magasinList;
    }
}
