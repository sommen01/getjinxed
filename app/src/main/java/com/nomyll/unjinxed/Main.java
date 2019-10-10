package com.nomyll.unjinxed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class Main extends AppCompatActivity {
    static int table_id;
    String login, type;
    TextView welcome;
    Button options;
    ListView table_lv;
    int quantity_aux;

    List<String> table_list = new ArrayList<>();
    ArrayAdapter<String> table_arrayadapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    View.OnClickListener opt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main.this, Options.class);
            startActivity(intent);
        }
    };

    public static int getTableid() {
        return table_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome = findViewById(R.id.welcome);
        type = AppType.getType();
        startFirebase();

        if (type.equals("Caixa")) {
            login = Login.getLogin_aux();
        }
        if (type.equals("Garçom")) {
            login = Login.getLogin_aux();
        }
        if (type.equals("Developer")) {
            login = Login.getLogin_aux();
        }

        welcome.setText("Bem vindo: " + login + "" +
                "\n Você está como: " + type);

        options = findViewById(R.id.options);
        options.setOnClickListener(opt);

        table_lv = findViewById(R.id.table_list);

        databaseReference.child("Unjinxed-Support").child("TableInformation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                quantity_aux = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        table_list.clear();

        if (quantity_aux == 0) {
            quantity_aux = Login.getTable_quantity();
        }

        for (int i = 0; i < quantity_aux; i++) {
            table_list.add("Mesa " + (i + 1));
        }


        table_arrayadapter = new ArrayAdapter<>(Main.this, android.R.layout.simple_list_item_1, table_list);
        table_lv.setAdapter(table_arrayadapter);

        table_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                table_id = position;
                Toast.makeText(Main.this, table_list.get(position) + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Main.this, TableManager.class);
                startActivity(intent);
            }
        });
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(Main.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
