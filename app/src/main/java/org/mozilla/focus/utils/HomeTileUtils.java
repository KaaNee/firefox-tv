/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.utils;

import android.content.Context;
import org.json.JSONObject;
import java.io.InputStream;

public class HomeTileUtils {
    public static JSONObject loadDefaultTiles(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("defaults/DefaultTiles.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
