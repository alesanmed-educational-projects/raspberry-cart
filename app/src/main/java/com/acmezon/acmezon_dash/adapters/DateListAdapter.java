package com.acmezon.acmezon_dash.adapters;/*
 * Created by alesanmed on 29/06/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acmezon.acmezon_dash.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateListAdapter extends ArrayAdapter<Date>{
    SimpleDateFormat dateFormat;
    public DateListAdapter(Context context, int resource, List<Date> objects) {
        super(context, resource, objects);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy-H:mm", Locale.ENGLISH);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.shopping_cart_row, null);
        }

        Date cart = getItem(position);

        if (cart != null) {
            TextView name = (TextView) v.findViewById(R.id.cart_date);

            if (name != null) {
                name.setText(dateFormat.format(cart));
            }
        }

        return v;
    }
}
