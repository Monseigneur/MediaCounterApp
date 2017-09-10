package com.example.MediaCounterApp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.MediaCounterApp.R;

/**
 * Created by Milan on 5/21/2016.
 */
public class MediaCounterAddActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_counter_add_activity);
    }

    public void addNewMediaCounter(View view)
    {
        Intent result = new Intent();

        EditText et = (EditText)findViewById(R.id.media_name);

        String mediaName = et.getText().toString();

        result.putExtra(MediaCounterActivity.MEDIA_COUNTER_NAME, mediaName);

        setResult(Activity.RESULT_OK, result);
        finish();
    }
}