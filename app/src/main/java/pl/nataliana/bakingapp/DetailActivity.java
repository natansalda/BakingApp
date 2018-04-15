package pl.nataliana.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import pl.nataliana.bakingapp.fragment.DetailFragment;
import pl.nataliana.bakingapp.fragment.RecipeStepFragment;
import pl.nataliana.bakingapp.model.Recipe;
import pl.nataliana.bakingapp.model.RecipeStep;
import pl.nataliana.bakingapp.presenter.DetailPresenter;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class DetailActivity extends AppCompatActivity implements DetailPresenter.Callbacks {

    private static final String BUNDLE_DATA = "pl.nataliana.bakingapp.data";

    public static Intent myIntent(Context packageContext, Recipe recipe) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(BUNDLE_DATA, recipe);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        Recipe recipe = getIntent().getExtras().getParcelable(BUNDLE_DATA);
        if (fragment == null) {
            fragment = DetailFragment.newInstance(recipe);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();

            if (getResources().getBoolean(R.bool.isTablet)) {
                Fragment newDetail = RecipeStepFragment.newInstance(recipe.getSteps().get(0));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetail)
                        .commit();
            }
        }
    }

    @Override
    public void recipeStepSelected(ArrayList<RecipeStep> stepList, int currentStep, String recipeName) {
        if (!getResources().getBoolean(R.bool.isTablet)) {
            Intent intent = RecipeStepActivity.newIntent(this, stepList, currentStep, recipeName);
            startActivity(intent);
        } else {
            Fragment newDetail = RecipeStepFragment.newInstance(stepList.get(currentStep));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}