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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by syd on 6/24/15.
 */
public class VolunteerREST extends RESTful {
    ArrayList<Volunteer> m_list;
    private Context m_context;
    private final Object m_lock = new Object();

    private class ObjectResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {

            synchronized (m_lock) {
                m_lock.notify();
            }
        }
    }

    private class ArrayResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray response) {

            synchronized (m_lock) {

                setStatus(200);
                m_list = new ArrayList<Volunteer>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        m_list.add(new Volunteer(obj.getString("first_name"),
                                obj.getString("last_name"),
                                obj.getInt("pin"),
                                obj.getInt("user_id"),
                                obj.getInt("registration_id"),
                                obj.getString("job")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public VolunteerREST(Context context)  {
        m_context = context;
    }

    public Object getVolunteers(String letter) {
        ClinicData data = ClinicData.getInstance();
        int clinicId = data.getId();

        VolleySingleton volley = VolleySingleton.getInstance();

        volley.initQueueIf(m_context);

        RequestQueue queue = volley.getQueue();

        String url = String.format("%s:%s/clinics/%d/volunteers/initial-last/%s/", getDBAPIHost(), getDBAPIPort(), clinicId, letter);

        AuthJSONArrayRequest request = new AuthJSONArrayRequest(url, null,  new ArrayResponseListener(), new ErrorListener());

        queue.add((JsonArrayRequest) request);

        return m_lock;
    }

    public ArrayList<Volunteer> getVolunteersResult() {
        return m_list;
    }
}
