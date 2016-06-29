package com.acmezon.acmezon_dash;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileOutputStream;

public class OptionsActivity extends AppCompatActivity {
    private final String FILENAME = "shopping_cart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initListeners();
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
