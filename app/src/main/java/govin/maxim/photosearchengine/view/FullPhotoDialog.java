package govin.maxim.photosearchengine.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import govin.maxim.photosearchengine.R;

public class FullPhotoDialog extends DialogFragment {

    private String mUrl;

    public static FullPhotoDialog newInstance(String url) {
        FullPhotoDialog fullPhotoDialog = new FullPhotoDialog();
        Bundle args = new Bundle();
        args.putString("url", url);
        fullPhotoDialog.setArguments(args);
        return fullPhotoDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getContext(), R.layout.fragment_full_photo_dialog, null);
        builder.setView(view);
        ImageView imageView = view.findViewById(R.id.image_full_photo);
        TextView textView;

        Glide.with(getActivity())
                .load(mUrl)
                .thumbnail(Glide.with(getActivity()).load(R.mipmap.ic_placeholder_loading))
                .into(imageView);

        return builder.create();
    }
}
