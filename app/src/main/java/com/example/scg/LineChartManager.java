package com.example.scg;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartManager {
    private LineChart lineChart;
    private XAxis xAxis;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private List<ILineDataSet> lineDataSets = new ArrayList<>();
    private List<String> timeList = new ArrayList<>(); //存储x轴的时间
    private int times = 1;
    private int iPointCnts = 100;
    //private CustomMarkerView mv;
    private Object Context;
    private YAxis yAxis;


    //多条曲线
    public LineChartManager(LineChart mLineChart, List<String> names, List<Integer> colors) {
        this.lineChart = mLineChart;
        //leftAxis = lineChart.getAxisLeft();
        //rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
//        yAxis.setDrawLabels(true);
 //       xAxis.setDrawLabels(true);
        lineDataSets.clear();
        timeList.clear();
        yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaximum((float)0.06);
        yAxis.setAxisMinimum((float)-0.06);
        initLineChart();
        initLineDataSet(names, colors);
        iPointCnts = 200;

        Log.e("---", "LineChartManager");

    }

    /**
     * 初始化LineChar
     */
    private void initLineChart() {
        lineChart.setTouchEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLinesBehindData(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setAutoScaleMinMaxEnabled(false);

        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);



        //X轴设置显示位置在底部
//        xAxis.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                int i = (int) (v % timeList.size());
                if (i <= 0) {
                    return "";
                } else {
                    return timeList.get((int) v % timeList.size());
                }
            }
        });


        //保证Y轴从0开始，不然会上移一点
       // leftAxis.setStartAtZero(false);
//        leftAxis.setAxisMinimum(0f);
//        rightAxis.setAxisMinimum(0f);
    }


    /**
     * 初始化折线（多条线）
     *
     * @param names
     * @param colors
     */
    public void initLineDataSet(List<String> names, List<Integer> colors) {
        for (int i = 0; i < names.size(); i++) {
            lineDataSet = new LineDataSet(null, names.get(i));
            lineDataSet.setColor(colors.get(i));
            lineDataSet.setLineWidth(1.0f);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setColor(colors.get(i));
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawCircles(false);


            lineDataSet.setDrawFilled(false);
            lineDataSet.setCircleColor(colors.get(i));
            lineDataSet.setHighLightColor(Color.BLACK);
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setValueTextSize(10f);
            lineDataSets.add(lineDataSet);

        }
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    /**
     * 动态添加数据（多条折线图）
     *
     * @param numbers
     */
    long lTimeStart = System.currentTimeMillis();
    long lTimeLast = System.currentTimeMillis();

    private boolean bPause = false;

    public void setbPause(boolean b) {
        bPause = b;
    }


    public void addEntry(List<Double> numbers) {
        if (bPause) return;
        // if ((System.currentTimeMillis() - lTimeLast)<500) return;
        lTimeLast = System.currentTimeMillis();

        if (lineDataSets.size() != numbers.size()) return;
        if (lineDataSets.get(0).getEntryCount() == 0) {
            lineData = new LineData(lineDataSets);
            lineChart.setData(lineData);
        }
        if (timeList.size() > 200) {
            timeList.clear();
        }
        timeList.add(String.format("%.1f", (double) (System.currentTimeMillis() - lTimeStart) / 1000.0));
        if ((System.currentTimeMillis() - lTimeLast) > 10000)
        {

            lineChart.setVisibleXRangeMaximum(iPointCnts);
            Log.e("x", String.format("cnts:%d ", iPointCnts));
            iPointCnts = 0;
            lTimeLast = System.currentTimeMillis();
        }
        for (int i = 0; i < numbers.size(); i++) {
            Entry entry = new Entry(lineDataSet.getEntryCount(), Float.valueOf(String.valueOf(numbers.get(i))));
            lineData.addEntry(entry, i);
        }
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.moveViewToX(lineData.getEntryCount() - lineChart.getVisibleXRange());
       // leftAxis.setAxisMaximum(lineChart.getYMax());
       // leftAxis.setAxisMinimum(lineChart.getYMin());
        lineChart.setVisibleXRangeMaximum(200);
    }

    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }


    /**
     * 清除图上的数据
     */
    public void clearLineData() {
        lineChart.clear();
    }


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    handler.removeMessages(0);
                    handler.sendEmptyMessageDelayed(0, 1000);
                    times++;
                    Log.e("---", "Times:" + times);
                    timeList.add(times + "");
                    break;
                case 1:
                    handler.removeMessages(0);
                    break;
            }
        }
    };

}