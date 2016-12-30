package com.example.MediaCounterApp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.MediaCounterApp.Model.MediaCounterDB;
import com.example.MediaCounterApp.R;
import com.example.MediaCounterApp.ViewModel.MediaInfoViewModel;

import java.util.List;

public class MediaInfoActivity extends Activity
{
    public static final String MEDIA_INFO = "media_info";

    private Button completeButton;

    private boolean originalStatus;
    private boolean currentStatus;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_info_activity);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        MediaInfoViewModel mivm = (MediaInfoViewModel)b.getSerializable(MEDIA_INFO);

        Log.i("info activity", mivm.toString());

        name = mivm.mediaName;
        TextView nameLabel = (TextView)findViewById(R.id.media_info_name);
        nameLabel.setText(name);

        TextView addedDateLabel = (TextView)findViewById(R.id.media_info_added_date);
        String addedDate = MediaCounterDB.dateString(this, mivm.addedDate);
        addedDateLabel.setText("Added: " + addedDate);

        TextView countLabel = (TextView)findViewById(R.id.media_info_count);
        countLabel.setText(mivm.epDates.size() + "");

        completeButton = (Button)findViewById(R.id.media_info_complete_status_button);

        originalStatus = mivm.completeStatus;
        currentStatus = originalStatus;
        // Set the initial state
        setButtonText();

        ListView listView = (ListView)findViewById(R.id.media_info_ep_list);
        Log.i("before constructor", R.layout.media_info_list_entry + " " + mivm.epDates);
        MediaInfoEpisodeAdapter adapter = new MediaInfoEpisodeAdapter(this, R.layout.media_info_list_entry, mivm.epDates);

        listView.setAdapter(adapter);
    }

    private void setButtonText()
    {
        if (currentStatus)
        {
            completeButton.setText(R.string.complete);
        }
        else
        {
            completeButton.setText(R.string.not_complete);
        }
    }

    public void changeCompleteStatus(View view)
    {
        Log.i("changeCompleteStatus", "changing status");
        currentStatus = !currentStatus;
        setButtonText();
    }

    @Override
    public void finish()
    {
        Intent result = new Intent();
        result.putExtra(MediaCounterActivity.MEDIA_INFO_COMPLETE_STATUS, currentStatus);
        result.putExtra(MediaCounterActivity.MEDIA_COUNTER_NAME, name);
        setResult(Activity.RESULT_OK, result);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MediaInfoEpisodeAdapter extends ArrayAdapter<Long>
    {
        private Context context;
        private LayoutInflater inflater;
        private int resource;
        private List<Long> epDates;

        public MediaInfoEpisodeAdapter(Context c, int r, List<Long> epDates)
        {
            super(c, r, epDates);
            Log.i("constructor", r + " " + epDates);
            context = c;
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resource = r;
            this.epDates = epDates;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemView = null;

            if (convertView == null)
            {
                itemView = inflater.inflate(resource, parent, false);
            }
            else
            {
                itemView = convertView;
            }

            long date = getItem(position);

            TextView name = (TextView)itemView.findViewById(R.id.episode_number);
            name.setText(position + 1 + "");

            TextView count = (TextView)itemView.findViewById(R.id.episode_date);
            count.setText(MediaCounterDB.dateString(context, date) + "");

            return itemView;
        }
    }
}