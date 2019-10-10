package com.nomyll.unjinxed;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuPopup extends AppCompatActivity {
    EditText name, value;
    Button add, remove;
    ListView menu_lv;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<ProductInfo> menu_list = new ArrayList<ProductInfo>();
    ArrayAdapter<ProductInfo> menu_arrayadapter;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(add.getWindowToken(), 0);
            if (v.getId() == R.id.add) {
                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(value.getText().toString())) {
                    if (TextUtils.isEmpty(name.getText().toString())) {
                        name.setError("Insira o nome");
                        return;
                    }
                    if (TextUtils.isEmpty(value.getText().toString())) {
                        value.setError("Insira o valor");
                        return;
                    }
                    return;
                } else {
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.setData(name.getText().toString(), Double.parseDouble(value.getText().toString()));
                    databaseReference.child("Unjinxed-Support").child("ProductInformation").child(productInfo.getName()).setValue(productInfo.getValue());
                    Toast.makeText(MenuPopup.this, "Cardápio atualizado!", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    value.setText("");
                    menu_arrayadapter.notifyDataSetChanged();
                    menu_lv.setAdapter(menu_arrayadapter);
                    firebase();
                }
            }
            if (v.getId() == R.id.remove) {
                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Insira o nome");
                    return;
                } else {
                    databaseReference.child("Unjinxed-Support").child("ProductInformation").child(name.getText().toString()).removeValue();
                    Toast.makeText(MenuPopup.this, "Cardápio atualizado!", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    value.setText("");
                    menu_arrayadapter.notifyDataSetChanged();
                    menu_lv.setAdapter(menu_arrayadapter);
                    firebase();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        name = findViewById(R.id.name);
        value = findViewById(R.id.value);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        menu_lv = findViewById(R.id.menu_lv);

        add.setOnClickListener(listener);
        remove.setOnClickListener(listener);

        startFirebase();
        firebase();

        menu_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                           @Override
                                           public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                                               ProductInfo productInfo = (ProductInfo) menu_lv.getItemAtPosition(position);
                                               String name_list = productInfo.getName();
                                               Double value_list = productInfo.getValue();
                                               name.setText(name_list);
                                               value.setText(value_list.toString());
                                           }
                                       }
        );
    }

    private void firebase() {
        databaseReference.child("Unjinxed-Support").child("ProductInformation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menu_list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.setName(ds.getKey());
                    productInfo.setValue(ds.getValue(Double.class));
                    menu_list.add(productInfo);
                }
                menu_arrayadapter = new ArrayAdapter<>(MenuPopup.this, android.R.layout.simple_list_item_1, menu_list);
                menu_lv.setAdapter(menu_arrayadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return;
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(MenuPopup.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
