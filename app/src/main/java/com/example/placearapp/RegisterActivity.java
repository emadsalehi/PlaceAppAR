package com.example.placearapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountBuuton;
    private EditText inputUsername, inputPassword, inputRePassword;
    private TextView passwordsNotSameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountBuuton = findViewById(R.id.register_button);
        inputUsername = findViewById(R.id.register_username_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputRePassword = findViewById(R.id.register_re_enter_password_input);
        passwordsNotSameText = findViewById(R.id.passwords_not_same);


    }
}
