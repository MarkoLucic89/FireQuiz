package com.example.firequiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firequiz.R;
import com.example.firequiz.models.Quiz;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private OnQuizListItemClicked onQuizListItemClicked;

    public QuizAdapter(OnQuizListItemClicked onQuizListItemClicked) {
        this.onQuizListItemClicked = onQuizListItemClicked;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz currentQuiz = quizList.get(position);
        holder.textViewListItemQuizTitle.setText(currentQuiz.getName());
        holder.textViewListItemQuizDescription.setText(currentQuiz.getDescription());
        holder.textViewListItemQuizDifficulty.setText(currentQuiz.getLevel());
        Glide
                .with(holder.itemView.getContext())
                .load(currentQuiz.getImage())
                .into(holder.imageViewListItem);
    }

    @Override
    public int getItemCount() {
        if (quizList == null) {
            return 0;
        } else {
            return quizList.size();
        }
    }



    public class QuizViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewListItem;
        private TextView textViewListItemQuizTitle;
        private TextView textViewListItemQuizDescription;
        private TextView textViewListItemQuizDifficulty;
        private Button buttonViewQuiz;


        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewListItem = itemView.findViewById(R.id.imageViewListItem);
            textViewListItemQuizTitle = itemView.findViewById(R.id.textViewListItemQuizTitle);
            textViewListItemQuizDescription = itemView.findViewById(R.id.textViewListItemQuizDescription);
            textViewListItemQuizDifficulty = itemView.findViewById(R.id.textViewListItemQuizDifficulty);
            buttonViewQuiz = itemView.findViewById(R.id.buttonItemViewQuiz);

            buttonViewQuiz.setOnClickListener(v -> onQuizListItemClicked.onQuizItemClicked(getAdapterPosition()));
        }
    }

    public interface OnQuizListItemClicked {
        void onQuizItemClicked(int position);
    }
}
