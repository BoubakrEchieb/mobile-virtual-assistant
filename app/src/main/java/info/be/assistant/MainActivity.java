package info.be.assistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.prefs.Preferences;

import info.be.assistant.connection.ConnectionAsyncTask;
import info.be.assistant.fragments.AboutFragment;
import info.be.assistant.fragments.HomeFragment;
import info.be.assistant.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //Preferences
    public static  String PREFS_LANGUAGE_KEY = "language";
    public static  String PREFS_ASSISTANT_CHARACTER_KEY = "assistant";
    public static  String PREFS_VIBRATE_KEY = "vibrate";
    public static  String PREFS_NOTIFICATIONS_KEY = "notifications";

    private String prefsLanguage;
    private String prefsAssistant;
    private boolean prefsVibrate;
    private boolean prefsNotifications;
    //
    private DrawerLayout mDrawer;//layout
    private Toolbar toolbar; //toolbar
    private NavigationView nvDrawer; //drawerview
    private ActionBarDrawerToggle drawerToggle; //toggle

    private HomeFragment homeFragment;//home fragment
    //private AboutFragment aboutFragment;
    private SettingsFragment settingsFragment;//settings

    private TextToSpeech tts;//Text to speech

   // private ConnectionAsyncTask conAsync; //connect to server

    private FrameLayout fragmentsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        nvDrawer.setItemIconTintList(null);
        setupDrawercontent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        tts = new TextToSpeech(this, this);
        fragmentsContainer = (FrameLayout) findViewById(R.id.flContent);
        setUpHomeView();
    }

    //set home view
    private void setUpHomeView() {
        homeFragment = new HomeFragment();
        homeFragment.setFirstStart(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, homeFragment).commit();
        speak(getString(R.string.opning_message));
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }


    private void setupDrawercontent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem){
        Class fragmentClass = null;
        int itemId = 0;
        switch (menuItem.getItemId()) {
            case R.id.nav_home_fragment:
                fragmentClass = HomeFragment.class;
                itemId = R.id.nav_home_fragment;
                break;
            case R.id.nav_settings_fragent:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_about_third:
                fragmentClass = AboutFragment.class;
                itemId = R.id.nav_about_third;
                break;
        }
        try {
            if(itemId != 0) {
                if(itemId == R.id.nav_home_fragment) {
                    //if (homeFragment == null) homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContent, homeFragment)
                            .commit();
                }
                else
                    fragmentsContainer.removeAllViews();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContent, (Fragment) fragmentClass.newInstance())
                            .commit();
            }else{
                fragmentsContainer.removeAllViews();
                settingsFragment = new SettingsFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.flContent, new SettingsFragment())
                        .commit();


            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);

    }

    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //init text to speesh
    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.ENGLISH);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == homeFragment.VOICE_RECOGNITION_REQUEST_CODE) { //test if the result is coming from the home fragment
            if(resultCode == RESULT_OK) { //
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                homeFragment.setRecognizedText(text.get(0)); // get Recognized Text

                homeFragment.setConnectionAsyncTask(new ConnectionAsyncTask(homeFragment.getAgentParole(), homeFragment.getRecognizedText(), this, homeFragment.getProgressBar()));
                homeFragment.getConnectionAsyncTask().execute();
                Snackbar.make(mDrawer, "Thinking.. ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
            else{
                    homeFragment.getAgentParole().setText(getString(R.string.reconnaissance_problem));
                    speak(getString(R.string.reconnaissance_problem));
            }
        }
    }
    //init preferences
    public void getPreferences(){
        //TODO
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefsLanguage = preferences.getString(PREFS_LANGUAGE_KEY,"EN");
        prefsAssistant = preferences.getString(PREFS_ASSISTANT_CHARACTER_KEY,"Girl");
        prefsVibrate = preferences.getBoolean(PREFS_VIBRATE_KEY,false);
        prefsNotifications = preferences.getBoolean(PREFS_NOTIFICATIONS_KEY,false);
    }
    //update preferences
    public void updatePreferences(){
        //TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
        tts = null;
    }

    public static String getPrefsLanguageKey() {
        return PREFS_LANGUAGE_KEY;
    }

    public static void setPrefsLanguageKey(String prefsLanguageKey) {
        PREFS_LANGUAGE_KEY = prefsLanguageKey;
    }
    public void vibrate(){
        getPreferences();
        if(isPrefsVibrate()){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
    }

    public String getPrefsLanguage() {
        return prefsLanguage;
    }

    public void setPrefsLanguage(String prefsLanguage) {
        this.prefsLanguage = prefsLanguage;
    }

    public String getPrefsAssistant() {
        return prefsAssistant;
    }

    public void setPrefsAssistant(String prefsAssistant) {
        this.prefsAssistant = prefsAssistant;
    }

    public boolean isPrefsVibrate() {
        return prefsVibrate;
    }

    public void setPrefsVibrate(boolean prefsVibrate) {
        this.prefsVibrate = prefsVibrate;
    }

    public boolean isPrefsNotifications() {
        return prefsNotifications;
    }

    public void setPrefsNotifications(boolean prefsNotifications) {
        this.prefsNotifications = prefsNotifications;
    }
}


