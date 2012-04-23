package com.android.server.status.widget;

import com.android.internal.R;
import com.android.server.status.widget.PowerButton;
import com.android.server.status.widget.StateTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.provider.Settings;

public class WifiButton extends PowerButton{

    Context mContext;

    static WifiButton ownButton = null;

    private static final StateTracker sWifiState = new WifiStateTracker();

    public void setupButton(Context context, int position) {
        currentPosition = position;
    }

    /**
     * Subclass of StateTracker to get/set Wifi state.
     */
    private static final class WifiStateTracker extends StateTracker {
        @Override
        public int getActualState(Context context) {
            WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                return wifiStateToFiveState(wifiManager.getWifiState());
            }
            return STATE_UNKNOWN;
        }

        @Override
        protected void requestStateChange(Context context,
                final boolean desiredState) {
            final WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                Log.d("WifiButton", "No wifiManager.");
                return;
            }

            // Actually request the wifi change and persistent
            // settings write off the UI thread, as it can take a
            // user-noticeable amount of time, especially if there's
            // disk contention.
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... args) {
                    /**
                     * Disable tethering if enabling Wifi
                     */
                    int wifiApState = wifiManager.getWifiApState();
                    if (desiredState
                            && ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) || (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED))) {
                        wifiManager.setWifiApEnabled(null, false);
                    }

                    wifiManager.setWifiEnabled(desiredState);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onActualStateChange(Context context, Intent intent) {
            if (!WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent
                    .getAction())) {
                return;
            }
            int wifiState = intent
                .getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            int widgetState=wifiStateToFiveState(wifiState);
            setCurrentState(context, widgetState);
        }

        /**
         * Converts WifiManager's state values into our Wifi/Bluetooth-common
         * state values.
         */
        private static int wifiStateToFiveState(int wifiState) {
            switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                return STATE_DISABLED;
            case WifiManager.WIFI_STATE_ENABLED:
                return STATE_ENABLED;
            case WifiManager.WIFI_STATE_DISABLING:
                return STATE_TURNING_OFF;
            case WifiManager.WIFI_STATE_ENABLING:
                return STATE_TURNING_ON;
            default:
                return STATE_UNKNOWN;
            }
        }
    }



    public void updateState(Context context) {
        mContext = context;
        boolean useCustomExp = Settings.System.getInt(mContext.getContentResolver(),
        Settings.System.NOTIF_EXPANDED_BAR_CUSTOM, 0) == 1;

        currentState = sWifiState.getTriState(context);
        switch (currentState) {
            case STATE_DISABLED:
                if (useCustomExp) {
                    currentIcon = com.android.internal.R.drawable.stat_wifi_off_cust;
                } else {
                    currentIcon = com.android.internal.R.drawable.stat_wifi_off;
                }
                break;
            case STATE_ENABLED:
                if (useCustomExp) {
                    currentIcon = com.android.internal.R.drawable.stat_wifi_on_cust;
                } else {
                    currentIcon = com.android.internal.R.drawable.stat_wifi_on;
                }
                break;
            case STATE_INTERMEDIATE:
                // In the transitional state, the bottom green bar
                // shows the tri-state (on, off, transitioning), but
                // the top dark-gray-or-bright-white logo shows the
                // user's intent. This is much easier to see in
                // sunlight.
                if (sWifiState.isTurningOn()) {
                    if (useCustomExp) {
                        currentIcon = com.android.internal.R.drawable.stat_wifi_on_cust;
                    } else {
                        currentIcon = com.android.internal.R.drawable.stat_wifi_on;
                    }
                } else {
                    if (useCustomExp) {
                        currentIcon = com.android.internal.R.drawable.stat_wifi_off_cust;
                    } else {
                        currentIcon = com.android.internal.R.drawable.stat_wifi_off;
                    }
                }
                break;
        }
    }

    public void onReceive(Context context, Intent intent) {
        sWifiState.onActualStateChange(context, intent);
    }

    public void toggleState(Context context) {
        int realstate = sWifiState.getActualState(context);
        sWifiState.toggleState(context);
    }


    public static WifiButton getInstance() {
        if (ownButton == null) {
            ownButton = new WifiButton();
        }

        return ownButton;
    }

    @Override
    void initButton(int position) {
    }

    @Override
    protected boolean handleLongClick() {
        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mView.getContext().startActivity(intent);
        return true;
    }

    public void toggleState(Context context, int newState) {
        int curState = sWifiState.getTriState(context);
        if (curState != STATE_INTERMEDIATE &&
                curState != newState) {
            toggleState(context);
        }
    }
}
