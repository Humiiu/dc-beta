package com.example.iseng;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. CEK AUTO LOGIN
        // Jika user sudah pernah login sebelumnya, Firebase akan menyimpan datanya
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "user udah login: " + currentUser.getEmail());
            // Langsung pindah ke MainActivity tanpa munculkan layar login
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Toast.makeText(this, "mencoba login", Toast.LENGTH_SHORT).show();
                loginUser();
            });
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "email/pw kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "login bisa");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "error";
                        Log.e(TAG, "login ngga bisa: " + msg);
                        Toast.makeText(LoginActivity.this, "gagal: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
