package com.nonodo;

import android.content.Context;
import android.content.SharedPreferences;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * One anonymous ping the first time this library runs on a robot. Later autons and
 * teleops do not ping again. Set {@link #enabled} to {@code false} before constructing
 * {@code NODOChassis} (or calling Blocks {@code initializeMecanumDrive} / {@code initializeTankDrive}) to opt out.
 */
public final class UsageTracker {

    public static volatile boolean enabled = true;

    private static final String ENDPOINT =
            "https://us-central1-nodo-usage-tracker.cloudfunctions.net/trackUsage";
    private static final String LIBRARY = "NODO";
    private static final String VERSION = "1.0.0-beta.1";
    private static final String PREFS_NAME = "com.nonodo.usage";
    private static final String PREF_PINGED = "install_ping_sent";
    private static final AtomicBoolean SENT_THIS_SESSION = new AtomicBoolean(false);

    private UsageTracker() {
    }

    /**
     * Fire-and-forget. Safe to call from every constructor. Persists a flag on the
     * Control Hub so only the first init after adding the library sends a request.
     */
    public static void ping(HardwareMap hardwareMap) {
        if (!enabled || hardwareMap == null || !SENT_THIS_SESSION.compareAndSet(false, true)) {
            return;
        }

        Context context = hardwareMap.appContext;
        if (context == null) {
            SENT_THIS_SESSION.set(false);
            return;
        }

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            if (prefs.getBoolean(PREF_PINGED, false)) {
                return;
            }
            prefs.edit().putBoolean(PREF_PINGED, true).apply();
        } catch (Exception ignored) {
            return;
        }

        Thread pingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendQuietly();
            }
        }, "non-odo-usage");
        pingThread.setDaemon(true);
        pingThread.start();
    }

    private static void sendQuietly() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ENDPOINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "text/plain");

            byte[] body = ("{\"library\":\"" + LIBRARY + "\",\"version\":\"" + VERSION + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(body.length);
            OutputStream output = connection.getOutputStream();
            try {
                output.write(body);
            } finally {
                output.close();
            }
            connection.getResponseCode();
        } catch (Exception ignored) {
            // No network, DNS, or HTTP failure must never affect the robot.
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
