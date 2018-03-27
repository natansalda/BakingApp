package pl.nataliana.bakingapp.networking;

import java.util.ArrayList;

import io.reactivex.Observable;
import pl.nataliana.bakingapp.model.Recipe;
import retrofit2.http.GET;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */


public interface ApiObservable {

    @GET(" ")
    Observable<ArrayList<Recipe>> getRecipeResult();
}
