package com.aasoo.freshifybeta.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.ViewHolder> {
    private Context mContext;
    private List<Product> mProducts;
    private String mUser;
    private String mCurrentUser;

    public UserProductAdapter(Context mContext, List<Product> mProducts, String mUser) {
        this.mContext = mContext;
        this.mProducts = mProducts;
        this.mUser = mUser;
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public UserProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_grid_view_layout, parent, false);
        return new UserProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProductAdapter.ViewHolder holder, int position) {
        final Product product = mProducts.get(position);
        if (product != null) {
            if (product.getProduct_offer_price().equals(product.getProduct_price())) {
                holder.product_offer.setVisibility(View.GONE);
                holder.product_offer_price.setText("₹ " + product.getProduct_price());
            } else {
                holder.product_offer.setVisibility(View.VISIBLE);
                holder.product_price.setText("₹ " + product.getProduct_price());
                holder.product_offer_price.setText("₹ " + product.getProduct_offer_price());
                holder.product_discount.setText(product.getProduct_discount() + "% Off");
            }
            Picasso.get().load(product.getProduct_title_image_url()).placeholder(R.drawable.placeholder).into(holder.product_title_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductInfo.class);
                intent.putExtra("product_model_no", product.getProduct_model_no());
                mContext.startActivity(intent);
            }
        });
        holder.add_in_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                final EditText editText = new EditText(mContext);
                editText.setText("0");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setCancelable(false);
                alert.setTitle("Add in cart");
                alert.setMessage("Enter your quantity");
                alert.setIcon(R.drawable.ic_alert);
                alert.setView(editText);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String quantity = editText.getText().toString();
                        if (quantity.equals("0") || quantity.equals("")) {
                            Toast.makeText(mContext, "Select valid quantity (eg. 1)", Toast.LENGTH_SHORT).show();
                        } else {
                            addInCart(quantity, product.getProduct_model_no());
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        holder.add_in_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInWishlist(product.getProduct_model_no());
            }
        });
    }

    private void addInWishlist(String product_model_no) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser).child("my_wishlist");
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_model_no", product_model_no);
        reference.child(product_model_no).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Product added in My wishlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addInCart(String quantity, String product_model_no) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setTitle("Adding in cart");
        dialog.setMessage("Please wait ...");
        dialog.create();
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser).child("my_cart");
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_model_no", product_model_no);
        map.put("product_quantity", quantity);
        reference.child(product_model_no).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(mContext, "Product added in cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView product_title_image;
        private TextView product_price, product_offer_price, product_discount;
        private LinearLayout product_offer;
        private ImageButton add_in_wishlist, add_in_cart;

        public ViewHolder(@NonNull View view) {
            super(view);
            product_title_image = view.findViewById(R.id.product_title_image);
            product_price = view.findViewById(R.id.product_price);
            product_offer_price = view.findViewById(R.id.product_offer_price);
            product_discount = view.findViewById(R.id.product_discount);
            product_offer = view.findViewById(R.id.product_offer);
            add_in_cart = view.findViewById(R.id.add_in_cart);
            add_in_wishlist = view.findViewById(R.id.add_in_wishlist);
            if (mUser.equals("admin_home")) {
                add_in_cart.setVisibility(View.GONE);
                add_in_wishlist.setVisibility(View.GONE);
            } else if (mUser.equals("user_cart")) {
                add_in_cart.setVisibility(View.GONE);
            } else if (mUser.equals("user_wish")) {
                add_in_wishlist.setVisibility(View.GONE);
            }
        }
    }

}
