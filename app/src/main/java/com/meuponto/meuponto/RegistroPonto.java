package com.meuponto.meuponto;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RegistroPonto extends AppCompatActivity {

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    // Radio Group
    private RadioGroup rgPonto;

    // Radio Button
    private RadioButton rbEntrada, rbSaida;

    // Button
    private Button btnRegistrar;

    // Checked Box
    private CheckBox cbAlmoco;

    // ListView
    private ListView lvHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_ponto);

        rgPonto = (RadioGroup) findViewById(R.id.rgPonto);
        rbEntrada = (RadioButton) findViewById(R.id.rbEntrada);
        rbSaida = (RadioButton) findViewById(R.id.rbSaida);

        cbAlmoco = (CheckBox) findViewById(R.id.cbAlmoco);

        lvHistorico = (ListView) findViewById(R.id.lvHistorico);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Ponto> pontoArrayList = new ArrayList<Ponto>();
                PontoAdapter pontoAdapter = new PontoAdapter(getApplicationContext(), pontoArrayList);

                Long size = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.w("Ponto", "change:" + snapshot.getKey() + "--" + snapshot.getValue().toString());
                    Ponto p = (Ponto) snapshot.getValue(Ponto.class);
                    pontoArrayList.add(p);
                }

                Collections.reverse(pontoArrayList);

                lvHistorico.setAdapter(pontoAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        rgPonto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbEntrada){
                    Toast.makeText(getApplicationContext(), "Entrada Selecionada!", Toast.LENGTH_SHORT).show();
                } else if(i == R.id.rbSaida){
                    Toast.makeText(getApplicationContext(), "Saida Selecionada!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectId = rgPonto.getCheckedRadioButtonId();

                if (selectId != rbEntrada.getId() &&
                        selectId != rbSaida.getId()){
                    Toast.makeText(getApplicationContext(), "Selecione uma opcao valida!", Toast.LENGTH_SHORT).show();
                } else {

                    Ponto ponto = new Ponto();
                    ponto.setHorario(new Date());

                    if (selectId == rbEntrada.getId()){
                        ponto.setEntrada(true);
                    } else {
                        ponto.setEntrada(false);
                    }

                    if (cbAlmoco.isChecked()){
                        ponto.setAlmoco(true);
                    } else {
                        ponto.setAlmoco(false);
                    }

                    databaseReference.child("Usuario").child("Ponto").push().setValue(ponto).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.w("CREATE_PONTO", "task " + task.isSuccessful());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.report(e);
                        }
                    });;

                    Toast.makeText(getApplicationContext(), "Ponto registrado com sucesso!", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

        if (valueEventListener != null) {
            databaseReference.child("Usuario").child("Ponto")
                    .addValueEventListener(valueEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.child("Usuario").child("Ponto")
                    .removeEventListener(valueEventListener);
        }

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}