package com.example.storiiapp.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storiiapp.R;
import com.example.storiiapp.data.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private TextView txtloginRedirectText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initui();
        nextlogin();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateEmail() && validatePassword()) {
                    // show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    addUser();
                }
            }
        });
    }

    private void addUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = databaseReference.push().getKey();
        String rule = "user";
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        User user = new User(email, password, rule,userId);

        databaseReference.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUp.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this,Login.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUp.this, "Tạo tài khoản thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initui() {
        edtEmail = findViewById(R.id.edt_signup_email);
        edtPassword = findViewById(R.id.edt_signup_password);
        btnSignUp = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);
        txtloginRedirectText = findViewById(R.id.txt_loginRedirectText);
    }

    private void nextlogin() {

        txtloginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,Login.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email của bạn");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Đinh dạng email không hợp lệ");
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        String password = edtPassword.getText().toString().trim();
        if(TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        } else if(password.length() < 6) {
            edtPassword.setError("Độ dài mật khẩu phải từ 6 kí tự");
            return false;
        } else {
            return true;
        }
    }
}