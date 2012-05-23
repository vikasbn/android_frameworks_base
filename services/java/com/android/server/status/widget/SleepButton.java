package com.android.server.status.widget;

import com.android.internal.R;
import com.android.server.status.widget.PowerButton;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public class SleepButton extends PowerButton {

    Context mContext;

    static SleepButton ownButton;

    public void updateState(Context context) {

        mContext = context;
        boolean useCustomExp = Settings.System.getInt(mContext.getContentResolver(),
        Settings.System.NOTIF_EXPANDED_BAR_CUSTOM, 0) == 1;

        if (useCustomExp) {
            currentIcon = com.android.internal.R.drawable.stat_sleep_cust;
        } else {
            currentIcon = com.android.internal.R.drawable.stat_sleep;
        }
        currentState = STATE_DISABLED;
    }

    public void toggleState(Context context) {
        PowerManager mPowerManager = (PowerManager)context.getSystemService(
                          Context.POWER_SERVICE);
        mPowerManager.goToSleep(SystemClock.uptimeMillis() + 1);
    }

    @Override
    public boolean handleLongClick(View mView) {
        Intent intent = new Intent("android.settings.DISPLAY_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mView.getContext().startActivity(intent);
        return true;
    }

    public static SleepButton getInstance() {
        if (ownButton==null) ownButton = new SleepButton();

        return ownButton;
    }

    @Override
    void initButton(int position) {
    }

}
