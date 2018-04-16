package com.example.fy071.floatingwidget.component.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.AlarmItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_alarm_list)
    RecyclerView recyclerView;

    private ItemAdapter<AlarmItem> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        initToolbar();

        itemAdapter = new ItemAdapter<>();
        FastAdapter<AlarmItem> fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter
                .withSelectable(true)
                .withOnClickListener(new OnClickListener<AlarmItem>() {
                    @Override
                    public boolean onClick(@Nullable View v, IAdapter<AlarmItem> adapter, AlarmItem item, int position) {
                        return false;
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);

        //Test item
        itemAdapter.add(new AlarmItem()
                .withDate("2018.04.16")
                .withTime("20:03")
                .withTitle("Test 1")
                .withContent("Test content 1"));
        itemAdapter.add(new AlarmItem()
                .withDate("2018.04.17")
                .withTime("20:05")
                .withTitle("Test 2")
                .withContent("Test content 2"));
        itemAdapter.add(new AlarmItem()
                .withDate("2018.04.18")
                .withTime("20:08")
                .withTitle("Test 3")
                .withContent("Test content 3"));
        itemAdapter.add(new AlarmItem()
                .withDate("2018.04.19")
                .withTime("20:10")
                .withTitle("Test 4")
                .withContent("Test content 4"));
    }

    private void initToolbar() {
        toolbar.setTitle("Alarm");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}