package com.acmezon.acmezon_dash.bluetooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acmezon.acmezon_dash.R;

import java.util.List;

/*
 * Created by alesanmed on 5/06/16.
 */
public class DeviceItemListAdapter extends ArrayAdapter<DeviceItem>{
    public DeviceItemListAdapter(Context context, int resource, List<DeviceItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.device_list_layout, parent, false);
        }

        DeviceItem device = getItem(position);

        if (device != null) {
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView mac = (TextView) v.findViewById(R.id.mac);

            if (name != null) {
                name.setText(device.getDeviceName());
            }

            if (mac != null) {
                mac.setText(device.getAddress());
            }
        }

        return v;
    }
}
