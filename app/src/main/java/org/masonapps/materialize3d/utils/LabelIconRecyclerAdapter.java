package org.masonapps.materialize3d.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.masonapps.materialize3d.R;

import java.util.ArrayList;

/**
 * Created by Bob on 6/20/2015.
 */
public class LabelIconRecyclerAdapter extends RecyclerView.Adapter<LabelIconRecyclerAdapter.ViewHolder> {

    private final ArrayList<String> labelList;
    private final ArrayList<Integer> resourceIDs;
    private OnRecyclerItemClickedListener listener;
    private int selectedItem = -1;
    private Context context;

    public LabelIconRecyclerAdapter(Context context, ArrayList<String> labelList, ArrayList<Integer> resourceIDs) {
        if(labelList.size() != resourceIDs.size()) throw new IllegalArgumentException("labels and resourceIDs must be same length");
        this.context = context;
        this.labelList = labelList;
        this.resourceIDs = resourceIDs;
    }

    @Override
    public LabelIconRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.icon_label_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LabelIconRecyclerAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    public void clearSelectedItem() {
        this.selectedItem = -1;
    }

    public void setListener(OnRecyclerItemClickedListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.label_text);
            imageView = (ImageView) itemView.findViewById(R.id.icon_imageview);
        }

        public void bind(int position) {
            itemView.setBackgroundColor(position == selectedItem ? context.getResources().getColor(R.color.accent) : Color.TRANSPARENT);
            textView.setText(labelList.get(position));
            imageView.setImageResource(resourceIDs.get(position));
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            setSelectedItem(position);
            if(listener != null) listener.onItemClicked(labelList.get(position));
        }
    }
    
    public interface OnRecyclerItemClickedListener{
        void onItemClicked(String label);
    }
}
