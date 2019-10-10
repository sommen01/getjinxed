package com.nomyll.unjinxed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateOrder extends AppCompatActivity {
    Spinner service;
    ListView products_lv;
    EditText quantity, name_owner;
    Button add, create;
    int table_id;
    int product_counter;
    Double total_price, price_aux;

    List<Product> products_list = new ArrayList<>();
    ArrayAdapter<Product> products_arrayadapter;

    ArrayList<String> service_data = new ArrayList<>();

    FirebaseDatabase firebase_database;
    DatabaseReference database_reference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.add) {
                if (TextUtils.isEmpty(quantity.getText().toString()) || TextUtils.isEmpty(name_owner.getText().toString())) {
                    if (TextUtils.isEmpty(quantity.getText().toString())) {
                        quantity.setError("Insira a quantidade");
                        return;
                    }
                    if (TextUtils.isEmpty(name_owner.getText().toString())) {
                        name_owner.setError("Insira o nome");
                        return;
                    }
                    return;
                } else {
                    database_reference.child("Unjinxed-Support").child("ProductInformation").child(service.getSelectedItem().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            price_aux = dataSnapshot.getValue(Double.class);
                            Product product = new Product();
                            product.setName(service.getSelectedItem().toString());
                            product.setPrice(price_aux * Integer.parseInt(quantity.getText().toString()));
                            product.setQuantity(Integer.parseInt(quantity.getText().toString()));

                            product.setIdComanda(name_owner.getText().toString());

                            database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(name_owner.getText().toString()).child(product.getName()).setValue(product);
                            refreshFirebase();

                            total_price += product.getPrice();
                            product_counter++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name_owner.getWindowToken(), 0);
                    return;
                }
            }
            if (v.getId() == R.id.create) {
                if (product_counter == 0) {
                    Toast.makeText(CreateOrder.this, "Comanda sem itens!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Order order = new Order();
                    order.setNameOwner(name_owner.getText().toString());
                    order.setTotalPrice(total_price);
                    database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(name_owner.getText().toString()).child("Valor Total").setValue(order);
                    refreshFirebase();
                    Toast.makeText(CreateOrder.this, "Nome: " + order.getNameOwner() + " | " + total_price + " reais", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        startFirebase();

        service = findViewById(R.id.service);
        serviceRefresh();

        quantity = findViewById(R.id.quantity);
        products_lv = findViewById(R.id.product_list);
        add = findViewById(R.id.add);
        name_owner = findViewById(R.id.name);
        create = findViewById(R.id.create);

        add.setOnClickListener(listener);
        create.setOnClickListener(listener);

        table_id = Main.getTableid() + 1;
        product_counter = 0;
        total_price = 0.0;

        products_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) products_lv.getItemAtPosition(position);
                String name_list = product.getName();
                int quantity_list = product.getQuantity();
                service.setSelection(((ArrayAdapter<String>) service.getAdapter()).getPosition(name_list));
                quantity.setText(quantity_list + "");
            }
        });

        service.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                quantity.setText("");
                return false;
            }
        });
    }

    private void serviceRefresh() {
        database_reference.child("Unjinxed-Support").child("ProductInformation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                service_data.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    service_data.add(ds.getKey());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CreateOrder.this, android.R.layout.simple_spinner_item, service_data);
                service.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(CreateOrder.this);
        firebase_database = FirebaseDatabase.getInstance();
        database_reference = firebase_database.getReference();
    }

    private void refreshFirebase() {
        database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(name_owner.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products_list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (!ds.getKey().equals("Valor Total")) {
                        products_list.add(product);
                    }
                }
                products_arrayadapter = new ArrayAdapter<Product>(CreateOrder.this, android.R.layout.simple_list_item_1, products_list);
                products_lv.setAdapter(products_arrayadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
