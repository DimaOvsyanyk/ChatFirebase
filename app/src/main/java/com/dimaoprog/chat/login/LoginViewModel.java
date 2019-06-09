package com.dimaoprog.chat.login;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;

public class LoginViewModel extends ViewModel {

    private String email;
    private String password;
    private String userName;
    private MutableLiveData<Boolean> emailOk = new MutableLiveData<>();
    private MutableLiveData<Boolean> passwordOk = new MutableLiveData<>();
    private MutableLiveData<Boolean> userNameOk = new MutableLiveData<>();
    private ObservableBoolean needVerification = new ObservableBoolean();

    private boolean checkEMail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return setEmailOk(email.matches(emailPattern));
    }

    public boolean inputFalse() {
        return !(checkEMail() & setPasswordOk(password.length() > 5) & setUserNameOk(userName.length() > 2));
    }

    public MutableLiveData<Boolean> getEmailOk() {
        return emailOk;
    }

    public MutableLiveData<Boolean> getPasswordOk() {
        return passwordOk;
    }

    public boolean setEmailOk(boolean emailOk) {
        this.emailOk.setValue(emailOk);
        return emailOk;
    }

    public boolean setPasswordOk(boolean passwordOk) {
        this.passwordOk.setValue(passwordOk);
        return passwordOk;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MutableLiveData<Boolean> getUserNameOk() {
        return userNameOk;
    }

    public boolean setUserNameOk(boolean userNameOk) {
        this.userNameOk.setValue(userNameOk);
        return userNameOk;
    }

    public ObservableBoolean getNeedVerification() {
        return needVerification;
    }

    public void setNeedVerification(boolean needVerification) {
        this.needVerification.set(needVerification);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
