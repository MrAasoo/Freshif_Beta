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

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.ShowOrderDetailActivity;
import com.aasoo.freshifybeta.model.Order;
import com.aasoo.freshifybeta.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserOderAdapter extends RecyclerView.Adapter<UserOderAdapter.ViewHolder> {
    private Context mContext;
    private List<Order> orders;

    public UserOderAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @NonNull
    @Override
    public UserOderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_oder_layout, parent, false);
        return new UserOderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOderAdapter.ViewHolder holder, int position) {
        final Order order = orders.get(position);
        if (order != null) {
            holder.order_quantity.setText(order.getOrder_quantity());
            holder.product_price.setText("₹ " + order.getOrder_item_price());
            holder.total_amount.setText("₹ " + order.getOrder_total_price());
            getProductDetail(order.getOrder_model_no(), holder.product_title_image, holder.product_name);

            holder.see_in_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowOrderDetailActivity.class);
                    intent.putExtra("order_id",order.getOrder_no());
                    mContext.startActivity(intent);
                }
            });
        }


    }

    private void getProductDetail(String model_no, final ImageView title_image,final TextView product_name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    Picasso.get().load(product.getProduct_title_image_url()).placeholder(R.drawable.placeholder).into(title_image);
                    product_name.setText(product.getProduct_name());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView product_title_image;
        public TextView order_quantity;
        public TextView product_price;
        public TextView total_amount;
        public TextView product_name;
        public TextView see_in_details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_title_image = itemView.findViewById(R.id.product_title_image);
            total_amount = itemView.findViewById(R.id.total_amount);
            order_quantity = itemView.findViewById(R.id.order_quantity);
            product_price = itemView.findViewById(R.id.product_price);
            product_name = itemView.findViewById(R.id.product_name);
            see_in_details = itemView.findViewById(R.id.see_in_details);
        }
    }
}
