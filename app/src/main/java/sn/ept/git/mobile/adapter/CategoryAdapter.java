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
import sn.ept.git.mobile.ui.category.CategoryFragment;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Categorie> categorieList;
    private CategoryFragment categoryFragment;

    public CategoryAdapter(List<Categorie> categorieList, CategoryFragment categoryFragment) {
        this.categorieList = categorieList;
        this.categoryFragment = categoryFragment;
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
        Categorie categorie = categorieList.get(position);
        holder.categorieNameTextView.setText(categorie.getNom());
        holder.categorieIDTextView.setText(String.valueOf(categorie.getId()));
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BUTTONS", "USER TAP EDIT CATEGORY BUTTON WITH ID: " + String.valueOf(categorie.getId()));
                // TODO I WANT TO DISPLAY THE DIALOG TO UPDATE THE CATEGORIE
                categoryFragment.showEditDialog(categorie);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryFragment.showDeleteDialog(categorie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categorieNameTextView;
        public TextView categorieIDTextView;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            categorieNameTextView = itemView.findViewById(R.id.category_text);
            categorieIDTextView = itemView.findViewById(R.id.category_id);
            editButton = itemView.findViewById(R.id.edit_category);
            deleteButton = itemView.findViewById(R.id.delete_category);
        }
    }

    public void setCategorieList(List<Categorie> categorieList) {
        this.categorieList = categorieList;
    }
}
