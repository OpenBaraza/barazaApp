package com.dewcis.baraza;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.dewcis.baraza.Utils.DataClient;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class ReportActivity extends AppCompatActivity {

    String accessToken = null;
    String viewLink = null;
    String linkValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        String viewName = "Leave Report";
        String rBody = "";
        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            accessToken = extras.getString("accessToken");
            viewLink = extras.getString("viewLink");
            viewName = extras.getString("viewName");
            linkValue = extras.getString("linkValue");
            System.out.println("BASE 2010 " + accessToken);

            String myLink = viewLink;
            if(linkValue != null) myLink = viewLink + "&linkdata=" + linkValue;
            rBody = DataClient.makeSecuredRequest(accessToken, myLink, "report", "{}");

            WebView WV = findViewById(R.id.WebView);
            WV.getSettings().setJavaScriptEnabled(true);
            WV.loadData(rBody, "text/HTML", "UTF-8");

            // Place toolbar
            Toolbar LTB = findViewById(R.id.ReportToolbar);
            setSupportActionBar(LTB);
            getSupportActionBar().setTitle(viewName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("BASE 1010 - tool bar press");
        finish();
        return true;
    }
}
