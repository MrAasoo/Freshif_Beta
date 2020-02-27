package com.aasoo.freshifybeta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.Dashboard;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private Context mContext;
    private List<Dashboard> mList;

    public DashboardAdapter(Context mContext, List<Dashboard> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dashboard_data_list, parent, false);
        return new DashboardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapter.ViewHolder holder, int position) {
                Dashboard dashboard = mList.get(position);
                if(dashboard!=null){
                    holder.dash_sr_no.setText(dashboard.getDash_sr_no());
                    holder.dash_type.setText(dashboard.getDash_type());
                    holder.dash_detail.setText(dashboard.getDash_detail());
                    holder.dash_date.setText(dashboard.getDash_date());
                    holder.dash_amount.setText(dashboard.getDash_amount());
                    holder.dash_due_amount.setText(dashboard.getDash_due_amount());
                    holder.dash_remark.setText(dashboard.getDash_remark());
                }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dash_sr_no;
        private TextView dash_date;
        private TextView dash_detail;
        private TextView dash_due_amount;
        private TextView dash_remark;
        private TextView dash_type;
        private TextView dash_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dash_sr_no = itemView.findViewById(R.id.dash_sr_no);
            dash_date = itemView.findViewById(R.id.dash_date);
            dash_detail = itemView.findViewById(R.id.dash_detail);
            dash_due_amount = itemView.findViewById(R.id.dash_amount_due);
            dash_remark = itemView.findViewById(R.id.dash_remark);
            dash_type = itemView.findViewById(R.id.dash_type);
            dash_amount = itemView.findViewById(R.id.dash_amount);
        }
    }
}
