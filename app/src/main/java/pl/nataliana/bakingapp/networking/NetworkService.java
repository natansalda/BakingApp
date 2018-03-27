package pl.nataliana.bakingapp.networking;

import java.util.ArrayList;

import io.reactivex.Observable;
import pl.nataliana.bakingapp.model.Recipe;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class NetworkService {

    private ApiObservable mApiObservable;

    public NetworkService(){
        mApiObservable = ApiUtils.getApiObservable();
    }

    public Observable<ArrayList<Recipe>> networkApiRequestRecipes() {
        Observable observer = mApiObservable.getRecipeResult();
        return observer;
    }
}
