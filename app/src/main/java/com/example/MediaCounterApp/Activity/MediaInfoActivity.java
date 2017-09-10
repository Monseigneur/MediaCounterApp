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
import com.example.MediaCounterApp.Model.MediaCounterStatus;
import com.example.MediaCounterApp.R;
import com.example.MediaCounterApp.ViewModel.MediaInfoViewModel;

import java.util.List;

public class MediaInfoActivity extends Activity
{
    private static String TAG = "MediaInfoActivity";

    public static final String MEDIA_INFO = "media_info";

    private Button completeButton;

    private MediaCounterStatus originalStatus;
    private MediaCounterStatus currentStatus;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_info_activity);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        MediaInfoViewModel mivm = (MediaInfoViewModel)b.getSerializable(MEDIA_INFO);

        Log.i(TAG, "onCreate: " + mivm.toString());

        name = mivm.mediaName;
        TextView nameLabel = (TextView)findViewById(R.id.media_info_name);
        nameLabel.setText(name);

        TextView addedDateLabel = (TextView)findViewById(R.id.media_info_added_date);
        String addedDate = MediaCounterDB.dateString(this, mivm.addedDate);
        addedDateLabel.setText("Added: " + addedDate);

        TextView countLabel = (TextView)findViewById(R.id.media_info_count);
        countLabel.setText(mivm.epDates.size() + "");

        completeButton = (Button)findViewById(R.id.media_info_status_button);

        originalStatus = mivm.status;
        currentStatus = originalStatus;
        // Set the initial state
        setButtonText();

        ListView listView = (ListView)findViewById(R.id.media_info_ep_list);
        Log.i(TAG, "onCreate: " + R.layout.media_info_list_entry + " " + mivm.epDates);
        MediaInfoEpisodeAdapter adapter = new MediaInfoEpisodeAdapter(this, R.layout.media_info_list_entry, mivm.epDates);

        listView.setAdapter(adapter);
    }

    /**
     * Sets the button text depending on the current status.
     */
    private void setButtonText()
    {
        int textId;
        switch (currentStatus)
        {
            default:
            case NEW:
                textId = R.string.new_text;
                break;
            case ONGOING:
                textId = R.string.ongoing_text;
                break;
            case COMPLETE:
                textId = R.string.complete_text;
                break;
            case DROPPED:
                textId = R.string.dropped_text;
                break;
        }
        completeButton.setText(textId);
    }

    /**
     * Changes the status
     * @param view
     */
    public void changeStatus(View view)
    {
        Log.i(TAG, "changeStatus: changing status");

        // Cycle through the statuses
        MediaCounterStatus newStatus;
        switch (currentStatus)
        {
            default:
            case NEW:
                newStatus = MediaCounterStatus.ONGOING;
                break;
            case ONGOING:
                newStatus = MediaCounterStatus.COMPLETE;
                break;
            case COMPLETE:
                newStatus = MediaCounterStatus.DROPPED;
                break;
            case DROPPED:
                newStatus = MediaCounterStatus.NEW;
                break;
        }
        currentStatus = newStatus;
        setButtonText();
    }

    @Override
    public void finish()
    {
        Intent result = new Intent();
        result.putExtra(MediaCounterActivity.MEDIA_INFO_STATUS, currentStatus);
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
        private String TAG = "MediaInfoEpisodeAdapter";

        private Context context;
        private LayoutInflater inflater;
        private int resource;
        private List<Long> epDates;

        public MediaInfoEpisodeAdapter(Context c, int r, List<Long> epDates)
        {
            super(c, r, epDates);
            Log.i(TAG,"Constructor: " + r + " " + epDates);
            context = c;
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resource = r;
            this.epDates = epDates;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemView;
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