package com.deeptimay.yuluvenues.networkCalls;

import com.deeptimay.yuluvenues.pojos.FoursquareJSON;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoursquareService {

    // A request to snap the current user to a place via the Foursquare API.
    @GET("venues/search?v=20161101&limit=100")
    Call<FoursquareJSON> snapToPlace(@Query("client_id") String clientID,
                                     @Query("client_secret") String clientSecret,
                                     @Query("ll") String ll,
                                     @Query("llAcc") double llAcc);

}