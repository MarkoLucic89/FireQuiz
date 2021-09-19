package com.example.firequiz.models;

import com.google.firebase.firestore.DocumentId;

public class Result {

    @DocumentId
    private String result_id;
    private int correct_answer;
    private int wrong_answer;
    private int not_answered;

    public Result() {
    }

    public Result(int correct_answer, int wrong_answer, int not_answered) {
        this.correct_answer = correct_answer;
        this.wrong_answer = wrong_answer;
        this.not_answered = not_answered;
    }

    public String getResult_id() {
        return result_id;
    }

    public void setResult_id(String result_id) {
        this.result_id = result_id;
    }

    public int getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(int correct_answer) {
        this.correct_answer = correct_answer;
    }

    public int getWrong_answer() {
        return wrong_answer;
    }

    public void setWrong_answer(int wrong_answer) {
        this.wrong_answer = wrong_answer;
    }

    public int getNot_answered() {
        return not_answered;
    }

    public void setNot_answered(int not_answered) {
        this.not_answered = not_answered;
    }

    @Override
    public String toString() {
        return "Result{" +
                "result_id='" + result_id + '\'' +
                ", correct_answer=" + correct_answer +
                ", wrong_answer=" + wrong_answer +
                ", not_answered=" + not_answered +
                '}';
    }
}
