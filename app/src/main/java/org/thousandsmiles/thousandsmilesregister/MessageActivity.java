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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageActivity extends Activity {

    public static final long DISCONNECT_TIMEOUT = 10000; // 5 min = 5 * 60 * 1000 ms
    private static Activity m_activity;

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {

        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            Intent intent = new Intent(m_activity, LetterSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_activity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);

        final Intent intent = getIntent();

        TextView text = (TextView)findViewById(R.id.messageText);
        String str = intent.getStringExtra("message");

        text.setText(str);
        text.setTextSize(24.0f);

        text = (TextView)findViewById(R.id.detailText);
        str = intent.getStringExtra("details");

        text.setText(str);
        text.setTextSize(20.0f);

        ArrayList<String> labels = intent.getStringArrayListExtra("labels");

        /*
         * for each label, create a button. We will return the index of the
         * selected button and shutdown the activity when the user selects
         * one of the buttons.
         */

        /* add left view space here */

        View view = new View(this);
        LinearLayout ll = (LinearLayout)findViewById(R.id.messageButtonLinearLayout);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f);
        ll.addView(view, lp);

        for (String s : labels) {
        	 /* add button here */

            final Button button = new Button(this);
            button.setText(s);
            button.setTextSize(36);

            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            ll.addView(button, lp);
            final String label = s;
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    intent.putExtra("clickedLabel", label);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });

            view = new View(this);
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f);
            ll.addView(view, lp);

        	/* add view space here */
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }

    @Override
    public void onBackPressed() {
        // swallow back presses, force user to make a choice
    }
}

