package com.aasoo.freshifybeta.jewellery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.ImageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductImageActivity extends AppCompatActivity {

    private static final int PICK_IMAGES = 1002;
    private static final int GALLERY_REQUEST = 1001;
    String TAG = "ProductImageActivity";

    ImageView title_image;
    Context context = this;
    String model_no;
    Button btn_save, btn_cancel;
    TextView change_title_image, select_image, image_err_msg;
    boolean imageUpload = false;
    boolean subImage = false;

    ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Uri ImageUri;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image);
        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setSubtitle("Product image detail");
        model_no = getIntent().getStringExtra("product_model_no");

        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        change_title_image = findViewById(R.id.change_title_image);
        title_image = findViewById(R.id.product_title_image);
        select_image = findViewById(R.id.select_image);
        recyclerView = findViewById(R.id.multi_image_layout);
        recyclerView.setHasFixedSize(true);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        ImageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(context, ImageList);
        recyclerView.setAdapter(imageAdapter);
        image_err_msg = findViewById(R.id.image_err_msg);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardChanges(model_no);
            }
        });

        change_title_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, GALLERY_REQUEST);
                }
            }
        });
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multiImageIntent = new Intent();
                multiImageIntent.setType("image/*");
                multiImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                multiImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(multiImageIntent, PICK_IMAGES);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUpload) {
                    uplodaProductImage(model_no);
                } else
                    Toast.makeText(context, "Select tile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadSubImages(final String model_no) {
        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setTitle("Uploading product images");
        mDialog.setMessage("Please wait..");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        final int count = 0;
        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("product_images").child(model_no).child("sub_image");
        for (int i = 0; i < ImageList.size(); i++) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageList.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] arrimg = bos.toByteArray();
            final StorageReference Imagename = storage.child("image" + i + ".jpg");
            Imagename.putBytes(arrimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String image_url = uri.toString();
                                DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no).child("sub_images");
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("image_url", image_url);
                                refrence.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            mDialog.dismiss();
                                            Toast.makeText(ProductImageActivity.this, "SlideImage Uploading Failed.Try Again.", Toast.LENGTH_SHORT).show();
                                            return;
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
                    Log.e(TAG, "onFailure: " + e.getMessage(), e);
                }
            });
        }
        mDialog.dismiss();
        Intent intent = new Intent(context, ProductOfferActivity.class);
        intent.putExtra("product_model_no", model_no);
        startActivity(intent);
        finish();
    }

    private void uplodaProductImage(final String model_no) {
        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setTitle("Uploading product images");
        mDialog.setMessage("Please wait..");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        title_image.setDrawingCacheEnabled(true);
        title_image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) title_image.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] arrimg = bos.toByteArray();

        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("product_images").child(model_no).child("title_image.jpg");
        storage.putBytes(arrimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String title_image_url = uri.toString();
                            DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("product_title_image_url", title_image_url);
                            map.put("show_in_offer", "No");
                            refrence.updateChildren(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (subImage) {
                                        mDialog.dismiss();
                                        uploadSubImages(model_no);
                                    } else {
                                        mDialog.dismiss();
                                        Intent intent = new Intent(context, ProductOfferActivity.class);
                                        intent.putExtra("product_model_no", model_no);
                                        startActivity(intent);
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
                Log.e(TAG, "onFailure: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public void onBackPressed() {
        discardChanges(model_no);
    }

    private void discardChanges(final String model_no) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure ?");
        dialog.setMessage("This will discard your changes and you may lose progress.");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail");
                reference.child(model_no).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                Bitmap img = null;
                if (uri != null) {
                    img = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    title_image.setImageBitmap(img);
                    imageUpload = true;
                }
            } catch (Exception e) {
                Toast.makeText(context, "Exception is : " + e, Toast.LENGTH_LONG).show();
            }

        }

        if (requestCode == PICK_IMAGES && resultCode == RESULT_OK) {
            image_err_msg.setVisibility(View.VISIBLE);
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                ImageList.clear();
                if (count > 0 && count <= 6) {
                    image_err_msg.setVisibility(View.GONE);
                    int i = 0;
                    while (i < count) {
                        ImageUri = data.getClipData().getItemAt(i).getUri();
                        ImageList.add(ImageUri);
                        i++;
                    }
                    imageAdapter.notifyDataSetChanged();
                    subImage = true;
                } else {
                    image_err_msg.setText("Do not select above 6 image.\nTotal selected SlideImage : " + count);
                }
            } else {
                image_err_msg.setText("Unable to select images.\nTry again.");
            }
        }
    }
}
