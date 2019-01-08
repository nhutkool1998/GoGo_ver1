package cs486.nmnhut.gogo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "GalleryRecyclerViewAdap";

    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

    public GalleryRecyclerViewAdapter(Context mContext, ArrayList<String> mImages) {
        this.mImages = mImages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // onBindViewHolder nay dung de do du lieu vao cai view cua minh
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    // cai ham nay dung de tao ra 1 cai layout mini
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

    void add(String imageName) {
        mImages.add(imageName);
        notifyItemInserted(mImages.size() - 1);
    }

    void remove(String imageName) {
        int place = mImages.indexOf(imageName);
        mImages.remove(place);
        notifyItemRemoved(place);
    }

    void update(String newFileName, String fileName) {
        int place = mImages.indexOf(fileName);
        mImages.set(place, newFileName);
        notifyItemChanged(place);
    }

}
