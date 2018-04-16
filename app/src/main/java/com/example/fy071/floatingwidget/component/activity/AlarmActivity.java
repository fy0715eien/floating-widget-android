package com.example.fy071.floatingwidget.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.database.Alarm;
import com.example.fy071.floatingwidget.component.database.DbManager;
import com.example.fy071.floatingwidget.entity.AlarmItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";

    private DbManager dbManager = new DbManager(this);

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_alarm_list)
    RecyclerView recyclerView;

    @BindView(R.id.new_alarm)
    FloatingActionButton floatingActionButton;

    @OnClick(R.id.new_alarm)
    void newAlarm() {
        Log.d(TAG, "newAlarm: called");
        Intent intent = new Intent(AlarmActivity.this, ReminderConfigActivity.class);
        startActivity(intent);
    }

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
                        Log.d(TAG, "onClick: custom");
                        Intent intent = new Intent(AlarmActivity.this, ReminderConfigActivity.class);
                        intent.putExtra("id", item.id);
                        startActivity(intent);
                        return false;
                    }
                })
                .withEventHook(new ClickEventHook<AlarmItem>() {
                    @Override
                    public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                        if (viewHolder instanceof AlarmItem.ViewHolder) {
                            return ((AlarmItem.ViewHolder) viewHolder).delete;
                        }
                        return null;
                    }

                    @Override
                    public void onClick(View v, int position, FastAdapter<AlarmItem> fastAdapter, AlarmItem item) {
                        Log.d(TAG, "onClick: delete");
                        // TODO: 2018/4/16 删除操作
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);

        initAlarmList();
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

    void initAlarmList() {
        List<Alarm> alarmList = dbManager.searchAll();
        for (Alarm alarm : alarmList) {
            itemAdapter.add(new AlarmItem().withAlarm(alarm));
        }
    }


}