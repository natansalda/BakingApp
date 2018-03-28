package pl.nataliana.bakingapp.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import pl.nataliana.bakingapp.databinding.RecipeListItemBinding;

import java.util.ArrayList;

import pl.nataliana.bakingapp.R;
import pl.nataliana.bakingapp.model.Recipe;
import pl.nataliana.bakingapp.util.OnItemClickListener;

/**
 * Created by natalia.nazaruk on 21.03.2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private ArrayList<Recipe> mRecipeList;
    private OnItemClickListener mRecipeOnItemClickListener;

    public RecipeAdapter(ArrayList<Recipe> parsedRecipes, OnItemClickListener<Recipe> recipeOnItemClickListener) {
        this.mRecipeList = parsedRecipes;
        this.mRecipeOnItemClickListener = recipeOnItemClickListener;
    }

    @Override
    public RecipeAdapter.RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecipeListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.recipe_list_item, parent, false);
        return new RecipeHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeHolder holder, int position) {
        holder.mRecipe = mRecipeList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(holder.mRecipe.getImage())
                .placeholder(R.drawable.plate)
                .error(R.drawable.plate)
                .dontAnimate()
                .into(holder.mBinding.ivRecipeImage);
        holder.mBinding.tvRecipeTitle.setText(holder.mRecipe.getName());
        holder.mBinding.tvRecipeTitle.setSelected(true);
        holder.mBinding.tvRecipeTitle.setHorizontallyScrolling(true);
        holder.mBinding.tvIngredientCount.setText(String.valueOf(holder.mRecipe.getIngredients().size()));
        holder.mBinding.tvServingCount.setText(String.valueOf(holder.mRecipe.getServings()));
        holder.mBinding.tvStepCount.setText(String.valueOf(holder.mRecipe.getSteps().size()));
    }

    @Override
    public int getItemCount() {
        return (mRecipeList == null) ? 0 : mRecipeList.size();
    }

    public class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecipeListItemBinding mBinding;
        private Recipe mRecipe;

        public RecipeHolder(RecipeListItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.cvRecipeList.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mRecipeOnItemClickListener.onClick(mRecipe, v);
        }
    }
}
