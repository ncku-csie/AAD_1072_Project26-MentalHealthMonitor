package com.mhmc.mentalhealthmonitor.twicePage.internal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.mhmc.mentalhealthmonitor.MYSQL.buffer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Renderer {
    protected final Paint mPaint;
    protected final Paint PointPaint;
    protected final Paint Point_for_line;
    protected final Paint mGridPaint, mLabelPaint;
    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
    private float defaultTextSize;

    private float x_previous;
    private float y_previous;

    private float x_coordinate;
    private float y_coordinate;
    private Matrix currentMatrix = new Matrix();
    private int ratio_x_visible = 1;
    private int size_data = 0;
    private float scale_visible = 1;

    protected EntryData data;

    protected RectF candleRect = new RectF(), barRect = new RectF();
    private final float candleBarRatio = 0.8f;// candleRect.height / barRect.height = 4

    // contain 4 points to draw 2 lines.
    protected float[] shadowBuffer = new float[8];
    // contain 2 points to draw a rect.
    protected float[] bodyBuffer = new float[4];
    // contain 1 points to get y value.
    protected float[] barBuffer = new float[2];

    private float[] calcTemp = new float[]{0, 0};

    /**
     * the space between the entries, default 0.1f (10%)
     */
    protected float mBarSpace = 0.1f;

    /**
     * the max visible entry count.
     */
    protected int visibleCount = 28;
    protected float tag_scale = 1;
    protected float test_scale = 1;
    protected int tag_if_init = 1;

    private boolean highlightEnable = false;
    private float[] highlightPoint = new float[2];

    /**
     * a y value formatter.
     */
    protected DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    int new_max = 2;

    public Renderer() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.GREEN);

        PointPaint = new Paint();
        //PointPaint.setAntialias(true);
        PointPaint.setAntiAlias(true);
        PointPaint.setColor(Color.RED);
        PointPaint.setStrokeWidth(12.0f);
        PointPaint.setStrokeCap(Paint.Cap.ROUND);

        Point_for_line = new Paint();
        //PointPaint.setAntialias(true);
        Point_for_line.setAntiAlias(true);

        //System.out.println("hgjh" + new_max);
        switch (buffer.getcount()%4) {
            case 3:
                Point_for_line.setColor(Color.BLUE);
                buffer.setgetcount();
                break;
            case 1:
                Point_for_line.setColor(Color.GREEN);
                buffer.setgetcount();
                break;
            case 2:
                Point_for_line.setColor(Color.YELLOW);
                buffer.setgetcount();
                break;
            case 0:
                Point_for_line.setColor(Color.CYAN);
                buffer.setgetcount();
                break;
        }
        Point_for_line.setStrokeWidth(10.0f);
        Point_for_line.setStrokeCap(Paint.Cap.ROUND);

        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(1);
        mGridPaint.setColor(Color.BLACK);

        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setColor(Color.BLACK);
    }

    public void setContentRect(RectF contentRect) {
        float barTop = contentRect.bottom -
                (1 - candleBarRatio) * (contentRect.height() - contentRect.top);
        float candleBottom = barTop - contentRect.top;

        this.candleRect.set(contentRect.left, contentRect.top, contentRect.right, candleBottom);
        this.barRect.set(contentRect.left, barTop, contentRect.right, contentRect.bottom);

        defaultTextSize = contentRect.top * 3 / 4;
    }

    int asdmax = 1;
    ArrayList<String[]> thisArrayList;

    public void setData(EntryData data, ArrayList<String[]> d) {
        this.data = data;
        size_data = data.entries.size();

        thisArrayList = d;


        for (String[] a : d) {
            if (a[1].equals("null") || a[1] == null) {
            } else {
                int v;
                if (a[1].contains(".")) {
                    String temp = String.valueOf(new BigDecimal(a[1]).setScale(0, BigDecimal.ROUND_HALF_UP));
                    v = Integer.valueOf(temp);
                } else
                    v = Integer.valueOf(a[1]);

                if (v > asdmax) {
                    asdmax = v;
                }
            }
        }

        if (size_data >= 7 && size_data < 14) {
            //matrixValues[Matrix.MSCALE_X] = 1;
            visibleCount = 7;
            ratio_x_visible = 1;
        } else if (size_data >= 14 && size_data < 21) {
            //matrixValues[Matrix.MSCALE_X] = 1;
            visibleCount = 14;
            ratio_x_visible = 2;
        } else if (size_data >= 21 && size_data < 28) {
            //matrixValues[Matrix.MSCALE_X] = 1;
            visibleCount = 21;
            ratio_x_visible = 3;
        } else if (size_data >= 28) {
            //matrixValues[Matrix.MSCALE_X] = 1;
            visibleCount = 28;
            ratio_x_visible = 4;
        }

        scale_visible = size_data / 7f;
        //matrixValues[Matrix.MSCALE_X] = scale_visible;

        prepareMatrixTouch(visibleCount);
        prepareMatrixValue(data.mYMax - data.mYMin, data.mYMin);
        prepareMatrixOffset(candleRect.left, candleRect.top);
    }

    public void enableHighlight(MotionEvent e) {
        highlightEnable = true;

        highlightPoint[0] = e.getX();
        highlightPoint[1] = e.getY();
    }

    public void disableHighlight() {
        highlightEnable = false;

        highlightPoint[0] = -1;
        highlightPoint[1] = -1;
    }

    /**
     * draw everything.
     */
    public void render(Canvas canvas) {
        // CALC
        calc();
        // DRAW GRIDS
        renderGrid(canvas);
        // DRAW LABELS
        renderLabels(canvas);

        int i = 0, j = 0;

        // set the entry draw area.
        canvas.save();
        canvas.clipRect(candleRect.left, candleRect.top, candleRect.right, barRect.bottom);
        for (i = visibleXMin; i < visibleXMax; i++) {
            Entry entry = data.entries.get(i);

            // draw step 0: set color
      /*
      if (entry.open >= entry.close) {
        mPaint.setColor(Color.GREEN);
      } else {
        mPaint.setColor(Color.RED);
      }
      */

            // draw step 1: draw shadow
            shadowBuffer[0] = i + 0.5f;
            shadowBuffer[2] = i + 0.5f;
            shadowBuffer[4] = i + 0.5f;
            shadowBuffer[6] = i + 0.5f;
            if (entry.open > entry.close) {
                shadowBuffer[1] = entry.high;
                shadowBuffer[3] = entry.open;
                shadowBuffer[5] = entry.close;
                shadowBuffer[7] = entry.low;
            } else {
                shadowBuffer[1] = entry.high;
                shadowBuffer[3] = entry.close;
                shadowBuffer[5] = entry.open;
                shadowBuffer[7] = entry.low;
            }
            mapPoints(shadowBuffer);
            //canvas.drawLines(shadowBuffer, mPaint);//draw the line in rect

            // draw step 2: draw body
            bodyBuffer[0] = i + 1 - mBarSpace;
            bodyBuffer[2] = i + mBarSpace;
            if (entry.open > entry.close) {
                bodyBuffer[1] = entry.open;
                bodyBuffer[3] = entry.close;
            } else {
                bodyBuffer[1] = entry.close;
                bodyBuffer[3] = entry.open;
            }

            //teststestsetsetsetsetset****************
            mapPoints(bodyBuffer);
            //canvas.drawRect(bodyBuffer[0], bodyBuffer[1], bodyBuffer[2], bodyBuffer[3], mPaint);//draw upper bar
            //canvas.drawPoint(100, 100, PointPaint); //test
            canvas.drawPoint((bodyBuffer[0] + bodyBuffer[2]) / 2, (bodyBuffer[1] + bodyBuffer[3]) / 2, PointPaint);

            // store the coordinate.
            Xcoef_LP[i - visibleXMin] = (bodyBuffer[0] + bodyBuffer[2]) / 2;
            Ycoef_LP[i - visibleXMin] = (bodyBuffer[1] + bodyBuffer[3]) / 2;

            // draw line of line chart
            if (i == visibleXMin) {
                x_previous = (bodyBuffer[0] + bodyBuffer[2]) / 2;
                y_previous = (bodyBuffer[1] + bodyBuffer[3]) / 2;
            } else {
                //canvas.drawLine(x_previous, y_previous, (bodyBuffer[0] + bodyBuffer[2]) / 2, (bodyBuffer[1] + bodyBuffer[3]) / 2, mPaint);
                x_previous = (bodyBuffer[0] + bodyBuffer[2]) / 2;
                y_previous = (bodyBuffer[1] + bodyBuffer[3]) / 2;
            }

            // draw step 3: draw bar
            barBuffer[0] = 0;
            barBuffer[1] = entry.volume;
            mMatrixBar.mapPoints(barBuffer);
            //canvas.drawRect(bodyBuffer[0], barRect.bottom - barBuffer[1], bodyBuffer[2], barRect.bottom - 1, mPaint); //draw lower bar.

            // extra calc: set highlight position
            if (highlightPoint[0] <= bodyBuffer[2] && highlightPoint[0] >= bodyBuffer[0]) {
                highlightPoint[0] = shadowBuffer[0];
                highlightPoint[1] = (bodyBuffer[1] + bodyBuffer[3]) / 2;

                // DRAW HIGHLIGHT
                if (highlightEnable) {
                    renderHighlight(canvas);
                }
            }
        }

        //canvas.drawPoint(100, 100, PointPaint);
        //canvas.drawPoint(visibleXMax, 100, PointPaint);


        x_coordinate = Xcoef_LP[visibleXMax - visibleXMin - 1];
        for (i = visibleXMax - visibleXMin - 1; i > 0; i--) {
            while (x_coordinate <= Xcoef_LP[i - 1]) {
                y_coordinate = 1;
                if (i == 0) {
                    y_coordinate = ((Ycoef_LP[i] * (x_coordinate - Xcoef_LP[i + 1])) / (Xcoef_LP[i] - Xcoef_LP[i + 1])) * ((x_coordinate - Xcoef_LP[i + 2]) / (Xcoef_LP[i] - Xcoef_LP[i + 2])) + ((Ycoef_LP[i + 1] * (x_coordinate - Xcoef_LP[i])) / (Xcoef_LP[i + 1] - Xcoef_LP[i])) * ((x_coordinate - Xcoef_LP[i + 2]) / (Xcoef_LP[i + 1] - Xcoef_LP[i + 2])) + ((Ycoef_LP[i + 2] * (x_coordinate - Xcoef_LP[i])) / (Xcoef_LP[i + 2] - Xcoef_LP[i])) * ((x_coordinate - Xcoef_LP[i + 1]) / (Xcoef_LP[i + 2] - Xcoef_LP[i + 1]));
                } else if (i == (visibleXMax - visibleXMin) - 1) {
                    y_coordinate = ((Ycoef_LP[i] * (x_coordinate - Xcoef_LP[i - 1])) / (Xcoef_LP[i] - Xcoef_LP[i - 1])) * ((x_coordinate - Xcoef_LP[i - 2]) / (Xcoef_LP[i] - Xcoef_LP[i - 2])) + ((Ycoef_LP[i - 1] * (x_coordinate - Xcoef_LP[i])) / (Xcoef_LP[i - 1] - Xcoef_LP[i])) * ((x_coordinate - Xcoef_LP[i - 2]) / (Xcoef_LP[i - 1] - Xcoef_LP[i - 2])) + ((Ycoef_LP[i - 2] * (x_coordinate - Xcoef_LP[i])) / (Xcoef_LP[i - 2] - Xcoef_LP[i])) * ((x_coordinate - Xcoef_LP[i - 1]) / (Xcoef_LP[i - 2] - Xcoef_LP[i - 1]));
                } else {
                    y_coordinate = ((Ycoef_LP[i] * (x_coordinate - Xcoef_LP[i - 1])) / (Xcoef_LP[i] - Xcoef_LP[i - 1])) * ((x_coordinate - Xcoef_LP[i + 1]) / (Xcoef_LP[i] - Xcoef_LP[i + 1])) + ((Ycoef_LP[i - 1] * (x_coordinate - Xcoef_LP[i])) / (Xcoef_LP[i - 1] - Xcoef_LP[i])) * ((x_coordinate - Xcoef_LP[i + 1]) / (Xcoef_LP[i - 1] - Xcoef_LP[i + 1])) + ((Ycoef_LP[i + 1] * (x_coordinate - Xcoef_LP[i])) / (Xcoef_LP[i + 1] - Xcoef_LP[i])) * ((x_coordinate - Xcoef_LP[i - 1]) / (Xcoef_LP[i + 1] - Xcoef_LP[i - 1]));
                }
                canvas.drawPoint(x_coordinate, y_coordinate, Point_for_line);
                x_coordinate = x_coordinate + 0.5f;
            }
        }


/*
    for (i = 0; i < Xcoef_LP.length; i++){
      System.out.println(i+" " +Xcoef_LP[i]);
    }
  System.out.println(Xcoef_LP.length);
*/

        canvas.restore();
    }


    /**
     * Calculate the current range of x and y.
     */
    protected void calc() {
        // calc step 0: calc min&max x index
        float[] pixels = new float[]{
                candleRect.right, 0
        };
        revertMapPoints(pixels);
        visibleXMin = (pixels[0] <= 0) ? 0 : (int) (pixels[0]);
        visibleXMax = visibleXMin + visibleCount + 1;// plus visibleCount+1 for smooth disappear both side.

        Xcoef_LP = new float[visibleXMax - visibleXMin];
        Ycoef_LP = new float[visibleXMax - visibleXMin];

        //System.out.printf("four:  %d  %d  %d  %d", visibleXMax, visibleXMin, rightmorepoint, leftmorepoint);

        if (visibleXMax > data.entries.size()) {
            visibleXMax = data.entries.size();
        }

        // calc step 1: calc min&max y value
        data.calcMinMax(visibleXMin, visibleXMax);
        prepareMatrixValue(data.mYMax - data.mYMin, data.mYMin);
        prepareMatrixBar(data.mMaxYVolume);
    }

    /**
     * Draw grid lines.
     */
    protected void renderGrid(Canvas canvas) {
        // CANDLE GRID
        int n_max = asdmax;
        canvas.drawRect(candleRect, mGridPaint); // draw upper column.
        for (int i = 1; i < n_max; i++) {
            canvas.drawLine(candleRect.left, (candleRect.height() * i) / n_max + candleRect.top, candleRect.right, (candleRect.height() * i) / n_max + candleRect.top, mGridPaint); //draw label horizontal line.
        }
        /*
        canvas.drawLine(candleRect.left, candleRect.height() / 4 + candleRect.top, candleRect.right, candleRect.height() / 4 + candleRect.top, mGridPaint); //draw label horizontal line.
        canvas.drawLine(candleRect.left, candleRect.height() * 2 / 4 + candleRect.top, candleRect.right, candleRect.height() * 2 / 4 + candleRect.top, mGridPaint);
        canvas.drawLine(candleRect.left, candleRect.height() * 3 / 4 + candleRect.top, candleRect.right, candleRect.height() * 3 / 4 + candleRect.top, mGridPaint);
        //canvas.drawLine(candleRect.left, candleRect.height() * 4 / 5 + candleRect.top, candleRect.right, candleRect.height() * 4 / 5 + candleRect.top, mGridPaint);
*/
        // BAR GRID
        //canvas.drawRect(barRect, mGridPaint); // draw lower column.
    }

    /**
     * Draw x and y labels.
     */
    protected void renderLabels(Canvas canvas) {
        // DRAW Y LABELS
        mLabelPaint.setTextAlign(Paint.Align.RIGHT);

        //draw max y value
        calcTemp[1] = candleRect.top;
        revertMapPoints(calcTemp);
        //String value = decimalFormatter.format(calcTemp[1]);
        //String value = "4";

        int n_max = asdmax;
        String value = String.valueOf(asdmax);
        mLabelPaint.setTextSize(36);
        mLabelPaint.setTextSize(candleRect.left * 9 / mLabelPaint.measureText(value));
        mLabelPaint.getFontMetrics(fontMetrics);
        canvas.drawText(
                value,
                candleRect.left * 9 / 10,
                candleRect.top - fontMetrics.top - fontMetrics.bottom - 20,
                mLabelPaint);

        // draw min y value
        calcTemp[1] = candleRect.bottom;
        revertMapPoints(calcTemp);
        //value = decimalFormatter.format(calcTemp[1]);
        String value_zero = "0";
        mLabelPaint.setTextSize(40);
        mLabelPaint.setTextSize(candleRect.left * 10 / mLabelPaint.measureText(value_zero));
        mLabelPaint.getFontMetrics(fontMetrics);
        canvas.drawText(
                value_zero,
                candleRect.left * 9 / 10,
                candleRect.bottom - fontMetrics.bottom + 20,
                mLabelPaint);

        for (int i = 1; i < asdmax; i++) {
            if (asdmax > 5 && (i % (asdmax / 5) == 0)) {
                calcTemp[1] = candleRect.height() * 2 / 3 + candleRect.top;
                revertMapPoints(calcTemp);
                //value = decimalFormatter.format(calcTemp[1]);
                String value_one = String.valueOf(i);
                mLabelPaint.setTextSize(40);
                mLabelPaint.setTextSize(candleRect.left * 10 / mLabelPaint.measureText(value_one));
                mLabelPaint.getFontMetrics(fontMetrics);
                canvas.drawText(
                        value_one,
                        candleRect.left * 9 / 10,
                        candleRect.height() * (n_max - i) / n_max + candleRect.top + fontMetrics.bottom,
                        mLabelPaint);
            } else if (asdmax <= 5) {
                calcTemp[1] = candleRect.height() * 2 / 3 + candleRect.top;
                revertMapPoints(calcTemp);
                //value = decimalFormatter.format(calcTemp[1]);
                String value_one = String.valueOf(i);
                mLabelPaint.setTextSize(40);
                mLabelPaint.setTextSize(candleRect.left * 10 / mLabelPaint.measureText(value_one));
                mLabelPaint.getFontMetrics(fontMetrics);
                canvas.drawText(
                        value_one,
                        candleRect.left * 9 / 10,
                        candleRect.height() * (n_max - i) / n_max + candleRect.top + fontMetrics.bottom,
                        mLabelPaint);
            }
        }

      /*
        calcTemp[1] = candleRect.height() * 2 / 3 + candleRect.top;
        revertMapPoints(calcTemp);
        //value = decimalFormatter.format(calcTemp[1]);
        String value_one = "1";
        mLabelPaint.setTextSize(40);
        mLabelPaint.setTextSize(candleRect.left * 10 / mLabelPaint.measureText(value_one));
        mLabelPaint.getFontMetrics(fontMetrics);
        canvas.drawText(
                value_one,
                candleRect.left * 9 / 10,
                candleRect.height() * (Integer.valueOf(value) - 1) / Integer.valueOf(value) + candleRect.top + fontMetrics.bottom,
                mLabelPaint);
//Integer.valueOf(value)-1 / Integer.valueOf(value)
        revertMapPoints(calcTemp);
        String value_two = "2";
        mLabelPaint.setTextSize(40);
        mLabelPaint.setTextSize(candleRect.left * 10 / mLabelPaint.measureText(value_two));
        mLabelPaint.getFontMetrics(fontMetrics);
        canvas.drawText(
                value_two,
                candleRect.left * 9 / 10,
                candleRect.height() * (Integer.valueOf(value) - 2) / Integer.valueOf(value) + candleRect.top + fontMetrics.bottom,
                mLabelPaint);
//Integer.valueOf(value)-2 / Integer.valueOf(value)
        revertMapPoints(calcTemp);
        String value_three = "3";
        mLabelPaint.setTextSize(40);
        mLabelPaint.setTextSize(candleRect.left * 10 / mLabelPaint.measureText(value_three));
        mLabelPaint.getFontMetrics(fontMetrics);
        canvas.drawText(
                value_three,
                candleRect.left * 9 / 10,
                candleRect.height() * (Integer.valueOf(value) - 3) / Integer.valueOf(value) + candleRect.top + fontMetrics.bottom,
                mLabelPaint);
        */

        canvas.save();
        canvas.clipRect(candleRect.left, candleRect.top, candleRect.right, barRect.bottom);

        // matrixValues[Matrix.MSCALE_X]
        // 7 : 72.452362
        // 28 : 17
        // 14 : 33.501839
        // 21 : 22.131264
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPaint.setTextSize(defaultTextSize);
        mLabelPaint.getFontMetrics(fontMetrics);

        if (size_data >= 7 && size_data < 14) {

            ratio_x_visible = 1;
        } else if (size_data >= 14 && size_data < 21) {
            // 7 : 2.229936
            if (matrixValues[Matrix.MSCALE_X] == scale_visible) {
                if (tag_if_init == 1) {
                    ratio_x_visible = 2;
                    tag_if_init = 0;
                } else {
                    ratio_x_visible = 1;
                }
                if (matrixValues[Matrix.MSCALE_X] == 1) {
                    ratio_x_visible = 2;
                    matrixValues[Matrix.MSCALE_X] = 2;
                }
            } else if (matrixValues[Matrix.MSCALE_X] >= (scale_visible / 2f) && matrixValues[Matrix.MSCALE_X] < scale_visible) {
                ratio_x_visible = 2;
                tag_if_init = 0;
            }
        } else if (size_data >= 21 && size_data < 28) {
            // 14 : 1.533333
            // 7 : 3.160751
            if (matrixValues[Matrix.MSCALE_X] == scale_visible) {
                if (tag_if_init == 1) {
                    ratio_x_visible = 3;
                    tag_if_init = 0;
                } else {
                    ratio_x_visible = 1;
                }
            } else if (matrixValues[Matrix.MSCALE_X] >= ((scale_visible * 2f) / 3f) && matrixValues[Matrix.MSCALE_X] < scale_visible) {
                ratio_x_visible = 2;
            } else if (matrixValues[Matrix.MSCALE_X] >= (scale_visible / 3f) && matrixValues[Matrix.MSCALE_X] < ((scale_visible * 2f) / 3f)) {
                ratio_x_visible = 3;
            }
            if (matrixValues[Matrix.MSCALE_X] == 1) {
                ratio_x_visible = 3;
                matrixValues[Matrix.MSCALE_X] = 2;
            }
        } else if (size_data >= 28) {
            // 21 : 1.340701
            // 14 : 2.042602
            // 7 : 4.357804
            if (matrixValues[Matrix.MSCALE_X] == scale_visible) {
                if (tag_if_init == 1) {
                    ratio_x_visible = 4;
                    tag_if_init = 0;
                } else {
                    ratio_x_visible = 1;
                }
            } else if (matrixValues[Matrix.MSCALE_X] >= ((scale_visible * 3f) / 4f) && matrixValues[Matrix.MSCALE_X] < scale_visible) {
                ratio_x_visible = 2;
            } else if (matrixValues[Matrix.MSCALE_X] >= ((scale_visible * 2f) / 4f) && matrixValues[Matrix.MSCALE_X] < ((scale_visible * 3f) / 4f)) {
                ratio_x_visible = 3;
            } else if (matrixValues[Matrix.MSCALE_X] >= (scale_visible / 4f) && matrixValues[Matrix.MSCALE_X] < ((scale_visible * 2f) / 4f)) {
                ratio_x_visible = 4;
            }
            if (matrixValues[Matrix.MSCALE_X] == 1) {
                ratio_x_visible = 4;
                matrixValues[Matrix.MSCALE_X] = 2;
            }
        }

        /**
         定義X軸
         */
        for (int i = visibleXMin; i < visibleXMax; i++) {
            if (i % ratio_x_visible == 0) {
                calcTemp[0] = i + 0.5f;
                mapPoints(calcTemp);
                String sdate = thisArrayList.get(i)[0].split(" ")[0].split("-")[2];
                canvas.drawText(
                        sdate,
                        calcTemp[0],
                        candleRect.bottom + defaultTextSize,
                        mLabelPaint);
                //canvas.drawLine(calcTemp[0], candleRect.top, calcTemp[0], candleRect.bottom, mGridPaint);// draw tag line for each x.
            }
        }


        canvas.restore();
    }

    /**
     * Draw highlight.
     */
    protected void renderHighlight(Canvas canvas) {
        canvas.drawLine(candleRect.left, highlightPoint[1], candleRect.right, highlightPoint[1], mGridPaint);
        canvas.drawLine(highlightPoint[0], candleRect.top, highlightPoint[0], barRect.bottom, mGridPaint);
    }

    /**
     * Transform an array of points with all matrices.
     * VERY IMPORTANT: Keep matrix order "value-touch-offset" when transforming.
     */
    protected void mapPoints(float[] pts) {
        mMatrixValue.mapPoints(pts);
        mMatrixTouch.mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }

    protected void revertMapPoints(float[] pixels) {
        Matrix tmp = new Matrix();

        // invert all matrices to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixValue.invert(tmp);
        tmp.mapPoints(pixels);
    }

    //--------------------------------------------------------------------------------
    public void prepareMatrixValue(float deltaY, float yMin) {
        // increase the y range for good looking.
        deltaY = deltaY * 12 / 10;
        yMin = yMin * 9 / 10;

        float scaleX = candleRect.width() / data.entries.size();
        float scaleY = candleRect.height() / deltaY;

        mMatrixValue.reset();
        mMatrixValue.postTranslate(0, -yMin);
        // the negative scale factor is used to draw x axis from right to left,y from down to up
        mMatrixValue.postScale(-scaleX, -scaleY);
        mMatrixValue.postTranslate(candleRect.width(), candleRect.height());
    }

    public void prepareMatrixTouch(float visibleCount) {
        float scaleX = data.entries.size() / visibleCount;
        float scaleY = 1;

        mMatrixTouch.reset();
        mMatrixTouch.postScale(scaleX, scaleY);

        resetScrollRange(scaleX);

//    mMatrixTouch.postTranslate(-maxTouchOffset, 0);
    }

    public void prepareMatrixOffset(float offsetX, float offsetY) {
        mMatrixOffset.reset();
        mMatrixOffset.postTranslate(offsetX, offsetY);
    }

    public void prepareMatrixBar(float maxY) {
        // increase the y range for good looking.
        maxY = maxY * 11 / 10;

        mMatrixBar.reset();
        mMatrixBar.postScale(1, barRect.height() / maxY);
    }

    private void resetScrollRange(float scaleX) {
        minTouchOffset = 0;
        maxTouchOffset = candleRect.width() * (scaleX - 1f);
    }

    /**
     * matrix to map the values to the screen pixels
     */
    protected Matrix mMatrixValue = new Matrix();

    /**
     * matrix to map chart scaled pixels
     */
    protected Matrix mMatrixTouch = new Matrix();

    /**
     * matrix to map the chart offset
     */
    protected Matrix mMatrixOffset = new Matrix();

    /**
     * matrix to map the volume value
     */
    protected Matrix mMatrixBar = new Matrix();

    protected int visibleXMin, visibleXMax;
    protected float maxTouchOffset, minTouchOffset;

    protected float[] matrixValues = new float[9];
    private boolean isOnBorder = true;

    private float[] Xcoef_LP;
    private float[] Ycoef_LP;


    public void refreshTouchMatrix(float dx, float dy) {
        isOnBorder = true;

        mMatrixTouch.getValues(matrixValues);

        matrixValues[Matrix.MTRANS_X] += -dx;
        matrixValues[Matrix.MTRANS_Y] += dy;

        if (matrixValues[Matrix.MTRANS_X] < -maxTouchOffset) {
            matrixValues[Matrix.MTRANS_X] = -maxTouchOffset;
            isOnBorder = false;
        }
        if (matrixValues[Matrix.MTRANS_X] > 0) {
            matrixValues[Matrix.MTRANS_X] = 0;
            isOnBorder = false;
        }

/*
        if (tag_if_init == 1) {

            if (size_data >= 7 && size_data < 14) {
                //matrixValues[Matrix.MSCALE_X] = 1;
                visibleCount = 7;
                ratio_x_visible = 1;
            } else if (size_data >= 14 && size_data < 21) {
                //matrixValues[Matrix.MSCALE_X] = 1;
                visibleCount = 14;
                ratio_x_visible = 2;
            } else if (size_data >= 21 && size_data < 28) {
                //matrixValues[Matrix.MSCALE_X] = 1;
                visibleCount = 21;
                ratio_x_visible = 3;
            } else if (size_data >= 28) {
                //matrixValues[Matrix.MSCALE_X] = 1;
                visibleCount = 28;
                ratio_x_visible = 4;
            }

            //matrixValues[Matrix.MSCALE_X] = scale_visible;
            //tag_if_init = 0;
        } else {
*/
        if (size_data >= 7 && size_data < 14) {
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;

            }
        } else if (size_data >= 14 && size_data < 21) {
            // 7 : 2.229936
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < (scale_visible * 0.5f)) {
                matrixValues[Matrix.MSCALE_X] = scale_visible * 0.5f;
            }
        } else if (size_data >= 21 && size_data < 28) {
            // 14 : 1.533333
            // 7 : 3.160751
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < (scale_visible / 3f)) {
                matrixValues[Matrix.MSCALE_X] = (scale_visible / 3f);
            }
        } else if (size_data >= 28) {
            // 21 : 1.340701
            // 14 : 2.042602
            // 7 : 4.357804
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < (scale_visible / 4f)) {
                matrixValues[Matrix.MSCALE_X] = (scale_visible / 4f);
            }
            // }

            System.out.printf("matrixValues : %f\n", matrixValues[Matrix.MSCALE_X]);

            mMatrixTouch.setValues(matrixValues);
        }
    }

    public boolean canScroll() {
        return isOnBorder;
    }

    /**
     * TODO Zoom in.
     *
     * @param x pivot x
     * @param y pivot y
     */
    public void zoomIn(float scale, float x, float y) {
    /*
    if(scale > 1){
      visibleCount++;
    }
    else {
      visibleCount--;
    }
    */
        //if(scale > 1)visibleCount++;
        //test_scale = test_scale * scale;
        tag_scale = tag_scale * scale;
        if (tag_scale < 1) {
            //tag_scale = 0.9f;
            //scale = 1 / tag_scale;
            //mMatrixTouch.set(currentMatrix);
            //mMatrixTouch.postScale(1, 1.0f, x, y);
            tag_scale = 1;
        } else if (tag_scale > 5) {
            //tag_scale = 5.1f;
            //scale = 5 / tag_scale;
            //mMatrixTouch.set(currentMatrix);
            //mMatrixTouch.postScale(5, 1.0f, x, y);
            tag_scale = 5;
        } else {
            //else if(tag_scale > 1){
            //if(tag_scale > 1 && tag_scale < 5){
            //mMatrixTouch.set(currentMatrix);
            //mMatrixTouch.postScale(scale, 1.0f, x, y);
        }
        //else {
        // mMatrixTouch.postScale(0.9f, 1.0f, x, y);
        //}
        mMatrixTouch.set(currentMatrix);

        //System.out.printf("matrixValues : %f\n", matrixValues[Matrix.MSCALE_X]);
        mMatrixTouch.postScale(scale, 1.0f, x, y);


        //System.out.printf("tag_scale : %f\n", tag_scale);
        //System.out.printf("test_scale : %f\n", test_scale);

        mMatrixTouch.getValues(matrixValues);
    /*if (matrixValues[Matrix.MSCALE_X] < 1) {
      matrixValues[Matrix.MSCALE_X] = 1;

    }*/
        if (size_data >= 7 && size_data < 14) {

            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;

            }
            if (matrixValues[Matrix.MSCALE_X] < scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;

            }
        } else if (size_data >= 14 && size_data < 21) {
            // 7 : 2.229936
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < (scale_visible / 2f)) {
                matrixValues[Matrix.MSCALE_X] = scale_visible / 2f;
            }
        } else if (size_data >= 21 && size_data < 28) {
            // 14 : 1.533333
            // 7 : 3.160751
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < (scale_visible / 3f)) {
                matrixValues[Matrix.MSCALE_X] = scale_visible / 3f;
            }
        } else if (size_data >= 28) {
            // 21 : 1.340701
            // 14 : 2.042602
            // 7 : 4.357804
            if (matrixValues[Matrix.MSCALE_X] > scale_visible) {
                matrixValues[Matrix.MSCALE_X] = scale_visible;
            }
            if (matrixValues[Matrix.MSCALE_X] < (scale_visible / 4f)) {
                matrixValues[Matrix.MSCALE_X] = scale_visible / 4f;
            }
        }

        mMatrixTouch.setValues(matrixValues);
        resetScrollRange(matrixValues[Matrix.MSCALE_X]);

    }

    /**
     * TODO Zoom out.
     *
     * @param x pivot x
     * @param y pivot y
     */
    public void zoomOut(float x, float y) {
        mMatrixTouch.postScale(0.7f, 1.0f, x, y);
        mMatrixTouch.getValues(matrixValues);
        if (matrixValues[Matrix.MSCALE_X] < 1) {
            matrixValues[Matrix.MSCALE_X] = 1;
        }
        mMatrixTouch.setValues(matrixValues);
        resetScrollRange(matrixValues[Matrix.MSCALE_X]);

        prepareMatrixValue(data.mYMax - data.mYMin, data.mYMin);
        prepareMatrixOffset(candleRect.left, candleRect.top);
    }


    public void set_currentmatrix() {
        currentMatrix.set(mMatrixTouch);
    }
}