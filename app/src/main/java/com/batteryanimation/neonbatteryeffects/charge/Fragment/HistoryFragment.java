package com.batteryanimation.neonbatteryeffects.charge.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.databinding.FragmentHistoryBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;

    public int getBatteryPercentage(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1) {
            return -1; // Error occurred
        }

        return (int) ((level / (float) scale) * 100);
    }

    public int calculateBatteryDischargeTime(Context context) {
        int batteryPercentage = getBatteryPercentage(context);

        if (batteryPercentage != -1) {
            // The battery consumption rate (in percentage per hour) is an estimate.
            // You may need to adjust this value based on device usage patterns.
            float consumptionRate = 5.0f; // 5% per hour (example value)

            int dischargeTime = (int) (batteryPercentage / consumptionRate);
            return dischargeTime;
        } else {
            return -1; // Unable to calculate discharge time due to an error
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        binding.batteryLife.setText(calculateBatteryDischargeTime(requireActivity())+" hrs");
        binding.batteryLife2.setText(calculateBatteryDischargeTime(requireActivity())+" hrs");

        setUpLineChart();
        setDataToLineChart();
        return binding.getRoot();
    }

    private void setUpLineChart() {
        binding.lineChart.animateX(1200, Easing.EaseInSine);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.setTouchEnabled(false);
        binding.lineChart.setScaleEnabled(false);
        binding.lineChart.setPinchZoom(false);
        binding.lineChart.setDragEnabled(false);

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1F);
        xAxis.setValueFormatter(new MyAxisFormatter());

        YAxis axisRight = binding.lineChart.getAxisRight();
        YAxis axisLeft = binding.lineChart.getAxisLeft();
        axisRight.setEnabled(true);
        axisLeft.setEnabled(false);
        xAxis.setTextColor(getResources().getColor(R.color.grey));
        axisRight.setTextColor(getResources().getColor(R.color.grey));
        binding.lineChart.setExtraLeftOffset(10f);
        binding.lineChart.setExtraRightOffset(10f);

        Legend legend = binding.lineChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(12F);
        legend.setTextColor(getResources().getColor(R.color.grey));
        legend.setForm(Legend.LegendForm.CIRCLE);
    }

    public class MyAxisFormatter extends IndexAxisValueFormatter {

        private ArrayList<String> items = new ArrayList<>();

        public MyAxisFormatter() {
            items.add("11");
            items.add("5");
            items.add("11");
            items.add("5");
            items.add("Now");
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < items.size()) {
                return items.get(index);
            } else {
                return null;
            }
        }
    }

    private ArrayList<Entry> week1() {
        ArrayList<Entry> sales = new ArrayList<>();
        sales.add(new Entry(0f, 15f));
        sales.add(new Entry(1f, 16f));
        sales.add(new Entry(2f, 13f));
        sales.add(new Entry(3f, 22f));
        sales.add(new Entry(4f, 20f));
        return sales;
    }

    private ArrayList<Entry> week2() {
        ArrayList<Entry> sales = new ArrayList<>();
        sales.add(new Entry(0f, getRandomNum()));
        sales.add(new Entry(1f, getRandomNum()));
        sales.add(new Entry(2f, getRandomNum()));
        sales.add(new Entry(3f, getRandomNum()));
        sales.add(new Entry(4f, getBatteryPercentage(requireActivity())));
        return sales;
    }

    private float getRandomNum() {
        final int min = 10;
        final int max = 100;
        final int random = new Random().nextInt((max - min) + 1) + min;

        return random;
    }

    private void setDataToLineChart() {
//        LineDataSet weekOneSales = new LineDataSet(week1(), "Week 1");
//        weekOneSales.setLineWidth(3f);
//        weekOneSales.setValueTextSize(15f);
//        weekOneSales.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        weekOneSales.setColor(ContextCompat.getColor(requireActivity(), R.color.red));
//        weekOneSales.setValueTextColor(ContextCompat.getColor(requireActivity(), R.color.red));
//        weekOneSales.enableDashedLine(20f, 10f, 0f);

        LineDataSet weekTwoSales = new LineDataSet(week2(), "Battery usage");
        weekTwoSales.setLineWidth(3f);
        weekTwoSales.setValueTextSize(0f);
        weekTwoSales.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        weekTwoSales.setColor(ContextCompat.getColor(requireActivity(), R.color.dark_cyan));
        weekTwoSales.setValueTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_cyan));

        ArrayList<ILineDataSet> dataSet = new ArrayList<>();

        dataSet.add(weekTwoSales);

        LineData lineData = new LineData(dataSet);
        binding.lineChart.setData(lineData);

        binding.lineChart.invalidate();
    }



}