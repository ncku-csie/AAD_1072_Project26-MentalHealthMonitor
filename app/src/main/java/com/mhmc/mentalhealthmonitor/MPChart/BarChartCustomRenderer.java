package com.mhmc.mentalhealthmonitor.MPChart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;


public class BarChartCustomRenderer extends BarChartRenderer {

    private Context context;
    private ArrayList<Bitmap> imageList;

    public BarChartCustomRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, ArrayList<Bitmap> imageList, Context context) {
        super(chart, animator, viewPortHandler);
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public void drawValues(Canvas c) {
        List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();
        final float valueOffsetPlus = Utils.convertDpToPixel(22f);
        float negOffset;

        for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {
            IBarDataSet dataSet = dataSets.get(i);
            applyValueTextStyle(dataSet);
            float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
            negOffset = valueTextHeight + valueOffsetPlus;

            BarBuffer buffer = mBarBuffers[i];

            float left, right, top, bottom;
            for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                if (j / 4 > imageList.size() - 1) {
                    break;
                }
                left = buffer.buffer[j];
                right = buffer.buffer[j + 2];
                top = buffer.buffer[j + 1];
                bottom = buffer.buffer[j + 3];

                float x = (left + right) / 2f;

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                if (!mViewPortHandler.isInBoundsY(top) || !mViewPortHandler.isInBoundsLeft(x))
                    continue;

                BarEntry entry = dataSet.getEntryForIndex(j / 4);
                float val = entry.getY();
                mValuePaint.setTextAlign(Paint.Align.CENTER);
                if (val > 0) {

                    drawValue(c, dataSet.getValueFormatter(), val, entry, i, x,
                            (bottom + negOffset),
                            dataSet.getValueTextColor(j / 4));
                }

                Bitmap bitmap = imageList.get(j / 4);//4

                if (bitmap != null) {
                    Bitmap scaledBitmap = getScaledBitmap(bitmap);
                    c.drawBitmap(scaledBitmap, x - scaledBitmap.getWidth() / 2f, (bottom + 0.5f * negOffset) - scaledBitmap.getWidth() / 2f, null);
                }
            }
        }
    }


    private Bitmap getScaledBitmap(Bitmap bitmap) {
        int width = 70;
        int height = 70;
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }


}