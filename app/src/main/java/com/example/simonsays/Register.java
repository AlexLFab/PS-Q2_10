package com.example.simonsays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.simonsays.Music.AudioService;
import com.example.simonsays.databaseItems.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private TextInputEditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

        editTextUsername = findViewById(R.id.usernameReg);
        editTextPassword = findViewById(R.id.passwordReg);
        editTextEmail = findViewById(R.id.emailReg);
        buttonReg = findViewById(R.id.buttonReg);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, password, email;
                username = editTextUsername.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty()) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Añadir logs para depurar
                Log.d(TAG, "Checking if username exists: " + username);

                // Verificación antes de configurar el listener
                mDatabase.orderByChild("username").equalTo(username)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, "DataSnapshot received");
                                if (dataSnapshot.exists()) {
                                    // El nombre de usuario ya existe
                                    Log.d(TAG, "Username already exists");
                                    Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    // El nombre de usuario no existe, proceder con el registro
                                    Log.d(TAG, "Username available, proceeding to register");
                                    registerUser(username, email, password);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Error en la consulta
                                Log.w(TAG, "Database error: " + databaseError.getMessage());
                                Toast.makeText(Register.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        Log.d(TAG, "Registering user with email: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, actualizar UI con la información del usuario registrado
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Guardar el usuario en la base de datos
                                String uid = user.getUid();
                                User newUser = new User(username, email, uid); // Ajusta los parámetros según tu modelo de usuario
                                Log.d(TAG, "Saving user to database with UID: " + uid);
                                mDatabase.child(uid).setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User saved in database");
                                                    Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.w(TAG, "Error saving user in database", task.getException());
                                                    Toast.makeText(Register.this, "Failed to save user in database", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Si el registro falla, mostrar un mensaje al usuario.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();

        Intent i = new Intent(this, AudioService.class);
        i.putExtra("action", AudioService.PAUSE);
        startService(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = new Intent(this, AudioService.class);
        i.putExtra("action", AudioService.START);
        startService(i);
    }

}
