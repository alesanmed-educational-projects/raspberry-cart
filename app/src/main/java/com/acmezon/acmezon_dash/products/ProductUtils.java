package com.acmezon.acmezon_dash.products;

import android.content.Context;
import android.util.Log;

import com.acmezon.acmezon_dash.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by alesanmed on 27/06/2016.
 */
public class ProductUtils {
    private static JSONObject[] getProducts(Context context, List<JSONObject> barcodes, List<JSONObject> names) {
        List<JSONObject> res = new ArrayList<>();
        for (JSONObject p : barcodes) {
            URL url = null;
            try {
                url = new URL(context.getString(R.string.domain).concat(
                        String.format("/api/product/barcode/%s", p.getString("barcode"))));
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection connection;
            try {
                assert url != null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Expirar a los 10 segundos si la conexión no se establece
                connection.setConnectTimeout(10000);
                // Esperar solo 15 segundos para que finalice la lectura
                connection.setReadTimeout(15000);
                connection.connect();

                int response = connection.getResponseCode();
                BufferedReader br;
                if (response >= 200 && response <=399) {
                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = br.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject obj = new JSONObject(responseStrBuilder.toString());
                    if(!obj.get("product").equals(null)) {
                        obj.put("quantity", p.getInt("quantity"));
                        res.add(obj);
                    }
                } else {
                    //TODO: Completar o eliminar el else
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        for (JSONObject p: names) {
            URL url = null;
            try {
                url = new URL(context.getString(R.string.domain).concat(
                        String.format("/api/product/text/search/%s", p.getString("name"))));
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection connection;
            try {
                assert url != null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Expirar a los 10 segundos si la conexión no se establece
                connection.setConnectTimeout(10000);
                // Esperar solo 15 segundos para que finalice la lectura
                connection.setReadTimeout(15000);
                connection.connect();

                int response = connection.getResponseCode();
                BufferedReader br;
                if (response >= 200 && response <=399) {
                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = br.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject obj = new JSONObject(responseStrBuilder.toString());
                    if (!obj.get("product").equals(null)) {
                        obj.put("quantity", p.getInt("quantity"));
                        res.add(obj);
                    }
                } else {
                    //TODO: Completar o eliminar el else
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res.toArray(new JSONObject[res.size()]);
    }

    public static JSONObject[] receiveProducts(Context context, final String stringProducts) {
        JSONObject[] completedProducts = null;
        try {
            JSONObject productsJSON = new JSONObject(stringProducts);

            JSONObject names = null;
            try {
                names = ((JSONObject) productsJSON.get("products"));
            } catch (JSONException e) {
                Log.d("SHOPPINGCART", e.getMessage());
            }

            JSONObject barcodes = null;
            try {
                barcodes = ((JSONObject) productsJSON.get("barcodes"));
            } catch (JSONException e) {
                Log.d("SHOPPINGCART", e.getMessage());
            }

            List<JSONObject> namesArray = new ArrayList<>();
            if (names != null) {
                Iterator<String> namesKeys = names.keys();
                while (namesKeys.hasNext()) {
                    String key = namesKeys.next();
                    JSONObject tmp = new JSONObject();
                    tmp.put("name", key);
                    tmp.put("quantity", names.get(key));
                    namesArray.add(tmp);
                }
            }

            List<JSONObject> barcodesArray = new ArrayList<>();
            if (barcodes != null) {
                Iterator<String> barcodesKeys = barcodes.keys();
                while (barcodesKeys.hasNext()) {
                    String key = barcodesKeys.next();
                    JSONObject tmp = new JSONObject();
                    tmp.put("barcode", key);
                    tmp.put("quantity", barcodes.get(key));
                    barcodesArray.add(tmp);
                }
            }

            completedProducts = getProducts(context, barcodesArray, namesArray);
        }catch (JSONException e) {
            Log.d("SHOPPINGCART", e.getMessage());
        }

        return completedProducts;
    }
}
