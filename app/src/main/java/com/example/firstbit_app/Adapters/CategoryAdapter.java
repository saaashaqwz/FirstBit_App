package com.example.firstbit_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.Models.Category;
import com.example.firstbit_app.R;

import java.util.List;

/**
 * адаптер для отображения списка категорий в горизонтальном RecyclerView
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private OnCategoryClickListener onCategoryClickListener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener { void onCategoryClick(Category category, int position); }

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) { this.onCategoryClickListener = listener; }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    /**
     * создаёт ViewHolder для категории
     */
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    /**
     * привязывает название категории к TextView
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryTitle.setText(category.getTitle());

        if (position == selectedPosition) {
            holder.background.setBackgroundResource(R.drawable.category_selected_bg);
            holder.categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.categoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.background.setBackgroundResource(R.drawable.category_default_bg);
            holder.categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.dark_purple));
            holder.categoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.dark_purple));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                setSelectedPosition(position);
                onCategoryClickListener.onCategoryClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

    /**
    * вложанный класс
    */
    public static final class CategoryViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout background;
        ImageView categoryIcon;
        TextView categoryTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.category_background);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryTitle = itemView.findViewById(R.id.category_title);
        }
    }
}