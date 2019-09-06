package com.example.placearapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placearapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword;
    private TextView loginErrorText;
    private ProgressDialog loadingBar;
    private String parentDBName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.login_username_input);
        inputPassword = findViewById(R.id.login_password_input);
        loginErrorText = findViewById(R.id.login_error_text);
        loadingBar = new ProgressDialog(this);
    }

    public void loginButtonPressed(View v) {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            loginErrorText.setText(R.string.username_must_be_filled);
            loginErrorText.setVisibility(View.VISIBLE);
            return;
        } else if (TextUtils.isEmpty(password)) {
            loginErrorText.setText(R.string.password_must_be_filled);
            loginErrorText.setVisibility(View.VISIBLE);
            return;
        }

        loginErrorText.setVisibility(View.INVISIBLE);
        loadingBar.setTitle(R.string.login_loading_bar_title);
        loadingBar.setMessage("Please Wait, While we are checking your account details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        allowAccessAccount(username, password);
    }

    private void allowAccessAccount (String username, String password) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(username).exists()) {
                    User userData = dataSnapshot.child(parentDBName).child(username).getValue(User.class);

                    if (userData.getPassword().equals(password) && userData.getUsername().equals(username)) {
                        loadingBar.dismiss();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.incorrect_password_or_username, Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
