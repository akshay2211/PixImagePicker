package com.fxn.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.fxn.interfaces.OnSelectionListener;
import com.fxn.modals.Img;
import com.fxn.pix.R;
import com.fxn.utility.Utility;

import java.util.ArrayList;

/**
 * Created by akshay on 17/03/18.
 */

public class InstantImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Img> list;
    private OnSelectionListener onSelectionListener;

    public InstantImageAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
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


    public InstantImageAdapter addImageList(ArrayList<Img> images) {
        list.addAll(images);
        notifyDataSetChanged();
        return this;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        return (image.getContentUrl().equalsIgnoreCase("")) ?
                MainImageAdapter.HEADER : MainImageAdapter.ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        Img image = list.get(position);
        if (h instanceof Holder) {
            Holder holder = (Holder) h;
            int margin = 2;
            float size = ((Utility.convertDpToPixel(72, context)) - (2));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) size, (int) size);
            layoutParams.setMargins(margin, margin, margin, margin);
            holder.itemView.setLayoutParams(layoutParams);
            int padding = (int) (size / 3.5);
            holder.selection.setPadding(padding, padding, padding, padding);
            holder.sdv.setLayoutParams(layoutParams);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(image.getContentUrl()))
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.sdv.getController())
                    .build();
            holder.sdv.setController(controller);
            holder.selection.setVisibility(image.getSelected() ? View.VISIBLE : View.GONE);
        } else {
            HolderNone hn = (HolderNone) h;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);
            hn.itemView.setLayoutParams(layoutParams);
            hn.itemView.setVisibility(View.GONE);
        }
        // Log.e("myurl", "file://" + (new File(i.getUrlPath())).getAbsolutePath());
        //holder.sdv.setImageURI(Uri.parse(i.getUrlPath()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        SimpleDraweeView sdv;
        ImageView selection;

        Holder(View itemView) {
            super(itemView);
            sdv = itemView.findViewById(R.id.sdv);
            selection = itemView.findViewById(R.id.selection);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.OnClick(list.get(id), view, id);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListener.OnLongClick(list.get(id), view, id);
            return true;
        }
    }

    public class HolderNone extends RecyclerView.ViewHolder {
        HolderNone(View itemView) {
            super(itemView);
        }
    }
}
