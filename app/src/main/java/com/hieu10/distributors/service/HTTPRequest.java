package com.hieu10.distributors.service;

import static com.hieu10.distributors.service.APIServices.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTTPRequest {
    private APIServices requestInterface;

    public HTTPRequest() {
        requestInterface = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(APIServices.class);
    }

    public APIServices callAPI() {
        return requestInterface;
    }
}