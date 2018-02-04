package com.kenjin.shareloc.Helper;


import com.kenjin.shareloc.model.Result;
import com.kenjin.shareloc.model.mLokasi;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Server on 18/08/2017.
 */

public interface RestAPI {

    @GET("GetLokasi")
    Call<Result> getLocation(@QueryMap Map<String, String> params);

    @POST("UploadLocation")
    Call<Result> sendMarking(@Body mLokasi alumni);

}
