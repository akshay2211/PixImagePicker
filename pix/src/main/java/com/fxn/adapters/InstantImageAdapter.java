package com.fxn.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.interfaces.OnSelectionListener;
import com.fxn.modals.Img;
import com.fxn.pix.R;
import com.fxn.utility.Utility;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by akshay on 17/03/18.
 */

public class InstantImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Img> list;
    private OnSelectionListener onSelectionListener;
    private RequestManager glide;
    private RequestOptions options;
    private float size;
private int margin = 3;
    private int padding;

    public InstantImageAdapter(Context context) {
        this.list = new ArrayList<>();
        size = Utility.convertDpToPixel(72, context) - 2;
        padding = (int) (size / 3.5);
        glide = Glide.with(context);
        options = new RequestOptions().override(256).transform(new CenterCrop()).transform(new FitCenter());
    }

    public void addOnSelectionListener(OnSelectionListener onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    public InstantImageAdapter addImage(Img image) {
        list.add(image);
        notifyDataSetChanged();
        return this;
    }

    public ArrayList<Img> getItemList() {
        return list;
    }

    public void addImageList(ArrayList<Img> images) {
        list.addAll(images);
        notifyDataSetChanged();
    }

    public void clearList() {
        list.clear();
    }

    public void select(boolean selection, int pos) {
        if (pos < 100) {
            list.get(pos).setSelected(selection);
            notifyItemChanged(pos);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MainImageAdapter.HEADER) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.inital_image, parent, false);
            return new HolderNone(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.inital_image, parent, false);
            return new Holder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Img image = list.get(position);
        return (image.getContentUrl().isEmpty()) ? MainImageAdapter.HEADER : MainImageAdapter.ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Img image = list.get(position);
        if (holder instanceof Holder) {
            Holder imageHolder = (Holder) holder;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) size, (int) size);
            if (position == 0) {
                layoutParams.setMargins(-(margin / 2), margin, margin, margin);
            } else {
                layoutParams.setMargins(margin, margin, margin, margin);
            }
            imageHolder.itemView.setLayoutParams(layoutParams);
            imageHolder.selection.setPadding(padding, padding, padding, padding);
            imageHolder.preview.setLayoutParams(layoutParams);
            if (image.getMedia_type() == 1) {
                glide.load(image.getContentUrl()).apply(options).into(imageHolder.preview);
                imageHolder.isVideo.setVisibility(View.GONE);
            } else if (image.getMedia_type() == 3) {
                glide.asBitmap()
                        .load(Uri.fromFile(new File(image.getUrl())))
                        .apply(options)
                        .into(imageHolder.preview);
                imageHolder.isVideo.setVisibility(View.VISIBLE);
            }
            imageHolder.selection.setVisibility(image.getSelected() ? View.VISIBLE : View.GONE);
        } else {
            HolderNone noneHolder = (HolderNone) holder;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);
            noneHolder.itemView.setLayoutParams(layoutParams);
            noneHolder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView preview;
        private ImageView selection;
        private ImageView isVideo;


        Holder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            selection = itemView.findViewById(R.id.selection);
            isVideo = itemView.findViewById(R.id.isVideo);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.onClick(list.get(id), view, id);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.onLongClick(list.get(id), view, id);
            return true;
        }
    }

    public class HolderNone extends RecyclerView.ViewHolder {
        HolderNone(View itemView) {
            super(itemView);
        }
    }
}
