package com.example.statusbox;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class StatusPagerAdapter extends FragmentStateAdapter {

    private final List<Status> imageStatusList;
    private final List<Status> videoStatusList;

    public StatusPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                              List<Status> imageStatusList, List<Status> videoStatusList) {
        super(fragmentActivity);
        this.imageStatusList = imageStatusList;
        this.videoStatusList = videoStatusList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return StatusFragment.newInstance(imageStatusList);
        else return StatusFragment.newInstance(videoStatusList);
    }

    @Override
    public int getItemCount() {
        return 2; // Images and Videos
    }
}
