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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class LetterSelectActivity extends ActionBarActivity {

    private boolean m_processing;
    private ArrayList<Volunteer> m_volunteers;
    private Activity m_activity = this;

    @Override
    protected void onResume() {
        super.onResume();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }

/*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
            super.onWindowFocusChanged(hasFocus);
            HideyHelper h = new HideyHelper();
            h.toggleHideyBar(this);
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_letter_select, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void handleButtonPress(View view) {
        if (m_processing == true) {
            return;
        }
        m_processing = true;
        Button b = (Button) view;
        final String buttonText = b.getText().toString();

        new Thread(new Runnable() {
            public void run() {
                final VolunteerREST x = new VolunteerREST(m_activity.getApplicationContext());
                final Object lock = x.getVolunteers(buttonText);

                Thread thread = new Thread(){
                    public void run() {
                        synchronized (lock) {
                            // we loop here in case of race conditions or spurious interrupts
                            while (true) {
                                try {
                                    lock.wait();
                                    break;
                                } catch (InterruptedException e) {
                                    continue;
                                }
                            }
                        }

                        if (x.getStatus() == 200) {

                            Intent intent = new Intent(m_activity, NameSelectActivity.class);

                            intent.putParcelableArrayListExtra("volunteers", x.getVolunteersResult());
                            startActivity(intent);
                            m_processing = false;

                            return;
                        } else if (x.getStatus() == 101) {
                            LetterSelectActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (x.getStatus() == 404) {
                            LetterSelectActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            LetterSelectActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        //finish();
                        m_processing = false;
                    }
                };

                thread.start();

            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_letter_select);
    }
}
