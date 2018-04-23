package govin.maxim.photosearchengine.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import govin.maxim.photosearchengine.R;
import govin.maxim.photosearchengine.model.Photo;

public class PhotosListFragment extends Fragment {

    private static final int COLUMN_COUNT = 3;
    private static final int PER_PAGE_COUNT = 60;

    //pagination variables
    private static final int START_PAGE = 1;
    private int mLastPageCount;
    private boolean mIsLastPage = false;
    private boolean mIsLoading = false;
    private String mQuery;

    private OnPhotosListFragmentListener mListener;
    private RecyclerView mRecycler;
    private RecyclerPhotosAdapter mAdapter;
    private View mRootView;
    private GridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerRecentPhotoScrollingListener mRecentScrollingListener;
    private RecyclerFoundPhotoScrollingListener mFoundScrollingListener;

    public interface OnPhotosListFragmentListener {

        void getRecent(int pageSize, int pageCount);

        void searchPhotos(int pageSize, int pageCount, String query);

        void setTabTitle(int titleResId);

        void showFullPhoto(Photo photo);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_photos_list, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecycler();
        initSwipeRefreshLayout();

        loadRecentFirstPage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotosListFragmentListener) {
            mListener = (OnPhotosListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhotosListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefresh = mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(new OnRefreshPhotosListener());
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void initRecycler() {
        mRecycler = mRootView.findViewById(R.id.recycler_photos);
        mLayoutManager = new GridLayoutManager(getContext(), COLUMN_COUNT);
        mAdapter = new RecyclerPhotosAdapter(Glide.with(this), R.layout.view_holder_photo_item, new OnPhotoClickListener());
        mRecentScrollingListener = new RecyclerRecentPhotoScrollingListener();
        mFoundScrollingListener = new RecyclerFoundPhotoScrollingListener();
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(mRecentScrollingListener);
    }

    public void showPhotos(List<Photo> photosList, int lastPageCount) {
        if (photosList.size() == 0) {
            mIsLastPage = true;
        }
        mLastPageCount = lastPageCount;
        mAdapter.setPhotosList(photosList);
        mSwipeRefresh.setRefreshing(false);
        mIsLoading = false;
    }

    public List<Photo> getCurrentlyPhotos() {
        return mAdapter.getPhotosList();
    }

    public void onQuerySubmit(String query) {
        mQuery = query;
        mLastPageCount = 1;

        mRecycler.removeOnScrollListener(mRecentScrollingListener);
        mRecycler.addOnScrollListener(mFoundScrollingListener);

        mIsLastPage = false;
        mAdapter.clearPhotosList();
        mSwipeRefresh.setRefreshing(true);
        searchPhotos(mLastPageCount, query);
        mListener.setTabTitle(R.string.found_tab_title);
    }

    private void loadRecentFirstPage() {
        mSwipeRefresh.setRefreshing(true);
        mAdapter.clearPhotosList();
        mListener.getRecent(PER_PAGE_COUNT, START_PAGE);
    }

    private void loadRecentNextPage(int pageCount) {
        mSwipeRefresh.setRefreshing(true);
        mListener.getRecent(PER_PAGE_COUNT, pageCount);
    }

    private void searchPhotos(int pageCount, String query) {
        mSwipeRefresh.setRefreshing(true);
        mListener.searchPhotos(PER_PAGE_COUNT, pageCount, query);
    }

    private class OnPhotoClickListener implements govin.maxim.photosearchengine.view.OnPhotoClickListener {

        @Override
        public void onPhotoClick(Photo photo) {
            mListener.showFullPhoto(photo);
        }
    }

    private class OnRefreshPhotosListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            mRecycler.removeOnScrollListener(mFoundScrollingListener);
            mRecycler.addOnScrollListener(mRecentScrollingListener);

            mSwipeRefresh.setRefreshing(true);
            mAdapter.clearPhotosList();
            mIsLastPage = false;

            mListener.getRecent(PER_PAGE_COUNT, START_PAGE);
            mListener.setTabTitle(R.string.recent_tab_title);
        }
    }

    private class RecyclerRecentPhotoScrollingListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mIsLoading && !mIsLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {

                    mIsLoading = true;
                    loadRecentNextPage(++mLastPageCount);
                }
            }
        }
    }

    private class RecyclerFoundPhotoScrollingListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mIsLoading && !mIsLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {

                    mIsLoading = true;
                    searchPhotos(++mLastPageCount, mQuery);
                }
            }
        }
    }
}
