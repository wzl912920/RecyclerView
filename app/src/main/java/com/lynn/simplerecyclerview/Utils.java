package com.lynn.simplerecyclerview;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by Lynn.
 */

public class Utils {
    public static int getScreenHeight() {
        return ((WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = BaseApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = BaseApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
