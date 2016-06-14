package com.acmezon.acmezon_dash;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.goka.blurredgridmenu.GridMenu;
import com.goka.blurredgridmenu.GridMenuFragment;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {
    private GridMenuFragment mGridMenuFragment;

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
                        Intent shoppingCart = new Intent(MainMenu.this, ShoppingCart.class);
                        startActivity(shoppingCart);
                        break;
                    case 2: //Settings
                        break;
                    case 3: //About us
                        break;
                    case 4: //Help
                        break;
                    case 5: //Exit
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        break;
                    default:
                        break;
                }
                Toast.makeText(getApplicationContext(), "Title:" + gridMenu.getTitle() + ", Position:" + position,
                        Toast.LENGTH_SHORT).show();
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
        if (0 == getSupportFragmentManager().getBackStackEntryCount()) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
