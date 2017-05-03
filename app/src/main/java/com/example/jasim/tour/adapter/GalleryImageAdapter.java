package com.example.jasim.tour.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import com.example.jasim.tour.R;
import com.example.jasim.tour.databinding.CustomGalleryItemBinding;


public class GalleryImageAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> picture = new ArrayList<>();
    SetBitmap setBitmap;
    ArrayList<String> selectedImage = new ArrayList<>();

    public GalleryImageAdapter(Context context, ArrayList<String> picture) {
        super(context, R.layout.custom_gallery_item, picture);
        this.context = context;
        this.picture = picture;
    }

    @Override
    public int getCount() {
        return picture.size();
    }

    @Override
    public String getItem(int position) {
        return picture.get(position);
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final CustomGalleryItemBinding binding;
        if (view == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.custom_gallery_item, parent, false);
        } else {
            binding = DataBindingUtil.bind(view);
        }
        String imagePath = picture.get(position);
        setBitmap = new SetBitmap(binding.imageView, context);
        setBitmap.execute(imagePath);
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.checkedImage.isChecked()) {
                    binding.checkedImage.setChecked(false);
                    selectedImage.remove(picture.get(position));
//                    binding.checkedImage.setVisibility(View.GONE);
                } else {
                    binding.checkedImage.setChecked(true);
                    binding.checkedImage.setVisibility(View.VISIBLE);
                    selectedImage.add(picture.get(position));
                }
            }
        });

        return binding.getRoot();
    }

    public ArrayList<String> getSelectedImage() {
        return selectedImage;
    }
}
