package com.example.firequiz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.firequiz.R;
import com.example.firequiz.databinding.FragmentResultBinding;
import com.example.firequiz.models.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String quizID;
    private String currentUserId;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        binding.buttonGoToHome.setOnClickListener(v -> goToListFragment());
        return binding.getRoot();
    }

    private void goToListFragment() {
        navController.navigate(R.id.action_resultFragment_to_listFragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        getSafeArgs();
        getFirebaseData();

    }

    private void getSafeArgs() {
        quizID = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();
    }

    private void getFirebaseData() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }
        firebaseFirestore
                .collection("QuizList")
                .document(quizID)
                .collection("Results")
                .document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Result currentResult = task.getResult().toObject(Result.class);
                        setResults(currentResult);
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setResults(Result currentResult) {
        binding.textViewCorrectAnswersResult.setText(String.valueOf(currentResult.getCorrect_answer()));
        binding.textViewWrongAnswersResult.setText(String.valueOf(currentResult.getWrong_answer()));
        binding.textViewQuestionsMissedResult.setText(String.valueOf(currentResult.getNot_answered()));
        int correctAnswers =  currentResult.getCorrect_answer();
        int wrongAnswers =  currentResult.getWrong_answer();
        int notAnswered =  currentResult.getNot_answered();
        int percentResult = (correctAnswers * 100) / (correctAnswers + wrongAnswers + notAnswered);
        binding.textViewPercents.setText(percentResult+"%");
        binding.progressBarResultFragment.setProgress(percentResult);
    }
}