package govin.maxim.photosearchengine.presenter;

import govin.maxim.photosearchengine.base.BasePresenter;
import govin.maxim.photosearchengine.base.MainContract;
import govin.maxim.photosearchengine.model.PhotosResponse;
import govin.maxim.photosearchengine.model.api.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    @Override
    public void getRecent(int pageSize, int pageCount) {
        Service service = new Service();

        service.getRecent(pageSize, pageCount).enqueue(new Callback<PhotosResponse>() {
            @Override
            public void onResponse(Call<PhotosResponse> call, Response<PhotosResponse> response) {
                if (response.body().getStat().equals(getStatusOk())) {
                    getView().showPhotos(response.body());
                } else {
                    getView().showMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<PhotosResponse> call, Throwable t) {
                getView().showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void searchPhotos(int pageSize, int pageCount, String query) {
        Service service = new Service();

        service.searchPhotos(pageSize, pageCount, query).enqueue(new Callback<PhotosResponse>() {
            @Override
            public void onResponse(Call<PhotosResponse> call, Response<PhotosResponse> response) {
                if (response.body().getStat().equals(getStatusOk())) {
                    getView().showPhotos(response.body());
                } else {
                    getView().showMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<PhotosResponse> call, Throwable t) {
                getView().showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
    }
}
