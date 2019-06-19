package com.dimaoprog.chat.login;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.dimaoprog.chat.Constants.LOG;

public class LoginViewModel extends ViewModel {

    private String email;
    private String password;
    private String userName;
    private ObservableBoolean needVerification = new ObservableBoolean();

    private MutableLiveData<Boolean> emailOk = new MutableLiveData<>();
    private MutableLiveData<Boolean> passwordOk = new MutableLiveData<>();
    private MutableLiveData<Boolean> userNameOk = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgressDialog = new MutableLiveData<>();
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> needUserName = new MutableLiveData<>();

    private LoginFragment.SignInListener signInListener;
    private FirebaseAuth auth;
    private DatabaseReference database;

    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void signIn() {
        if (inputFalse()) {
            return;
        }
        setShowProgressDialog(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkVerification();
                    } else {
                        Log.w(LOG, "signInWithEmail:failure", task.getException());
                        setNeedUserName(!task.isSuccessful());
                    }
                    setShowProgressDialog(false);
                });
    }

    public void createAccount() {
        Log.d(LOG, "createAccount:" + email);
        if (!setUserNameOk()) {
            return;
        }
        setShowProgressDialog(true);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    FirebaseUser newUser = Objects.requireNonNull(auth.getCurrentUser());
                    if (task.isSuccessful()) {
                        newUser.updateProfile(getUserNameChangeReq()).addOnCompleteListener(task1 -> sendEmailVerification(newUser));
                        database.child(newUser.getUid()).child("user_name").setValue(userName);
                    } else {
                        Log.w(LOG, "createUserWithEmail:failure", task.getException());
                        setToastMessage("Authentication failed");
                    }
                    setNeedUserName(false);
                    setShowProgressDialog(false);
                });
    }

    private UserProfileChangeRequest getUserNameChangeReq() {
        return new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
    }

    private void sendEmailVerification(FirebaseUser newUser) {
        newUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setToastMessage("Verification email sent to " + newUser.getEmail());
                        setNeedVerification(true);
                    } else {
                        Log.e(LOG, "sendEmailVerification", task.getException());
                        setToastMessage("Failed to send verification email");
                    }
                });
    }

    public void checkVerification() {
        setShowProgressDialog(true);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(__ -> {
                setNeedVerification(!currentUser.isEmailVerified());
                if (currentUser.isEmailVerified()) {
                    signInListener.checkUser();
                } else {
                    setToastMessage("Wait for verification");
                }
                setShowProgressDialog(false);
            });
        }
    }

    private boolean inputFalse() {
        return !(setEmailOk() & setPasswordOk());
    }

    public MutableLiveData<Boolean> getEmailOk() {
        return emailOk;
    }

    public MutableLiveData<Boolean> getPasswordOk() {
        return passwordOk;
    }

    private boolean setEmailOk() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        this.emailOk.setValue(email.matches(emailPattern));
        return email.matches(emailPattern);
    }

    public boolean setPasswordOk() {
        this.passwordOk.setValue(password.length() > 5);
        return password.length() > 5;
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

    public boolean setUserNameOk() {
        this.userNameOk.setValue(userName.length() > 3);
        return userName.length() > 3;
    }

    public ObservableBoolean getNeedVerification() {
        return needVerification;
    }

    public void setNeedVerification(boolean needVerification) {
        this.needVerification.set(needVerification);
    }

    public MutableLiveData<Boolean> getShowProgressDialog() {
        return showProgressDialog;
    }

    public void setShowProgressDialog(boolean showProgressDialog) {
        this.showProgressDialog.setValue(showProgressDialog);
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String toastMessage) {
        this.toastMessage.setValue(toastMessage);
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

    public void setSignInListener(LoginFragment.SignInListener signInListener) {
        this.signInListener = signInListener;
    }

    public MutableLiveData<Boolean> getNeedUserName() {
        return needUserName;
    }

    public void setNeedUserName(boolean needUserName) {
        this.needUserName.setValue(needUserName);
    }
}
