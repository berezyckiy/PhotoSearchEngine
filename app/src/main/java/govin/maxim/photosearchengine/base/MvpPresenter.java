package govin.maxim.photosearchengine.base;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V view);

    void detachView();

    V getView();
}
