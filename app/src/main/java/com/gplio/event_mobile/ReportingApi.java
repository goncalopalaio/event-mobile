package com.gplio.event_mobile;

import com.gplio.event_mobile.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by goncalopalaio on 29/04/18.
 */

class ReportingApi {
    private static EventService service;

    static EventService getEventInstance() {
        if (service == null) {
            service = build();
        }
        return service;
    }

    private ReportingApi() {
    }

    private static EventService build() {
        Retrofit retrofit = new Retrofit.Builder()
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
