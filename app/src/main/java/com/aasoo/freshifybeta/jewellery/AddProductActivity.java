package com.aasoo.freshifybeta.jewellery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aasoo.freshifybeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity{

    private TextView txt_type;
    private String TAG = "AddProductActivity";
    private String gen = "default",type = "default";
    private EditText jewellery_name,jewellery_model_no,jewellery_other_txt;
    private TextView type_err_msg,gen_err_msg;
    private RadioButton jewellery_women, jewellery_man, jewellery_kid,jewellery_others;
    private Spinner jewellery_list;
    private Button btn_next;
    private String[] list;
    private ArrayAdapter<? extends String> adapter;


    private Context context = this;
    static String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setTitle("Add Product");

        jewellery_name = findViewById(R.id.jewellery_name);
        jewellery_model_no = findViewById(R.id.jewellery_model_no);

        jewellery_women = findViewById(R.id.jewellery_womens);
        jewellery_man = findViewById(R.id.jewellery_man);
        jewellery_kid = findViewById(R.id.jewellery_kid);
        jewellery_others = findViewById(R.id.jewellery_others);

        jewellery_other_txt = findViewById(R.id.jewellery_other_txt);
        jewellery_list = findViewById(R.id.jewellery_list);
        btn_next = findViewById(R.id.btn_next);

        type_err_msg = findViewById(R.id.type_err_msg);
        gen_err_msg = findViewById(R.id.gen_err_msg);
        txt_type = findViewById(R.id.txt_type);


        jewellery_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = list[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = "default";
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = jewellery_name.getText().toString().trim();
                final String model = jewellery_model_no.getText().toString().toUpperCase().trim();
                if(checkData(name,model)){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Confirm Add product");
                    dialog.setCancelable(false);
                    dialog.setMessage("Model no : "+model+"\nName : "+name+"\nFor :"+gen+"\ntype :"+type);
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterfacet, int which) {
                                addNewProduct(model,name, gen, type);
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.create();
                    dialog.show();
                }
            }
        });


    }

    private void addNewProduct(final String model, final String name, final String gen, final String type) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(model).exists()){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Confirm Add product");
                    dialog.setCancelable(false);
                    dialog.setMessage("Model model : "+model+" is already exist");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.create();
                    dialog.show();
                }else{
                    final ProgressDialog mDialog = new ProgressDialog(context);
                    mDialog.setTitle("Adding product");
                    mDialog.setMessage("Please wait");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("product_model_no", model);
                    map.put("product_name", name);
                    map.put("product_for", gen);
                    map.put("product_type", type);
                    reference.child(model).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mDialog.dismiss();
                                Intent intent = new Intent(context,ProductDetailActivity.class);
                                intent.putExtra("product_model_no", model);
                                startActivity(intent);
                                finish();
                            }else{
                                mDialog.dismiss();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                dialog.setTitle("Confirm Add product");
                                dialog.setCancelable(false);
                                dialog.setMessage("Failed adding project\nPlease try again...");
                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.create();
                                dialog.show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkData(String name,String model) {
        if(model.equals("")){
            jewellery_model_no.setError("Enter Model no.");
            jewellery_model_no.requestFocus();
            return false;
        }else {
            jewellery_model_no.setText(model);
        }
        if(name.equals("")){
            jewellery_name.setError("Enter Jewellery name");
            jewellery_name.requestFocus();
            return false;
        }else {
            jewellery_name.setText(name);
        }
        if(gen.equals("default")){
            gen_err_msg.setVisibility(View.VISIBLE);
            return false;
        }else if(gen.equals("Others")){
            type = jewellery_other_txt.getText().toString().trim();
        }else
            gen_err_msg.setVisibility(View.GONE);
        if(type.equals("default") || type.equals("")){
            type_err_msg.setVisibility(View.VISIBLE);
            return false;
        }else
            type_err_msg.setVisibility(View.GONE);
        return true;
    }

    public void onGenderClicked(View view) {
        txt_type.setVisibility(View.VISIBLE);
        if(view.getId() == R.id.jewellery_others){
            jewellery_other_txt.setVisibility(View.VISIBLE);
            jewellery_list.setVisibility(View.GONE);
        }else{
            jewellery_other_txt.setVisibility(View.GONE);
            jewellery_list.setVisibility(View.VISIBLE);
        }
        switch (view.getId()){
            case R.id.jewellery_womens:
                gen = jewellery_women.getText().toString();
                list = getResources().getStringArray(R.array.women_jewellery_list);
                setListAndAdapterInSpinner(list);
                break;
            case R.id.jewellery_man:
                gen = jewellery_man.getText().toString();
                list = getResources().getStringArray(R.array.man_jewellery_list);
                setListAndAdapterInSpinner(list);
                break;
            case R.id.jewellery_kid:
                gen = jewellery_kid.getText().toString();
                list = getResources().getStringArray(R.array.kids_jewellery_list);
                setListAndAdapterInSpinner(list);
                break;
            case R.id.jewellery_others:
                gen = jewellery_others.getText().toString();
                type = "default";
                break;
        }
    }

    private void setListAndAdapterInSpinner(String[] list) {
        type = "default";
        adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jewellery_list.setAdapter(adapter);
    }
}
