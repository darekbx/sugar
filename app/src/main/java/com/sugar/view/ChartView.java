package com.sugar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.sugar.model.Summary;

import java.util.List;

/**
 * Created by daba on 2016-04-12.
 */
public class ChartView extends View {

    private Paint mPaint;
    private List<Summary> mData;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);
        mPaint.setTextSize(18f);
    }

    public void setData(List<Summary> data) {
        for (Summary summary : data) {
            summary.color = getContext().getResources().getColor(summary.color);
        }
        mData = data;
        invalidate();
    }

    public void setDataNoColor(List<Summary> data) {
        mData = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mData != null) {
            float width = getWidth();
            float height = 150F;
            float chunkSize = (width / (float)(mData.size() - 1));
            float start = 0F;

            float max = (float)getMaxValue();
            float heightRatio = height / max;
            PointF temp = null;
            int tempColor = 0;

            for (Summary summary : mData) {
                float value = height - ((float)summary.sugar_amount * heightRatio);
                if (temp == null) {
                    temp = new PointF(start, value);
                    tempColor = summary.color;
                    continue;
                }

                start += chunkSize;

                mPaint.setShader(new LinearGradient(temp.x, temp.y, start, value,
                        tempColor, summary.color, Shader.TileMode.MIRROR));
                canvas.drawLine(temp.x, temp.y, start, value, mPaint);

                temp = new PointF(start, value);
                tempColor = summary.color;
            }
        }
    }

    private double getMaxValue() {
        double max = 0.0;
        for (Summary summary : mData) {
            max = Math.max(summary.sugar_amount, max);
        }
        return max;
    }
}
