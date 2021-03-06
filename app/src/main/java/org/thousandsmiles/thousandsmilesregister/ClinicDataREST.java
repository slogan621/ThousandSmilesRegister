
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by syd on 6/24/15.
 */
public class ClinicDataREST extends RESTful {
    private Context m_context;
    private final Object m_lock = new Object();

    private class ResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {

            synchronized (m_lock) {
                ClinicData data = ClinicData.getInstance();
                setStatus(200);
                try {
                    data.setId(response.getInt("id"));
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
                ClinicData data = ClinicData.getInstance();
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
        public AuthJSONObjectRequest(int method, String url, JSONObject jsonRequest,Response.Listener listener, ErrorListener errorListener)
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

    public class AuthJSONArrayRequest extends JsonArrayRequest{

        public AuthJSONArrayRequest(String url, JSONArray jsonRequest,
                              Response.Listener<JSONArray> listener, ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public AuthJSONArrayRequest(String url, Response.Listener<JSONArray> listener,
                              Response.ErrorListener errorListener, String username, String password) {
            super(url, listener, errorListener);

        }

        private Map<String, String> headers = new HashMap<String, String>();
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            //return headers;
            Map headers = new HashMap();
            headers.put("Authorization", getDBAPIToken());
            return headers;
        }

    }

    public ClinicDataREST(Context context) {
        m_context = context;
    }

    public Object getClinicData(int year, int month, int day) {

        //year = 2016; month = 5; day = 1;
        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();

        String url = String.format("%s:%s/clinics/year/%d/month/%d/day/%d/", getDBAPIHost(), getDBAPIPort(), year, month, day);

        AuthJSONObjectRequest request = new AuthJSONObjectRequest(Request.Method.GET, url, null,  new ResponseListener(), new ErrorListener());

        queue.add((JsonObjectRequest) request);

        return m_lock;
    }
}
