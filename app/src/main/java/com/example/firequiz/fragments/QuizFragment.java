package com.example.firequiz.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.firequiz.R;
import com.example.firequiz.databinding.FragmentQuizBinding;
import com.example.firequiz.models.Question;
import com.example.firequiz.models.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;

    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private List<Question> questionList = new ArrayList<>();
    private List<Question> randomQuestionList = new ArrayList<>();

    //safe args
    private String quizID;
    private int totalQuestions;
    private String quizName;

    //vars
    private String TAG = "QUIZ_FRAGMENT";
    private NavController navController;
    private CountDownTimer countDownTimer;
    private boolean canAnswer = false;
    private int currentQuestionNumber = 0;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnswered = 0;
    private String currentUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        setListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        initFirebase();
        getSafeArgs();
        prepareQuiz();

    }

    private void prepareQuiz() {
        beforePrepareUI();
        prepareTimer();
    }

    private void prepareTimer() {
        countDownTimer = new CountDownTimer(5000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.textViewTime.setText(String.valueOf(millisUntilFinished / 1000 + 1));
                Long percent = millisUntilFinished / 50;
                binding.progressBar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                getQuestion();
            }
        };
        countDownTimer.start();
    }

    private void beforePrepareUI() {
        binding.imageButtonClose.setVisibility(View.INVISIBLE);
        binding.textViewLoadingQuiz.setText("Starting quiz in...");
        binding.textViewQuestion.setText("Loading quiz");
        binding.buttonAnswer1.setVisibility(View.INVISIBLE);
        binding.buttonAnswer2.setVisibility(View.INVISIBLE);
        binding.buttonAnswer3.setVisibility(View.INVISIBLE);
        binding.buttonAnswer4.setVisibility(View.INVISIBLE);
        binding.buttonNextQuestion.setVisibility(View.INVISIBLE);
        binding.textAnswerDescription.setVisibility(View.INVISIBLE);
    }


    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }
    }

    private void setListeners() {
        binding.imageButtonClose.setOnClickListener(v -> skipQuestion());
        binding.buttonAnswer1.setOnClickListener(v -> answerSelected(binding.buttonAnswer1));
        binding.buttonAnswer2.setOnClickListener(v -> answerSelected(binding.buttonAnswer2));
        binding.buttonAnswer3.setOnClickListener(v -> answerSelected(binding.buttonAnswer3));
        binding.buttonAnswer4.setOnClickListener(v -> answerSelected(binding.buttonAnswer4));
        binding.buttonNextQuestion.setOnClickListener(v -> nextQuestion(currentQuestionNumber));
    }

    private void skipQuestion() {
        countDownTimer.cancel();
        currentQuestionNumber++;
        notAnswered++;
        nextQuestion(currentQuestionNumber);
    }

    private void answerSelected(Button button) {
        if (checkAnswer(button)) {
            binding.textAnswerDescription.setText("CORRECT! \n" + randomQuestionList.get(currentQuestionNumber).getAnswer_description());
            button.setBackgroundColor(getResources().getColor(R.color.green));
            correctAnswers++;
        } else {
            binding.textAnswerDescription.setText("WRONG! \n" + randomQuestionList.get(currentQuestionNumber).getAnswer_description());
            button.setBackgroundColor(getResources().getColor(R.color.red));
            setCorrectAnswerColor();
            wrongAnswers++;
        }
        currentQuestionNumber++;
        countDownTimer.cancel();
        setUiAnswerSelected();
    }

    private boolean checkAnswer(Button button) {
        return randomQuestionList.get(currentQuestionNumber).getAnswer().equals(button.getText());
    }

    private void setCorrectAnswerColor() {
        if (checkAnswer(binding.buttonAnswer1)) {
            binding.buttonAnswer1.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (checkAnswer(binding.buttonAnswer2)) {
            binding.buttonAnswer2.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (checkAnswer(binding.buttonAnswer3)) {
            binding.buttonAnswer3.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (checkAnswer(binding.buttonAnswer4)) {
            binding.buttonAnswer4.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void setUiAnswerSelected() {
        binding.buttonAnswer1.setEnabled(false);
        binding.buttonAnswer2.setEnabled(false);
        binding.buttonAnswer3.setEnabled(false);
        binding.buttonAnswer4.setEnabled(false);
        binding.textAnswerDescription.setVisibility(View.VISIBLE);
        binding.buttonNextQuestion.setVisibility(View.VISIBLE);
        binding.buttonNextQuestion.setEnabled(true);
        if (currentQuestionNumber == totalQuestions) {
            binding.buttonNextQuestion.setText("Results");
        }
    }

    private void nextQuestion(int questionNumber) {
        if (questionNumber < totalQuestions) {
            setQuestionTextViews(questionNumber);
            setUiBeforeAnswer();
            startTimer(questionNumber);
        } else {
            goToResultFragment();
        }

    }

    private void goToResultFragment() {
        Result result = new Result(correctAnswers, wrongAnswers, notAnswered);

        firebaseFirestore.collection("QuizList")
                .document(quizID)
                .collection("Results")
                .document(currentUserId)
                .set(result)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuizFragmentDirections.ActionQuizFragmentToResultFragment actionQuizFragmentToResultFragment
                                = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                        actionQuizFragmentToResultFragment.setQuizId(quizID);
                        navController.navigate(actionQuizFragmentToResultFragment);
                    } else {
                        binding.textViewLoadingQuiz.setText(task.getException().getMessage());
                    }
                });
    }

    private void setQuestionTextViews(int questionNumber) {
        Question currentQuestion = randomQuestionList.get(questionNumber);
        binding.textViewQuestion.setText(currentQuestion.getQuestion());
        binding.textViewQuestionNumberResult.setText(String.valueOf(questionNumber + 1));
        binding.buttonAnswer1.setText(currentQuestion.getOption_a());
        binding.buttonAnswer2.setText(currentQuestion.getOption_b());
        binding.buttonAnswer3.setText(currentQuestion.getOption_c());
        binding.buttonAnswer4.setText(currentQuestion.getOption_d());
    }

    private void setUiBeforeAnswer() {
        binding.buttonAnswer1.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        binding.buttonAnswer2.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        binding.buttonAnswer3.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        binding.buttonAnswer4.setBackgroundColor(getResources().getColor(R.color.dark_grey));

        binding.buttonAnswer1.setVisibility(View.VISIBLE);
        binding.buttonAnswer2.setVisibility(View.VISIBLE);
        binding.buttonAnswer3.setVisibility(View.VISIBLE);
        binding.buttonAnswer4.setVisibility(View.VISIBLE);

        binding.buttonAnswer1.setEnabled(true);
        binding.buttonAnswer2.setEnabled(true);
        binding.buttonAnswer3.setEnabled(true);
        binding.buttonAnswer4.setEnabled(true);

        binding.textAnswerDescription.setVisibility(View.INVISIBLE);
        binding.buttonNextQuestion.setEnabled(false);
        binding.buttonNextQuestion.setVisibility(View.INVISIBLE);
    }

    private void startTimer(int questionNumber) {
        Long time = randomQuestionList.get(questionNumber).getTimer();
        binding.textViewTime.setText(String.valueOf(time));
        countDownTimer = new CountDownTimer(time * 1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.textViewTime.setText(String.valueOf(millisUntilFinished / 1000 + 1));
                Long percent = millisUntilFinished / (time * 10);
                binding.progressBar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                setUiAnswerSelected();
                setCorrectAnswerColor();
                binding.textAnswerDescription.setText("TIME UP!\n" + randomQuestionList.get(currentQuestionNumber).getAnswer_description());
                notAnswered++;
                currentQuestionNumber++;
            }
        };
        countDownTimer.start();
    }

    private void getQuestion() {
        firebaseFirestore.collection("QuizList")
                .document(quizID)
                .collection("Questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        questionList = task.getResult().toObjects(Question.class);
                        createRandomQuestionList();
                        binding.imageButtonClose.setVisibility(View.VISIBLE);
                        binding.textViewLoadingQuiz.setText(quizName);
                        nextQuestion(currentQuestionNumber);
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void getSafeArgs() {
        quizID = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        totalQuestions = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();
        quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();
    }

    private void createRandomQuestionList() {
        Random random = new Random();
        for (int i = 0; i < totalQuestions; i++) {
            int randomNumber = random.nextInt(totalQuestions);
            randomQuestionList.add(questionList.get(randomNumber));
            Log.d(TAG, "createRandomQuestionList: QUESTION " + (i + 1) + ": " + questionList.get(randomNumber).getQuestion());
            questionList.remove(randomNumber);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }
}