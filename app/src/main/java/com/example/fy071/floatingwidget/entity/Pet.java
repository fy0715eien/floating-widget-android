package com.example.fy071.floatingwidget.entity;

import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

/**
 * Created by fy071 on 2018/3/17.
 */

public class Pet implements View.OnTouchListener {
    public ImageView background;
    private FrameLayout frameLayout;
    private ImageView petModel;
    private float fingerStartX, fingerStartY, viewStartX, viewStartY;

    public Pet() {

    }


    public void addSelfToView(View view) {
        this.frameLayout = view.findViewById(R.id.layout_pet);
        this.frameLayout.setX(PreferenceHelper.petLastX);
        this.frameLayout.setY(PreferenceHelper.petLastY);
        this.frameLayout.setOnTouchListener(this);

        this.petModel = this.frameLayout.findViewById(R.id.imageView_pet);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.petModel.setImageResource(R.drawable.test2);

                fingerStartX = event.getRawX();
                fingerStartY = event.getRawY();
                viewStartX = v.getX();
                viewStartY = v.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                v.setX(event.getRawX() + viewStartX - fingerStartX);
                v.setY(event.getRawY() + viewStartY - fingerStartY);
                break;
            case MotionEvent.ACTION_UP:
                this.petModel.setImageResource(R.drawable.test);
                //写入最后坐标
                SharedPreferences.Editor editor = PreferenceHelper.sharedPreferences.edit();
                editor.putFloat(Key.PET_LAST_X, v.getX());
                editor.putFloat(Key.PET_LAST_Y, v.getY());
                editor.apply();
                break;
        }
        return true;
    }
}
