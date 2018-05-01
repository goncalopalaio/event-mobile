package com.gplio.event_mobile;

import android.content.Context;

import com.gplio.event_mobile.models.Category;
import com.gplio.event_mobile.models.Event;

import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by goncalopalaio on 29/04/18.
 */

public class ReportingApi {
    private static EventService service;

    public static EventService getEventInstance(Context context) {
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
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(httpLoggingInterceptor)
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

        @GET("categories/")
        Call<List<Category>> listAllCategories();

        @POST("events/")
        Call<Event> createEvent(@Body Event event);
    }
}
