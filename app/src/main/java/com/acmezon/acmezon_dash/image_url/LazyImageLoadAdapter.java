package com.acmezon.acmezon_dash.image_url;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmezon.acmezon_dash.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LazyImageLoadAdapter extends BaseAdapter implements DialogInterface.OnClickListener {

    private Activity activity;
    private static LayoutInflater inflater = null;
    private List<JSONObject> products;
    public ImageLoader imageLoader;
    public Activity mainActivity;

    public LazyImageLoadAdapter(Activity a, JSONObject[] products, Activity mainActivity) {
        activity = a;
        this.products = new ArrayList<JSONObject>(Arrays.asList(products));
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        ImageView image;
        TextView quantity;
        Button add,sub;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public JSONObject getItem(int pos) {
        return products.get(pos);
    }

    public JSONObject remove(int pos) {
        return products.remove(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if(convertView == null) {

            vi = inflater.inflate(R.layout.product_row, null);

            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.product_name);
            holder.image = (ImageView) vi.findViewById(R.id.product_image);
            holder.quantity = (TextView) vi.findViewById(R.id.product_quantity);
            holder.add = (Button) vi.findViewById(R.id.btn_add);
            holder.sub = (Button) vi.findViewById(R.id.btn_substract);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        final JSONObject product = getItem(pos);
        JSONObject productObj = null;
        int quantity;

        try {
            quantity = product.getInt("quantity");
            productObj = new JSONObject(product.getString("product"));
            holder.name.setText(productObj.getString("name"));
            holder.quantity.setText(String.valueOf(quantity));
            holder.add.setTag(pos);
            holder.sub.setTag(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView image = holder.image;
        try {
            assert productObj != null;
            imageLoader.displayImage(activity.getApplicationContext().getString(
                    R.string.domain)
                    .concat("/img/")
                    .concat(productObj.getString("image")), image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adds 1 to the counter
                try {
                    int quantity = product.getInt("quantity");
                    product.put("quantity", quantity + 1);
                    holder.quantity.setText(String.valueOf(product.getInt("quantity")));
                    System.out.println("Aumentó en 1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adds 1 to the counter
                try {
                    int quantity = product.getInt("quantity");
                    if (quantity>1) {
                        product.put("quantity", quantity - 1);
                        holder.quantity.setText(String.valueOf(product.getInt("quantity")));
                        System.out.println("Disminuyo en 1");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        mainActivity);
                alert.setTitle(mainActivity.getResources().getString(R.string.delete_title));
                alert.setMessage(mainActivity.getResources().getString(R.string.delete_subtitle) + " " +
                        holder.name.getText() + "?");
                alert.setPositiveButton(mainActivity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(pos);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton(mainActivity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return false;
            }
        });


        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        mainActivity);
                alert.setTitle(mainActivity.getResources().getString(R.string.delete_title));
                alert.setMessage(mainActivity.getResources().getString(R.string.delete_subtitle) + " " +
                    holder.name.getText() + "?");
                alert.setPositiveButton(mainActivity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(pos);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton(mainActivity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return false;
            }
        });

            return vi;
    }

}