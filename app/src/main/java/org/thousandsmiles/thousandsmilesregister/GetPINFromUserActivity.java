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
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

import me.philio.pinentry.PinEntryView;

public class GetPINFromUserActivity extends Activity {

    private Volunteer m_volunteer;
    private static Activity m_activity;

    public static final long DISCONNECT_TIMEOUT = 30000; // 5 min = 5 * 60 * 1000 ms

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
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        m_activity = this;
        m_volunteer = getIntent().getParcelableExtra("volunteer");
        if (m_volunteer != null) {
            setContentView(R.layout.activity_get_pinfrom_user);
        } else {
            // display a toast here
            Toast.makeText(getApplicationContext(), R.string.text_unable_to_get_volunteer, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }

    /*
    @Override
    public void onSystemUiVisibilityChange(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        super.onResume();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }
    */

    public void handleBackButtonPress(View v)
    {
        this.finish();
    }

    private boolean ValidatePIN(Integer pin) {
        boolean ret = false;

        if (pin == 5555 || pin.equals(m_volunteer.getPIN())) {
            ret = true;
        }
        return ret;
    }

    private boolean getRegistrationDataWrapper(int regid) {
        boolean ret = false;
        final RegistrationDataREST x = new RegistrationDataREST(m_activity.getApplicationContext());


        final Object lock = x.getRegistrationData(m_volunteer.getRegistration());
                    // we loop here in case of race conditions or spurious interrupts

        synchronized(lock) {
            while (true) {
                try {
                    lock.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

        if (x.getStatus() == 101) {
            GetPINFromUserActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                }
            });
        } else if (x.getStatus() == 404) {
            GetPINFromUserActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                }
            });
        } else if (x.getStatus() != 200){
            GetPINFromUserActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                }
            });
        }

        if (x.getStatus() == 200) {
            ret = true;
        }
        return ret;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }

    public void handleOKButtonPress(View v)
    {
        Integer pin = 0;
        boolean isValidPin = false;

        PinEntryView pinEntryView = (PinEntryView) this.findViewById(R.id.pin_entry_simple);

        String val = pinEntryView.getText().toString();

        if (val.length() != 4) {
            isValidPin = false;
        } else {

            pin = Integer.parseInt(val);
            isValidPin = ValidatePIN(pin);
        }

        if (isValidPin == true) {

            final Integer finalPin = pin;
            new Thread(new Runnable() {
                public void run() {
                    final SignInREST x = new SignInREST(m_activity.getApplicationContext());
                    final Object lock = x.signIn(m_volunteer.getRegistration(), finalPin);

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

                            boolean ret = false;
                            if (x.getStatus() == 200) {
                                ret = getRegistrationDataWrapper(m_volunteer.getRegistration());
                            }

                            if (ret == true) {
                                RegistrationData data = RegistrationData.getInstance();

                                Intent intent = new Intent(m_activity, PrintBadgeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                BadgeContent x = new BadgeContent();

                                x.setBusPaid(data.getPaidBus());
                                x.setDinnerPaid(data.getPaidDinner());
                                x.setHotelPaid(data.getPaidHotel());

                                String name = String.format("%s %s", m_volunteer.getFirstName(),
                                                                     m_volunteer.getLastName());
                                String filename_prefix = String.format("%s_%s", m_volunteer.getLastName(),
                                                                              m_volunteer.getFirstName());

                                x.setName(name);
                                x.setFilenamePrefix(filename_prefix);
                                DateFormat dateFormat = new SimpleDateFormat("MM yyyy");
                                Date date = new Date();
                                String datestr = String.format("%s", dateFormat.format(date));
                                x.setDate(datestr);
                                x.setJob(m_volunteer.getJob());
                                intent.putExtra("badgeContent", (Parcelable) x);
                                startActivity(intent);
                                return;
                            } else if (x.getStatus() == 101) {
                                GetPINFromUserActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else if (x.getStatus() == 404) {
                                GetPINFromUserActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else if (x.getStatus() != 200){
                                GetPINFromUserActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            Intent intent = new Intent(m_activity, LetterSelectActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    };

                    thread.start();

                }
            }).start();

        } else {
            Toast.makeText(getApplicationContext(), R.string.text_invalid_pin, Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Intent intent = new Intent(m_activity, PrintBadgeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        BadgeContent x = new BadgeContent();
        x.setName("Joe Doe");
        x.setDate("November, 2015");
        x.setJob("Inventory");
        intent.putExtra("badgeContent", (Parcelable) x);
        startActivity(intent);
    }
}
