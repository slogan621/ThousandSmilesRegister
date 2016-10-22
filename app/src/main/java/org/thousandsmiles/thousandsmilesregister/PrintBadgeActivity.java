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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class PrintBadgeActivity extends ActionBarActivity {

    private BadgeContent m_badgeContent;
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

            ArrayList<String> labels = new ArrayList<String>();

            labels.add("OK");

            Intent intent = new Intent(m_activity, MessageActivity.class);
            intent.putExtra("labels", labels);
            intent.putExtra("message", "You have successfully signed in");
            intent.putExtra("details", "");

            startActivityForResult(intent, 0);

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

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        super.onResume();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        m_activity = this;
        //HideyHelper h = new HideyHelper();
        //h.toggleHideyBar(this);
        m_badgeContent = getIntent().getParcelableExtra("badgeContent");
        if (m_badgeContent != null) {
            setContentView(R.layout.activity_print_badge);
            getAndDisplayBadge();
        } else {
            // display a toast here
            Toast.makeText(getApplicationContext(), R.string.text_unable_to_get_badge_content, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_generate_badge, menu);
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


    public void handleBackButtonPress(View v)
    {
        this.finish();
    }


    private void printBadge3(final Integer id)
    {
        new Thread(new Runnable() {
            public void run() {
                final PrinterREST x = new PrinterREST(m_activity.getApplicationContext());
                final Object lock = x.printBadge3(id);

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

                            JSONObject res = x.getResponse();
                            Integer id, state = 0;

                            try {
                                id = res.getInt("id");
                                state = res.getInt("state");
                                if (state == 0) {

                                    final Integer jid = id;
                                    PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.text_printing_queued, Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    SystemClock.sleep(5000);
                                    printBadge3(jid);
                                } else if (state == 1) {
                                    final Integer jid = id;
                                    PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.text_printing_printing, Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    SystemClock.sleep(5000);
                                    printBadge3(jid);

                                } else if (state == 2) {
                                    PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.text_printing_failed, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else if (state == 3) {
                                    final Integer jid = id;
                                    printBadge4(jid);
                                    PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.text_printing_success, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                            catch(JSONException e) {
                            }


                            if (state == 2 || state == 3) {

                                final Integer rstate = state;

                                PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {

                                        ArrayList<String> labels = new ArrayList<String>();

                                        labels.add("OK");

                                        Intent intent = new Intent(m_activity, MessageActivity.class);
                                        intent.putExtra("labels", labels);
                                        if (rstate == 2) {
                                            intent.putExtra("message", m_activity.getBaseContext().getResources().getString(R.string.text_printing_failed));
                                            intent.putExtra("details", "Please have someone check the badge printer, then sign in once again.");
                                        } else {
                                            intent.putExtra("message", m_activity.getBaseContext().getResources().getString(R.string.text_printing_success));
                                            intent.putExtra("details", "Please proceed to the printer to pick up your ID badge.");
                                        }

                                        startActivityForResult(intent, 0);

                                    }
                                });
                            }
                            return;
                        } else if (x.getStatus() == 101) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (x.getStatus() == 404) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                };

                thread.start();

            }
        }).start();
    }

    private void printBadge2()
    {
        new Thread(new Runnable() {
            public void run() {
                final PrinterREST x = new PrinterREST(m_activity.getApplicationContext());
                final Object lock = x.printBadge2(BadgeImage.getInstance(), m_badgeContent.getFilenamePrefix());

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

                        //ClinicData data = ClinicData.getInstance();
                        if (x.getStatus() == 200) {
                            final JSONObject res = x.getResponse();
                            final Integer id;

                            try {
                                id = res.getInt("id");
                                PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        printBadge3(id);
                                    }
                                });
                            }
                            catch(JSONException e) {
                            }

                            return;
                        } else if (x.getStatus() == 101) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (x.getStatus() == 404) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        Intent intent = new Intent(m_activity, LetterSelectActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                };

                thread.start();

            }
        }).start();
    }

    private void printBadge4(final Integer id)
    {
        new Thread(new Runnable() {
            public void run() {
                final PrinterREST x = new PrinterREST(m_activity.getApplicationContext());
                final Object lock = x.printBadge4(id);

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

                            return;
                        } else if (x.getStatus() == 101) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (x.getStatus() == 404) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                };

                thread.start();

            }
        }).start();
    }

    private void printBadge()
    {
        new Thread(new Runnable() {
            public void run() {
                final PrinterREST x = new PrinterREST(m_activity.getApplicationContext());
                final Object lock = x.printBadge();

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

                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    printBadge2();
                                }
                            });

                            return;
                        } else if (x.getStatus() == 101) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (x.getStatus() == 404) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        Intent intent = new Intent(m_activity, LetterSelectActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                };

                thread.start();

            }
        }).start();
    }

    private void getAndDisplayBadge()
    {
        new Thread(new Runnable() {
            public void run() {
                final BadgeREST x = new BadgeREST(m_activity.getApplicationContext());
                final Object lock = x.getBadgeImage(m_badgeContent);

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

                            // update badge image

                            BadgeImage bimg = BadgeImage.getInstance();
                            final Bitmap bmp = bimg.getBitmap();

                            m_activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    ImageView iv = (ImageView) findViewById(R.id.badgeImageView);
                                    iv.setImageBitmap(bmp);
                                }
                            });


                            return;
                        } else if (x.getStatus() == 101) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_connect, Toast.LENGTH_LONG).show();
                                }
                            });

                        } else if (x.getStatus() == 404) {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unable_to_find_clinic, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            PrintBadgeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                };

                thread.start();

            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
        HideyHelper h = new HideyHelper();
        h.toggleHideyBar(this);
    }

    public void handlePrintButtonPress(View v)
    {

        Button b = (Button) findViewById(R.id.badgeImagePrintButton);
        b.setVisibility(View.GONE);

        stopDisconnectTimer();

        printBadge();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        stopDisconnectTimer();
        Intent intent = new Intent(m_activity, LetterSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}


