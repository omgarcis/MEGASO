package com.example.megaso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Menu2 extends AppCompatActivity {
    private Button btnsigno;
    private Button btnserv;
    private FirebaseAuth Authc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);
        Authc = FirebaseAuth.getInstance();

        btnsigno = (Button) findViewById(R.id.btn_cerrarsesion);
        btnserv = (Button) findViewById(R.id.btn_servicios);

        btnserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu2.this, servicios.class));
            }
        });


        btnsigno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authc.signOut();
                startActivity(new Intent(Menu2.this, authActivity.class));
                finish();

            }
        });
    }

}