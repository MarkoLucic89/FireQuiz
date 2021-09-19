package com.example.firequiz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firequiz.database.QuizRepository;
import com.example.firequiz.models.Quiz;

import java.util.List;

public class QuizViewModel extends ViewModel implements QuizRepository.OnFirestoreTaskComplete {

    private QuizRepository quizRepository = new QuizRepository(this);
    private MutableLiveData<List<Quiz>> quizListLiveData = new MutableLiveData<>();

    public LiveData<List<Quiz>> getQuizListLiveData() {
        return quizListLiveData;
    }

    public QuizViewModel() {
        quizRepository.getQuizList();
    }

    @Override
    public void quizListAdded(List<Quiz> quizList) {
        quizListLiveData.setValue(quizList);
    }

    @Override
    public void onError(Exception e) {

    }
}
