package com.acmezon.acmezon_dash;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import java.io.FileOutputStream;
import java.util.Map;

public class OptionsActivity extends AppCompatActivity {
    private final String FILENAME = "shopping_cart";
    private final String ACTION_CLOSE = "com.acmezon.acmezon_dash.ACTION_CLOSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initListeners();

        IntentFilter filter = new IntentFilter(ACTION_CLOSE);
        OptionsReceiver optionsReceiver = new OptionsReceiver();
        registerReceiver(optionsReceiver, filter);
    }

    private void initListeners() {
        LinearLayout createFile = (LinearLayout) findViewById(R.id.create_file);
        assert createFile != null;

        createFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream outputStream;
                String stringProducts = "{\"products\": {\"dream\": 1}, \"barcodes\": {\"0049325377060\": 1}}";

                try {
                    outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    outputStream.write(stringProducts.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),
                        getString(R.string.cart_file_created),
                        Toast.LENGTH_LONG).show();
            }
        });

        LinearLayout pastCarts = (LinearLayout) findViewById(R.id.past_carts);
        assert  pastCarts != null;

        pastCarts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Application) getApplication()).getConnection() == null) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.bluetooth_needed),
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent pastCarts = new Intent(OptionsActivity.this, PastCartsActivity.class);
                    startActivity(pastCarts);
                }
            }
        });

        LinearLayout storeAccount = (LinearLayout) findViewById(R.id.store_account);
        assert  storeAccount != null;

        storeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(OptionsActivity.this);
                alert.setTitle(getString(R.string.remove_account));
                alert.setMessage(getString(R.string.remove_account_subtitle));
                alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog loading = ProgressDialog.show(OptionsActivity.this, "",
                                getResources().getString(R.string.removing_account), true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.show();
                            }
                        });
                        SharedPreferences prefs = new SecurePreferences(getApplicationContext());

                        SharedPreferences.Editor preferencesEditor = prefs.edit();
                        preferencesEditor.remove("EMAIL");
                        preferencesEditor.remove("PASSWORD");
                        boolean committed = preferencesEditor.commit();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                            }
                        });
                        if(committed) {
                            Toast.makeText(getApplicationContext(),
                                    String.format("%s", getString(R.string.account_removed)),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    String.format("%s", getString(R.string.account_removed_false)),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class OptionsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_CLOSE)) {
                OptionsActivity.this.finish();
            }
        }
    }
}
