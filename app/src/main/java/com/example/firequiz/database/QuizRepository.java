package com.example.firequiz.database;

import com.example.firequiz.models.Quiz;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class QuizRepository {

    private OnFirestoreTaskComplete onFirestoreTaskComplete;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference quizListRef = firebaseFirestore.collection("QuizList");

    public QuizRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public void getQuizList() {
        quizListRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onFirestoreTaskComplete.quizListAdded(task.getResult().toObjects(Quiz.class));
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }

    public interface OnFirestoreTaskComplete {
        void quizListAdded(List<Quiz> quizList);

        void onError(Exception e);
    }
}
