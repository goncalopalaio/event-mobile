package com.gplio.event_mobile;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by goncalopalaio on 01/05/18.
 */

public class Utils {

    public static String safeGetText(TextView view) {
        CharSequence text = view.getText();
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return text.toString();
    }
}
