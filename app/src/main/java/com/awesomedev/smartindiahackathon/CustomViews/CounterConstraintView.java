package com.awesomedev.smartindiahackathon.CustomViews;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.awesomedev.smartindiahackathon.R;
import com.awesomedev.smartindiahackathon.RecyclerViewAdapter;

public class CounterConstraintView extends ConstraintLayout{

    private Context context = null;
    private RecyclerViewAdapter recyclerViewAdapter = null;

    private RecyclerView queueRecyclerView = null;
    private TextView tvThroughput = null;

    private int count = 0;

    public void setCount ( int count ){
        this.count = count;
        this.recyclerViewAdapter.setCount(count);
    }

    public int getCount ( ) {
        return this.count;
    }

    public void setAverageWaitingTime ( float throughput ){
        float avgWT = throughput * this.count;
        this.tvThroughput.setText(String.format("%.2fs" , avgWT));
    }

    public void addElements ( int num ){
        this.count += num;

        recyclerViewAdapter.setCount(this.count);
        recyclerViewAdapter.notifyItemRangeInserted(this.count - num , num);

    }

    public void removeElements ( int num ){
        if ( num > count ){
            return;
        }

        this.count = this.count - num;
        recyclerViewAdapter.setCount(this.count);
        recyclerViewAdapter.notifyItemRangeRemoved(0, num);

    }

    public CounterConstraintView(Context context) {
        super(context);
        initViews(context);
    }

    public CounterConstraintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CounterConstraintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }


    private void initViews ( Context context ){
        this.context = context;
        // inflate compound view
        inflate(context, R.layout.queue_layout, this);

        recyclerViewAdapter = new RecyclerViewAdapter(context);
        recyclerViewAdapter.setCount(this.count);

        queueRecyclerView = this.findViewById(R.id.rv_queue);
        queueRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        queueRecyclerView.setAdapter(recyclerViewAdapter);

        tvThroughput = this.findViewById(R.id.tv_throughput);
    }
}
