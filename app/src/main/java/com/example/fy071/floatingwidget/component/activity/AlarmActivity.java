package com.example.fy071.floatingwidget.component.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.database.Alarm;
import com.example.fy071.floatingwidget.component.database.DbManager;
import com.example.fy071.floatingwidget.entity.AlarmItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmActivity extends AppCompatActivity implements ItemFilterListener<AlarmItem> {
    private static final String TAG = "AlarmActivity";

    private DbManager dbManager = new DbManager(this);

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view_alarm_list)
    RecyclerView recyclerView;

    @BindView(R.id.imageView_alarm_hint)
    ImageView imageView;

    @BindView(R.id.textView_alarm_hint)
    TextView textView;

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

        // 设置itemAdapter的过滤器用于搜索操作
        itemAdapter = new ItemAdapter<>();
        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AlarmItem>() {
            @Override
            public boolean filter(@NonNull AlarmItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return item.title.getText().toString().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                        item.content.getText().toString().toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });
        itemAdapter.getItemFilter().withItemFilterListener(this);

        // 设置fastAdapter的修改、删除监听器
        FastAdapter<AlarmItem> fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter
                .withSelectable(true)
                .withOnClickListener(new OnClickListener<AlarmItem>() {
                    @Override
                    public boolean onClick(@Nullable View v, IAdapter<AlarmItem> adapter, AlarmItem item, int position) {
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
                            return ((AlarmItem.ViewHolder) viewHolder).deleteContainer;
                        }
                        return null;
                    }

                    @Override
                    public void onClick(View v, final int position, FastAdapter<AlarmItem> fastAdapter, AlarmItem item) {
                        Log.d(TAG, "onClick: delete");
                        new AlertDialog.Builder(AlarmActivity.this)
                                .setTitle(R.string.dialog_title_delete)
                                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        itemAdapter.remove(position);
                                        // TODO: 2018/4/16 数据库删除操作
                                    }
                                })
                                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create();
                    }
                });

        // 将adapter应用至recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);


        //测试样例
        itemAdapter.add(new AlarmItem().withAlarm(new Alarm(123, "2018.04.17", "12:13", "Apple", "Intel")));
        itemAdapter.add(new AlarmItem().withAlarm(new Alarm(123, "2018.04.18", "12:13", "Pear", "AMD")));
        itemAdapter.add(new AlarmItem().withAlarm(new Alarm(123, "2018.04.19", "12:13", "Mango", "NVIDIA")));
        itemAdapter.add(new AlarmItem().withAlarm(new Alarm(123, "2018.04.20", "12:13", "Pineapple", "Microsoft")));
    }

    @Override
    protected void onStart() {
        super.onStart();
        initAlarmList();
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.drawer_item_reminder);
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

    private void initAlarmList() {
        List<Alarm> alarmList = dbManager.searchAll();
        if (alarmList.size() > 0) {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
        for (Alarm alarm : alarmList) {
            itemAdapter.add(new AlarmItem().withAlarm(alarm));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        menu.findItem(R.id.search).setIcon(R.drawable.ic_search_white_24dp);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                itemAdapter.filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                itemAdapter.filter(s);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<AlarmItem> results) {

    }

    @Override
    public void onReset() {

    }
}