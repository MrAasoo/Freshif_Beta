package com.aasoo.freshifybeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.MemberDetailActivity;
import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.jewellery.ProductInfo;
import com.aasoo.freshifybeta.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context mContext;
    private List<Product> mProducts;

    public ProductAdapter(Context context, List<Product> products) {
        this.mContext = context;
        this.mProducts = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.admin_product_layout, parent, false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = mProducts.get(position);
        if(product != null){
            holder.product_model_no.setText(product.getProduct_model_no());
            holder.product_name.setText(product.getProduct_name());
            holder.product_price.setText(product.getProduct_price());
            Picasso.get().load(product.getProduct_title_image_url()).placeholder(R.drawable.placeholder).into(holder.title_image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductInfo.class);
                    intent.putExtra("product_model_no", product.getProduct_model_no());
                    mContext.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView title_image;
        private TextView product_model_no,product_name,product_price;

        public ViewHolder(@NonNull View view) {
            super(view);
            title_image = view.findViewById(R.id.product_title_image);
            product_model_no = view.findViewById(R.id.product_model_no);
            product_name = view.findViewById(R.id.product_name);
            product_price = view.findViewById(R.id.product_price);
        }
    }
}
