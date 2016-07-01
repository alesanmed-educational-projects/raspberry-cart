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

public class ProductUtils {
    private static List<JSONObject> getProducts(Context context, List<JSONObject> barcodes, List<JSONObject> names) {
        List<JSONObject> res = new ArrayList<>();
        JSONObject aux_product;
        for (JSONObject p : barcodes) {
            try {
                aux_product = httpProductQuery(context.getString(R.string.domain).concat(
                        String.format("/api/product/barcode/%s", p.getString("barcode"))), p.getInt("quantity"));

                if(aux_product == null) {
                    res = new ArrayList<>();
                    break;
                } else {
                    res.add(aux_product);
                }
            } catch (JSONException e) {
                res = new ArrayList<>();
                break;
            }
        }

        for (JSONObject p: names) {
            try {
                aux_product = httpProductQuery(context.getString(R.string.domain).concat(
                        String.format("/api/product/text/search/%s", p.getString("name"))), p.getInt("quantity"));

                Log.d("SHOPPINGCART", "Null: " + (aux_product == null));
                if(aux_product == null) {
                    res = new ArrayList<>();
                    break;
                } else {
                    res.add(aux_product);
                }
            } catch (JSONException e) {
                res = new ArrayList<>();
                break;
            }
        }
        return res;
    }

    private static JSONObject httpProductQuery (String urlString, int quantity) {
        JSONObject result = new JSONObject();
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d("SHOPPINGCART", e.getMessage());
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
                JSONObject product = obj.getJSONObject("product");
                if(obj.get("product") != null) {
                    result.put("_id", product.getInt("_id"));
                    result.put("quantity", quantity);
                    result.put("name", product.getString("name"));
                    result.put("image", product.getString("image"));
                    result.put("available", obj.getBoolean("available"));
                }
            } else {
                Log.d("SHOPPINGCART", "Response: "+response);
            }
        } catch (IOException e) {
            Log.d("SHOPPINGCART", e.getMessage());
            return null;
        } catch (JSONException e) {
            Log.d("SHOPPINGCART", e.getMessage());
            return null;
        }

        return result;

    }

    public static List<JSONObject> receiveProducts(Context context, final String stringProducts) {
        List<JSONObject> completedProducts = null;
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
