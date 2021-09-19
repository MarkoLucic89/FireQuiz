package com.example.firequiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.firequiz.R;
import com.example.firequiz.adapters.QuizAdapter;
import com.example.firequiz.databinding.FragmentListBinding;
import com.example.firequiz.databinding.ListItemBinding;
import com.example.firequiz.models.Quiz;
import com.example.firequiz.viewmodel.QuizViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements QuizAdapter.OnQuizListItemClicked {

    private FragmentListBinding binding;
    private QuizViewModel quizViewModel;
    private QuizAdapter quizAdapter;
    private Animation animationFadeIn;
    private Animation animationFadeOut;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        quizAdapter = new QuizAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(quizAdapter);
        animationFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.animation_fade_in);
        animationFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.animation_fade_out);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quizViewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);
        quizViewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), quizList -> {
            quizAdapter.setQuizList(quizList);
            quizAdapter.notifyDataSetChanged();
            binding.progressBarListFragment.startAnimation(animationFadeOut);
            binding.recyclerView.startAnimation(animationFadeIn);
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        binding = null;
    }

    @Override
    public void onQuizItemClicked(int position) {
        ListFragmentDirections.ActionListFragmentToDetailsFragment actionListFragmentToDetailsFragment
                = ListFragmentDirections.actionListFragmentToDetailsFragment();
        actionListFragmentToDetailsFragment.setPosition(position);
        navController.navigate(actionListFragmentToDetailsFragment);
    }
}