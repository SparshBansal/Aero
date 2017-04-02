package com.awesomedev.smartindiahackathon.CustomViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.awesomedev.smartindiahackathon.Data.DatabaseContract;
import com.awesomedev.smartindiahackathon.Models.CounterPerson;
import com.awesomedev.smartindiahackathon.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.*;

/**
 * Created by sparsh on 4/2/17.
 */

public class CounterViews extends View {

    List<CounterPerson> mList = new ArrayList<>();
    Paint paint = null;

    public CounterViews(Context context) {
        this(context,null,0,0);
    }

    public CounterViews(Context context, AttributeSet attrs) {
        this(context,attrs,0,0);
    }

    public CounterViews(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,defStyleAttr,0);
    }

    public CounterViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize(){

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(32);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void addCounter(CounterPerson counterPerson){
        mList.add(counterPerson);
        invalidate();
    }

    public void setPersons(int counterNumber , int num_persons){
        for (CounterPerson counterPerson : mList){
            if (counterPerson.getCounterNumber() == counterNumber){
                counterPerson.setNum_persons(num_persons);
            }
        }
        invalidate();
    }

    public void setData(Cursor cursor){
        mList.clear();

        while(cursor.moveToNext()){

            int counterNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_NUMBER)));
            int counterCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_COUNT)));
            float throughput = Float.parseFloat(cursor.getString(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_THROUGHPUT)));
            float avg_waiting_time = Float.parseFloat(cursor.getString(cursor.getColumnIndex(CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME)));

            CounterPerson counterPerson = new CounterPerson(avg_waiting_time,throughput,counterCount,counterNumber);

            mList.add(counterPerson);
        }
        Collections.sort(mList,new Comparator<CounterPerson>() {
            @Override
            public int compare(CounterPerson o1, CounterPerson o2) {
                return o1.getCounterNumber() - o2.getCounterNumber();
            }
        });
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {;

        int left = getPaddingLeft();
        int right = getPaddingRight();
        int top = getPaddingTop();
        int bottom = getPaddingBottom();

        int height = getHeight() - top - bottom;
        int width = getWidth() - left - right;

        Bitmap counter = BitmapFactory.decodeResource(getResources(), R.drawable.counter);
        Bitmap scaledCounter = Bitmap.createScaledBitmap(counter,150,150,false);

        Bitmap person = BitmapFactory.decodeResource(getResources(), R.drawable.person);
        Bitmap scaledPerson = Bitmap.createScaledBitmap(person,100,100,false);

        Bitmap clock = BitmapFactory.decodeResource(getResources(),R.drawable.clock);
        Bitmap scaledClock = Bitmap.createScaledBitmap(clock, 80 , 80 , false);

        int xInc = width/mList.size() + 10;

        int xStart = left;

        for (int i=0 ; i<mList.size() ; i++){
            int yInc = scaledPerson.getHeight() + 20;
            int yStart = top;

            canvas.drawBitmap(scaledClock , xStart,yStart,null);
            canvas.drawText(String.format("%d" , (int) mList.get(i).getAvg_waiting_time()),
                    xStart + scaledClock.getWidth()/2,yStart + scaledClock.getHeight()/2,paint);
            yStart = yStart + scaledClock.getHeight()+ 10;

            canvas.drawBitmap(scaledCounter,xStart,yStart,null);
            yStart = yStart + scaledCounter.getHeight() + 20;

            for (int j=0; j<mList.get(i).getNum_persons() ; j++){
                canvas.drawBitmap(scaledPerson,xStart+((scaledCounter.getWidth()-scaledPerson.getWidth())/2),yStart,null);
                yStart += yInc;
            }
            xStart += xInc;
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
