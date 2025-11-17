package com.example.firstbit_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.firstbit_app.R;

import java.util.ArrayList;

/**
 * адаптер для автоматической прокрутки изображений
 */
public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<Integer> imageList;
    private ViewPager2 viewPager2;

    public ImageSliderAdapter(Context context, ArrayList<Integer> imageList, ViewPager2 viewPager2) {
        this.context = context;
        this.imageList = imageList;
        this.viewPager2 = viewPager2;
    }

    /**
     * создаёт ViewHolder для изображения
     */
    @NonNull
    @Override
    public ImageSliderAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.image_container, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * устанавливает изображение и запускает бесконечную прокрутку
     */
    @Override
    public void onBindViewHolder(@NonNull ImageSliderAdapter.ImageViewHolder holder, int position) {

        holder.image.setImageResource(imageList.get(position));

        if (position == imageList.size() - 1){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * вложанный класс
     */
    public static final class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView image = itemView.findViewById(R.id.image_in_image);

        public ImageViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Runnable для бесконечной прокрутки
     */
    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            imageList.addAll(imageList);
            notifyDataSetChanged();
        }
    };
}
