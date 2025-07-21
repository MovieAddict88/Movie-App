package com.cinecraze;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AdBlocker {

    private static final String TAG = "AdBlocker";
    private static final String AD_HOSTS_FILE = "easylist.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();
    private static final Set<String> VIDEO_WHITELIST = new HashSet<>(Arrays.asList(
        "vidsrc.net", "vidjoy.pro", "vidcloud.pro", "mycloud.blue", "playhydrax.com"
    ));

    public static void init(Context context) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(AD_HOSTS_FILE)));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("||") && line.endsWith("^")) {
                        String host = line.substring(2, line.length() - 1);
                        // Skip if host is in video whitelist
                        if (!VIDEO_WHITELIST.contains(host)) {
                            AD_HOSTS.add(host);
                        }
                    }
                }
                reader.close();
                Log.d(TAG, "AdBlocker initialized with " + AD_HOSTS.size() + " hosts.");
            } catch (IOException e) {
                Log.e(TAG, "Error reading ad hosts file", e);
            }
        }).start();
    }

    public static boolean isAd(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String host = Uri.parse(url).getHost();
        if (host == null) {
            return false;
        }
        
        // Always allow video sources
        if (VIDEO_WHITELIST.contains(host)) {
            return false;
        }
        
        // Check host and parent domains
        while (host.contains(".")) {
            if (AD_HOSTS.contains(host)) {
                return true;
            }
            host = host.substring(host.indexOf('.') + 1);
        }
        return false;
    }
}