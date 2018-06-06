package govin.maxim.photosearchengine.model.api;

import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleDistanceApi {

    @GET("maps/api/distancematrix/json?key=AIzaSyDpRJ4z3X8BLXjlEjaPqD6HnOgXag1NrVc")
    Call<DistanceMatrix> getDistanceMatrix(@Query("origins") String origins, @Query("destinations") String destinations);
}
