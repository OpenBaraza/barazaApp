package com.dewcis.baraza;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dewcis.baraza.Adapters.ReAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Statement extends AppCompatActivity {

    Button button;
    TextView textView;
    String statement;
    ConstraintLayout constraintLayout;
    RelativeLayout relativeLayout;

    TableLayout tableLayout;
    TableRow tableRow;
    TextView tvTest;

    ArrayList<String> tableHeaders;

    Map<Integer,String> mapHeaders;
    Map<String, String> mapValues;

    JSONArray jsonArray;
    String test [][];
    JSONObject obj;
    JSONArray jArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rev_base);

        relativeLayout = new RelativeLayout(this);
        constraintLayout = (ConstraintLayout)findViewById(R.id.mainLayout);
        constraintLayout.addView(relativeLayout);

        button = (Button)findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        statement = getIntent().getStringExtra("statement");
        Log.e("statem",statement);

        try{
            JSONObject jsonObject = new JSONObject(statement);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            tableLayout = new TableLayout(this);

            relativeLayout.addView(tableLayout);

            int flag = 1;

            for (int i=-1;i<jsonArray.length();i++){
                if (flag == 1){
                    tableRow = new TableRow(this);
                    tableLayout.addView(tableRow);

                    TextView textView = new TextView(this);
                    textView.setText("Activity name");
                    tableRow.addView(textView);

                    TextView tvCredit = new TextView(this);
                    textView.setText("Credit");
                    tableRow.addView(tvCredit);

                    TextView tvDebit = new TextView(this);
                    textView.setText("Debit");
                    tableRow.addView(tvDebit);

                    flag=0;

                }

            }

        }catch (JSONException e){
            Log.e("JSONError",e.toString());
        }

      /*  ArrayList<ArrayList<String>> tableheaders = new ArrayList<>();

        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayValues = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(statement);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Log.e("List",jsonArray.toString());

            JSONObject json = jsonArray.getJSONObject(0);
            JSONArray array = json.names();

            //Get the table headers
            for (int i=0;i<array.length();i++){
                arrayList.add(array.getString(i));
            }
            tableheaders.add(arrayList);


            obj = new JSONObject();
            jArray = new JSONArray();

            for (int k=0;k<jsonArray.length();k++){
                JSONObject object = jsonArray.getJSONObject(k);
                Log.e("test102",object.toString());
                //Get key values

                Iterator<String> keys= object.keys();
                while (keys.hasNext())
                {
                    String keyValue = (String)keys.next();
                    String valueString = object.getString(keyValue);
                    arrayValues.add(valueString);
                }
                obj.put("jsonValues",arrayValues.toString());

                jArray.put(obj);
                obj = new JSONObject();

                arrayValues.clear();
            }

            Log.e("base123",jArray.toString());



            for (int m=0;m<jArray.length();m++){
                JSONObject jsObject = jArray.getJSONObject(m);
                Log.e("base124",jsObject.toString());
                Log.e("base126",jsObject.get("jsonValues").toString());
                Object arrayStr = jsObject.get("jsonValues");
                ArrayList<String> arrayTest = (ArrayList<String>)(arrayStr);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }catch (ClassCastException e){
            Log.e("base125",e.toString());

       }
*/


    }
}
