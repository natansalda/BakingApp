package pl.nataliana.bakingapp.util;

import android.view.View;

/**
 * Created by natalia.nazaruk on 21.03.2018.
 */

public interface OnItemClickListener<MODEL> {
    void onClick(MODEL model, View view);
}