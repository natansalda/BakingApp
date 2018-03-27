package pl.nataliana.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import pl.nataliana.bakingapp.databinding.ActivityRecipeStepViewpagerBinding;

import pl.nataliana.bakingapp.fragment.RecipeStepFragment;
import pl.nataliana.bakingapp.model.RecipeStep;

/**
 * Created by natalia.nazaruk on 21.03.2018.
 */

public class RecipeStepActivity extends AppCompatActivity {

    private static final String BUNDLE_STEP_DATA = "pl.nataliana.bakingapp.step_data";
    private static final String BUNDLE_CURRENT_RECIPE = "pl.nataliana.bakingapp.current_recipe";
    private static final String BUNDLE_CURRENT_STEP = "pl.nataliana.bakingapp.current_step";
    private ViewPager mViewPager;
    private ActivityRecipeStepViewpagerBinding binding;

    public static Intent newIntent(Context packageContext, ArrayList<RecipeStep> stepList,
                                   int currentStep, String recipeName) {
        Intent intent = new Intent(packageContext, RecipeStepActivity.class);
        intent.putExtra(BUNDLE_STEP_DATA, stepList);
        intent.putExtra(BUNDLE_CURRENT_STEP, currentStep);
        intent.putExtra(BUNDLE_CURRENT_RECIPE, recipeName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_step_viewpager);

        final ArrayList<RecipeStep> stepList = getIntent().getExtras().getParcelableArrayList(BUNDLE_STEP_DATA);
        final int currentStep = getIntent().getExtras().getInt(BUNDLE_CURRENT_STEP);
        String currentRecipeName = getIntent().getExtras().getString(BUNDLE_CURRENT_RECIPE);
        setSupportActionBar(binding.tbToolbar.toolbar);
        binding.tbToolbar.toolbar.setTitle(currentRecipeName);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_activity_step_viewpager);
        for (RecipeStep recipeStep : stepList) {
            tabLayout.addTab(tabLayout.newTab().setText(
                    String.format(getString(R.string.step_format), (recipeStep.getId() + 1))));

        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.tbToolbar.toolbar.setVisibility(View.GONE);
            binding.tlActivityStepViewpager.setVisibility(View.GONE);
        }

        mViewPager = (ViewPager) findViewById(R.id.vp_activity_step_viewpager);
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return RecipeStepFragment.newInstance(stepList.get(position));
            }

            @Override
            public int getCount() {
                return stepList.size();
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mViewPager.setCurrentItem(currentStep);
    }
}
