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
import sn.ept.git.mobile.api.models.produit.Produit;
import sn.ept.git.mobile.api.models.stock.Stock;
import sn.ept.git.mobile.ui.produit.ProduitFragment;
import sn.ept.git.mobile.ui.produit.ProduitViewModel;
import sn.ept.git.mobile.ui.stock.StockFragment;
import sn.ept.git.mobile.ui.stock.StockViewModel;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<Stock> stockList;
    private StockFragment stockFragment;
    boolean isEnable=false;
    boolean isSelectAll=false;
    List<Stock> selectedStocks =new ArrayList<>();
    StockViewModel stockViewModel;

    public StockAdapter(List<Stock> stockList, StockFragment stockFragment) {
        this.stockList = stockList;
        this.stockFragment = stockFragment;
        stockViewModel = new StockViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        if (stock.getProduit() != null && stock.getMagasin() != null) {
            holder.quantity.setText(String.valueOf(stock.getQuantite()));
            holder.produitName.setText(stock.getProduit().getNom());
            holder.magasinName.setText(stock.getMagasin().getNom());
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stockFragment.showDeleteDialog(stock);}
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stockFragment.showEditDialog(stock);}
        });

        holder.expandCollaspseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.magasinSection.getVisibility() == View.GONE) {
                    //TransitionManager.beginDelayedTransition(, new AutoTransition());
                    holder.expandCollaspseButton.setRotation(90);
                    holder.magasinSection.setVisibility(View.VISIBLE);
                } else {
                    holder.expandCollaspseButton.setRotation(0);
                    holder.magasinSection.setVisibility(View.GONE);
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
                            stockViewModel.getText().observe(
                                (LifecycleOwner) stockFragment, new Observer<String>() {
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
                                for(Stock s:selectedStocks)
                                {
                                    // TODO MAKE API CALL TO DELETE PRODUCT
                                    stockList.remove(s);
                                }
                                if(stockList.size()==0)
                                {
                                    // TODO CREATE TEXT VIEW AND IF LIST IS EMPTY DISPLAY SOMETHING
                                }
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectedStocks.size()==stockList.size()) {
                                    isSelectAll = false;
                                    selectedStocks.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedStocks.clear();
                                    selectedStocks.addAll(stockList);
                                }
                                stockViewModel.setText(String.valueOf(selectedStocks.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedStocks.clear();
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
            holder.inboxIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.inboxIcon.setVisibility(View.VISIBLE);
        }
    }

    private void ClickItem(ViewHolder holder) {
        Stock s = stockList.get(holder.getAdapterPosition());
        if(holder.select.getVisibility()==View.GONE)
        {
            holder.inboxIcon.setVisibility(View.GONE);
            holder.select.setVisibility(View.VISIBLE);
            selectedStocks.add(s);
        }
        else
        {
            holder.select.setVisibility(View.GONE);
            holder.inboxIcon.setVisibility(View.VISIBLE);
            selectedStocks.remove(s);
        }
        stockViewModel.setText(String.valueOf(selectedStocks.size()));
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quantity;
        public TextView produitName;
        public TextView magasinName;
        public LinearLayout magasinSection;
        public ImageView select;
        public ImageView inboxIcon;
        public ImageButton expandCollaspseButton;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.stock_quantity);
            produitName = itemView.findViewById(R.id.stock_text);
            magasinName = itemView.findViewById(R.id.magasin_text);
            magasinSection = itemView.findViewById(R.id.magasin_section);
            select = itemView.findViewById(R.id.checkbox_icon);
            inboxIcon = itemView.findViewById(R.id.stock_icon);
            expandCollaspseButton = itemView.findViewById(R.id.expand_collapse);
            editButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
        }
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }
}
