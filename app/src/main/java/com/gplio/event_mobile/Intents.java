package com.gplio.event_mobile;

import android.content.Context;
import android.content.Intent;

import com.gplio.event_mobile.models.Event;

/**
 * Created by goncalopalaio on 01/05/18.
 */

public class Intents {
    public static String EXTRA_DETAILS = "gplio.events.intent.extra.details";

    public static void openDetails(Context context, Event event) {
        Intent intent = new Intent();
        intent.setClass(context, DetailsActivity.class);
        intent.putExtra(EXTRA_DETAILS, event);
        context.startActivity(intent);
    }
}
