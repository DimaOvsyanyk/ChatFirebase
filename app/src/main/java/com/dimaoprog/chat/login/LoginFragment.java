package com.dimaoprog.chat.login;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.LoginFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.dimaoprog.chat.Constants.LOG;

public class LoginFragment extends Fragment {

    private LoginViewModel lViewModel;
    private LoginFragmentBinding binding;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private SignInListener signInListener;
    private DatabaseReference database;

    public interface SignInListener {
        void checkUser();
    }

    public void setSignInListener(SignInListener signInListener) {
        this.signInListener = signInListener;
    }

    public static LoginFragment newInstance(SignInListener signInListener) {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        fragment.setSignInListener(signInListener);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        lViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        auth = FirebaseAuth.getInstance();
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
        binding.setLoginModel(lViewModel);

        lViewModel.getUserNameOk().observe(this, aBoolean -> binding.etUserName.setError(aBoolean ? null : "enter your name"));
        lViewModel.getEmailOk().observe(this, aBoolean -> binding.etEMail.setError(aBoolean ? null : "invalid e-mail"));
        lViewModel.getPasswordOk().observe(this, bBoolean -> binding.etPassword.setError(bBoolean ? null : "invalid password"));

        binding.btnLogin.setOnClickListener(__ -> signIn());
        binding.btnCheckVerification.setOnClickListener(__ -> checkVerification());

        database = FirebaseDatabase.getInstance().getReference();
        setupProgressDialog();
        return binding.getRoot();
    }

    private void checkVerification() {
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().reload().addOnCompleteListener(__ -> {
                lViewModel.setNeedVerification(!auth.getCurrentUser().isEmailVerified());
                if (auth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(getContext(), "E-mail verified", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showProgressDialog(boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Checking......");
        progressDialog.setMessage("Please wait");
    }

    private void createAccount() {
        Log.d(LOG, "createAccount:" + lViewModel.getEmail());
        if (lViewModel.inputFalse()) {
            return;
        }
        showProgressDialog(true);
        auth.createUserWithEmailAndPassword(lViewModel.getEmail(), lViewModel.getPassword())
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerification(auth.getCurrentUser());
                        database.child("users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("user_name").setValue(lViewModel.getUserName());
                        Log.d(LOG, "createUserWithEmail:success" + Objects.requireNonNull(auth.getCurrentUser()).getUid());
                    } else {
                        Log.w(LOG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                    showProgressDialog(false);
                });
    }

    private void sendEmailVerification(FirebaseUser newUser) {
        Objects.requireNonNull(newUser).sendEmailVerification()
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Verification email sent to " + newUser.getEmail(), Toast.LENGTH_SHORT).show();
                        lViewModel.setNeedVerification(true);
                        Log.d(LOG, "setNeedVerification=true");
                    } else {
                        Log.e(LOG, "sendEmailVerification", task.getException());
                        Toast.makeText(getContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIn() {
        Log.d(LOG, "signIn:" + lViewModel.getEmail());
        if (lViewModel.inputFalse()) {
            return;
        }
        showProgressDialog(true);
        auth.signInWithEmailAndPassword(lViewModel.getEmail(), lViewModel.getPassword())
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG, "signInWithEmail:success");
                        if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()) {
                            signInListener.checkUser();
                        } else {
                            lViewModel.setNeedVerification(true);
                            Log.d(LOG, "setNeedVerification=true");
                        }
                    } else {
                        Log.w(LOG, "signInWithEmail:failure", task.getException());
                        createAccount();
                    }
                    showProgressDialog(false);
                });
    }

}
