package govin.maxim.photosearchengine.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import govin.maxim.photosearchengine.R;
import govin.maxim.photosearchengine.model.Photo;

public class RecyclerPhotosAdapter extends RecyclerView.Adapter<RecyclerPhotosAdapter.ViewHolder> {

    private List<Photo> mPhotosList = new ArrayList<>();
    private RequestManager mGlide;
    private int mLayoutId;
    private OnPhotoClickListener mListener;

    public RecyclerPhotosAdapter(RequestManager glide, int layoutId, OnPhotoClickListener listener) {
        mGlide = glide;
        mLayoutId = layoutId;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateView(parent), mListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mPhotosList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotosList.size();
    }

    public void setPhotosList(List<Photo> photosList) {
        if (photosList != null) {
            mPhotosList.addAll(photosList);
            notifyDataSetChanged();
        }
    }

    public void clearPhotosList() {
        mPhotosList.clear();
        notifyDataSetChanged();
    }

    public List<Photo> getPhotosList() {
        return new ArrayList<>(mPhotosList);
    }

    private View inflateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private OnPhotoClickListener mListener;
        private CardView mCardView;
        private ImageView mImagePhoto;
        private TextView mTextTitle;

        public ViewHolder(View view, OnPhotoClickListener listener) {
            super(view);
            mListener = listener;

            mCardView = view.findViewById(R.id.card_view);
            mImagePhoto = view.findViewById(R.id.image_photo);
            mTextTitle = view.findViewById(R.id.text_photo_image);
        }

        void bind(final Photo photo) {
            mTextTitle.setText(photo.getTitle());
            mGlide.load(photo.getUrlN())
                    .thumbnail(mGlide.load(R.mipmap.ic_placeholder_loading))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            int width = resource.getMinimumWidth();
                            int height = resource.getMinimumHeight();
                            mCardView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                            mImagePhoto.setImageDrawable(resource);
                        }
                    });

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPhotoClick(photo);
                }
            });
        }
    }
}
