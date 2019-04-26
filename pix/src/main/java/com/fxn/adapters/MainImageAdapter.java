package com.fxn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.interfaces.OnSelectionListener;
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
    public static final int SPAN_COUNT = 3;
    private static final int MARGIN = 2;

    private ArrayList<Img> list;
    private OnSelectionListener onSelectionListener;
    private FrameLayout.LayoutParams layoutParams;
    private RequestManager glide;
    private RequestOptions options;

    public MainImageAdapter(Context context) {
        this.list = new ArrayList<>();
        int size = Utility.WIDTH / SPAN_COUNT;
        layoutParams = new FrameLayout.LayoutParams(size, size);
        layoutParams.setMargins(MARGIN, MARGIN - 1, MARGIN, MARGIN - 1);
        options = new RequestOptions().override(360).transform(new CenterCrop()).transform(new FitCenter());
        glide = Glide.with(context);
    }

    public ArrayList<Img> getItemList() {
        return list;
    }

    public MainImageAdapter addImage(Img image) {
        list.add(image);
        notifyDataSetChanged();
        return this;
    }

    public void addOnSelectionListener(OnSelectionListener onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    public void addImageList(ArrayList<Img> images) {
        list.addAll(images);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Img i = list.get(position);
        return (i.getContentUrl().equalsIgnoreCase("")) ?
                HEADER : ITEM;
    }

    public void clearList() {
        list.clear();
    }

    public void select(boolean selection, int pos) {
        list.get(pos).setSelected(selection);
        notifyItemChanged(pos);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getContentUrl().hashCode();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Img image = list.get(position);
        if (holder instanceof Holder) {
            Holder imageHolder = (Holder) holder;
            glide.load(image.getContentUrl()).apply(options).into(imageHolder.preview);
            imageHolder.selection.setVisibility(image.getSelected() ? View.VISIBLE : View.GONE);
            imageHolder.previouslySelected.setVisibility(image.getPreviouslySelected() && ! image.getSelected() ? View.VISIBLE : View.GONE);
        } else if (holder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(image.getHeaderDate());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getHeaderPositionForItem(int position) {
        int itemPosition = position;
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.header_row;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        Img image = list.get(headerPosition);
        ((TextView) header.findViewById(R.id.header)).setText(image.getHeaderDate());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItemViewType(itemPosition) == 1;
    }

    @Override
    public String getSectionText(int position) {
        return list.get(position).getHeaderDate();
    }

    public String getSectionMonthYearText(int position) {
        return list.get(position).getScrollerDate();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView preview;
        private ImageView selection;
        private ImageView previouslySelected;

        Holder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            selection = itemView.findViewById(R.id.selection);
            previouslySelected = itemView.findViewById(R.id.previouslySelected);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            preview.setLayoutParams(layoutParams);
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

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView header;

        HeaderHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
        }
    }
}
