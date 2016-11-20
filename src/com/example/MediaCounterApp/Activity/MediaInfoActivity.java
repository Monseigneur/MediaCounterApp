package com.example.MediaCounterApp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.MediaCounterApp.Model.MediaCounterDB;
import com.example.MediaCounterApp.R;
import com.example.MediaCounterApp.ViewModel.MediaInfoViewModel;

import java.util.List;

/**
 * Created by Milan on 5/28/2016.
 */
public class MediaInfoActivity extends Activity
{
    public static final String MEDIA_INFO = "media_info";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_info_activity);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        MediaInfoViewModel mivm = (MediaInfoViewModel)b.getSerializable(MEDIA_INFO);

        Log.i("info activity", mivm.toString());

        TextView nameLabel = (TextView)findViewById(R.id.media_info_name);
        nameLabel.setText(mivm.mediaName);

        TextView addedDateLabel = (TextView)findViewById(R.id.media_info_added_date);
        String addedDate = MediaCounterDB.dateString(this, mivm.addedDate);
        addedDateLabel.setText("Added: " + addedDate);

        TextView countLabel = (TextView)findViewById(R.id.media_info_count);
        countLabel.setText(mivm.epDates.size() + "");

        TextView completeLabel = (TextView)findViewById(R.id.media_info_complete_status);

        // Set the initial state
        if (mivm.completeStatus)
        {
            completeLabel.setText(R.string.complete);
        }
        else
        {
            completeLabel.setText(R.string.not_complete);
        }

        ListView listView = (ListView)findViewById(R.id.media_info_ep_list);
        Log.i("before constructor", R.layout.media_info_list_entry + " " + mivm.epDates);
        MediaInfoEpisodeAdapter adapter = new MediaInfoEpisodeAdapter(this, R.layout.media_info_list_entry, mivm.epDates);

        listView.setAdapter(adapter);
    }

    public class MediaInfoEpisodeAdapter extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;
        private int resource;
        private List<Long> epDates;

        public MediaInfoEpisodeAdapter(Context c, int r, List<Long> epDates)
        {
            Log.i("constructor", r + " " + epDates);
            context = c;
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resource = r;
            this.epDates = epDates;
        }

        @Override
        public int getCount()
        {
            return epDates.size();
        }

        @Override
        public Object getItem(int position)
        {
            return epDates.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
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

            long date = (long)getItem(position);

            TextView name = (TextView)itemView.findViewById(R.id.episode_number);
            name.setText(position + 1 + "");

            TextView count = (TextView)itemView.findViewById(R.id.episode_date);
            count.setText(MediaCounterDB.dateString(context, date) + "");

            return itemView;
        }

        @Override
        public boolean isEmpty()
        {
            return epDates.isEmpty();
        }
    }
}