package com.example.megaso;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class authActivity extends AppCompatActivity {
    //creacion de objetos tipo edit text
    private EditText txtema;
    private EditText txtpas;
    private Button btnregauth;
    private Button btnlog;
    private SignInButton btnsigning;
    //variables para el login
    private String email= "";
    private String password= "";
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;

    //objeto del paquete de autenticacion de Firebase
    FirebaseAuth Auth;
    DatabaseReference mDatabase;


    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //creando instance
        Auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //relacionando objetos con el xml
        btnregauth = (Button) findViewById(R.id.btnregistrarauth);
        btnlog = (Button) findViewById(R.id.btnlogin);
        txtema = (EditText) findViewById(R.id.txt_emailauth);
        txtpas = (EditText) findViewById(R.id.txt_passwauth);
        btnsigning = (SignInButton) findViewById(R.id.btnsigningoo);


        //GOOGLE SIGN IN
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnsigning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnsigningoo:
                        signIn();
                        break;
                    // ...
                }
            }
        });




        //funcion boton acceder
        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = txtema.getText().toString().trim();
                password = txtpas.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()) {
                    loginUser();
                }else{
                    Toast.makeText(authActivity.this,"ingrese los datos para acceder", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //creando funcion para el boton registrar
        btnregauth.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View view){
            startActivity(new Intent(authActivity.this, Register.class));
        }
        });

    }// cerrando oncreate


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                startActivity(new Intent(authActivity.this,Menu2.class));
                finish();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    //metodo login
    public void loginUser(){
        Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(authActivity.this,Menu2.class));
                    finish();
                }else{
                    Toast.makeText(authActivity.this, "no se pudo iniciar sesion compruebe los datos",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = Auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }

                    private void updateUI(FirebaseUser user) {
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        if(Auth.getCurrentUser() != null) {
            startActivity(new Intent(authActivity.this, Menu2.class));
            finish();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = Auth.getCurrentUser();
    }

    //mantener sesion iniciada al cerrar la aplicacion (escribir onstar para llamarlo)
}