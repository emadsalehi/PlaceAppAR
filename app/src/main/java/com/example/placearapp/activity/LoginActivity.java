package com.example.placearapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.placearapp.Handler.MySingleton;
import com.example.placearapp.Handler.SessionHandler;
import com.example.placearapp.R;
import com.example.placearapp.task.AccountTask;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String LOGIN_URL = "http://placeapp.000webhostapp.com/login.php";
    private EditText inputUsername, inputPassword;
    private TextView loginErrorText;
    private ProgressDialog loadingBar;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionHandler(getApplicationContext());

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

        allowAccessAccountVolley(username, password);
    }

    private void allowAccessAccountVolley(String username, String password) {
        JSONObject request = new JSONObject();
        try {
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_URL, request, response -> {
                    loadingBar.dismiss();
                    try {
                        if (response.getInt(KEY_STATUS) == 0) {
                            session.loginUser(username,response.getString(KEY_FULL_NAME));
                            loadHomeActivity();

                        }else{
                            Toast.makeText(getApplicationContext(),
                                    response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private void loadHomeActivity() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }


    private void allowAccessAccount(String username, String password) {
        AccountTask accountTask = new AccountTask(this);
        accountTask.execute("login", username, password);
        finish();
    }
}
