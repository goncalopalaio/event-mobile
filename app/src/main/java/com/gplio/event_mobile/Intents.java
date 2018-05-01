package com.gplio.event_mobile;

import android.content.Context;
import android.content.Intent;

import com.gplio.event_mobile.activities.DetailsActivity;
import com.gplio.event_mobile.models.Event;

/**
 * Created by goncalopalaio on 01/05/18.
 */

public class Intents {
    public static String EXTRA_DETAILS = "gplio.events.intent.extra.details";
    public static String EXTRA_DETAILS_LAST_LAT= "gplio.events.intent.extra.details.last.lat";
    public static String EXTRA_DETAILS_LAST_LON= "gplio.events.intent.extra.details.last.lon";

    public static void openDetails(Context context, Event event) {
        Intent intent = new Intent();
        intent.setClass(context, DetailsActivity.class);
        intent.putExtra(EXTRA_DETAILS, event);
        context.startActivity(intent);
    }

    public static void openNew(Context context, double lat, double lon) {
        Intent intent = new Intent();
        intent.setClass(context, DetailsActivity.class);
        intent.putExtra(EXTRA_DETAILS_LAST_LAT, lat);
        intent.putExtra(EXTRA_DETAILS_LAST_LON, lon);

        context.startActivity(intent);
    }

    public static void openNew(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DetailsActivity.class);
        context.startActivity(intent);
    }
}
