package com.nomyll.unjinxed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    static String login_aux;
    static int table_quantity;
    Button auth;
    EditText login, password;
    String cashier_login, cashier_password, waiter_login, waiter_password;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(login.getText()) || TextUtils.isEmpty(password.getText())) {
                if (TextUtils.isEmpty(login.getText())) {
                    login.setError("Insira seu login");
                    return;
                }
                if (TextUtils.isEmpty(password.getText())) {
                    password.setError("Insira sua senha");
                    return;
                }
            } else {
                if (login.getText().toString().equals(Developer.login) && password.getText().toString().equals(Developer.password)) {
                    Intent intent = new Intent(Login.this, Options.class);
                    startActivity(intent);
                    AppType.setType("Developer");
                    Login.setLogin_aux(Developer.getLogin());
                    return;
                } else if (login.getText().toString().equals(waiter_login) && password.getText().toString().equals(waiter_password)) {
                    Intent intent = new Intent(Login.this, Main.class);
                    startActivity(intent);
                    AppType.setType("Garçom");
                    Login.setLogin_aux(waiter_login);
                    return;
                } else if (login.getText().toString().equals(cashier_login) && password.getText().toString().equals(cashier_password)) {
                    Intent intent = new Intent(Login.this, Main.class);
                    startActivity(intent);
                    AppType.setType("Caixa");
                    Login.setLogin_aux(cashier_login);
                    return;
                } else {
                    Toast.makeText(Login.this, "Login/Senha inválido(s)", Toast.LENGTH_SHORT).show();
                    login.setText("");
                    password.setText("");
                    return;
                }
            }
        }
    };

    public static String getLogin_aux() {
        return login_aux;
    }

    public static void setLogin_aux(String login_aux) {
        Login.login_aux = login_aux;
    }

    public static int getTable_quantity() {
        return table_quantity;
    }

    public static void setTable_quantity(int table_quantity) {
        Login.table_quantity = table_quantity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.main);
        password = findViewById(R.id.password);

        login.setText("");
        password.setText("");
        auth = findViewById(R.id.auth);
        auth.setOnClickListener(listener);

        startFirebase();
        createLoginData();
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(Login.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void createLoginData() {
        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Cashier").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cashier cashier = dataSnapshot.getValue(Cashier.class);
                cashier_login = cashier.getLogin();
                cashier_password = cashier.getPassword();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        databaseReference.child("Unjinxed-Support").child("UserInformation").child("Waiter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Waiter waiter = dataSnapshot.getValue(Waiter.class);
                waiter_login = waiter.getLogin();
                waiter_password = waiter.getPassword();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        databaseReference.child("Unjinxed-Support").child("TableInformation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Login.setTable_quantity(dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
