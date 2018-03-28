package pl.nataliana.bakingapp.presenter;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.nataliana.bakingapp.R;
import pl.nataliana.bakingapp.model.Recipe;
import pl.nataliana.bakingapp.networking.NetworkService;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class MainMenuPresenter implements MainMenuPresenterViewContract.Presenter {

    private final String TAG = MainMenuPresenter.class.getSimpleName();
    private final MainMenuPresenterViewContract.View mView;
    private final NetworkService mNetworkService;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public interface Callbacks {
        void openRecipeDetails(Recipe recipe);
    }

    public MainMenuPresenter(MainMenuPresenterViewContract.View view,
                               NetworkService networkService) {
        this.mView = view;
        this.mNetworkService = networkService;
    }

    @Override
    public void parseRecipes() {
        Observable<ArrayList<Recipe>> retrofitObserver;
        retrofitObserver = this.mNetworkService.networkApiRequestRecipes();
        retrofitObserver.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(networkApiRecipeObserver());
    }

    private Observer<ArrayList<Recipe>> networkApiRecipeObserver() {
        return new Observer<ArrayList<Recipe>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(ArrayList<Recipe> networkRecipeResult) {
                Log.d(TAG, "networkApiRecipeObserver.onNext");
                ArrayList<Recipe> recipeList = new ArrayList<>();
                recipeList.addAll(networkRecipeResult);
                if(mView.isActive()) {
                    mView.updateAdapter(recipeList);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "networkApiRecipeObserver.onError");
                if(mView.isActive()) {
                    mView.displayErrorMessage(R.string.error_message);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "networkApiRecipeObserver.onComplete");
            }
        };
    }

    @Override
    public void recipeChosen(Recipe recipe, View view) {
        ((Callbacks) view.getContext()).openRecipeDetails(recipe);
    }

    @Override
    public void viewDestroy() {
        mCompositeDisposable.clear();
    }
}
