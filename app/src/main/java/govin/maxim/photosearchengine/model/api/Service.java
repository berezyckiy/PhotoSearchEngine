package govin.maxim.photosearchengine.model.api;

import govin.maxim.photosearchengine.model.PhotosResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service implements FlickrService {

    private static final String BASE_URL = "https://api.flickr.com/";

    private FlickrApi mFlickCalls;

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

    private FlickrApi buildFlickrService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FlickrApi.class);
    }
}
