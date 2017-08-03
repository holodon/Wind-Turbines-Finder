package vladimir.apps.dwts.anlagensuche;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * WEA Suche
 *
 * @author
 *      Vladimir (jelezarov.vladimir@gmail.com)
 */

public class MainActivity extends Activity {

    private static final String PROGRAM_VERSION =   "Created by Vladimir Zhelezarov\n" +
                                                    "Version 2.6.3 (040717)\n\n" +
                                                    "Geo data: Deutsche Windtechnik";
    private EditText mSearchView;
    private ListView mListView;
    private ListViewAdapter adapter;
    public static final String TAG = "WEA_Suche by Vladimir";
    private ProgressBar progressBar;
    File lastUpdateFile;
    File convRoutesFile;
    JSONObject jso;
    String park_id = "";
    String park_name = "";
    String lat;
    String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastUpdateFile = new File(this.getFilesDir(), "lastUpdate");

        convRoutesFile = new File(this.getFilesDir(), "converted_routes.csv");

        mSearchView = (EditText) findViewById(R.id.searchView);
        mListView = (ListView) findViewById(R.id.myList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (!updateDatabase()) {
            Toast toast = Toast.makeText(this, "Keine WEA-Datei vorhanden!\nBitte aktualisieren!",
                    Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }

    }

    private boolean updateDatabase () {
        List<dataWEA> database = new ArrayList<>();
        String next[];
        try {
            FileInputStream inputStream = new FileInputStream(convRoutesFile);
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
            while(true) {
                next = reader.readNext();
                if(next != null) {
                    database.add(new dataWEA(next[0],next[1],next[2],next[3]));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        adapter = new ListViewAdapter(this, database);
        mListView.setAdapter(adapter);
        mSearchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = mSearchView.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() == 0) {
                    mListView.setVisibility(View.INVISIBLE);
                } else mListView.setVisibility(View.VISIBLE);
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
        return true;
    }

    public void about(View view) {
        Toast toast = Toast.makeText(MainActivity.this, PROGRAM_VERSION, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public void update(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Letztes Update am: " + readOneLine())
                .setMessage("Willst du neu aktualisieren?")
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new getAndConvert().execute();
                    }
                })
                .show();

    }

    @NonNull
    private String readOneLine() {

        StringBuilder fileContents = new StringBuilder();

        try (Scanner scanner = new Scanner(lastUpdateFile)) {
            fileContents.append(scanner.nextLine());
            return fileContents.toString();
        } catch (Exception e) {
            Log.e(TAG, "I/O Error");
            return "unbekannt";
        }
    }

    private class getAndConvert extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            StringBuilder res = new StringBuilder();

            HttpHandler sh = new HttpHandler();

            String urlWEAS = "";	// TODO: fill this!
            String urlWindParks = "";	// TODO: fill this!

            String jsonStrWEAS =        sh.makeServiceCall(urlWEAS);
            String jsonStrWindParks =   sh.makeServiceCall(urlWindParks);

/*
            Log.d(TAG, "Response from url_1: " + jsonStrWEAS);
            Log.d(TAG, "Response from url_2: " + jsonStrWindParks);
*/
            if ((jsonStrWEAS != null) && (jsonStrWindParks != null)) {
                try {
                    // Getting JSON Array node
                    JSONArray windparks =    new JSONArray(jsonStrWindParks);
                    JSONArray weas =         new JSONArray(jsonStrWEAS);

                    String or_name =  "original_name";
                    for (int i = 0; i < windparks.length(); i++) {
                        jso =       windparks.getJSONObject(i);
                        park_id =   jso.get("id").toString();
                        park_name = jso.get(or_name).toString();

                        for (int k = 0; k < weas.length(); k++)
                        {
                            jso = weas.getJSONObject(k);
                            String p_id = (String) jso.get("p_id");
                            if (p_id.equals(park_id)) {
                                lat = (String) jso.get("lat");
                                res.append( lat.replaceAll("[^\\d.]", "") );
                                res.append( ',' );
                                lon = (String) jso.get("lon");
                                res.append( lon.replaceAll("[^\\d.]", "") );
                                res.append( ',' );
                                res.append( (String) jso.get(or_name) );
                                res.append( ',' );
                                res.append( park_name );
                                res.append( '\n' );
                            }
                        }
                    }

                    if (convRoutesFile.exists()) //noinspection ResultOfMethodCallIgnored
                        convRoutesFile.delete();
                    PrintWriter pw = new PrintWriter(convRoutesFile);
                    pw.write(res.toString());
                    pw.close();

                } catch (Exception e) {
                    //Log.e(TAG, "Json parsing error: " + e.getMessage());
                    return false;
                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Server nicht erreichbar oder umkonfiguriert!",
                                Toast.LENGTH_LONG).show();}
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (!result) {
                Toast.makeText(MainActivity.this, "Problem beim Parsen von der Datei",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String fDate = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(new Date());

            try (FileOutputStream stream = new FileOutputStream(lastUpdateFile)) {
                stream.write(fDate.getBytes());
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "I/O Fehler!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!updateDatabase()) {
                Toast.makeText(MainActivity.this, "I/O Problem mit der Datei",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "Datei aktualisiert!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
