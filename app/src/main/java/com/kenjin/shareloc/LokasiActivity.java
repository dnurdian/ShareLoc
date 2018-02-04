package com.kenjin.shareloc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kenjin.shareloc.Helper.MyConstant;
import com.kenjin.shareloc.adapter.LokasiAdapter;
import com.kenjin.shareloc.model.mLokasi;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LokasiActivity extends AppCompatActivity {
    AsyncTask<Void, Void, Void> matikan;
    ArrayList<mLokasi> lokasiArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    LokasiAdapter adapter;
    private SearchView findTXTEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findTXTEdit = (SearchView) findViewById(R.id.findTXTEdit);
        recyclerView = (RecyclerView) findViewById(R.id.lvItems);
        MyConstant.hideSoftKeyboard(this);
        matikan = new AsyncRefresh().execute();
        if(getIntent().getStringExtra("darimain")!=null)
            Toast.makeText(this,getIntent().getStringExtra("darimain").toString(),Toast.LENGTH_SHORT).show();
        findTXTEdit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null)
                    adapter.setFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //getFragmentManager().popBackStack();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(LokasiActivity.this,
                        R.style.AppTheme_Dark_Dialog);
            }
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Get Data...");
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            loadData();
            if (progressDialog.isShowing()) progressDialog.hide();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String url = MyConstant.URL_SEKOLAH + "/GetLokasi?Status=1";
            String json = GetreadHttp(url, "");
            try {
                JSONArray jsonA = new JSONArray(json);
                for (int i = 0; i < jsonA.length(); i++) {
                    JSONObject c = jsonA.getJSONObject(i);
                    mLokasi lokasi = new mLokasi(c.getDouble("Latitude"), c.getDouble("Longitude"), c.getString("LocationName"), c.getString("DateTaken"));
                    lokasiArrayList.add(lokasi);
                }

            } catch (Exception ex) {
                Log.e("error get Lokasi", ex.toString());
            }


            return null;
        }
    }


    private void loadData() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new LokasiAdapter(lokasiArrayList);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.colorPrimaryDark)
                .size(2)
                .build());
        recyclerView.setAdapter(adapter);
        MyConstant.hideSoftKeyboard(this);
    }

    public String GetreadHttp(String url, String Authorization) {
        HttpURLConnection urlConnection = null;
        StringBuilder builder = new StringBuilder();
        try {

            URL urlLink = new URL(url);
            urlConnection = (HttpURLConnection) urlLink.openConnection();
            urlConnection.setRequestMethod("GET");
            if (Authorization != null) {
                urlConnection.setRequestProperty("Authorization", Authorization);
            }
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            Log.e("isi response Get", "code :" + urlConnection.getResponseCode() + "-" + url);

            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("isi post", line + " ");
                builder.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("error cui Mal", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error cui IO", e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error cui EX", e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return builder.toString();
    }
}
