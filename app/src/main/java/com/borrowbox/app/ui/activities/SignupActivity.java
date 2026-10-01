package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.User;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirm, etCollege;
    private CheckBox cbAgree;
    private MaterialButton btnCreate;
    private ImageButton btnBack;
    private TextView tvGoToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etSignupName);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        etConfirm = findViewById(R.id.etSignupConfirmPassword);
        etCollege = findViewById(R.id.etSignupCollege);
        cbAgree = findViewById(R.id.cbAgreeTerms);
        btnCreate = findViewById(R.id.btnCreateAccount);
        btnBack = findViewById(R.id.btnSignupBack);
        tvGoToSignIn = findViewById(R.id.tvGoToSignIn);

        btnBack.setOnClickListener(v -> finish());
        tvGoToSignIn.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();
            String college = etCollege.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty() || college.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(this, "Please enter a valid college email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbAgree.isChecked()) {
                Toast.makeText(this, "Please agree to the Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already registered in SQLite DB
            if (DataManager.getInstance().isEmailRegistered(email)) {
                Toast.makeText(this, "An account with this email already exists. Please Sign In.", Toast.LENGTH_LONG).show();
                return;
            }

            // Create new User entity and save to SQLite DB
            String userId = "u_" + System.currentTimeMillis();
            String defaultAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=120&h=120&fit=crop&auto=format";
            User newUser = new User(userId, name, email, password, college, defaultAvatar);

            boolean registered = DataManager.getInstance().registerUser(newUser);
            if (registered) {
                Toast.makeText(this, "Account created successfully! Welcome " + name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
