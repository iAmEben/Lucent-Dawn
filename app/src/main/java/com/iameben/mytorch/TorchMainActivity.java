package com.iameben.mytorch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import java.util.Objects;

/*** This application is a very simple torchlight application that simply turns a torchlight
 * on and off on an android phone.
 *  For the next version I'll add an extra function to make the torchlight blink on the
 *  onLongClick method of the button.
 * It also has a menu button that toggles between dark mode and night mode.
 * The way this application is designed it does'nt require any permission from the android device.
 * SEE CODES BELOW
 * */


public class TorchMainActivity extends AppCompatActivity {


    // variables
    private boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    private CameraManager cameraManager;
    private String cameraID;
    private boolean blinkEnabler;
    private boolean ledState = true;
    private boolean threadState;
    ImageButton switchOn;
    ImageButton switchOff;
    MediaPlayer mediaPlayer;

    private static final String TAG = "TorchMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torch_main);

        Log.d(TAG, "onCreate: TorchMainActivity has been created");

        // these blocks of code modifies the toolbar tab used to no title and zero elevation respectively
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);
        setUserThemeMode();


        //Accesses the camera manager
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraManager != null;
            cameraID = cameraManager.getCameraIdList()[0];  // in the cameraIdList the value 0 is the index for the back camera / flashlight
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Attaching Ui components
        mediaPlayer = MediaPlayer.create(this, R.raw.button_clicks);
        switchOn = findViewById(R.id.button_on);
        switchOff = findViewById(R.id.button_off);



        //set an onclicklistener for the off button
        switchOff.setOnClickListener(view -> {

            switchOff.setVisibility(View.GONE);
            switchOn.setVisibility(View.VISIBLE);
            try {
                cameraManager.setTorchMode(cameraID, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        });

        //sets an onclicklistener for the on button
        switchOn.setOnClickListener(view -> {

            switchOff.setVisibility(View.VISIBLE);
            switchOn.setVisibility(View.GONE);
            //sets the threadState to false so the onLongClickListener can run again
            threadState = false;
            // If the onLongClickListener is running and blinkEnabler is set to true,
            // Set blinkEnabler to false and runs the code
            if (blinkEnabler) {
                blinkEnabler = false;
                // used a sleep method because I want the system to
                // recover some time after switching the led off, and on again.
                SystemClock.sleep(370);
            }
            try {
                cameraManager.setTorchMode(cameraID, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        });

        //sets an onlongclicklistener for the on button
        switchOn.setOnLongClickListener(view -> {
            blinkEnabler = true;
            // Because I want the method inside this if statement to be called once and not repeated
            if (!threadState) {
                startThread();

            }
            threadState = true;
            return true;
        });


    }

    //Thread that handles the flash blinks
    private void startThread() {
        Thread thread = new Thread(() -> {
            while (blinkEnabler) {
                Log.d(TAG, "startThread: Thread loop, loping" + ' ' + Thread.currentThread() + "started");
                if (ledState) {
                    try {
                        cameraManager.setTorchMode(cameraID, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        cameraManager.setTorchMode(cameraID, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                ledState = !ledState;
                SystemClock.sleep(350);
            }
            try {
                cameraManager.setTorchMode(cameraID, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });
        thread.start();


    }

    // This method saves the theme selected in a sharedpreference
    private void setUserThemeMode() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        isDarkMode = preferences.getBoolean("DARK_MODE_PREF", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }




    // overrides the onCreateOptionsMenu and inflates a menu to it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);

        int icon;
        if (isDarkMode) {
            icon = R.drawable.ic_theme_light;
        } else {
            icon = R.drawable.ic_theme_night;
        }
        menu.findItem(R.id.menu_night).setIcon(icon);

        return true;
    }

    // creates and reference an action to each item on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_night) {
            SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
            preferences.edit().putBoolean("DARK_MODE_PREF", !isDarkMode).apply();
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //This is called because mediaPlayer has to be destroyed properly
    protected void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();

        }
        super.onStop();

    }


}