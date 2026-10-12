package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.data.SessionManager;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.data.repository.UserRepository;
import com.borrowbox.app.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToSignUp, tvForgotPassword;
    private ProgressBar progressBar;

    private UserRepository userRepository;
    private SessionManager sessionManager;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = UserRepository.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        tilEmail = findViewById(R.id.tilLoginEmail);
        tilPassword = findViewById(R.id.tilLoginPassword);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressLogin);

        // Clear any previous error on typing
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilEmail.setError(null);
        });
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilPassword.setError(null);
        });

        btnLogin.setOnClickListener(v -> {
            // Double-tap debouncing
            if (SystemClock.elapsedRealtime() - lastClickTime < 800) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            attemptLogin();
        });

        tvGoToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Please enter your college email");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Please enter your password");
            hasError = true;
        }

        if (hasError) return;

        setLoading(true);

        userRepository.authenticateUser(email, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                sessionManager.createLoginSession(user);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                setLoading(false);
                String msg = throwable.getMessage() != null ? throwable.getMessage() : "Invalid email or password.";
                tilPassword.setError(msg);
                Snackbar.make(btnLogin, msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
        // Build clean programmatic input dialog
        TextInputLayout tilResetEmail = new TextInputLayout(this, null, com.google.android.material.R.attr.textInputOutlinedStyle);
        tilResetEmail.setHint(getString(R.string.college_email));
        TextInputEditText etResetEmail = new TextInputEditText(tilResetEmail.getContext());
        etResetEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        tilResetEmail.addView(etResetEmail);

        TextInputLayout tilResetPw = new TextInputLayout(this, null, com.google.android.material.R.attr.textInputOutlinedStyle);
        tilResetPw.setHint(getString(R.string.new_password_label));
        tilResetPw.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        TextInputEditText etResetPw = new TextInputEditText(tilResetPw.getContext());
        etResetPw.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilResetPw.addView(etResetPw);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 16);
        layout.addView(tilResetEmail);
        layout.addView(tilResetPw);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_password_title)
                .setMessage(R.string.reset_password_instructions)
                .setView(layout)
                .setPositiveButton(R.string.reset_password_action, (dialog, which) -> {
                    String resetEmail = etResetEmail.getText() != null ? etResetEmail.getText().toString().trim() : "";
                    String resetPw = etResetPw.getText() != null ? etResetPw.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(resetEmail) || resetPw.length() < 6) {
                        Snackbar.make(btnLogin, "Please enter email and a password of at least 6 characters.", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    userRepository.resetPassword(resetEmail, resetPw, new RepositoryCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Snackbar.make(btnLogin, R.string.password_reset_success, Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Snackbar.make(btnLogin, throwable.getMessage() != null ? throwable.getMessage() : "Password reset failed.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }
}
