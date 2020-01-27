package com.example.test;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, ePass;
    Button login;
    FirebaseAuth mAuth;

    private User data;

    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = null;
        phone = null;

        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.user);
        ePass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = mEmail.getText().toString().trim();
                String pass = ePass.getText().toString().trim();

                if (TextUtils.isEmpty(user)) {
                    mEmail.setError("User required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    ePass.setError("Pass required");
                    return;
                }

                mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logged IN", Toast.LENGTH_SHORT).show();

                            DatabaseReference referenceFromUrl = database.getReferenceFromUrl("https://test-1be3d.firebaseio.com/users");

                            Query query = referenceFromUrl.orderByChild("Username").equalTo("su");

                            ValueEventListener valueEventListener = new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // here you can get your data from this snapshot object
                                    for (  DataSnapshot child : dataSnapshot.getChildren() ){
                                        data = child.getValue(User.class);
                                        phone = data.Phone;
                                        Toast.makeText(LoginActivity.this, "Phone" + phone, Toast.LENGTH_SHORT).show();
                                        verify();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, "I was cancelled :(", Toast.LENGTH_SHORT).show();
                                    System.out.println("I was cancelled :(");
                                }
                            };

                            query.addListenerForSingleValueEvent(valueEventListener);


//
//                            if (data != null) {
//                                Toast.makeText(LoginActivity.this, "it worked", Toast.LENGTH_SHORT).show();
//                                verify();
//                            } else {
//                                Toast.makeText(LoginActivity.this, "Not working", Toast.LENGTH_SHORT).show();
//                            }


                        } else {
                            Toast.makeText(LoginActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks closure;

    public void verify() {

        Toast.makeText(LoginActivity.this, "Verify::start", Toast.LENGTH_SHORT).show();

        closure = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this, "Verify::completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Verify::failed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                Toast.makeText(LoginActivity.this, "Verify::super code sent", Toast.LENGTH_SHORT).show();

                super.onCodeSent(s, forceResendingToken);

                Toast.makeText(LoginActivity.this, "Verify::code sent", Toast.LENGTH_SHORT).show();

                Toast.makeText(LoginActivity.this, data.Phone, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, PhoneActivity.class);
                                intent.putExtra("AuthCredentials", s);
                                startActivity(intent);
                            }
                        }, 10
                );

            }
        };

        String realNumba =  "+351" + phone;

        Toast.makeText(LoginActivity.this, "Sending to: " + realNumba, Toast.LENGTH_SHORT).show();

        mAuth.setLanguageCode("pt");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                realNumba,
                10,
                TimeUnit.SECONDS,
                this,
                closure
        );
    }

}


