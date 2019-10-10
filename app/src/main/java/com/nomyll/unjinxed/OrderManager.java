package com.nomyll.unjinxed;

import android.content.Context;
import android.content.Intent;
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

public class OrderManager extends AppCompatActivity {
    Spinner service;
    ListView product_lv;
    EditText quantity, name_owner;
    Button update, update_order, close;
    int table_id, product_count;
    String type, owner_name;
    Double total_price, price_aux;

    List<Product> product_list = new ArrayList<>();
    ArrayAdapter<Product> product_arrayadapter;

    ArrayList<String> service_data = new ArrayList<>();

    FirebaseDatabase firebase_database;
    DatabaseReference database_reference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.update) {

                if (TextUtils.isEmpty(quantity.getText().toString())) {
                    quantity.setError("Insira a quantidade");
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
                            if (product.getQuantity() == 0) {
                                database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(owner_name).child(product.getName()).removeValue();
                                refreshFirebase();
                            }
                            refreshFirebase();
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
            if (v.getId() == R.id.update_order) {
                database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(owner_name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        total_price = 0.0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Product product = ds.getValue(Product.class);
                            if (!ds.getKey().equals("Valor Total")) {
                                total_price += product.getPrice();
                                product_count++;
                            }
                        }
                        Order order = new Order();
                        order.setNameOwner(owner_name);
                        order.setTotalPrice(total_price);
                        if (product_count > 0) {
                            database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(owner_name).child("Valor Total").setValue(order);
                            Toast.makeText(OrderManager.this, "Comanda Atualizada!", Toast.LENGTH_SHORT).show();
                            if (type.equals("Garçom")) {
                                finish();
                            } else {
                                return;
                            }
                        } else {
                            Toast.makeText(OrderManager.this, "Comanda Vazia!", Toast.LENGTH_SHORT).show();
                            database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(owner_name).child("Valor Total").removeValue();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            if (v.getId() == R.id.close) {
                Intent intent = new Intent(OrderManager.this, Payment.class);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        startFirebase();

        type = AppType.getType();
        name_owner = findViewById(R.id.name);
        service = findViewById(R.id.service);
        quantity = findViewById(R.id.quantity);
        product_lv = findViewById(R.id.product_list);
        update = findViewById(R.id.update);
        update_order = findViewById(R.id.update_order);
        table_id = Main.getTableid();
        close = findViewById(R.id.close);
        table_id = Main.getTableid() + 1;
        name_owner.setEnabled(false);
        owner_name = TableManager.getOwner_name();
        name_owner.setText(owner_name);
        product_count = 0;

        service = findViewById(R.id.service);
        serviceRefresh();

        if (type.equals("Garçom")) {
            close.setVisibility(View.GONE);
        }

        product_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) product_lv.getItemAtPosition(position);
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

        update_order.setOnClickListener(listener);
        update.setOnClickListener(listener);
        close.setOnClickListener(listener);

        refreshFirebase();
    }

    private void refreshFirebase() {
        database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(name_owner.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                product_list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (!ds.getKey().equals("Valor Total")) {
                        product_list.add(product);
                    }
                }
                product_arrayadapter = new ArrayAdapter<Product>(OrderManager.this, android.R.layout.simple_list_item_1, product_list);
                product_lv.setAdapter(product_arrayadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(OrderManager.this, android.R.layout.simple_spinner_item, service_data);
                service.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(OrderManager.this);
        firebase_database = FirebaseDatabase.getInstance();
        database_reference = firebase_database.getReference();
    }
}
