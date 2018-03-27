package pl.nataliana.bakingapp.networking;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class ApiUtils {

    private ApiUtils(){}

    public static final String BASE_URL_RECIPES = "http://go.udacity.com/android-baking-app-json/";

    public static ApiObservable getApiObservable() {
        return RetrofitClient.getClient(BASE_URL_RECIPES).create(ApiObservable.class);
    }
}
