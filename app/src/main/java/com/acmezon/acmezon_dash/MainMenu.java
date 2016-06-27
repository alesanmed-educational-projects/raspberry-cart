package com.acmezon.acmezon_dash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.goka.blurredgridmenu.GridMenu;
import com.goka.blurredgridmenu.GridMenuFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {
    private GridMenuFragment mGridMenuFragment;
    private final String FILENAME = "shopping_cart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mGridMenuFragment = GridMenuFragment.newInstance(R.drawable.back);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, mGridMenuFragment);
        tx.addToBackStack(null);
        tx.commit();

        setupGridMenu();

        mGridMenuFragment.setOnClickMenuListener(new GridMenuFragment.OnClickMenuListener() {
            @Override
            public void onClickMenu(GridMenu gridMenu, int position) {
                switch (position) {
                    case 0: //
                        Intent bluetoothScreen = new Intent(MainMenu.this, BluetoothScreen.class);
                        startActivity(bluetoothScreen);
                        break;
                    case 1: //Shopping cart
                        File shoppingCartFile = getBaseContext().getFileStreamPath(FILENAME);
                        Log.d("SHOPPINGCART", "Exists: " + shoppingCartFile.exists());

                        if (((Application) getApplication()).getConnection() == null
                                && !shoppingCartFile.exists()){
                            Toast.makeText(getApplicationContext(),
                                            getString(R.string.bluetooth_needed),
                                            Toast.LENGTH_LONG).show();
                        } else {
                            Intent shoppingCart = new Intent(MainMenu.this, ShoppingCart.class);
                            startActivity(shoppingCart);
                        }
                        break;
                    case 2: //Settings
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
                                "Archivo de carrito creado",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 3: //About us
                        Intent activityAboutUs = new Intent(MainMenu.this, AboutUs.class);
                        startActivity(activityAboutUs);
                        break;
                    case 4: //Help
                        break;
                    case 5: //Exit
                        android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setupGridMenu() {
        List<GridMenu> menus = new ArrayList<>();
        menus.add(new GridMenu(getString(R.string.connect_dash), R.drawable.get_cart));
        menus.add(new GridMenu(getString(R.string.get_last_cart), R.drawable.basket));
        menus.add(new GridMenu(getString(R.string.settings), R.drawable.settings));
        menus.add(new GridMenu(getString(R.string.about), R.drawable.info));
        menus.add(new GridMenu(getString(R.string.help), R.drawable.help));
        menus.add(new GridMenu(getString(R.string.exit), R.drawable.exit));

        mGridMenuFragment.setupMenu(menus);
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
