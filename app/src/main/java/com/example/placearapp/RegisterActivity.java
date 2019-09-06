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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword, inputRePassword;
    private TextView registerErrorText;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.register_username_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputRePassword = findViewById(R.id.register_re_enter_password_input);
        registerErrorText = findViewById(R.id.register_error_text);
        loadingBar = new ProgressDialog(this);
    }

    public void registerButtonPressed(View v) {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String rePassword = inputRePassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            registerErrorText.setText(R.string.username_must_be_filled);
            registerErrorText.setVisibility(View.VISIBLE);
            return;
        } else if (TextUtils.isEmpty(password)) {
            registerErrorText.setText(R.string.password_must_be_filled);
            registerErrorText.setVisibility(View.VISIBLE);
            return;
        } else if (!password.equals(rePassword)) {
            registerErrorText.setText(R.string.passwords_not_same);
            registerErrorText.setVisibility(View.VISIBLE);
            return;
        }
        registerErrorText.setVisibility(View.INVISIBLE);
        loadingBar.setTitle(R.string.register_loading_bar_title);
        loadingBar.setMessage("Please Wait, While we are checking your account details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        validateUsername(username, password);
    }

    private void validateUsername(String username, String password) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(username).exists())) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("username", username);
                    userDataMap.put("password", password);
                    rootReference.child("Users").child(username).updateChildren(userDataMap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Toast.makeText(RegisterActivity.this, R.string.account_created, Toast.LENGTH_SHORT)
                                            .show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(RegisterActivity.this, R.string.username_not_exists, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                } else {
                    registerErrorText.setText(R.string.username_is_not_available);
                    registerErrorText.setVisibility(View.VISIBLE);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
