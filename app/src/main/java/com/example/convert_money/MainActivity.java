package com.example.convert_money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.convert_money.Adapter.ResultAdapter;
import com.example.convert_money.Model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private Spinner spnFrom;
    private Spinner spnTo;
    private EditText value;
    private Button convert;
    private Button clear;
    private TextView valueResult;
    private RecyclerView resultRecycleView;
    private ProgressDialog progressDialog;
    private HashMap<String,String> listCountryCode =new HashMap<>();
    private List<String> listCountry = new ArrayList<>();
    private List<Result> results= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get view from activity
        spnFrom = (Spinner) findViewById(R.id.spinnerFrom);
        spnTo = (Spinner) findViewById(R.id.spinnerTo);
        convert = (Button) findViewById(R.id.button);
        value= (EditText) findViewById(R.id.value);
        clear=(Button) findViewById(R.id.clear);
        valueResult=(TextView) findViewById(R.id.valueResult);
        resultRecycleView =(RecyclerView) findViewById(R.id.result) ;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        resultRecycleView.setLayoutManager(layoutManager);

        //Set list country data
        listCountry.add("None");
        new GetDataCountry().execute();


        //Set value into spinners

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listCountry);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFrom.setAdapter(adapter);
        spnTo.setAdapter(adapter);


        //Set invisible resutl when focus edittext
        value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    resultRecycleView.setVisibility(View.INVISIBLE);

                }
                if(!hasFocus)
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                    resultRecycleView.setVisibility(View.VISIBLE);
                }
            }
        });
        value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE)
                {
                    value.clearFocus();
                }
                return false;
            }
        });

        //set Event clear history
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultRecycleView.setAdapter(null);
                valueResult.setText("");
                results.clear();
            }
        });

        // set event when clicked button
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!value.getText().toString().equals("")){
                    if (!spnFrom.getSelectedItem().equals("None")&&!spnTo.getSelectedItem().equals("None"))
                    {

                        value.clearFocus();
                        new GetDataCurrency().execute(listCountryCode.get(spnFrom.getSelectedItem().toString()));

                    }
                }
            }
        });
    }

    // Get countries have data to exchange currency
    protected class GetDataCountry extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching data");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer response = null;
            try {
                String path = "https://www.fxexchangerate.com/currency-converter-rss-feed.html";
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                Log.d("TAG", String.valueOf(responseCode));
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    // Reading response from input Stream
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String output;
                    response = new StringBuffer();

                    while ((output = in.readLine()) != null) {
                        response.append(output);
                    }
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            Document document =  Jsoup.parse(s);
            Element context =  document.getElementsByClass("rss").first();
            List<Element> list=context.getElementsByTag("li");
            for (int i=0; i<list.size();i++)
            {
                String url=list.get(i).getElementsByTag("a").first().attr("href");
                String currencyName=list.get(i).text();
                listCountry.add(currencyName);
                listCountryCode.put(currencyName,url);
            }
        }
    }


    protected class GetDataCurrency extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching data");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer response = null;
            try {

                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    // Reading response from input Stream
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String output;
                    response = new StringBuffer();

                    while ((output = in.readLine()) != null) {
                        response.append(output);
                    }
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing())
                progressDialog.dismiss();


            Document document =  Jsoup.parse(s);
            Elements listItem=document.body().getElementsByTag("item");

            for (int i=0;i<listItem.size();i++) {

                String url=listItem.get(i).getElementsByTag("guid").first().text();

                if(listCountryCode.get(spnTo.getSelectedItem().toString()).contains(url))
                {
                    String description=listItem.get(i).getElementsByTag("description").first().text();
                    Pattern pattern = Pattern.compile("[0-9]{0,10}\\.[0-9]{0,10}");
                    Matcher matcher = pattern.matcher(description);
                    boolean matchFound = matcher.find();
                    if(matchFound) {

                        float resultFloat= Float.valueOf(matcher.group(0))*Float.valueOf(value.getText().toString());
                        String currencyFrom=spnFrom.getSelectedItem().toString().split(" ")[0];
                        String currencyTo=spnTo.getSelectedItem().toString().split(" ")[0];

                        Result result=new Result(value.getText().toString(),String.valueOf(resultFloat),currencyFrom,currencyTo);
                        results.add(result);
                        valueResult.setText(String.valueOf(resultFloat));
                        ResultAdapter resultAdapter= new ResultAdapter(getBaseContext(),results);
                        resultRecycleView.setAdapter(resultAdapter);
                    }

                    break;
                }

            }

        }
    }


}