package com.acmezon.acmezon_dash;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    Button submit_btn,cancel_btn;
    EditText email_view,password_view;
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submit_btn=(Button)findViewById(R.id.submit_btn);
        cancel_btn=(Button)findViewById(R.id.cancel_btn);
        email_view=(EditText)findViewById(R.id.input_email);
        password_view=(EditText)findViewById(R.id.input_password);

        final String color_error = "#ff4444";

        final Drawable oldBackground = email_view.getBackground();

        shared = new SecurePreferences(this);
        List<String> credentials = retrieveCredentials();
        if (credentials.size()>0)  {
            email_view.setText(credentials.get(0));
            password_view.setText(credentials.get(1));
        }

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_view.getText().toString();
                String password = password_view.getText().toString();
                boolean isEmail = isEmail(email);

                if (!email.isEmpty() && !password.isEmpty() && isEmail &&
                        password.length() >= 8 && password.length() <= 32) {
                    email_view.setBackgroundDrawable(oldBackground);
                    password_view.setBackgroundDrawable(oldBackground);
                    System.out.println("Email: " + email + ", Password: " + password);
                    launch_post_query(email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.error_bad_params),
                            Toast.LENGTH_LONG).show();
                    if (email.isEmpty() || !isEmail) {
                        email_view.setBackgroundColor(Color.parseColor(color_error));
                    }
                    if (password.isEmpty() || password.length() < 8 || password.length() > 32) {
                        password_view.setBackgroundColor(Color.parseColor(color_error));
                    }
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean launch_post_query(String email, String password) {
        storeCredentials(email, MD5(password));
        return true;
        /*URL url;
        HttpURLConnection connection = null;

        JSONObject params = new JSONObject();

        try {
            params.put("email", email);
            params.put("password", MD5(password));
            storeCredentials(email, MD5(password));
            System.out.println(params);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        try {
            url = new URL("http://www.TODD.com/");
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.connect();

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream ());
            wr.writeBytes(params.toString());
            wr.flush();
            wr.close ();

            int response = connection.getResponseCode();
            if (response >= 200 && response <=399){
                storeCredentials(email, password);
                //TODO: Redirigir a otra vista
                return true;
            } else {
                if (response==400 || response==403) {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.error_authentication),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getResources().getString(R.string.error_server),
                            Toast.LENGTH_LONG).show();
                }

                return false;
            }


        } catch (Exception e) {

            e.printStackTrace();
            return false;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }*/
    }

    private String MD5 (String input) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(input.getBytes(),0,input.length());
            return new BigInteger(1,m.digest()).toString(16);
        } catch(NoSuchAlgorithmException x) {
            return "BadMD5Conversion";
        }
    }

    private void storeCredentials(String email, String password) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", password);
        editor.clear();
        editor.apply();
    }

    private List<String> retrieveCredentials() {
        List<String> r = new ArrayList<>();
        try {
            r.add(shared.getString("EMAIL", null));
            r.add(shared.getString("PASSWORD", null));
        } catch (Exception ignored) {
        }
        return r;
    }

    private boolean isEmail(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return m.matches();
    }
}

