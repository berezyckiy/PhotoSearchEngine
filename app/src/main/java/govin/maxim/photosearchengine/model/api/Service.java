package govin.maxim.photosearchengine.model.api;

import android.support.annotation.NonNull;

import govin.maxim.photosearchengine.model.PhotosResponse;
import govin.maxim.photosearchengine.model.directions.DirectionResponse;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service
        implements FlickrService,
        GoogleDistanceService {

    private static final String FLICKR_BASE_URL = "https://api.flickr.com/";
    private static final String GOOGLE_BASE_URL = "https://maps.googleapis.com/";

    private FlickrApi mFlickCalls;
    private GoogleDistanceApi mGoogleMapsApi;

    @Override
    public Call<PhotosResponse> getRecent(int pageSize, int pageCount) {
        if (mFlickCalls == null) {
            mFlickCalls = buildFlickrService();
        }
        return mFlickCalls.getRecent(pageSize, pageCount);
    }

    @Override
    public Call<PhotosResponse> searchPhotos(int pageSize, int pageCount, String query) {
        if (mFlickCalls == null) {
            mFlickCalls = buildFlickrService();
        }

        return mFlickCalls.getFoundPhotos(pageSize, pageCount, query);
    }

    @Override
    public Call<DistanceMatrix> getDistanceMatrix(@NonNull String origins, @NonNull String destinations) {
        if (mGoogleMapsApi == null) {
            mGoogleMapsApi = buildGoogleMapsApi();
        }
        return mGoogleMapsApi.getDistanceMatrix(origins, destinations);
    }

    @Override
    public Call<DirectionResponse> getDirections(@NonNull String origin, @NonNull String destination) {
        if (mGoogleMapsApi == null) {
            mGoogleMapsApi = buildGoogleMapsApi();
        }
        return mGoogleMapsApi.getDirections(origin, destination);
    }

    private FlickrApi buildFlickrService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FLICKR_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FlickrApi.class);
    }

    private GoogleDistanceApi buildGoogleMapsApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GoogleDistanceApi.class);
    }
}
