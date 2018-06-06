package govin.maxim.photosearchengine.base;

import govin.maxim.photosearchengine.model.PhotosResponse;

public interface MainContract {

    interface View extends MvpView {

        void showPhotos(PhotosResponse photosResponse);
    }

    interface Presenter extends MvpPresenter<View> {

        void getRecent(int pageSize, int pageCount);

        void searchPhotos(int pageSize, int pageCount, String query);
    }
}
