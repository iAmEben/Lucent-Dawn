package com.iameben.mytorch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.os.SystemClock;
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
 * ALL COMMENTED CODES ARE GOING TO BE UPDATED IN THE NEXT VERSION OF THIS PROJECT!!!
 * */


public class TorchMainActivity extends AppCompatActivity {


    private boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;

    // variables
    private CameraManager cameraManager;
    private String cameraID;
//    private boolean blinkEnabler = true;
//    private boolean ledState = true;
//    private boolean sLong;
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
//            Log.i(TAG, "onCreate: switchon Onclick listener started" + "slong:" + sLong );
//            if (sLong = !sLong) {
//                sLong = false;
                try {
                    cameraManager.setTorchMode(cameraID, true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
//            }else {
//                blinkEnabler = false;
//            }
            mediaPlayer.start();
        });

//        switchOn.setOnLongClickListener(view -> {
//          if (blinkEnabler) {
//              blinkEnabler = true;
//              startThread();
//              sLong = true;
//          }else{
//              blinkEnabler = false;
//          }
//           return true;
//       });


    }
//    private void startThread(){
//        Thread thread = new Thread(() -> {
//            while (blinkEnabler) {
//                if (ledState) {
//                    try {
//                        cameraManager.setTorchMode(cameraID, false);
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("first if statement started correctly");
//                } else {
//                    try {
//                        cameraManager.setTorchMode(cameraID, true);
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//                System.out.println("second if statement started correctly");
//                ledState = !ledState;
//                SystemClock.sleep(1000);
//            }
//            try {
//                cameraManager.setTorchMode(cameraID, false);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//
//        });
//        thread.start();
//
//    }
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


    int icon;

    // overrides the onCreateOptionsMenu and inflates a menu to it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);

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
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();

        }
    }


}