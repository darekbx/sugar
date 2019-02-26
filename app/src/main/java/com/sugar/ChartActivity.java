package com.sugar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sugar.data.DataManager;
import com.sugar.model.Summary;
import com.sugar.view.ChartView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class ChartActivity extends AppCompatActivity {

    @Bind(R.id.chart)
    ChartView chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        new DataManager().getSummaries(this).subscribe(new Action1<List<Summary>>() {
            @Override
            public void call(List<Summary> summaries) {
                Collections.reverse(summaries);
                chart.setData(summaries);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
