package com.example.challengeup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.AchievementViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.AchievementFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends Fragment {

    private AchievementViewModel mViewModel;
    private MainActivityViewModel mainViewModel;
    private List<TrophyEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    private String id;
    private UserEntity user;

    private static final int collumnNumber = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new AchievementFactory(
                appContainer.mRequestExecutor
        )).get(AchievementViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.achievements_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), collumnNumber));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), GridLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);


        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        id = mainViewModel.getUser().getValue().getId();
//        id = "a";

        mViewModel.getUserById(id, result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                user = ((Result.Success<UserEntity>) result).data;

                mViewModel.getAchievementsAsTrophies(user, result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        mArrayList = ((Result.Success<List<TrophyEntity>>) result2).data;
                        mAdapter.setDone(true);
                        mAdapter.setDataset(mArrayList);
                        mAdapter.notifyItemRangeInserted(0, mArrayList.size());




                        mViewModel.getUndoneAchievementsAsTrophies(user, result3 -> {
                            if (result3 instanceof Result.Success) {
                                //noinspection unchecked
                                mArrayList = ((Result.Success<List<TrophyEntity>>) result3).data;
                                mAdapter.setDone(false);

                                int start = mAdapter.getItemCount();
                                mAdapter.mDataset.addAll(start,mArrayList);
                                mAdapter.notifyItemRangeInserted(start, mArrayList.size());
                            }
                        });
                    }
                });

            }
        });
    }



    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<TrophyEntity> mDataset;

        boolean done;

        public Adapter(@NonNull List<TrophyEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_achievements, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
            TrophyEntity trophy = mDataset.get(position);

            holder.name.setText(trophy.getName());

            if(done){
                //set done image
                //todo MAYBE set another image, do not change background
                holder.trophy.setImageResource(R.drawable.ic_trophy);
                holder.trophy.setBackgroundColor(getResources().getColor(R.color.trophyColor));
            }
            else{
                //set undone image
                holder.trophy.setImageResource(R.drawable.ic_trophy);
            }

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setDataset(List<TrophyEntity> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView trophy;
            TextView name;

            public MyViewHolder(View itemView) {
                super(itemView);

                trophy = itemView.findViewById(R.id.icon);
                name = itemView.findViewById(R.id.name);
            }
        }
    }

}
