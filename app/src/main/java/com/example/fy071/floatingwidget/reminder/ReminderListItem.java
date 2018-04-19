package com.example.fy071.floatingwidget.reminder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.reminder.database.Alarm;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderListItem extends AbstractItem<ReminderListItem, ReminderListItem.ViewHolder> {
    public int id;
    public StringHolder date;
    public StringHolder time;
    public StringHolder title;
    public StringHolder content;

    public ReminderListItem withAlarm(Alarm alarm) {
        this.id = alarm.getId();
        this.date = new StringHolder(alarm.getDate());
        this.time = new StringHolder(alarm.getTime());
        this.title = new StringHolder(alarm.getTitle());
        this.content = new StringHolder(alarm.getContent());
        return this;
    }

    public ReminderListItem withDate(String date) {
        this.date = new StringHolder(date);
        return this;
    }

    public ReminderListItem withTime(String time) {
        this.time = new StringHolder(time);
        return this;
    }

    public ReminderListItem withTitle(String title) {
        this.title = new StringHolder(title);
        return this;
    }

    public ReminderListItem withContent(String content) {
        this.content = new StringHolder(content);
        return this;
    }

    @NonNull
    @Override
    public ReminderListItem.ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.alarm_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_alarm;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<ReminderListItem> {
        public View view;

        @BindView(R.id.alarm_date)
        public TextView date;

        @BindView(R.id.alarm_time)
        public TextView time;

        @BindView(R.id.alarm_title)
        public TextView title;

        @BindView(R.id.alarm_content)
        public TextView content;

        @BindView(R.id.delete_container)
        public RelativeLayout deleteContainer;

        @BindView(R.id.delete)
        public ImageView delete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @Override
        public void bindView(@NonNull ReminderListItem item, @NonNull List<Object> payloads) {
            StringHolder.applyTo(item.date, date);
            StringHolder.applyTo(item.time, time);
            StringHolder.applyTo(item.title, title);
            StringHolder.applyTo(item.content, content);
        }

        @Override
        public void unbindView(@NonNull ReminderListItem item) {
            date.setText(null);
            time.setText(null);
            title.setText(null);
            content.setText(null);
        }
    }
}
