package com.gplio.event_mobile;

import android.content.Context;

import com.gplio.event_mobile.models.Event;

import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by goncalopalaio on 29/04/18.
 */

class ReportingApi {
    private static EventService service;

    static EventService getEventInstance(Context context) {
        if (service == null) {
            service = build(context);
        }
        return service;
    }

    private ReportingApi() {
    }

    private static EventService build(Context context) {
        int cacheSize = 50 * 1024 * 1024; // 50 MB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://events-rep.herokuapp.com/v1/")
                .build();
        return retrofit.create(EventService.class);
    }

    public interface EventService {
        @GET("events/")
        Call<List<Event>> listAllEvents();
    }
}
