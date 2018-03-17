package com.example.fy071.floatingwidget.component;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.Key;


public class HomeFragment extends Fragment implements View.OnTouchListener {
    private static final String TAG = "HomeFragment";
    float fingerStartX, fingerStartY, viewStartX, viewStartY;
    FrameLayout pet;
    ImageView imageView;
    SharedPreferences sharedPreferences;
    public HomeFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_home, container, false);

        Drawable drawable = getResources().getDrawable(R.drawable.background1);
        currentView.setBackground(drawable);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        pet = currentView.findViewById(R.id.layout_pet);
        imageView = pet.findViewById(R.id.imageView_pet);
        pet.setOnTouchListener(this);
        pet.setX(sharedPreferences.getFloat(Key.PET_LAST_X, 0));
        pet.setY(sharedPreferences.getFloat(Key.PET_LAST_Y, 0));
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
                imageView.setImageResource(R.drawable.test2);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouch: move");
                v.setX(event.getRawX() + viewStartX - fingerStartX);
                v.setY(event.getRawY() + viewStartY - fingerStartY);
                break;
            case MotionEvent.ACTION_UP:
                imageView.setImageResource(R.drawable.test);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(Key.PET_LAST_X, v.getX());
                editor.putFloat(Key.PET_LAST_Y, v.getY());
                editor.apply();
                break;
        }
        return true;
    }
}
