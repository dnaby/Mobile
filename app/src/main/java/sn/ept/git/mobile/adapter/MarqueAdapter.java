package sn.ept.git.mobile.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sn.ept.git.mobile.R;
import sn.ept.git.mobile.api.models.categorie.Categorie;
import sn.ept.git.mobile.api.models.marque.Marque;
import sn.ept.git.mobile.ui.category.CategoryFragment;
import sn.ept.git.mobile.ui.marque.MarqueFragment;

public class MarqueAdapter extends RecyclerView.Adapter<MarqueAdapter.ViewHolder> {
    private List<Marque> marqueList;
    private MarqueFragment marqueFragment;

    public MarqueAdapter(List<Marque> marqueList, MarqueFragment marqueFragment) {
        this.marqueList = marqueList;
        this.marqueFragment = marqueFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categorie_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Marque marque = marqueList.get(position);
        holder.marqueNameTextView.setText(marque.getNom());
        holder.marqueIDTextView.setText(String.valueOf(marque.getId()));
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BUTTONS", "USER TAP EDIT CATEGORY BUTTON WITH ID: " + String.valueOf(marque.getId()));
                // TODO I WANT TO DISPLAY THE DIALOG TO UPDATE THE CATEGORIE
                marqueFragment.showEditDialog(marque);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marqueFragment.showDeleteDialog(marque);
            }
        });
    }

    @Override
    public int getItemCount() {
        return marqueList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView marqueNameTextView;
        public TextView marqueIDTextView;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            marqueNameTextView = itemView.findViewById(R.id.category_text);
            marqueIDTextView = itemView.findViewById(R.id.category_id);
            editButton = itemView.findViewById(R.id.edit_category);
            deleteButton = itemView.findViewById(R.id.delete_category);
        }
    }

    public void setMarqueList(List<Marque> marqueList) {
        this.marqueList = marqueList;
    }
}
