package com.aasoo.freshifybeta.image_slide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.ImageSliderListAdapter;
import com.aasoo.freshifybeta.model.SlideImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddSlideActivity extends AppCompatActivity {

    private static final int GALLERY_REQ_CODE = 1001;
    Boolean imageUpload = false;
    ImageView image;
    TextView err;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slide);
        getSupportActionBar().setTitle("Select Image");
        image = findViewById(R.id.slide_image);
        err = findViewById(R.id.choose_err_msg);
        TextView choose_image = findViewById(R.id.choose_image);
        Button btn_save = findViewById(R.id.btn_save);

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, GALLERY_REQ_CODE);
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUpload) {
                    uploadData();
                } else {
                    err.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void uploadData() {
        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setTitle("Uploading slider SlideImage");
        mDialog.setMessage("Please wait..");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        byte[] arrimg = bos.toByteArray();

        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("slide_images");

        final DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("slide_detail");
        final String id = refrence.push().getKey();
        final StorageReference imageref = storage.child(id).child("image.jpg");
        imageref.putBytes(arrimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image_url = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("image_url", image_url);
                            map.put("image_id", id);
                            refrence.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();
                                        finish();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("AddSlideActivity", "onFailure: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                Bitmap img = null;
                if (uri != null) {
                    img = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    image.setImageBitmap(img);
                    err.setVisibility(View.GONE);
                    imageUpload = true;
                }
            } catch (Exception e) {
                Toast.makeText(context, "Exception is : " + e, Toast.LENGTH_LONG).show();
            }
        }

    }

    public static class ManageSlidingActivity extends AppCompatActivity {
        Context context = this;

        private RecyclerView recyclerView;
        private ImageSliderListAdapter imageAdapter;
        private List<SlideImage> slideImages;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_manage_sliding);
            getSupportActionBar().setTitle("Manage Slide");
            TextView add_slide = findViewById(R.id.add_slide);
            add_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, AddSlideActivity.class));
                }
            });

            recyclerView = findViewById(R.id.image_list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            slideImages = new ArrayList<>();
            imageAdapter = new ImageSliderListAdapter(context, slideImages);
            recyclerView.setAdapter(imageAdapter);
            getSliderData();

        }

        private void getSliderData() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide_detail");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    slideImages.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SlideImage slideImage = snapshot.getValue(SlideImage.class);
                        if(slideImage !=null){
                            slideImages.add(slideImage);
                        }
                    }
                    imageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
