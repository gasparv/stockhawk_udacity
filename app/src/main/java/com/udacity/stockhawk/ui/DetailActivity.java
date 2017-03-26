package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Tools;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.history_chart)
    LineChartView historyChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        historyChart.setAlpha(1);
        Intent callerIntent = getIntent();
        if(callerIntent.hasExtra(getString(R.string.intent_extra_stock_id)))
        {
            String symbol = callerIntent.getStringExtra(getString(R.string.intent_extra_stock_id));
            setTitle(symbol);
        }
        if(callerIntent.hasExtra(getString(R.string.intent_extra_stock_history)))
        {
            String history = callerIntent.getStringExtra(getString(R.string.intent_extra_stock_history));
            try {
                List<PointValue> entryList = new ArrayList<>();
                entryList = Tools.parseStringHistoryData(history);
                Line line = new Line(entryList);
                line.setHasPoints(true);
                line.setColor(R.color.black);
                line.setHasLabelsOnlyForSelected(true);
                line.setFilled(false);
                line.setPointRadius(4);
                line.setStrokeWidth(2);
                line.setCubic(false);
                line.setAreaTransparency(128);
                line.setPointColor(R.color.colorAccent);
                List<Line> lines = new ArrayList<>();
                lines.add(line);

                LineChartData data = new LineChartData();
                data.setValueLabelBackgroundAuto(false);
                data.setLines(lines);
                Axis y = new Axis().setHasLines(true);
                data.setAxisYLeft(y);
                historyChart.setLineChartData(data);
            }
            catch(NegativeArraySizeException e){e.printStackTrace();}
        }
    }
}
