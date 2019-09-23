package com.example.placearapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.placearapp.Handler.MySingleton;
import com.example.placearapp.Handler.SessionHandler;
import com.example.placearapp.R;
import com.example.placearapp.task.AccountTask;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputRePassword;
    private EditText inputName;
    private TextView registerErrorText;
    private ProgressDialog loadingBar;
    private String registerUrl = "http://placeapp.000webhostapp.com/register.php";
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());

        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.register_username_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputRePassword = findViewById(R.id.register_re_enter_password_input);
        registerErrorText = findViewById(R.id.register_error_text);
        inputName = findViewById(R.id.register_name_input);
        loadingBar = new ProgressDialog(this);
    }

    public void registerButtonPressed(View v) {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String rePassword = inputRePassword.getText().toString();
        String name = inputName.getText().toString();

        if (TextUtils.isEmpty(username)) {
            registerErrorText.setText(R.string.username_must_be_filled);
            registerErrorText.setVisibility(View.VISIBLE);
            return;
        } else if (TextUtils.isEmpty(password)) {
            registerErrorText.setText(R.string.password_must_be_filled);
            registerErrorText.setVisibility(View.VISIBLE);
            return;
        } else if (TextUtils.isEmpty(name)) {
            registerErrorText.setText(R.string.name_must_be_filled);
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
        validateUsernameVolley(name, username, password);
    }

    private void validateUsernameVolley(String name, String username, String password) {
        JSONObject request = new JSONObject();
        try {
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);
            request.put(KEY_FULL_NAME, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, registerUrl, request, response -> {
                    loadingBar.dismiss();
                    try {
                        if (response.getInt(KEY_STATUS) == 0) {
                            session.loginUser(username,name);
                            loadHomeActivity();
                        }else if(response.getInt(KEY_STATUS) == 1){
                            System.out.println("Hey");
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    loadingBar.dismiss();
                    System.out.println(error.toString());
                    System.out.println(error.networkResponse);
                    Toast.makeText(RegisterActivity.this.getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                });
        jsArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private void loadHomeActivity() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }

    private void validateUsername(String name, String username, String password) {
        AccountTask accountTask = new AccountTask(this);
        accountTask.execute("register", name, username, password);
        loadingBar.dismiss();
    }
}
