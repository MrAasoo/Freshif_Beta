package com.aasoo.freshifybeta.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.SlideImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ImageSliderListAdapter extends RecyclerView.Adapter<ImageSliderListAdapter.ViewHolder> {

    private Context mContext;
    private List<SlideImage> mSlideImages;

    public ImageSliderListAdapter(Context context, List<SlideImage> slideImages) {
        this.mContext = context;
        this.mSlideImages = slideImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sliding_image_list_layout,parent,false);
        return new ImageSliderListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SlideImage slideImage = mSlideImages.get(position);
        if(slideImage != null){
            Picasso.get().load(slideImage.getImage_url()).placeholder(R.drawable.placeholder).into(holder.imageView);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle("Delete Slide")
                            .setMessage("Are you sure ?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String id = slideImage.getImage_id();
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide_detail").child(id);
                            StorageReference storage = FirebaseStorage.getInstance().getReference().child("slide_images").child(id).child("image.jpg");
                            storage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(mContext, "Slide removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(mContext,"Unable to delete slide,Try again latter",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .create()
                    .show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mSlideImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slide_image);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
