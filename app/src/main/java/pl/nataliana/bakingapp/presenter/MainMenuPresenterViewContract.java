package pl.nataliana.bakingapp.presenter;

import android.support.annotation.StringRes;

import java.util.ArrayList;

import pl.nataliana.bakingapp.model.Recipe;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public interface MainMenuPresenterViewContract {

    interface View {
        void updateAdapter(ArrayList<Recipe> recipeList);
        void displayErrorMessage(@StringRes int stringResId);
        boolean isActive();
    }

    interface Presenter {
        void parseRecipes();
        void recipeChosen(Recipe recipe, android.view.View view);
        void viewDestroy();
    }
}
