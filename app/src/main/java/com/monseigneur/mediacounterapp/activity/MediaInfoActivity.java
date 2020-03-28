package com.monseigneur.mediacounterapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.monseigneur.mediacounterapp.R;
import com.monseigneur.mediacounterapp.model.MediaCounterDB;
import com.monseigneur.mediacounterapp.model.MediaCounterStatus;
import com.monseigneur.mediacounterapp.viewmodel.MediaInfoViewModel;

import java.util.List;

public class MediaInfoActivity extends Activity
{
    private static final String TAG = "MediaInfoActivity";

    public static final String MEDIA_INFO = "media_info";

    private Button completeButton;

    private MediaCounterStatus currentStatus;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_info_activity);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        MediaInfoViewModel mivm = (MediaInfoViewModel) b.getSerializable(MEDIA_INFO);

        Log.i(TAG, "onCreate: " + mivm.toString());

        name = mivm.mediaName;
        TextView nameLabel = findViewById(R.id.media_info_name);
        nameLabel.setText(name);

        TextView addedDateLabel = findViewById(R.id.media_info_added_date);
        String addedDate = MediaCounterDB.dateString(this, mivm.addedDate);
        addedDateLabel.setText("Added: " + addedDate);

        TextView countLabel = findViewById(R.id.media_info_count);
        countLabel.setText(String.valueOf(mivm.epDates.size()));

        completeButton = findViewById(R.id.media_info_status_button);

        currentStatus = mivm.status;
        // Set the initial state
        setButtonText();

        ListView listView = findViewById(R.id.media_info_ep_list);
        Log.i(TAG, "onCreate: " + R.layout.media_info_list_entry + " " + mivm.epDates);
        MediaInfoEpisodeAdapter adapter = new MediaInfoEpisodeAdapter(this, mivm.epDates);

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
     *
     * @param view view
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
        if (item.getItemId() == android.R.id.home)
        {
            // Respond to the action bar's Up/Home button
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class MediaInfoEpisodeAdapter extends ArrayAdapter<Long>
    {
        private final String TAG = "MediaInfoEpisodeAdapter";

        private final Context context;
        private final LayoutInflater inflater;
        private final int resource;

        MediaInfoEpisodeAdapter(Context c, List<Long> epDates)
        {
            super(c, R.layout.media_info_list_entry, epDates);
            Log.i(TAG, "Constructor: " + R.layout.media_info_list_entry + " " + epDates);
            context = c;
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resource = R.layout.media_info_list_entry;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            MediaInfoEpisodeAdapter.ViewHolder vh;
            if (convertView == null)
            {
                convertView = inflater.inflate(resource, parent, false);

                vh = new ViewHolder();

                vh.name = convertView.findViewById(R.id.episode_number);
                vh.count = convertView.findViewById(R.id.episode_date);

                convertView.setTag(vh);
            }
            else
            {
                vh = (MediaInfoEpisodeAdapter.ViewHolder) convertView.getTag();
            }

            long date = getItem(position);

            TextView name = vh.name;
            name.setText(String.valueOf(position + 1));

            TextView count = vh.count;
            count.setText(String.valueOf(MediaCounterDB.dateString(context, date)));

            return convertView;
        }

        // ViewHolder pattern to increase Adapter performance
        private static class ViewHolder
        {
            TextView name;
            TextView count;
        }
    }
}