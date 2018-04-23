package govin.maxim.photosearchengine.model.api;

import govin.maxim.photosearchengine.model.PhotosResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {

    @GET("services/rest/?method=flickr.photos.getRecent&api_key=d787eed8b71d6af8e4cd273cf63a731c&format=json&nojsoncallback=1&extras=geo, url_n, url_c")
    Call<PhotosResponse> getRecent(@Query("per_page") int pageSize, @Query("page") int pageCount);

    @GET("services/rest/?method=flickr.photos.search&api_key=d787eed8b71d6af8e4cd273cf63a731c&format=json&nojsoncallback=1&extras=geo, url_n, url_c")
    Call<PhotosResponse> getFoundPhotos(@Query("per_page") int pageSize, @Query("page") int pageCount, @Query("text") String keyWord);
}
