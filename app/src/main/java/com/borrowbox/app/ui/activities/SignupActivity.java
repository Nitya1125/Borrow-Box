package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.data.SessionManager;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.data.repository.UserRepository;
import com.borrowbox.app.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm, tilCollege;
    private TextInputEditText etName, etEmail, etPassword, etConfirm, etCollege;
    private CheckBox cbAgree;
    private MaterialButton btnCreate;
    private ImageButton btnBack;
    private TextView tvGoToSignIn;
    private ProgressBar progressBar;

    private UserRepository userRepository;
    private SessionManager sessionManager;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userRepository = UserRepository.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        tilName = findViewById(R.id.tilSignupName);
        tilEmail = findViewById(R.id.tilSignupEmail);
        tilPassword = findViewById(R.id.tilSignupPassword);
        tilConfirm = findViewById(R.id.tilSignupConfirmPassword);
        tilCollege = findViewById(R.id.tilSignupCollege);

        etName = findViewById(R.id.etSignupName);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        etConfirm = findViewById(R.id.etSignupConfirmPassword);
        etCollege = findViewById(R.id.etSignupCollege);

        cbAgree = findViewById(R.id.cbAgreeTerms);
        btnCreate = findViewById(R.id.btnCreateAccount);
        btnBack = findViewById(R.id.btnSignupBack);
        tvGoToSignIn = findViewById(R.id.tvGoToSignIn);
        progressBar = findViewById(R.id.progressSignup);

        btnBack.setOnClickListener(v -> finish());
        tvGoToSignIn.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 800) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            attemptSignup();
        });
    }

    private void attemptSignup() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);
        tilCollege.setError(null);

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirm = etConfirm.getText() != null ? etConfirm.getText().toString().trim() : "";
        String college = etCollege.getText() != null ? etCollege.getText().toString().trim() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Please enter your full name");
            hasError = true;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Please enter your college email");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Please create a password");
            hasError = true;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            hasError = true;
        }

        if (TextUtils.isEmpty(confirm)) {
            tilConfirm.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirm)) {
            tilConfirm.setError("Passwords do not match");
            hasError = true;
        }

        if (TextUtils.isEmpty(college)) {
            tilCollege.setError("Please enter your college/institution");
            hasError = true;
        }

        if (!cbAgree.isChecked()) {
            Snackbar.make(btnCreate, "Please agree to the Terms & Conditions to proceed.", Snackbar.LENGTH_LONG).show();
            hasError = true;
        }

        if (hasError) return;

        setLoading(true);

        userRepository.registerUser(name, email, password, college, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                sessionManager.createLoginSession(user);

                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                setLoading(false);
                String msg = throwable.getMessage() != null ? throwable.getMessage() : "Registration failed. Please try again.";
                tilEmail.setError(msg);
                Snackbar.make(btnCreate, msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCreate.setEnabled(!loading);
    }
}
