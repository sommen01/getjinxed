package com.nomyll.unjinxed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class TableManager extends AppCompatActivity {
    static String owner_name;
    double value_aux;
    ListView order_lv;
    int table_number;
    Button add, opt;
    String type, name_owner;
    FirebaseDatabase firebase_database;
    DatabaseReference database_reference;
    List<Order> order_list = new ArrayList<>();
    ArrayAdapter<Order> order_arrayadapter;

    public static String getOwner_name() {
        return owner_name;
    }

    public static void setOwner_name(String owner_name) {
        TableManager.owner_name = owner_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_manager);

        order_lv = findViewById(R.id.table_list);
        add = findViewById(R.id.add);
        table_number = Main.getTableid() + 1;
        type = AppType.getType();

        if (type.equals("Caixa")) {
            add.setVisibility(View.GONE);
        }

        startFirebase();
        refreshFirebaseNames();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableManager.this, CreateOrder.class);
                startActivity(intent);
            }
        });

        opt = findViewById(R.id.options);
        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableManager.this, Options.class);
                startActivity(intent);
            }
        });

        order_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TableManager.this, order_list.get(position) + "", Toast.LENGTH_SHORT).show();

                Order order = (Order) parent.getItemAtPosition(position);
                TableManager.setOwner_name(order.getNameOwner());

                Intent intent = new Intent(TableManager.this, OrderManager.class);
                startActivity(intent);
            }
        });
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(TableManager.this);
        firebase_database = FirebaseDatabase.getInstance();
        database_reference = firebase_database.getReference();
    }

    private void refreshFirebaseNames() {
        database_reference.child("Unjinxed").child("Mesa " + (table_number)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order_list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    name_owner = ds.getKey();
                    refreshFirebase(name_owner);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void refreshFirebase(final String name_owner) {
        database_reference.child("Unjinxed").child("Mesa " + (table_number)).child(name_owner).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals("Valor Total"))
                    {
                        Order order = ds.getValue(Order.class);
                        if (order.getTotalPrice() > 0.0)
                        {
                            order_list.add(order);
                        }
                        if (order.getTotalPrice() == 0.0)
                        {
                            String name_owner_aux = name_owner;
                            database_reference.child("Unjinxed").child("Mesa " + (table_number)).child(name_owner_aux).removeValue();
                        }
                    }
                }
                order_arrayadapter = new ArrayAdapter<Order>(TableManager.this, android.R.layout.simple_list_item_1, order_list);
                order_lv.setAdapter(order_arrayadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}