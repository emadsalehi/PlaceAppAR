package com.example.placearapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.placearapp.R;
import com.example.placearapp.handler.SessionHandler;

public class MainActivity extends AppCompatActivity {

    private Button joinButton, loginButton;
    private SessionHandler session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());

        if(session.isLoggedIn()){
            loadHomeActivity();
        }
        setContentView(R.layout.activity_main);

        joinButton = findViewById(R.id.main_join_button);
        loginButton = findViewById(R.id.main_login_button);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loginButton.setEnabled(true);
            joinButton.setEnabled(true);
        } else {
            joinButton.setEnabled(false);
            loginButton.setEnabled(false);
        }

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        joinButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loadHomeActivity() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }
}
