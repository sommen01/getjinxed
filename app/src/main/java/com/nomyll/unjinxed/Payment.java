package com.nomyll.unjinxed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Payment extends AppCompatActivity {
    TextView total, remaining, change;
    ListView history_lv;
    EditText paid;
    Button close, pay;
    int table_id;
    String owner_name, history_log;

    Double total_value, paid_value, remaining_value, change_value, value_aux;

    List<String> history_list = new ArrayList<>();
    ArrayAdapter<String> history_arrayadapter;

    FirebaseDatabase firebase_database;
    DatabaseReference database_reference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.pay) {
                if (TextUtils.isEmpty(paid.getText())) {
                    paid.setError("Insira um valor");
                    return;
                } else {
                    paid_value = Double.parseDouble(paid.getText().toString());
                    value_aux = remaining_value;
                    remaining_value = value_aux - paid_value;
                    history_log = "R$ " + value_aux + " - R$ " + paid_value + " = R$ " + remaining_value;
                    remaining.setText("R$ " + remaining_value);
                    paid.setText("");
                    history_list.add(history_log);

                    if (remaining_value <= 0.0) {
                        if (remaining_value < 0) {
                            change_value = remaining_value * (-1);
                            change.setText("R$ " + change_value);
                        } else {
                            change.setText("R$ 0.0");
                        }
                        pay.setEnabled(false);
                        paid.setEnabled(false);
                        remaining.setEnabled(false);
                        remaining.setText("R$ 0.0");
                        close.setEnabled(true);
                    }
                }
            }
            if (v.getId() == R.id.close) {
                paymentFirebase();
                Toast.makeText(Payment.this, "Comanda paga!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Payment.this, Main.class);
                startActivity(intent);
            }
        }
    };

    private void paymentFirebase()
    {
        database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(owner_name).removeValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        startFirebase();

        total = findViewById(R.id.total);
        remaining = findViewById(R.id.remaining);
        change = findViewById(R.id.change);
        history_lv = findViewById(R.id.history);
        paid = findViewById(R.id.paid);
        close = findViewById(R.id.close);
        pay = findViewById(R.id.pay);
        table_id = Main.getTableid() + 1;
        owner_name = TableManager.getOwner_name();

        history_arrayadapter = new ArrayAdapter<>(Payment.this, android.R.layout.simple_list_item_1, history_list);
        history_lv.setAdapter(history_arrayadapter);

        pay.setOnClickListener(listener);
        close.setOnClickListener(listener);

        firebaseRefresh();

        close.setEnabled(false);
    }

    private void firebaseRefresh()
    {
        database_reference.child("Unjinxed").child("Mesa " + (table_id)).child(owner_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total_value = dataSnapshot.child("Valor Total/totalPrice").getValue(Double.class);
                updateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateData()
    {
        total.setText("R$ " + total_value);
        remaining.setText("R$ " + total_value);
        remaining_value = total_value;
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(Payment.this);
        firebase_database = FirebaseDatabase.getInstance();
        database_reference = firebase_database.getReference();
    }
}
