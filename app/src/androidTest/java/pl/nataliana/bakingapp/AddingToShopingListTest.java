package pl.nataliana.bakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Natalia Nazaruk on 14.04.2018.
 */

@RunWith(AndroidJUnit4.class)
public class AddingToShopingListTest {

    @Rule
    public ActivityTestRule<DetailActivity> mActivityTestRule = new ActivityTestRule<>(DetailActivity.class);

    @Test
    public void checkIngredientButton_MarkItAsChecked() {
        onView(withId(R.id.ll_ingredient_checklist))
                .perform(click())
                .check(matches(isClickable()));
    }
}
