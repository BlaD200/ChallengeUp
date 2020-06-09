package com.example.challengeup.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.MainActivity;
import com.example.challengeup.R;
import com.example.challengeup.backend.User;
import com.example.challengeup.databinding.ActivityMainBinding;
import com.example.challengeup.databinding.FragmentProfileBinding;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.ProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Profile extends Fragment {

    private FragmentProfileBinding mBinding;
    private MainActivityViewModel mMainActivityViewModel;
    private ProfileViewModel mProfileViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMainActivityViewModel = new ViewModelProvider(requireActivity())
                .get(MainActivityViewModel.class);
        mProfileViewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);
        mBinding.setViewModel(mProfileViewModel);
        mBinding.setLifecycleOwner(this);

        if (getArguments() != null && ProfileArgs.fromBundle(getArguments()).getUid() != null) {
            String uid = ProfileArgs.fromBundle(getArguments()).getUid();
            setupUI(Objects.requireNonNull(mMainActivityViewModel
                    .getUser().getValue()).getId().equals(uid));

            mMainActivityViewModel.getUserById(uid, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    User user = ((Result.Success<User>) result).data;
                    UserDTO userDTO = new UserDTO(user.getId(), user.getNick(),
                            user.getTag(), user.getEmail());
                    Bitmap avatar = user.getPhoto();
                    mProfileViewModel.setUser(userDTO);
                    mProfileViewModel.setUserAvatar(avatar);
                }
            });
        } else {
            setupUI(true);
            mProfileViewModel.setUser(mMainActivityViewModel.getUser().getValue());
            mProfileViewModel.setUserAvatar(mMainActivityViewModel.getUserAvatar().getValue());
        }
    }

    private void setupUI(boolean isMainUser) {
        mBinding.btnReport.setVisibility(isMainUser ? View.GONE : View.VISIBLE);
        mBinding.btnChallenge.setVisibility(isMainUser ? View.GONE : View.VISIBLE);
        mBinding.btnFollow.setVisibility(isMainUser ? View.GONE : View.VISIBLE);
        mBinding.iconSettings.setVisibility(!isMainUser ? View.GONE : View.VISIBLE);
    }
}
