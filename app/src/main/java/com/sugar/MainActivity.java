package com.sugar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.sugar.adapter.ExpandableRecyclerAdapter;
import com.sugar.data.DataManager;
import com.sugar.dialogs.AddDialog;
import com.sugar.model.Summary;
import com.sugar.view.ChartView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String EXPORT_FILE_NAME = "sugar_export.json";

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.todays_sugar)
    TextView todaysSugar;

    @Bind(R.id.items_count)
    TextView itemsCount;

    @Bind(R.id.sugar_eaten)
    TextView sugarEaten;

    @Bind(R.id.chart)
    ChartView chart;

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

        chart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showExportDialog();
                return false;
            }
        });
    }

    private void showExportDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.export_message)
                .setPositiveButton(R.string.export_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        export();
                    }
                })
                .setNegativeButton(R.string.export_cancel, null)
                .show();
    }

    private void export() {
        dataManager
                .getAllEntries(this)
                .map(new Func1<List<Entry>, String>() {
                    @Override
                    public String call(List<Entry> entries) {

                        return entriesAsString(entries);
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String data) {
                        return writeExportData(data);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String path) {
                        Toast.makeText(MainActivity.this, getString(R.string.export_saved, path), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String writeExportData(String data) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, EXPORT_FILE_NAME);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
        return file.getAbsolutePath();
    }

    @NotNull
    private String entriesAsString(List<Entry> entries) {
        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (Entry entry : entries) {
            builder.append("{\"description\":\"");
            builder.append(entry.getDescription());
            builder.append("\",\"amount\":");
            builder.append(entry.getSugar_amount());
            builder.append(",\"date\":");
            builder.append(entry.getDate());
            builder.append("},");
        }
        String out = builder.substring(0, builder.length() - 1);
        return out + "]";
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

                List<Summary> summariesForChart = new ArrayList(summaries);
                Collections.reverse(summariesForChart);

                for (Summary summary : summariesForChart) {
                    summary.color = getResources().getColor(summary.color);
                }
                chart.setDataNoColor(summariesForChart);
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
