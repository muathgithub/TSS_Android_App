package com.example.muath.tss;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TablesActivity extends AppCompatActivity {

    ListView tablesListView;
    ArrayList<TableAdapter> tablesArrayList;
    MyCustomAdapter myAdapter;
    boolean isWebViewVisible = false;
    WebView tablesWebView;
    ProgressBar tablesProgressBar;
    TextView noTablesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        getSupportActionBar().setTitle("Tables");

        tablesProgressBar = (ProgressBar) findViewById(R.id.tablesProgressBar);
        Drawable progressDrawable = tablesProgressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.parseColor("#ffffff"), android.graphics.PorterDuff.Mode.SRC_IN);
        tablesProgressBar.setProgressDrawable(progressDrawable);
        noTablesTextView = (TextView) findViewById(R.id.noTablesTextView);

        tablesWebView = (WebView) findViewById(R.id.tablesWebView);

        tablesArrayList = new ArrayList<TableAdapter>();
        myAdapter = new MyCustomAdapter(tablesArrayList);
        tablesListView = (ListView) findViewById(R.id.tablesListView);
        tablesListView.setAdapter(myAdapter);

        tablesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tablesWebView.setVisibility(View.VISIBLE);
                isWebViewVisible = true;
                tablesWebView.loadUrl(tablesArrayList.get(position).getTableUrl());
                tablesWebView.getSettings().setJavaScriptEnabled(true);
                tablesWebView.setWebViewClient(new WebViewClient());
                tablesWebView.setWebChromeClient(new WebChromeClient());
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tables");

        query.whereEqualTo("TableClass", ParseUser.getCurrentUser().getString("class")).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0){

                    for(ParseObject table : objects) {

                        TableAdapter currentTable = new TableAdapter();

                        currentTable.setTableName(table.getString("TableName"));

                        String [] tableLastUpdateDate = table.getUpdatedAt().toString().split(" ");

                        currentTable.setTableLastUpdate(
                                      tableLastUpdateDate[0] +
                                " " + tableLastUpdateDate[1] +
                                " " + tableLastUpdateDate[2] +
                                " " + tableLastUpdateDate[tableLastUpdateDate.length-1]);

                        currentTable.setTableUrl(table.getString("TableUrl"));

                        tablesArrayList.add(currentTable);
                        tablesProgressBar.setVisibility(View.GONE);
                        myAdapter.notifyDataSetChanged();
                    }
                }else{
                    tablesProgressBar.setVisibility(View.GONE);
                    noTablesTextView.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<TableAdapter> listNewsDataAdapter;

        public MyCustomAdapter(ArrayList<TableAdapter> listNewsDataAdapter) {
            this.listNewsDataAdapter = listNewsDataAdapter;
        }


        @Override
        public int getCount() {
            return listNewsDataAdapter.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.tables_listview_ticket, null);

            final TableAdapter s = listNewsDataAdapter.get(position);

            TextView tableNameTextView = (TextView) myView.findViewById(R.id.tableNameTextView);
            tableNameTextView.setText(s.getTableName());

            TextView tableLastUpdateTextView = (TextView) myView.findViewById(R.id.tableLastUpdateTextView);
            tableLastUpdateTextView.setText(s.getTableLastUpdate());

            return myView;
        }

    }


    @Override
    public void onBackPressed() {
        if(isWebViewVisible == true){
            tablesWebView.stopLoading();
            tablesWebView.setVisibility(View.GONE);
            isWebViewVisible = false;
        } else {
            finish();
        }
    }
}
