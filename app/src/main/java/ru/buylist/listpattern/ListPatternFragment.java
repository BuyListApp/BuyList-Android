package ru.buylist.listpattern;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.buylist.databinding.FragmentListPatternBinding;

public class ListPatternFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_PATTERN_ID = "pattern_id";

    private ListPatternViewModel viewModel;

    public static ListPatternFragment newInstance(int patternId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PATTERN_ID, patternId);

        ListPatternFragment fragment = new ListPatternFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ListPatternActivity.obtainViewModel(getActivity());

        int patternId = getArguments() != null ? getArguments().getInt(ARG_PATTERN_ID, 0) : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentListPatternBinding binding = FragmentListPatternBinding
                .inflate(inflater, container, false);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }
}
