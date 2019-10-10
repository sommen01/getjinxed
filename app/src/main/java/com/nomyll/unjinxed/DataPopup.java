package com.nomyll.unjinxed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataPopup extends AppCompatActivity {
    EditText login, password;
    Button refresh_login, refresh_password;

    String type;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.refresh_login) {
                if (TextUtils.isEmpty(login.getText().toString())) {
                    login.setError("Insira o login.");
                    return;
                } else {
                    if (type.equals("Caixa")) {
                        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Cashier").child("login").setValue(login.getText().toString());
                    }
                    if (type.equals("Garçom")) {
                        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Waiter").child("login").setValue(login.getText().toString());
                    }
                    Toast.makeText(DataPopup.this, "Login alterado!", Toast.LENGTH_SHORT).show();
                    login.setText("");
                    return;
                }
            }
            if (v.getId() == R.id.refresh_password) {
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("Insira a senha.");
                    return;
                } else {
                    if (type.equals("Caixa")) {
                        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Cashier").child("password").setValue(password.getText().toString());
                    }
                    if (type.equals("Garçom")) {
                        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Waiter").child("password").setValue(password.getText().toString());
                    }
                    Toast.makeText(DataPopup.this, "Senha alterada!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    return;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        login = findViewById(R.id.main);
        password = findViewById(R.id.password);
        refresh_login = findViewById(R.id.refresh_login);
        refresh_password = findViewById(R.id.refresh_password);

        type = AppType.getType();

        refresh_login.setOnClickListener(listener);
        refresh_password.setOnClickListener(listener);

        startFirebase();
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(DataPopup.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
