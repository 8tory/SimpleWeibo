package com.sina.weibo.simple;

import de.devland.esperandro.Esperandro;
import android.content.Context;

public class AccessTokenPreferences {
    public static AccessToken create(Context context) {
        return Esperandro.getPreferences(AccessToken.class, context);
    }
}
