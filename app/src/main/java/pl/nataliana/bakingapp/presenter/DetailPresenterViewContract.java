package pl.nataliana.bakingapp.presenter;

import java.util.ArrayList;

import pl.nataliana.bakingapp.model.RecipeStep;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public interface DetailPresenterViewContract {

    interface View {}

    interface Presenter {
        void recipeStepClicked(ArrayList<RecipeStep> stepList, int currentStep,
                               String recipeName, android.view.View view);
    }
}
