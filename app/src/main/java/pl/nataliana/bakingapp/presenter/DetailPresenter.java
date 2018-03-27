package pl.nataliana.bakingapp.presenter;


import android.view.View;

import java.util.ArrayList;

import pl.nataliana.bakingapp.model.RecipeStep;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class DetailPresenter implements DetailPresenterViewContract.Presenter {



    private final DetailPresenterViewContract.View mView;


    public interface Callbacks {
        void recipeStepSelected(ArrayList<RecipeStep> recipeStepList, int currentStep, String recipeName);
    }

    public DetailPresenter(DetailPresenterViewContract.View view) {
        this.mView = view;
    }

    @Override
    public void recipeStepClicked(ArrayList<RecipeStep> stepList, int currentStep, String recipeName, View view) {
        ((Callbacks) view.getContext()).recipeStepSelected(stepList, currentStep, recipeName);
    }
}
