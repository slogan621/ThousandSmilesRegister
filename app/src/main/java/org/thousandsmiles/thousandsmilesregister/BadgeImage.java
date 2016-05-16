/*
 * (C) Copyright Syd Logan 2016
 * (C) Copyright Thousand Smiles Foundation 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thousandsmiles.thousandsmilesregister;

import android.graphics.Bitmap;

/**
 * Created by slogan on 10/3/15.
 */
public class BadgeImage {
    private static BadgeImage instance;
    private Bitmap m_bitmap;
    private String m_base64;

    public void setBitmap(Bitmap bitmap) {
        m_bitmap = bitmap;
    }

    public void setBase64(String in) {
        m_base64 = in;
    }

    public Bitmap getBitmap() {
        return m_bitmap;
    }

    public String getBase64() { return m_base64; }

    public static BadgeImage getInstance() {
        if (instance == null) {
            instance = new BadgeImage();
        }
        return instance;
    }
}


