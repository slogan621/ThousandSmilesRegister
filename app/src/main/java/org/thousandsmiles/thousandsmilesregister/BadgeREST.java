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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by syd on 6/24/15.
 */
public class BadgeREST extends RESTful {
    private Context m_context;
    private final Object m_lock = new Object();

    private class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {

            synchronized (m_lock) {
                BadgeImage data;
                data = BadgeImage.getInstance();
                setStatus(200);
                try {
                    byte[] decodedString;
                    byte[] respBytes;
                    String str = response.getString("badge");
                    respBytes = str.getBytes();
                    decodedString = Base64.decode(respBytes, Base64.NO_WRAP);
                    InputStream inputStream  = new ByteArrayInputStream(decodedString);
                    Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
                    data.setBitmap(bitmap);
                    data.setBase64(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m_lock.notify();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

            synchronized (m_lock) {
                if (error.networkResponse == null) {
                    if (error.getCause() instanceof java.net.ConnectException || error.getCause() instanceof  java.net.UnknownHostException) {
                        setStatus(101);
                    } else {
                        setStatus(-1);
                    }
                } else {
                   setStatus(error.networkResponse.statusCode);
                }
                m_lock.notify();
            }
        }
    }

    public class AuthJSONObjectRequest extends JsonObjectRequest
    {
        public AuthJSONObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener listener, ErrorListener errorListener)
        {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            headers.put("Authorization", getDBAPIToken());
            return headers;
        }
    }

    public BadgeREST(Context context) {
        m_context = context;
    }

    public Object getBadgeImage(BadgeContent badge) {

        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();

        String url = String.format("%s:%s/badges/", getDBAPIHost(), getDBAPIPort());

        JSONObject data = new JSONObject();

        try {
            data.put("date", badge.getDate());
            data.put("name", badge.getName());
            data.put("job", badge.getJob());
            data.put("bus", badge.getBusPaid());
            data.put("dinner", badge.getDinnerPaid());
            data.put("hotel", badge.getHotelPaid());
        } catch(Exception e) {
            // not sure this would ever happen, ignore. Continue on with the request with the expectation it fails
            // because of the bad JSON sent
        }

        AuthJSONObjectRequest request = new AuthJSONObjectRequest(Request.Method.POST, url, data,  new ResponseListener(), new ErrorListener());

        queue.add((JsonObjectRequest) request);

        return m_lock;
    }
}
