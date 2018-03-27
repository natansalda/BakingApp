package pl.nataliana.bakingapp.networking;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by natalia.nazaruk on 20.03.2018.
 */

public class RetrofitClient {

    private static Retrofit retrofitRecipes = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofitRecipes==null) {
            retrofitRecipes = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return retrofitRecipes;
    }
}
