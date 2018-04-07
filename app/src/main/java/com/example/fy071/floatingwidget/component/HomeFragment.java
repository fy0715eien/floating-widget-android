package com.example.fy071.floatingwidget.component;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.Pet;


public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";
    Pet pet;
    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View currentView = inflater.inflate(R.layout.fragment_home, container, false);
        pet = new Pet();
        pet.addSelfToView(currentView);
        return currentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
