package com.example.rma20celosmanovicselma04.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionChecker {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
