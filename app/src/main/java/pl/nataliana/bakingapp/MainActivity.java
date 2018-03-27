package pl.nataliana.bakingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;

import pl.nataliana.bakingapp.fragment.MainMenuFragment;
import pl.nataliana.bakingapp.model.Recipe;
import pl.nataliana.bakingapp.presenter.MainMenuPresenter;
import pl.nataliana.bakingapp.widget.WidgetService;

public class MainActivity extends MyCoreActivity implements MainMenuPresenter.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new MainMenuFragment();
    }

    @Override
    public void openRecipeDetails(Recipe recipe) {
        Intent intent = DetailActivity.myIntent(this, recipe);
        WidgetService.startActionUpdateRecipeWidgets(this, recipe);
        startActivity(intent);
    }
}
