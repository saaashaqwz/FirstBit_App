package com.example.firstbit_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.Models.Product;
import com.example.firstbit_app.Models.Service;
import com.example.firstbit_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * объединённый адаптер для отображения товаров и услуг в одном RecyclerView
 */
public class CombinedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_SERVICE = 1;

    private List<Object> items;
    private ProductAdapter productAdapter;
    private ServiceAdapter serviceAdapter;

    public CombinedAdapter(ProductAdapter productAdapter, ServiceAdapter serviceAdapter) {
        this.productAdapter = productAdapter;
        this.serviceAdapter = serviceAdapter;
        this.items = new ArrayList<>();
    }

    /**
     * устанавливает данные для отображения
     */
    public void setData(List<Product> products, List<Service> services) {
        items.clear();

        if (products != null) {
            items.addAll(products);
            android.util.Log.d("CombinedAdapter", "Добавлены " + products.size() + " продукты");
        }

        if (services != null) {
            items.addAll(services);
            android.util.Log.d("CombinedAdapter", "Добавлены " + services.size() + " услуги");
        }

        notifyDataSetChanged();
        android.util.Log.d("CombinedAdapter", "Всего предметов: " + items.size());
    }

    /**
     * определяет тип элемента (товар или услуга)
     */
    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Product) {
            return TYPE_PRODUCT;
        } else if (item instanceof Service) {
            return TYPE_SERVICE;
        }
        return -1;
    }

    /**
     * создаёт ViewHolder в зависимости от типа
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SERVICE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.service_item, parent, false);
            return new ServiceAdapter.ServiceViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_item, parent, false);
            return new ProductAdapter.ProductViewHolder(view);
        }
    }

    /**
     * привязывает данные к ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof ProductAdapter.ProductViewHolder && item instanceof Product) {
            productAdapter.onBindViewHolder((ProductAdapter.ProductViewHolder) holder,
                    getProductPosition((Product) item));
        } else if (holder instanceof ServiceAdapter.ServiceViewHolder && item instanceof Service) {
            serviceAdapter.onBindViewHolder((ServiceAdapter.ServiceViewHolder) holder,
                    getServicePosition((Service) item));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * вспомогательный метод поиска позиции товара
     */
    private int getProductPosition(Product product) {
        List<Product> products = productAdapter.productList;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                return i;
            }
        }
        return 0;
    }

    /**
     * вспомогательный метод поиска позиции услуги
     */
    private int getServicePosition(Service service) {
        List<Service> services = serviceAdapter.serviceList;
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId() == service.getId()) {
                return i;
            }
        }
        return 0;
    }
}
