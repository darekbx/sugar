package com.sugar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sugar.adapter.ExpandableRecyclerAdapter;
import com.sugar.data.DataManager;
import com.sugar.dialogs.AddDialog;
import com.sugar.model.Summary;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.todays_sugar)
    TextView todaysSugar;

    @Bind(R.id.items_count)
    TextView itemsCount;

    @Bind(R.id.sugar_eaten)
    TextView sugarEaten;

    private ExpandableRecyclerAdapter adapter;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_main);

        ButterKnife.bind(this);

        setUpList();

        dataManager = new DataManager();
        loadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog dialog = new AddDialog();
                dialog.setListener(new AddDialog.Listener() {
                    @Override
                    public void onAdd() {
                        loadData();
                    }
                });
                dialog.show(getFragmentManager(), "tag");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chart:
                startActivity(new Intent(this, ChartActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpList() {
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        dataManager.getSummaries(this).subscribe(new Action1<List<Summary>>() {
            @Override
            public void call(List<Summary> summaries) {
                adapter = new ExpandableRecyclerAdapter(getApplicationContext(), summaries);
                list.setAdapter(adapter);
                adapter.expand(0);
                adapter.notifyDataSetChanged();

                setTodaySummary(summaries);
            }
        });
    }

    private void setTodaySummary(List<Summary> summaries) {
        if (summaries.size() > 0) {
            double sumSugar = 0;
            int sumCount = 0;
            for (Summary item : summaries) {
                sumSugar += item.sugar_amount;
                sumCount += item.entries.size();
            }
            Summary first = summaries.get(0);
            SpannableString text = new SpannableString(
                    getString(R.string.today_sugar, first.sugar_amount));
            text.setSpan(new ForegroundColorSpan(
                    getResources().getColor(first.color)), 14, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            todaysSugar.setText(text);
            itemsCount.setText(getString(R.string.items_count, sumCount));
            sugarEaten.setText(getString(R.string.sugar_eaten, sumSugar / 1000));
        }
    }
}
