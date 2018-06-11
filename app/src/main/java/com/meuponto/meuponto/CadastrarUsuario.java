package com.meuponto.meuponto;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CadastrarUsuario extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    // Edit Text
    private EditText etNome;
    private EditText etEmail;
    private EditText etPass1;
    private EditText etPass2;

    // Buttons
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        etNome  = (EditText) findViewById(R.id.etNome);
        etPass1 = (EditText) findViewById(R.id.etPass1);
        etPass2 = (EditText) findViewById(R.id.etPass2);
        etEmail = (EditText) findViewById(R.id.etEmail);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        btnCadastrar = (Button) findViewById(R.id.btnCadastrarFinal);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = etNome.getText().toString();
                String email = etEmail.getText().toString();

                String senha1 = etPass1.getText().toString();
                String senha2 = etPass2.getText().toString();

                if (nome.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Nome deve ser informado!", Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email deve ser informado!", Toast.LENGTH_SHORT).show();
                } else if (senha1.isEmpty() || senha2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Os dois campos de senha devem ser informados!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!senha1.equals(senha2)){
                        Toast.makeText(getApplicationContext(), "As senhas informadas devem ser iguais!", Toast.LENGTH_SHORT).show();
                    } else {
                        createUser(email, senha1);

                        Usuario usuario = new Usuario();
                        usuario.setNome(nome.toUpperCase());
                        usuario.setEmail(email);

                        databaseReference.child("Usuario").push().setValue(usuario).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.w("CREATE_USER", "task " + task.isSuccessful());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseCrash.report(e);
                            }
                        });

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "Usuario cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseUser != null){
                    Log.w("FirebaseAuth", "Login: " + firebaseUser.getUid());
                } else {
                    Log.w("FirebaseAuth", "logOut");
                }
            }
        };
    }

    /**
     * Cria novo usario email e senha
     * @param email
     * @param password
     */
    protected void createUser (String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.w("CREATE_USER", "EmailAndPassword: " + task.isSuccessful());
                    }
                }).
                addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}
