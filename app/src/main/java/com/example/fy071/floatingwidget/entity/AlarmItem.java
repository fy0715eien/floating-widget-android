package com.example.fy071.floatingwidget.entity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmItem extends AbstractItem<AlarmItem, AlarmItem.ViewHolder> {
    public StringHolder date;
    public StringHolder time;
    public StringHolder title;
    public StringHolder content;

    public AlarmItem withDate(String date) {
        this.date = new StringHolder(date);
        return this;
    }

    public AlarmItem withTime(String time) {
        this.time = new StringHolder(time);
        return this;
    }

    public AlarmItem withTitle(String title) {
        this.title = new StringHolder(title);
        return this;
    }

    public AlarmItem withContent(String content) {
        this.content = new StringHolder(content);
        return this;
    }

    @NonNull
    @Override
    public AlarmItem.ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.alarm_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.alarm_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<AlarmItem> {
        public View view;

        @BindView(R.id.alarm_date)
        public TextView date;

        @BindView(R.id.alarm_time)
        public TextView time;

        @BindView(R.id.alarm_title)
        public TextView title;

        @BindView(R.id.alarm_content)
        public TextView content;

        @BindView(R.id.delete)
        public ImageView delete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @Override
        public void bindView(@NonNull AlarmItem item, @NonNull List<Object> payloads) {
            StringHolder.applyTo(item.date, date);
            StringHolder.applyTo(item.time, time);
            StringHolder.applyTo(item.title, title);
            StringHolder.applyTo(item.content, content);
        }

        @Override
        public void unbindView(@NonNull AlarmItem item) {
            date.setText(null);
            time.setText(null);
            title.setText(null);
            content.setText(null);
        }

        public static class DeleteClickEvent extends ClickEventHook<AlarmItem> {
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof AlarmItem.ViewHolder) {
                    return ((ViewHolder) viewHolder).delete;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<AlarmItem> fastAdapter, AlarmItem item) {

            }
        }
    }
}
