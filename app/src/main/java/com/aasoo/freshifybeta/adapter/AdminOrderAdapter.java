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

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {
    private Context mContext;
    private List<Order> orders;

    public AdminOrderAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @NonNull
    @Override
    public AdminOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.admin_order_layout,parent,false);
        return new AdminOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderAdapter.ViewHolder holder, int position) {
        final Order order = orders.get(position);
        if(order!=null){
            String dateTime = order.getOrder_date()+"   "+order.getOrder_time();
            holder.date.setText(dateTime);
            holder.order_id.setText(order.getOrder_no());
            holder.pro_model_no.setText(order.getOrder_model_no());
            holder.quantity.setText(order.getOrder_quantity());
            holder.total_price.setText("₹ " + order.getOrder_total_price());
            holder.order_due.setText("₹ " + order.getOrder_due());
            getProductDetail(order.getOrder_model_no(),holder.product_title_image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowOrderDetailActivity.class);
                    intent.putExtra("order_id",order.getOrder_no());
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void getProductDetail(String model_no, final ImageView title_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if(product!=null){
                    Picasso.get().load(product.getProduct_title_image_url()).placeholder(R.drawable.placeholder).into(title_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView product_title_image;
        public TextView date;
        public TextView order_id;
        public TextView pro_model_no;
        public TextView quantity;
        public TextView total_price;
        public TextView order_due;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_title_image = itemView.findViewById(R.id.product_title_image);
            date = itemView.findViewById(R.id.order_date);
            order_id = itemView.findViewById(R.id.order_id);
            pro_model_no = itemView.findViewById(R.id.product_model_no);
            quantity = itemView.findViewById(R.id.order_quantity);
            total_price = itemView.findViewById(R.id.order_total_price);
            order_due = itemView.findViewById(R.id.order_due);
        }
    }
}
