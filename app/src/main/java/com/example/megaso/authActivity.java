package com.example.megaso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class authActivity extends AppCompatActivity {
    //creacion de objetos tipo edit text
    private EditText txtema;
    private EditText txtpas;
    private Button btnreg;
    //variables para el registro
    private String email= "";
    private String password= "";

    //objeto del paquete de autenticacion de Firebase
    FirebaseAuth Auth;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //creando instance
        Auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //relacionando objetos con el xml
        txtema = (EditText)findViewById(R.id.txt_email);
        txtpas = (EditText)findViewById(R.id.txt_passw);
        btnreg = (Button) findViewById(R.id.btnregistrar);



        //creando funcion para el boton registrar
        btnreg.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View view){
                email = txtema.getText().toString();
                password = txtpas.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()) {
                    if (password.length() >= 6) {
                        //ejecutando un metodo
                        registerUser();
                    } else {
                        Toast.makeText(authActivity.this, "la contraseña debe tener minimo 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(authActivity.this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
                }
        }
        });

    }
    private void registerUser(){
        Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map = new HashMap<>();
                    map.put( "email", email);
                    map.put("password", password);
                    String id = Auth.getCurrentUser().getUid();

                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>(){


                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                startActivity(new Intent(authActivity.this, MenuActivity.class));
                                finish();
                            }else{
                                Toast.makeText(authActivity.this, "no se pudieron crear los datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(authActivity.this, "no se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}