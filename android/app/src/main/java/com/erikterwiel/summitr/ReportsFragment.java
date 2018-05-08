package com.erikterwiel.summitr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReportsFragment extends Fragment {

    public static final String TAG = "ReportsFragment.java";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.erikterwiel.summitr.R.layout.fragment_feed, container, false);
        return view;
    }
}
