package com.acmezon.acmezon_dash;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class LoginActivity extends AppCompatActivity {
    Button submit_btn,cancel_btn;
    EditText email_view,password_view;
    SharedPreferences shared;
    String products_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submit_btn=(Button)findViewById(R.id.submit_btn);
        cancel_btn=(Button)findViewById(R.id.cancel_btn);
        email_view=(EditText)findViewById(R.id.input_email);
        password_view=(EditText)findViewById(R.id.input_password);

        Bundle b = getIntent().getExtras();
        products_json = b.getString("shopping_cart");

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
                    if (android.os.Build.VERSION.SDK_INT > 15) {
                        email_view.setBackground(oldBackground);
                        password_view.setBackground(oldBackground);
                    } else {
                        //noinspection deprecation
                        email_view.setBackgroundDrawable(oldBackground);
                        //noinspection deprecation
                        password_view.setBackgroundDrawable(oldBackground);
                    }
                    launch_post_query(email, password, products_json);
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

    public boolean launch_post_query(String email, String password, String products) {
        storeCredentials(email, MD5(password));
        new LoginAndSubmitCartTask().execute(email, password, products);
        return true;
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

    private class LoginAndSubmitCartTask extends AsyncTask<String, Void, Boolean> {
        private String message;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;

            Looper.prepare();
            String email = params[0];
            String password = params[1];
            String products = params[2];

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.domain).concat("/api/raspberry/cartlines/save"));

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", MD5(password)));
                nameValuePairs.add(new BasicNameValuePair("products", products));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);
                Log.d("SUBMIT", response.getStatusLine().toString());

                if (response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=399) {
                    // 200
                    result = true;
                } else {
                    // 500
                    String json_string = EntityUtils.toString(response.getEntity());
                    JSONObject response_body = new JSONObject(json_string);
                    this.message = response_body.getString("message");
                    result = false;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                File dir = getFilesDir();
                String FILENAME = "shopping_cart";
                File shoppingCartFile = new File(dir, FILENAME);
                boolean deleted = shoppingCartFile.delete();
                if(!deleted) {
                    Toast.makeText(getApplicationContext(),
                            R.string.delete_cart_error,
                            Toast.LENGTH_LONG).show();
                }
                finish();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.submit_success),
                        Toast.LENGTH_LONG).show();
            } else {
                Log.d("SUBMIT", message);
                String message_toast;
                switch (message) {
                    case "Internal Server Error":
                        message_toast = getString(R.string.error_server);
                        finish();
                        break;
                    case "Some product not available":
                        message_toast = getString(R.string.error_not_available_product);
                        finish();
                        break;
                    case "Authentication failed":
                        message_toast = getString(R.string.error_authentication);
                        break;
                    default:
                        message_toast = getString(R.string.error_server);
                        finish();
                        break;
                }
                Toast.makeText(getApplicationContext(),
                        message_toast,
                        Toast.LENGTH_LONG).show();
            }
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

