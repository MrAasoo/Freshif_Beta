package com.aasoo.freshifybeta.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.jewellery.ProductInfo;
import com.aasoo.freshifybeta.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context mContext;
    private List<Product> mProducts;
    private String mCurrentUser;

    public WishlistAdapter(Context mContext, List<Product> mProducts,String mCurrentUser) {
        this.mContext = mContext;
        this.mProducts = mProducts;
        this.mCurrentUser = mCurrentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wishlist_list_layout, parent, false);
        return new WishlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = mProducts.get(position);
        if(product != null) {
            final String model_no = product.getProduct_model_no();
            getProductDetail(model_no,holder.image,holder.name,holder.pro_for,holder.pro_type,holder.layout,holder.price,holder.discount,holder.offer_price);

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setTitle("Remove from Wishlist")
                            .setMessage("Are you sure ?")
                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeFromWishlist(model_no);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductInfo.class);
                    intent.putExtra("product_model_no", model_no);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private void removeFromWishlist(String model_no) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser).child("my_wishlist");
        HashMap<String,Object> map = new HashMap<>();
        map.put(model_no,null);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext, "Product removed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getProductDetail(String model_no, final ImageView image, final TextView name, final TextView pro_for, final TextView pro_type, final LinearLayout layout, final TextView price, final TextView discount, final TextView offer_price) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    Picasso.get().load(product.getProduct_title_image_url()).placeholder(R.drawable.placeholder).into(image);
                    name.setText(product.getProduct_name());
                    if(product.getProduct_price().equals(product.getProduct_offer_price())){
                        layout.setVisibility(View.GONE);
                    }else {
                        layout.setVisibility(View.VISIBLE);
                        price.setText("₹ "+product.getProduct_price());
                        discount.setText(product.getProduct_discount()+" % Off");
                    }
                    offer_price.setText("₹ "+product.getProduct_offer_price());
                    pro_for.setText(product.getProduct_for());
                    pro_type.setText(product.getProduct_type());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageButton remove;
        private TextView name, pro_for, pro_type, price, offer_price, discount;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            remove = itemView.findViewById(R.id.remove);
            name = itemView.findViewById(R.id.name);
            pro_for = itemView.findViewById(R.id.product_for);
            pro_type = itemView.findViewById(R.id.product_type);
            price = itemView.findViewById(R.id.product_price);
            discount = itemView.findViewById(R.id.product_discount);
            offer_price = itemView.findViewById(R.id.product_offer_price);
            layout = itemView.findViewById(R.id.product_offer);
        }
    }
}
