package com.maws.loonandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.LogDao;
import java.util.List;

/**
 * Created by Andrexxjc on 14/09/2015.
 */
public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        ListView logLV = (ListView) findViewById(R.id.logLV);

        LogDao logDao = new LogDao(this);
        List<String> logs = logDao.getAll();

        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.log_item_layout, R.id.logTextTV, logs);

        // assign the list adapter
        logLV.setAdapter(myAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_email:

                //concat all strings
                LogDao logDao = new LogDao(this);
                List<String> logs = logDao.getAll();
                StringBuilder toSend = new StringBuilder("");
                for(String message: logs){
                    toSend.append(message);
                    toSend.append(";");
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"andres@mobileaws.com", "joel@mobileaws.com"} );
                intent.putExtra(Intent.EXTRA_SUBJECT, "Logs from Caresentinel");
                intent.putExtra(Intent.EXTRA_TEXT, toSend.toString());
                startActivity(Intent.createChooser(intent, "Send Email"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
