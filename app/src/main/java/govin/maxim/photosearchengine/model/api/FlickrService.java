package govin.maxim.photosearchengine.model.api;

import govin.maxim.photosearchengine.model.PhotosResponse;
import retrofit2.Call;

public interface FlickrService {

    Call<PhotosResponse> getRecent(int pageSize, int pageCount);

    Call<PhotosResponse> searchPhotos(int pageSize, int pageCount, String query);
}
