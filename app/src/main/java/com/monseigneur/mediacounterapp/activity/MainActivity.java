package com.monseigneur.mediacounterapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.monseigneur.mediacounterapp.BuildConfig;
import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.databinding.MainActivityBinding;
import com.monseigneur.mediacounterapp.model.IonMediaDataSerializer;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.MediaCounterRepository;
import com.monseigneur.mediacounterapp.viewmodel.MediaViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private MainActivityBinding binding;
    private AppBarConfiguration appBarConfiguration;

    private MediaViewModel mediaViewModel;

    private final ActivityResultLauncher<String> exportLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"),
            uri -> {
                boolean exportSuccess = exportData(uri);

                showToast(exportSuccess, getString(R.string.export_succeeded), getString(R.string.export_failed));
            });

    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
            uri -> {
                boolean importSuccess = importData(uri);

                showToast(importSuccess, getString(R.string.import_succeeded), getString(R.string.import_failed));
            });

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.setRepository(new MediaCounterRepository(new MediaCounterDB(this), new IonMediaDataSerializer(false)));

        // Workaround from https://stackoverflow.com/questions/58320487/using-fragmentcontainerview-with-navigation-component
        // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // MJMJ add a way to toggle this
        menu.findItem(R.id.action_delete_all).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        boolean handled = true;

        if (id == R.id.action_import)
        {
            importLauncher.launch(new String[]{"text/plain"});
        }
        else if (id == R.id.action_export)
        {
            if (!mediaViewModel.isEmpty())
            {
                exportLauncher.launch(getDefaultExportFilename());
            }
            else
            {
                showToast(getString(R.string.export_empty));
            }
        }
        else if (id == R.id.action_delete_all)
        {
            mediaViewModel.deleteAllMedia();

            showToast(getString(R.string.all_media_deleted));
        }
        else if (id == R.id.action_settings)
        {
            showToast("Not yet implemented");
        }
        else if (id == R.id.action_version)
        {
            showToast(BuildConfig.VERSION_NAME);
        }
        else
        {
            handled = super.onOptionsItemSelected(item);
        }

        return handled;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Show a toast message
     *
     * @param text message to show
     */
    private void showToast(String text)
    {
        Log.i("showToast", "showing toast [" + text + "]");
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Show a toast message based on a condition
     *
     * @param condition condition for message
     * @param trueText  text when condition is true
     * @param falseText text when condition is false
     */
    private void showToast(boolean condition, String trueText, String falseText)
    {
        showToast(condition ? trueText : falseText);
    }

    private boolean importData(Uri uri)
    {
        if (uri == null)
        {
            return false;
        }

        Log.i("importData", "uri " + uri.getPath());

        boolean success = false;
        try (InputStream is = getContentResolver().openInputStream(uri))
        {
            success = mediaViewModel.importData(is);
        }
        catch (IOException e)
        {
            Log.e("importData", "caught exception " + e);
        }

        return success;
    }

    private boolean exportData(Uri uri)
    {
        if (uri == null)
        {
            return false;
        }

        Log.i("exportData", "uri " + uri.getPath());

        boolean success = false;
        try (OutputStream os = getContentResolver().openOutputStream(uri))
        {
            success = mediaViewModel.exportData(os);
        }
        catch (IOException e)
        {
            Log.e("exportData", "caught exception " + e);
        }

        return success;
    }

    private static String getDefaultExportFilename()
    {
        Calendar date = Calendar.getInstance();

        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;       // January is 0?
        int year = date.get(Calendar.YEAR);
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);

        Log.i("fileTimestamp", "DATE STRING: Y=" + year + " M=" + month + " D=" + dayOfMonth + " H=" + hour + " M=" + minute + " S=" + second);

        return String.format(Locale.US, "MCB_%d%02d%02d_%02d%02d%02d.txt", year, month, dayOfMonth, hour, minute, second);
    }
}
