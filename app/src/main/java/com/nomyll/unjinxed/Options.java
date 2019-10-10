package com.nomyll.unjinxed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Options extends AppCompatActivity {
    TextView menu, tables, data, reset;
    String type;

    Button restart;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.update_menu) {
                startActivity(new Intent(Options.this, MenuPopup.class));
            }
            if (v.getId() == R.id.update_tables) {
                startActivity(new Intent(Options.this, TablesPopup.class));
            }
            if (v.getId() == R.id.update_data) {
                startActivity(new Intent(Options.this, DataPopup.class));
            }
            if (v.getId() == R.id.reset_database) {
                startFirebase();
                firebaseLogin();
                firebaseTable();
                firebaseProduct();
                Toast.makeText(Options.this, "HardReset Success!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        menu = findViewById(R.id.update_menu);
        tables = findViewById(R.id.update_tables);
        data = findViewById(R.id.update_data);
        reset = findViewById(R.id.reset_database);

        menu.setOnClickListener(listener);
        tables.setOnClickListener(listener);
        data.setOnClickListener(listener);
        reset.setOnClickListener(listener);

        restart = findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Options.this, Login.class));
            }
        });

        type = AppType.getType();
        if (!type.equals("Developer")) {
            reset.setVisibility(View.GONE);
        } else {
            data.setEnabled(false);
            data.setTextColor(Color.parseColor("#D7D7D7"));
        }
    }

    private void firebaseProduct() {
        List<ProductInfo> product_info_list = new ArrayList<>();

        ProductInfo product_info = new ProductInfo();
        databaseReference.child("Unjinxed-Support").child("ProductInformation").setValue("");
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                product_info.setData("Água Mineral", 1.5);
            }
            if (i == 1) {
                product_info.setData("Cerveja Lata", 3.5);
            }
            if (i == 2) {
                product_info.setData("Coca-Cola 1L", 5.0);
            }
            if (i == 3) {
                product_info.setData("Coca-Cola Lata", 3.0);
            }
            if (i == 4) {
                product_info.setData("Mousse", 4.0);
            }
            if (i == 5) {
                product_info.setData("Prato Feito", 11.0);
            }
            if (i == 6) {
                product_info.setData("Self-Service", 15.0);
            }
            if (i == 7) {
                product_info.setData("Suco Com Água", 5.0);
            }
            if (i == 8) {
                product_info.setData("Suco Garrafa", 8.0);
            }
            if (i == 9) {
                product_info.setData("Tubaína", 2.5);
            }
            product_info_list.add(product_info);
            databaseReference.child("Unjinxed-Support").child("ProductInformation").child(product_info.getName()).setValue(product_info.getValue());
        }
    }

    private void firebaseTable() {
        databaseReference.child("Unjinxed-Support").child("TableInformation").setValue(5);
        int tableQuantity = 5;
        databaseReference.child("Unjinxed").setValue("");
        for (int i = 0; i < tableQuantity; i++) {
            databaseReference.child("Unjinxed").child("Mesa " + (i + 1)).setValue("");
        }
        return;
    }

    private void firebaseLogin() {
        Cashier cashier_data = new Cashier();
        cashier_data.setLogin("cashier");
        cashier_data.setPassword("cashier");
        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Cashier").setValue(cashier_data);

        Waiter waiter_data = new Waiter();
        waiter_data.setLogin("waiter");
        waiter_data.setPassword("waiter");
        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Waiter").setValue(waiter_data);
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(Options.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}