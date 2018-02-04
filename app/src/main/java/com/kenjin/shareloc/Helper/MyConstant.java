package com.kenjin.shareloc.Helper;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by kenjin on 02/12/17.
 */

public class MyConstant {

    public static final String URL_SEKOLAH = "http://ikalpitu.ps492.or.id/Member/";

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(
                    new View(activity).getWindowToken(), 0);
    }
}
