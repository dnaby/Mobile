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
import sn.ept.git.mobile.api.models.employe.Employe;
import sn.ept.git.mobile.ui.client.ClientFragment;
import sn.ept.git.mobile.ui.client.ClientViewModel;
import sn.ept.git.mobile.ui.employe.EmployeFragment;
import sn.ept.git.mobile.ui.employe.EmployeViewModel;

public class EmployeAdapter extends RecyclerView.Adapter<EmployeAdapter.ViewHolder> {
    private List<Employe> employeList;
    private EmployeFragment employeFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<Employe> selectedEmployes =new ArrayList<>();
    EmployeViewModel employeViewModel;

    public EmployeAdapter(List<Employe> employeList, EmployeFragment employeFragment) {
        this.employeList = employeList;
        this.employeFragment = employeFragment;
        employeViewModel = new EmployeViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employe_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employe employe = employeList.get(position);
        holder.id.setText(String.valueOf(employe.getId()));
        holder.nom.setText(employe.getPrenom() + " " + employe.getNom());
        holder.telephone.setText(employe.getTelephone());
        holder.email.setText(employe.getEmail());
        holder.actif.setText(String.valueOf(employe.getActif()));
        holder.magasin.setText(employe.getMagasin().getNom());
        if (employe.getManager() != null) {
            holder.manager.setText(employe.getManager().getPrenom() + " " + employe.getManager().getNom());
        }

        holder.expandCollaspseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.telephoneSection.getVisibility() == View.GONE) {
                    //TransitionManager.beginDelayedTransition(, new AutoTransition());
                    holder.expandCollaspseButton.setRotation(90);
                    holder.telephoneSection.setVisibility(View.VISIBLE);
                    holder.emailSection.setVisibility(View.VISIBLE);
                    holder.actifSection.setVisibility(View.VISIBLE);
                    holder.magasinSection.setVisibility(View.VISIBLE);
                    holder.managerSection.setVisibility(View.VISIBLE);
                } else {
                    holder.expandCollaspseButton.setRotation(0);
                    holder.telephoneSection.setVisibility(View.GONE);
                    holder.emailSection.setVisibility(View.GONE);
                    holder.actifSection.setVisibility(View.GONE);
                    holder.magasinSection.setVisibility(View.GONE);
                    holder.managerSection.setVisibility(View.GONE);
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
                            employeViewModel.getText().observe(
                                (LifecycleOwner) employeFragment, new Observer<String>() {
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
                                for(Employe e:selectedEmployes)
                                {
                                    // TODO MAKE API CALL TO DELETE EMPLOYE
                                    employeList.remove(e);
                                }
                                if(employeList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedEmployes.size()==employeList.size()) {
                                    isSelectAll = false;
                                    selectedEmployes.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedEmployes.clear();
                                    selectedEmployes.addAll(employeList);
                                }
                                employeViewModel.setText(String.valueOf(selectedEmployes.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedEmployes.clear();
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
            holder.personIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.personIcon.setVisibility(View.VISIBLE);
        }
    }

    private void ClickItem(ViewHolder holder) {
        Employe e = employeList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.personIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedEmployes.add(e);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.personIcon.setVisibility(View.VISIBLE);
            selectedEmployes.remove(e);
        }
        employeViewModel.setText(String.valueOf(selectedEmployes.size()));
    }

    @Override
    public int getItemCount() {
        return employeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView nom;
        public TextView telephone;
        public TextView email;
        public TextView actif;
        public TextView magasin;
        public TextView manager;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageButton expandCollaspseButton;
        public LinearLayout telephoneSection;
        public LinearLayout emailSection;
        public LinearLayout actifSection;
        public LinearLayout magasinSection;
        public LinearLayout managerSection;
        public ImageView select;
        public ImageView personIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.employe_id);
            nom = itemView.findViewById(R.id.employe_text);
            telephone = itemView.findViewById(R.id.phone_text);
            email = itemView.findViewById(R.id.email_text);
            actif = itemView.findViewById(R.id.actif_text);
            magasin = itemView.findViewById(R.id.magasin_text);
            manager = itemView.findViewById(R.id.manager_text);
            editButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
            expandCollaspseButton = itemView.findViewById(R.id.expand_collapse);
            telephoneSection = itemView.findViewById(R.id.phone_section);
            emailSection = itemView.findViewById(R.id.email_section);
            actifSection = itemView.findViewById(R.id.actif_section);
            magasinSection = itemView.findViewById(R.id.magasin_section);
            managerSection = itemView.findViewById(R.id.manager_section);
            select = itemView.findViewById(R.id.checkbox_icon);
            personIcon = itemView.findViewById(R.id.store_icon);
        }
    }

    public void setEmployeList(List<Employe> employeList) {
        this.employeList = employeList;
    }
}
