package pl.nataliana.bakingapp.fragment;

import android.support.v4.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import pl.nataliana.bakingapp.databinding.FragmentRecipeListBinding;

import java.util.ArrayList;

import pl.nataliana.bakingapp.R;
import pl.nataliana.bakingapp.adapter.RecipeAdapter;
import pl.nataliana.bakingapp.model.Recipe;
import pl.nataliana.bakingapp.networking.NetworkService;
import pl.nataliana.bakingapp.presenter.MainMenuPresenter;
import pl.nataliana.bakingapp.presenter.MainMenuPresenterViewContract;
import pl.nataliana.bakingapp.util.OnItemClickListener;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class MainMenuFragment extends Fragment implements MainMenuPresenterViewContract.View {

    private final String RECIPE_LIST = "recipe_list";
    private FragmentRecipeListBinding binding;
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private MainMenuPresenter mRecipeListPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE_LIST)) {
                mRecipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        final View view = binding.getRoot();
        binding.tbToolbar.toolbar.setTitle(getContext().getResources().getString(R.string.app_name));

        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        mRecipeRecyclerView.setHasFixedSize(true);
        mRecipeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.recipe_list_columns)));

        mRecipeListPresenter = new MainMenuPresenter(this, new NetworkService());
        mAdapter = new RecipeAdapter(mRecipeList, new OnItemClickListener<Recipe>() {

            @Override
            public void onClick(Recipe recipe, View view) {
                mRecipeListPresenter.recipeChosen(recipe, view);
            }
        });

        mRecipeRecyclerView.setAdapter(mAdapter);

        if (mRecipeList == null || mRecipeList.size() == 0) {
            mRecipeListPresenter.parseRecipes();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecipeListPresenter.viewDestroy();
    }

    @Override
    public void updateAdapter(ArrayList<Recipe> recipeList) {
        mRecipeList.clear();
        mRecipeList.addAll(recipeList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayErrorMessage(int stringResId) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.cl_list_container),
                getString(stringResId),
                Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RECIPE_LIST, mRecipeList);
        super.onSaveInstanceState(outState);
    }
}
