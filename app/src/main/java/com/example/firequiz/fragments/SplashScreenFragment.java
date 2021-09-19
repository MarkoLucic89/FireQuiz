package com.example.firequiz.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.firequiz.R;
import com.example.firequiz.databinding.FragmentSplashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding binding;
    private FirebaseAuth firebaseAuth;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(() -> {
            goToListFragment();
        }, 3000);

    }

    private void goToListFragment() {
        if (firebaseAuth.getCurrentUser() == null) {
            binding.textViewTextSplashScreenFragment.setText("Creating account");
            firebaseAuth.signInAnonymously()
                    .addOnSuccessListener(authResult -> {
                        binding.textViewTextSplashScreenFragment.setText("Account created");
                        navController.navigate(R.id.action_splashScreenFragment_to_listFragment);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show());
        } else {
            binding.textViewTextSplashScreenFragment.setText("Logged in");
            navController.navigate(R.id.action_splashScreenFragment_to_listFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}