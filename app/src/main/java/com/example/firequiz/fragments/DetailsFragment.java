package com.example.firequiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.firequiz.R;
import com.example.firequiz.databinding.FragmentDetailsBinding;
import com.example.firequiz.models.Quiz;
import com.example.firequiz.models.Result;
import com.example.firequiz.viewmodel.QuizViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private NavController navController;
    private QuizViewModel quizViewModel;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    //safe args
    private int position;
    private int totalQuestions;
    private String quizID;
    private String quizName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();
        binding.buttonDetailsStartQuiz.setOnClickListener(v -> goToQuizFragment());
        return binding.getRoot();
    }

    private void goToQuizFragment() {
        DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment actionDetailsFragmentToQuizFragment
                = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
        actionDetailsFragmentToQuizFragment.setQuizId(quizID);
        actionDetailsFragmentToQuizFragment.setTotalQuestions(totalQuestions);
        actionDetailsFragmentToQuizFragment.setQuizName(quizName);
        navController.navigate(actionDetailsFragmentToQuizFragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        loadDataFromViewModel();
    }


    private void loadDataFromViewModel() {
        quizViewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);
        quizViewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), quizList -> {
            Quiz currentQuiz = quizList.get(position);
            Glide.with(getContext()).load(currentQuiz.getImage()).into(binding.imageViewDetails);
            binding.textViewDetailsTitle.setText(currentQuiz.getName());
            binding.textViewDetailsDescription.setText(currentQuiz.getDescription());
            binding.textViewDetailsFragmentDifficultyResult.setText(currentQuiz.getLevel());
            binding.textViewDetailsFragmentTotalQuestionsResult.setText(String.valueOf(currentQuiz.getQuestions()));
            quizID = currentQuiz.getId();
            quizName = currentQuiz.getName();
            totalQuestions = (int) currentQuiz.getQuestions();
            loadResultFromFirebase();
        });
    }

    private void loadResultFromFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("QuizList")
                .document(quizID)
                .collection("Results")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Result result = task.getResult().toObject(Result.class);
                        if (result != null) {
                            int lastScore = (result.getCorrect_answer() * 100) / (result.getCorrect_answer() + result.getWrong_answer() + result.getNot_answered());
                            binding.textViewDetailsFragmentYourLastScoreResult.setText(lastScore+"%");
                        }
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}