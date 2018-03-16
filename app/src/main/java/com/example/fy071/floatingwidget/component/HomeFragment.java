package com.example.fy071.floatingwidget.component;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.fy071.floatingwidget.R;


public class HomeFragment extends Fragment implements View.OnTouchListener {
    private static final String TAG = "HomeFragment";
    float fingerStartX, fingerStartY, viewStartX, viewStartY;
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
                fingerStartX = event.getRawX();
                fingerStartY = event.getRawY();
                viewStartX = v.getX();
                viewStartY = v.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouch: move");
                float dx = event.getRawX() - fingerStartX;
                float dy = event.getRawY() - fingerStartY;
                v.setX(viewStartX + dx);
                v.setY(viewStartY + dy);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
