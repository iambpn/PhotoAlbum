package com.example.photoalbum;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AlbumFragment.Communicate,
        PhotosFragment.Communicate {

    private final int REQUEST_CODE = 29380; // this can be any number
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ArrayList<String[]> imagePathWithDate; // path is in URI format image path is a absolute path start from /
    private String[] uniquePaths; // or folder paths, folder path is a absolute path
    private MenuItem previousPositionOnNavigationDrawer = null;

    private SharedPreferences preferences;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagePathWithDate = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        //initialize uniquePaths.
        uniquePaths = new String[0];

        permissionManagement(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show ham icon
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer_string, R.string.close_drawer_string);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) { // to prevent calling this code if app is already loaded.
            navigationView.setCheckedItem(R.id.nav_photo_album);
            previousPositionOnNavigationDrawer = navigationView.getCheckedItem();
        }

        //load settings Fragments shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // set up text to speech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Text to speech", "Language not supported");
                    } else {
                        // trigger any thing after tts have been set up
                        if (tts.isSpeaking()) {
                            tts.stop();
                        }
                    }
                } else {
                    Log.e("Text to speech", "onInit: Failed");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // make ham icon work
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0 && previousPositionOnNavigationDrawer != null) {
            navigationView.setCheckedItem(previousPositionOnNavigationDrawer);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).addToBackStack("Settings frag").commit();
                previousPositionOnNavigationDrawer = navigationView.getCheckedItem();
                break;
            case R.id.nav_photo_album:
                clearFragmentBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AlbumFragment()).commit();
                previousPositionOnNavigationDrawer = navigationView.getMenu().findItem(R.id.nav_photo_album);
                break;
            case R.id.nav_shareApp:
                Toast.makeText(this, "Share Application", Toast.LENGTH_SHORT).show();
                // Intent to open download app page to share app
                Intent shareApp = new Intent(Intent.ACTION_VIEW);
                shareApp.setData(Uri.parse("https://github.com/iambpn/PhotoAlbum"));
                startActivity(shareApp);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // clears the fragment back stack
    public void clearFragmentBackStack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(0);
            getSupportFragmentManager().popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void permissionManagement(String[] permissions) {
        // check if permission is already granted or not
        if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            // if not granted then ask for permission
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        } else {
            // if already granted
            Log.d("Permission", "permissionManagement: permission Granted");
            //call fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AlbumFragment()).commit();
        }
    }

    // this runs after asking for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // if permission granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // Call fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AlbumFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_photo_album);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) { // if permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) { // if only denied
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage("This permission is necessary for searching photos on your phone.");
                    dialog.setCancelable(true);
                    dialog.setTitle("Permission Required.");
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        permissionManagement(permissions);
                    });
                    dialog.setNegativeButton("No Thanks", (dialog1, which) -> {
                        Toast.makeText(this, "Sorry. Permission denied.", Toast.LENGTH_SHORT).show();
                        this.finish();
                    });
                    dialog.show();
                } else { // if denied with don't ask again
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
            }
        }
    }

    private void showErrorDialog(String message) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("Error While Indexing Images");
        dialog.setMessage(message);
        dialog.show();
    }

    // get all image path from media store
    private void getAllImagesPath(Activity activity) {
        // ArrayList<String[]> imagesWithDate and
        // String[] uniquePaths is Instance variable.

        Uri uri;
        Cursor cursor = null;
        int column_index_data, column_index_date_added;
        String absolutePathOfImage = null, dateAdded = null;
        int i = 0;
        Set<String> uniquePaths = new HashSet<>(); // set is used because set cannot store duplicate content
        this.imagePathWithDate = new ArrayList<>();

        try {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // location

            String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED}; // which column to select from db

            // importing data from media store
            // Selection means where clause and selection args means argument to selection string.
            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, MediaStore.Images.Media.DATE_ADDED + " DESC");  // sort order means order by

            //getting column index
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_date_added = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                dateAdded = cursor.getString(column_index_date_added);
                imagePathWithDate.add(new String[]{absolutePathOfImage, dateAdded});
                uniquePaths.add(imagePathWithDate.get(i)[0].substring(1, imagePathWithDate.get(i)[0].lastIndexOf('/')));
                i++;
            }
            this.uniquePaths = uniquePaths.toArray(new String[0]);
            Arrays.sort(this.uniquePaths);
        } catch (Exception ex) {
            showErrorDialog(ex.getMessage());
            Log.e("ErrorHandled", "by getAllShownImagesPath");
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public String[] getUniquePaths() {
        // re-indexing every time unique paths/ album fragment is launched
        getAllImagesPath(this);
        return uniquePaths;
    }

    @Override
    public String getThumbnail(String folderPath, String folderName) {
        for (String[] imagePathAndDate : imagePathWithDate) {
            if (imagePathAndDate[0].contains(folderPath)) {
                if (imagePathAndDate[0].split("/")[imagePathAndDate[0].split("/").length - 2].equals(folderName)) {
                    return imagePathAndDate[0];
                }
            }
        }
        return "none";
    }

    // this is to change the fragment when clicking on album
    @Override
    public void onAlbumSelected(int pos) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PhotosFragment.newInstance(uniquePaths[pos])) // passing argument using factory method of PhotoFragment.java
                .addToBackStack("photos")
                .commit();
    }

    @Override
    public ArrayList<String> getPathOfPhotos(String folderPath) {
        String folderName = folderPath.split("/")[folderPath.split("/").length - 1];
        ArrayList<String> photosPath = new ArrayList<>();
        for (String[] imagePathAndDate : imagePathWithDate) {
            if (imagePathAndDate[0].contains(folderPath)) {
                if (imagePathAndDate[0].split("/")[imagePathAndDate[0].split("/").length - 2].equals(folderName)) {
                    photosPath.add(imagePathAndDate[0]);
                }
            }
        }
        return photosPath;
    }

    @Override
    public void onPhotoSelected(int pos, PhotoData selectedPhoto) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ProcessingFragment.newInstance(selectedPhoto)) // passing argument using factory method of processingFragment.java
                .addToBackStack("Processing")
                .commit();
    }

    public SharedPreferences getSettingsSharedPreference() {
        return preferences;
    }

    public TextToSpeech getTextToSpeech() {
        return tts;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}