package pl.nataliana.bakingapp.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import pl.nataliana.bakingapp.databinding.RecipeStepListItemBinding;

import java.util.ArrayList;

import pl.nataliana.bakingapp.R;
import pl.nataliana.bakingapp.model.RecipeStep;
import pl.nataliana.bakingapp.util.OnItemClickListener;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.StepHolder> {

    private ArrayList<RecipeStep> mStepList;
    private OnItemClickListener mStepOnItemClickListener;
    private int mSelectedPosition = 0;

    public RecipeStepAdapter(ArrayList<RecipeStep> incomingStepSet, OnItemClickListener<RecipeStep> StepOnItemClickListener) {
        this.mStepList = incomingStepSet;
        this.mStepOnItemClickListener = StepOnItemClickListener;
    }


    @Override

    public RecipeStepAdapter.StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecipeStepListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.recipe_step_list_item, parent, false);
        return new StepHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapter.StepHolder holder, int position) {
        holder.mRecipeStep = mStepList.get(position);
        holder.mBinding.cvStepItemHolder.setSelected(mSelectedPosition == position);

        if (holder.mRecipeStep.getVideoURL() != null && !holder.mRecipeStep.getVideoURL().matches("")) {
            Glide.with(holder.itemView.getContext())
                    .load(holder.mRecipeStep.getThumbnailURL())
                    .placeholder(R.drawable.video)
                    .error(R.drawable.video)
                    .dontAnimate()
                    .into(holder.mBinding.ivStepItemVideoThumb);
        } else {
            holder.mBinding.ivStepItemVideoThumb.setImageResource(R.drawable.nomovie);
        }
        holder.mBinding.tvStepListStepNumber.setText(String.valueOf(holder.mRecipeStep.getId() + 1) + ": ");
        holder.mBinding.tvStepListStepShortDesc.setText(holder.mRecipeStep.getShortDescription());
    }

    @Override
    public int getItemCount() {
        return (mStepList == null) ? 0 : mStepList.size();
    }

    public class StepHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecipeStepListItemBinding mBinding;
        private RecipeStep mRecipeStep;

        public StepHolder(RecipeStepListItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            binding.cvStepItemHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(mSelectedPosition);
            mSelectedPosition = getLayoutPosition();
            notifyItemChanged(mSelectedPosition);
            mStepOnItemClickListener.onClick(mRecipeStep, v);
        }
    }

    public int getStepAdapterCurrentPosition() {
        return mSelectedPosition;
    }

    public void setStepAdapterCurrentPosition(int savedPosition) {
        mSelectedPosition = savedPosition;
    }
}
