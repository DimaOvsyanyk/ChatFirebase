package com.dimaoprog.chat.login;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dimaoprog.chat.MyProgressDialog;
import com.dimaoprog.chat.R;
import com.dimaoprog.chat.databinding.LoginFragmentBinding;
import com.dimaoprog.chat.databinding.UserNameDialogBinding;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private LoginViewModel lViewModel;
    private LoginFragmentBinding binding;
    private UserNameDialogBinding dialogBinding;
    private SignInListener signInListener;
    private MyProgressDialog progressDialog;
    private AlertDialog userNameDialog;

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

        if (savedInstanceState == null) {
            lViewModel.setSignInListener(signInListener);
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
        binding.setLoginModel(lViewModel);
        progressDialog = new MyProgressDialog(getContext());
        subscribeLiveDataObservers();

        return binding.getRoot();
    }

    private void subscribeLiveDataObservers() {
        lViewModel.getToastMessage().observe(getViewLifecycleOwner(), text -> showToast(text));
        lViewModel.getShowProgressDialog().observe(getViewLifecycleOwner(), show -> progressDialog.showMyProgressDialog(show));
        lViewModel.getEmailOk().observe(getViewLifecycleOwner(), ok -> binding.etEMail.setError(ok ? null : "invalid e-mail"));
        lViewModel.getPasswordOk().observe(getViewLifecycleOwner(), ok -> binding.etPassword.setError(ok ? null : "invalid password"));
        lViewModel.getNeedUserName().observe(getViewLifecycleOwner(), needName -> showUserNameDialog(needName));
        lViewModel.getUserNameOk().observe(getViewLifecycleOwner(), ok -> dialogBinding.etUserName.setError(ok ? null : "very short name"));
    }

    private void showUserNameDialog(boolean needName) {
        if (needName) {
            if (userNameDialog != null) {
                userNameDialog.show();
            } else {
                buildUserDialog();
            }
        } else {
            userNameDialog.dismiss();
        }
    }

    private void buildUserDialog() {
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.user_name_dialog, null, false);
        dialogBinding.setLoginModel(lViewModel);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .setTitle("You are not registered, enter your name")
                .setPositiveButton("OK", (dialog, which) -> lViewModel.createAccount())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        userNameDialog = dialogBuilder.create();
        userNameDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
