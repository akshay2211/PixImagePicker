package com.fxn.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.fxn.interfaces.OnSelectionListner;
import com.fxn.interfaces.SectionIndexer;
import com.fxn.modals.Img;
import com.fxn.pix.R;
import com.fxn.utility.HeaderItemDecoration;
import com.fxn.utility.Utility;

import java.util.ArrayList;

/**
 * Created by akshay on 17/03/18.
 */

public class MainImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements HeaderItemDecoration.StickyHeaderInterface, SectionIndexer {
    public static final int HEADER = 1;
    public static final int ITEM = 2;
    public static int spanCount = 3;
    Context context;
    ArrayList<Img> list;
    OnSelectionListner onSelectionListner;

    int margin = 2;
    float size = ((Utility.WIDTH / spanCount));

    FrameLayout.LayoutParams layoutParams;

    public MainImageAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        layoutParams = new FrameLayout.LayoutParams((int) size, (int) size);
        layoutParams.setMargins(margin, margin, margin, margin);

    }

    public ArrayList<Img> getItemList() {
        return list;
    }

    public MainImageAdapter addImage(Img image) {
        list.add(image);
        notifyDataSetChanged();
        return this;
    }

    public void AddOnSelectionListner(OnSelectionListner onSelectionListner) {
        this.onSelectionListner = onSelectionListner;
    }

    public MainImageAdapter addImageList(ArrayList<Img> imagelist) {
        list.addAll(imagelist);
        notifyDataSetChanged();
        return this;
    }

    public void ClearList() {
        list.clear();
    }

    public void Select(boolean selection, int pos) {
        list.get(pos).setSelected(selection);
        notifyItemChanged(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.header_row, parent, false));
        } else {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.main_image, parent, false);
            return new Holder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Img i = list.get(position);
        if (holder instanceof Holder) {
            Holder h = (Holder) holder;

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(i.getContentUrl()))
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(130, 130))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
            h.sdv.setController(controller);
            h.selection.setVisibility(i.getSelected() ? View.VISIBLE : View.GONE);
        } else if (holder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(i.getHeaderDate());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Img i = list.get(position);
        return (i.getContentUrl().equalsIgnoreCase("")) ?
                HEADER : ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        // Log.e("itemPosition", " ---- " + itemPosition);
        return headerPosition;
       /*
        pos = () ? itemPosition : pos;
        Log.e("itemPosition", " ---- " + itemPosition + "  pos  - " + pos);
        return pos;*/
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        //  Log.e("headerPosition", " ---- " + headerPosition);
        return R.layout.header_row;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        Img i = list.get(headerPosition);
        ((TextView) header.findViewById(R.id.header)).setText(i.getHeaderDate());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItemViewType(itemPosition) == 1;
    }

    @Override
    public String getSectionText(int position) {
        Img i = list.get(position);
        return "" + i.getHeaderDate();
    }

    public String getSectionMonthYearText(int position) {
        Img i = list.get(position);
        return "" + i.getScrollerDate();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        SimpleDraweeView sdv;
        ImageView selection;

        public Holder(View itemView) {
            super(itemView);
            sdv = itemView.findViewById(R.id.sdv);
            selection = itemView.findViewById(R.id.selection);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            sdv.setLayoutParams(layoutParams);
        }

        @Override
        public void onClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListner.OnClick(list.get(id), view, id);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = this.getLayoutPosition();
            onSelectionListner.OnLongClick(list.get(id), view, id);
            return true;
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
        }
    }
}
