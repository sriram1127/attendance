package com.example.sriram.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;
    private EditText usernameET;
    private EditText passwordET;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        //menu.setIcon(R.mipmap.icon);
        setTitle("Attendance - Login");
        getWindow().getDecorView().setBackgroundResource(R.drawable.bg);
        usernameET = ((EditText) findViewById(R.id.studentId));
        passwordET = ((EditText) findViewById(R.id.password));
        loginBtn = (Button) findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void close(View view)
    {
        System.exit(0);
    }

    public void login(View view) {
        username = usernameET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        if (username.equals("") || username.equals("null")) {
            Toast.makeText(this, "User Name cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            if (password.equals("") || password.equals("null")) {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(username+"@uncc.edu", password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            usernameET.setText("");
                            passwordET.setText("");
                            mDatabase.child("Professor").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Professor professor = dataSnapshot.getValue(Professor.class);
                                    if (professor == null) {
                                        moveTOMainActivity(StudentMainActivity.class);
                                    } else {
                                        moveTOMainActivity(ProfessorMainActivity.class);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    public void moveTOMainActivity(Class mainClass) {
        Intent intent = new Intent(this, mainClass);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
