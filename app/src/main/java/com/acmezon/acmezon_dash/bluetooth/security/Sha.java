package com.acmezon.acmezon_dash.bluetooth.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Created by alesanmed on 27/06/2016.
 */
public class Sha {
    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
