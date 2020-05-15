package com.witype.kotlindemo.mvp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import butterknife.BindView
import butterknife.ButterKnife
import com.witype.kotlindemo.R
import com.witype.kotlindemo.entity.RecordsBean

/**
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
class QuarterAdapter() : RecyclerView.Adapter<QuarterAdapter.DataUsageHolder>() {

    var recordsBeans: MutableList<RecordsBean>

    init {
        recordsBeans = ArrayList();
    }

    fun setData(recordsBeans: MutableList<RecordsBean>) {
        this.recordsBeans = recordsBeans
        notifyDataSetChanged()
    }

    fun addData(recordsBeans: List<RecordsBean>?) {
        this.recordsBeans.addAll(recordsBeans ?: ArrayList());
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataUsageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quarter, parent, false)
        return DataUsageHolder(view)
    }

    override fun onBindViewHolder(holder: DataUsageHolder, position: Int) {
        val recordsBean = getItem(position)
        val context = holder.content.context
        if (recordsBean != null) {
            holder.year.text = recordsBean.quarterYearNum.toString()
            holder.quarterTotal.text = String.format(context.getString(R.string.label_tip_total_of_year), recordsBean.total_of_year.toFloat().toString())
            holder.quarter.text = String.format("Q%s", recordsBean.quarterQuaterNum)
            holder.relative.text = String.format(context.getString(R.string.label_increase), recordsBean.volume_offset.toFloat().toString())
            holder.usage.text = recordsBean.volume_of_mobile_data.toString()
            holder.view.visibility = if (recordsBean.volume_offset < 0) View.VISIBLE else View.INVISIBLE
            holder.content.visibility = if (isFirstQuarterOfYear(recordsBean.quarterQuaterNum)) View.VISIBLE else View.GONE
            if (recordsBean.volume_offset < 0) {
                holder.relative.setTextColor(ContextCompat.getColor(context, R.color.data_usage_down))
            } else {
                holder.relative.setTextColor(ContextCompat.getColor(context, R.color.data_usage_up))
            }
        }
    }

    override fun getItemCount(): Int {
        return recordsBeans.size
    }

    fun getItem(position: Int): RecordsBean? {
        return if (recordsBeans.size < position) null else recordsBeans[position]
    }

    private fun isFirstQuarterOfYear(recordsBean: Int): Boolean {
        return recordsBean == 1
    }

    class DataUsageHolder(itemView: View) : ViewHolder(itemView) {
        @BindView(R.id.year_content)
        lateinit var content: LinearLayout;

        @BindView(R.id.year)
        lateinit var year: TextView

        @BindView(R.id.total_of_year)
        lateinit var quarterTotal: TextView

        @BindView(R.id.quarter)
        lateinit var quarter: TextView

        @BindView(R.id.usage)
        lateinit var usage: TextView

        @BindView(R.id.relative)
        lateinit var relative: TextView

        @BindView(R.id.view)
        lateinit var view: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

}