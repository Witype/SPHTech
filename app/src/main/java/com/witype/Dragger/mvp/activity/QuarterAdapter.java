package com.witype.Dragger.mvp.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witype.Dragger.R;
import com.witype.Dragger.entity.RecordsBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
public class QuarterAdapter extends RecyclerView.Adapter<QuarterAdapter.DataUsageHolder> {

    public static final int TYPE_NORMAL = View.GONE , TYPE_WITH_TITLE = View.VISIBLE;

    private List<RecordsBean> recordsBeans;

    public QuarterAdapter() {
    }

    public QuarterAdapter(List<RecordsBean> recordsBeans) {
        this.recordsBeans = recordsBeans;
    }

    public void setData(List<RecordsBean> recordsBeans) {
        this.recordsBeans = recordsBeans;
        notifyDataSetChanged();
    }

    public void addData(List<RecordsBean> recordsBeans) {
       if (this.recordsBeans == null) {
           this.recordsBeans = new ArrayList<>();
       }
       this.recordsBeans.addAll(recordsBeans);
    }

    @NonNull
    @Override
    public DataUsageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quarter, parent, false);
        return new DataUsageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataUsageHolder holder, int position) {
        RecordsBean recordsBean = getItem(position);
        Context context = holder.content.getContext();
        if (recordsBean != null) {
            holder.year.setText(String.valueOf(recordsBean.getQuarterYearNum()));
            holder.quarterTotal.setText(String.format(context.getString(R.string.label_tip_total_of_year),String.valueOf(recordsBean.getTotal_of_year())));
            holder.quarter.setText(String.format("Q%s",recordsBean.getQuarterQuaterNum()));
            holder.relative.setText(String.format(context.getString(R.string.label_increase),String.valueOf(recordsBean.getVolume_offset())));
            holder.usage.setText(String.valueOf(recordsBean.getVolume_of_mobile_data()));
            holder.view.setVisibility(recordsBean.getVolume_offset() < 0 ? View.VISIBLE : View.INVISIBLE);
            holder.content.setVisibility(isFirstQuarterOfYear(recordsBean.getQuarterQuaterNum()) ? View.VISIBLE : View.GONE);
            if (recordsBean.getVolume_offset() < 0) {
                holder.relative.setTextColor(ContextCompat.getColor(context,R.color.data_usage_down));
            } else {
                holder.relative.setTextColor(ContextCompat.getColor(context,R.color.data_usage_up));
            }
        }
    }

    @Override
    public int getItemCount() {
        return recordsBeans == null ? 0 : recordsBeans.size();
    }

    public RecordsBean getItem(int position) {
        return recordsBeans == null || recordsBeans.size() < position ? null : recordsBeans.get(position);
    }

    private boolean isFirstQuarterOfYear(int recordsBean) {
        return recordsBean == 1;
    }

    public static class DataUsageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.year_content)
        LinearLayout content;
        @BindView(R.id.year)
        TextView year;
        @BindView(R.id.total_of_year)
        TextView quarterTotal;
        @BindView(R.id.quarter)
        TextView quarter;
        @BindView(R.id.usage)
        TextView usage;
        @BindView(R.id.relative)
        TextView relative;
        @BindView(R.id.view)
        ImageView view;

        public DataUsageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
