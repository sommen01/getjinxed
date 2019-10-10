package com.nomyll.unjinxed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TablesPopup extends AppCompatActivity {
    Button refresh;
    TextView tables_text;
    EditText tables;
    int tables_quantity;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(tables.getText().toString())) {
                tables.setError("Insira a quantidade.");
                return;
            } else {
                tables_quantity = Integer.parseInt(tables.getText().toString());
                tables.setText("");
                databaseReference.child("Unjinxed-Support").child("TableInformation").setValue(tables_quantity);
                Toast.makeText(TablesPopup.this, "Quantidade atualizada!", Toast.LENGTH_SHORT).show();
                databaseReference.child("Unjinxed").setValue("");
                for (int i = 0; i < tables_quantity; i++) {
                    databaseReference.child("Unjinxed").child("Mesa " + (i + 1)).setValue("");
                }
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        refresh = findViewById(R.id.refresh);
        tables_text = findViewById(R.id.tables_text);
        tables = findViewById(R.id.tables);

        refresh.setOnClickListener(listener);

        startFirebase();
        firebase();
    }

    private void firebase() {
        databaseReference.child("Unjinxed-Support").child("TableInformation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tables_quantity = dataSnapshot.getValue(Integer.class);
                tables_text.setText("Atualmente são usadas " + tables_quantity + " mesas.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return;
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(TablesPopup.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
