package com.example.fy071.floatingwidget.component;


import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.fy071.floatingwidget.R;


public class HomeFragment extends Fragment implements View.OnTouchListener {
    private static final String TAG = "HomeFragment";
    float x, y, x2, y2;
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_home, container, false);

        Drawable drawable = getResources().getDrawable(R.drawable.background1);
        currentView.setBackground(drawable);

        FrameLayout pet = currentView.findViewById(R.id.layout_pet);
        pet.setOnTouchListener(this);
        Log.d(TAG, "onCreateView: " + pet.getX() + " " + pet.getY());
        return currentView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                x = event.getRawX();
                y = event.getRawY();
                x2 = v.getX();
                y2 = v.getY();

                break;

            case MotionEvent.ACTION_MOVE:

                Log.d(TAG, "onTouch: move");
                float dx = event.getRawX() - x;
                float dy = event.getRawY() - y;
                v.setX(x2 + dx);
                v.setY(y2 + dy);
                break;

            case MotionEvent.ACTION_UP:
                break;


        }
        return true;
    }
}
