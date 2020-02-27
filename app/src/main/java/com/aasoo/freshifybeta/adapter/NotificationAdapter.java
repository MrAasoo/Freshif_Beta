package com.aasoo.freshifybeta.adapter;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.MyNotification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<MyNotification> mNotifications;

    public NotificationAdapter(Context mContext, List<MyNotification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyNotification notification = mNotifications.get(position);
        if(notification!=null){
            holder.notification_msg.setText(notification.getNotification_msg());
            holder.notification_title.setText(notification.getNotification_title());
            holder.notification_date.setText(notification.getNotification_date());
        }

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView notification_title,notification_msg,notification_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_msg = itemView.findViewById(R.id.notification_msg);
            notification_title = itemView.findViewById(R.id.notification_title);
            notification_date = itemView.findViewById(R.id.notification_date);
        }
    }
}
