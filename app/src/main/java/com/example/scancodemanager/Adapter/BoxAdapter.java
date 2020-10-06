package com.example.scancodemanager.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scancodemanager.R;
import com.example.scancodemanager.TableActivity;

import java.util.List;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder>{

    private List<String> boxNameList;
    private Context context;
    private Activity activity;
    private int type;

    public BoxAdapter(List<String> boxNameList,Context context,Activity activity,int type) {
        this.boxNameList = boxNameList;
        this.context = context;
        this.activity = activity;
        this.type = type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView boxImage;
        TextView boxName;
        View boxView;

        public ViewHolder( View itemView) {
           super(itemView);
           boxView = itemView;
           boxImage = itemView.findViewById(R.id.iv_box_image);
           boxName = itemView.findViewById(R.id.tv_box_name);
           boxName.setSelected(true);
       }
   }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.box_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoxAdapter.ViewHolder viewHolder, int position) {
        String boxName = boxNameList.get(position);
        viewHolder.boxImage.setImageResource(R.drawable.image_box);
        viewHolder.boxName.setText(boxName);
        viewHolder.boxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TableActivity.class);
                intent.putExtra("name",boxNameList.get(position));
                intent.putExtra("type",type);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boxNameList.size();
    }
}
