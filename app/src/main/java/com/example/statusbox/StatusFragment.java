package com.example.statusbox;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment {

    private static final String ARG_STATUS_LIST = "status_list";
    private List<Status> statusList;

    public static StatusFragment newInstance(List<Status> list) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STATUS_LIST, new ArrayList<>(list));
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusList = (List<Status>) getArguments().getSerializable(ARG_STATUS_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        StatusAdapter adapter = new StatusAdapter(requireContext(), statusList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator()); // Animations
        return recyclerView;
    }
}
