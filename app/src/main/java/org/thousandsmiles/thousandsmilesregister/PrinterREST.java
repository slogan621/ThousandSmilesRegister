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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by syd on 6/24/15.
 */
public class PrinterREST extends RESTful {
    private Context m_context;
    private final Object m_lock = new Object();
    private JSONObject m_response;

    public JSONObject getResponse() {
        return m_response;
    }

    private class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {

            synchronized (m_lock) {
                BadgeImage data;
                data = BadgeImage.getInstance();
                setStatus(200);
                m_response = response;
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
            return headers;
        }
    }

    public PrinterREST(Context context) {
        m_context = context;
    }

    public String getPort() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(m_context);
        String val = sharedPref.getString("port", "");
        return val;
    }

    public String getIP() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(m_context);
        String val = sharedPref.getString("ipAddress", "");
        return val;
    }

    public Object printBadge() {

        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();


        String url = String.format("http://%s:%s/api/v1/zequs/enable/", getIP(), getPort());

        JSONObject data = new JSONObject();


        AuthJSONObjectRequest request = new AuthJSONObjectRequest(Request.Method.PUT, url, data,  new ResponseListener(), new ErrorListener());

        queue.add((JsonObjectRequest) request);

        return m_lock;
    }


    public Object printBadge2(BadgeImage bimg, String filenamePrefix) {

        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();


        String url = String.format("http://%s:%s/api/v1/zequs/", getIP(), getPort());

        JSONObject data = new JSONObject();

        try {

            data.put("data", bimg.getBase64());
            data.put("filename_prefix", filenamePrefix);

        } catch(Exception e) {
            // not sure this would ever happen, ignore. Continue on with the request with the expectation it fails
            // because of the bad JSON sent
        }

        AuthJSONObjectRequest request = new AuthJSONObjectRequest(Request.Method.POST, url, data,  new ResponseListener(), new ErrorListener());

        queue.add((JsonObjectRequest) request);

        return m_lock;
    }

    public Object printBadge3(Integer id) {

        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();

        String url = String.format("http://%s:%s/api/v1/zequs/%d/", getIP(), getPort(), id);


        JSONObject data = new JSONObject();


        AuthJSONObjectRequest request = new AuthJSONObjectRequest(Request.Method.GET, url, data,  new ResponseListener(), new ErrorListener());

        queue.add((JsonObjectRequest) request);

        return m_lock;
    }

    public Object printBadge4(Integer id) {

        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();

        String url = String.format("http://%s:%s/api/v1/zequs/%d/", getIP(), getPort(), id);


        JSONObject data = new JSONObject();


        AuthJSONObjectRequest request = new AuthJSONObjectRequest(Request.Method.DELETE, url, data,  new ResponseListener(), new ErrorListener());

        queue.add((JsonObjectRequest) request);

        return m_lock;
    }
}
