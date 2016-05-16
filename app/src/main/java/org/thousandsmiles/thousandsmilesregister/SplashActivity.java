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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIME_OUT = 5000;
    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_splash);
        context = this.getApplicationContext();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView text = (TextView) findViewById(R.id.versiontext);
            text.setText("Version " + version);
        }
        catch(PackageManager.NameNotFoundException e) {

        }

        final boolean b = new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                final ClinicDataREST clinicDataREST = new ClinicDataREST(context);

                final Object lock;

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                lock = clinicDataREST.getClinicData(year, month, day);

                final Thread thread = new Thread() {
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

                        ClinicData data = ClinicData.getInstance();
                        if (clinicDataREST.getStatus() == 200) {
                            int id = data.getId();
                            Intent i = new Intent(SplashActivity.this, LetterSelectActivity.class);
                            startActivity(i);
                            finish();
                            return;
                        } else if (clinicDataREST.getStatus() == 101) {
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (clinicDataREST.getStatus() == 404) {
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        // error, loop - don't exit application

                        // Execute some code after 2 seconds have passed
                        Intent i = new Intent(SplashActivity.this, SplashActivity.class);
                        startActivity(i);
                        finish();

                        return;
                    }
                };

                thread.start();

            }
        }, SPLASH_TIME_OUT);
    }
}

