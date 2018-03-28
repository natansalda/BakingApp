package pl.nataliana.bakingapp.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

import pl.nataliana.bakingapp.databinding.FragmentRecipeDetailBinding;

import pl.nataliana.bakingapp.R;
import pl.nataliana.bakingapp.adapter.RecipeStepAdapter;
import pl.nataliana.bakingapp.model.Ingredient;
import pl.nataliana.bakingapp.model.Recipe;
import pl.nataliana.bakingapp.model.RecipeStep;
import pl.nataliana.bakingapp.presenter.DetailPresenter;
import pl.nataliana.bakingapp.presenter.DetailPresenterViewContract;
import pl.nataliana.bakingapp.util.OnItemClickListener;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class DetailFragment extends Fragment implements DetailPresenterViewContract.View {

    private static final String BUNDLE_DATA = "pl.nataliana.bakingapp.data";
    private final String ADAPTER_POSITION = "adapter_position";
    private final String RECIPE = "recipe";
    private final String INGREDIENTS_ID = "ingredients_id";
    private final String INGREDIENTS_COUNT = "ingredients_count";
    private final String RECIPE_STEPS = "recipe_steps";

    private ShareActionProvider mShareActionProvider;
    private FragmentRecipeDetailBinding binding;
    private Recipe mRecipe;
    private DetailPresenter mDetailPresenter;
    private RecyclerView mStepRecyclerView;
    private RecipeStepAdapter mRecipeStepAdapter;
    private int mRecipeStepAdapterSavedPosition = 0;
    private ArrayList<RecipeStep> mRecipeStepList = new ArrayList<>();
    private ArrayList<CheckBox> mIngredientList = new ArrayList<>();

    public static DetailFragment newInstance(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_DATA, recipe);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if ((arguments != null) && (arguments.containsKey(BUNDLE_DATA))) {
            mRecipe = arguments.getParcelable(BUNDLE_DATA);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(ADAPTER_POSITION)) {
            mRecipeStepAdapterSavedPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE)) {
                mRecipe = savedInstanceState.getParcelable(RECIPE);
            }

            if (savedInstanceState.containsKey(RECIPE_STEPS)) {
                mRecipeStepList = savedInstanceState.getParcelableArrayList(RECIPE_STEPS);
            }

            if (savedInstanceState.containsKey(INGREDIENTS_COUNT)) {
                int ingredientCount = savedInstanceState.getInt(INGREDIENTS_COUNT);
                for (int i = 0; i < ingredientCount; i++) {
                    CheckBox checkBox = new CheckBox(this.getContext());
                    checkBox.setText(String.valueOf(mRecipe.getIngredients().get(i).getQuantity()) +
                            String.valueOf(mRecipe.getIngredients().get(i).getMeasure()) + " " + mRecipe.getIngredients().get(i).getIngredient());
                    checkBox.setChecked(savedInstanceState.getBoolean(INGREDIENTS_ID + String.valueOf(i)));
                    mIngredientList.add(checkBox);
                }
            }
        }

        mDetailPresenter = new DetailPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);
        final View view = binding.getRoot();
        binding.tbToolbar.toolbar.setTitle(mRecipe.getName());
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.tbToolbar.toolbar);

        if (mIngredientList.size() > 0) {
            for (CheckBox cbIngredientView : mIngredientList) {
                binding.llIngredientChecklist.addView(cbIngredientView);
            }
        } else {
            if (mRecipe.getIngredients() != null && mRecipe.getIngredients().size() > 0) {
                for (Ingredient ingredient : mRecipe.getIngredients()) {
                    CheckBox checkBox = new CheckBox(this.getContext());
                    checkBox.setText(String.valueOf(ingredient.getQuantity()) +
                            String.valueOf(ingredient.getMeasure()) + " " + ingredient.getIngredient());
                    binding.llIngredientChecklist.addView(checkBox);
                    mIngredientList.add(checkBox);
                }
            }
        }

        if (mRecipe.getSteps() != null && mRecipe.getSteps().size() > 0 && mRecipeStepList.size() == 0) {
            mRecipeStepList.addAll(mRecipe.getSteps());
        }

        mStepRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_detail_steps_recycler_view);
        mStepRecyclerView.setHasFixedSize(true);
        mStepRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mRecipeStepAdapter = new RecipeStepAdapter(mRecipeStepList, new OnItemClickListener<RecipeStep>() {

            @Override
            public void onClick(RecipeStep recipeStep, View view) {
                mDetailPresenter.recipeStepClicked(mRecipeStepList, recipeStep.getId(), mRecipe.getName(), view);
            }
        });

        mRecipeStepAdapter.setStepAdapterCurrentPosition(mRecipeStepAdapterSavedPosition);
        mStepRecyclerView.setAdapter(mRecipeStepAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.detail_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_detail);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        String msg = mRecipe.getName() + "\n" + "----\n" +
                getString(R.string.ingredients) + ":\n" + "----\n";
        for (CheckBox ingredient : mIngredientList) {
            msg = msg + ingredient.getText() + "\n";
        }

        msg += getString(R.string.steps) + ":\n" + "----\n";
        for (RecipeStep recipeStep : mRecipeStepList) {
            msg = msg + recipeStep.getShortDescription() + "\n";
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ADAPTER_POSITION, mRecipeStepAdapter.getStepAdapterCurrentPosition());
        outState.putParcelable(RECIPE, mRecipe);
        outState.putParcelableArrayList(RECIPE_STEPS, mRecipeStepList);
        outState.putInt(INGREDIENTS_COUNT, mIngredientList.size());
        for (int ingedientId = 0; ingedientId < mIngredientList.size(); ingedientId++) {
            outState.putBoolean(INGREDIENTS_ID + String.valueOf(ingedientId),
                    mIngredientList.get(ingedientId).isChecked());
        }
    }
}