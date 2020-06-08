package com.example.challengeup.viewModel;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.result.ICallback;
import com.example.challengeup.result.Result;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChallengesViewModel extends ViewModel {

    private ExecutorService mExecutor = Executors.newFixedThreadPool(4);
    private Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    public void getAllChallenges(ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = getAllChallenges();
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });
    }

    public void getUserById(String uid, ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = getUserByIdSync(uid);
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });
    }

    public void numberOfPeopleWhoAccepted(Challenge challenge, ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = numberOfPeopleWhoAcceptedSync(challenge);
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });

    }

    public void numberOfPeopleWhoComplete(Challenge challenge, ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = numberOfPeopleWhoCompleteSync(challenge);
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });

    }

    public Result getAllChallenges() {
        List<Challenge> challenges = Challenge.getAllChallenges();
        return new Result.Success<>(challenges);
    }

    public Result getUserByIdSync(String uid) {
        User user = User.getUserById(uid);
        return new Result.Success<>(user);
    }

    public Result numberOfPeopleWhoAcceptedSync(Challenge challenge){
        Long number = challenge.numberOfPeopleWhoAccepted();
        return new Result.Success<>(number);
    }

    public Result numberOfPeopleWhoCompleteSync(Challenge challenge){
        Long number = challenge.numberOfPeopleWhoComplete();
        return new Result.Success<>(number);
    }

    private void notifyResult(Result result, ICallback callback) {
        mainThreadHandler.post(() -> callback.onComplete(result));
    }
}
