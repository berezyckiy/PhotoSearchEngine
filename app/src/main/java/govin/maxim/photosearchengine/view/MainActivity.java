package govin.maxim.photosearchengine.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import govin.maxim.photosearchengine.R;
import govin.maxim.photosearchengine.base.MainContract;
import govin.maxim.photosearchengine.model.Photo;
import govin.maxim.photosearchengine.model.PhotosResponse;
import govin.maxim.photosearchengine.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity
        implements MainContract.View, PhotosListFragment.OnPhotosListFragmentListener, GoogleMapsFragment.OnGoogleMapsFragmentListener {

    private final static String TAG_PHOTOS_TAB = "PhotosTab";

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initTabLayout();
        initViewPager();

        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (String.valueOf(mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).getTag()).equals(TAG_PHOTOS_TAB)) {
            menu.setGroupVisible(0, true);
        } else {
            menu.setGroupVisible(0, false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem actionSearch = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(actionSearch);

        searchView.setInputType(InputType.TYPE_NULL);
        searchView.setQueryHint(getString(R.string.action_search_hint));
        searchView.setOnQueryTextListener(new OnSearchQueryListener());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPhotos(PhotosResponse photosResponse) {
        PhotosListFragment photosList = getPhotosListFragmentInstance();
        GoogleMapsFragment googleMaps = getGoogleMapsFragmentInstance();
        if (photosList != null && googleMaps != null) {
            photosList.showPhotos(photosResponse.getPhotos().getPhoto(), photosResponse.getPhotos().getPage());
            googleMaps.setCurrentlyPhotosList(photosList.getCurrentlyPhotos());
        }
    }

    @Override
    public void getRecent(int pageSize, int pageCount) {
        mPresenter.getRecent(pageSize, pageCount);
    }

    @Override
    public void searchPhotos(int pageSize, int pageCount, String query) {
        mPresenter.searchPhotos(pageSize, pageCount, query);
    }

    @Override
    public void setTabTitle(int titleResId) {
        mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).setText(titleResId);
        getGoogleMapsFragmentInstance().clearMarkers();
    }

    @Override
    public void showFullPhoto(Photo photo) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        FullPhotoDialog fullPhotoDialog = FullPhotoDialog.newInstance(photo.getUrlC());
        fullPhotoDialog.show(fragmentTransaction, "Dialog");
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.support_toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initTabLayout() {
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setTag(TAG_PHOTOS_TAB).setText(R.string.recent_tab_title));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_map));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addOnTabSelectedListener(new TabSelectedListener());
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.view_pager);
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    private PhotosListFragment getPhotosListFragmentInstance() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof PhotosListFragment) {
                return (PhotosListFragment) fragment;
            }
        }
        return null;
    }

    private GoogleMapsFragment getGoogleMapsFragmentInstance() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof GoogleMapsFragment) {
                return (GoogleMapsFragment) fragment;
            }
        }
        return null;
    }

    private class TabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
            invalidateOptionsMenu();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        private static final int PHOTOS_LIST_TAB = 0;
        private static final int GOOGLE_MAPS_TAB = 1;

        private int mTabsCount;

        PagerAdapter(FragmentManager fragmentManager, int tabsCount) {
            super(fragmentManager);
            mTabsCount = tabsCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PHOTOS_LIST_TAB:
                    return new PhotosListFragment();
                case GOOGLE_MAPS_TAB:
                    return new GoogleMapsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mTabsCount;
        }
    }

    private class OnSearchQueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            PhotosListFragment fragment = getPhotosListFragmentInstance();
            if (fragment != null) {
                fragment.onQuerySubmit(query);
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }
}
