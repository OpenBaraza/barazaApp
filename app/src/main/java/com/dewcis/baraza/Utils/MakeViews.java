package com.dewcis.baraza.Utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dewcis.baraza.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Faith on 3/23/2018.
 */

public class MakeViews {
    EditText editText = null;
    TextView textView = null;
    AutoCompleteTextView autoCompleteTextView = null;
    Button button = null;
    Spinner spinner = null;
    CheckBox checkBox;
    RadioGroup radioGroup = null;
    RadioButton radio1 = null;
    RadioButton radio2 = null;
    DatePicker datePicker = null;
    TimePicker timePicker = null;
    ImageView imageView = null;
    Calendar myCalendar = null;
    String dateFormat = "dd/MM/yyyy";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    JSONArray jsonArray = null;
    String value = null;
    String jsonKey;
    String spinnerItem;
    ArrayList<String> arrayList = null;

    public static String url = "http://demo.dewcis.com/revenuecollection/dataserver";

    //Constructor to create views
    public MakeViews(JSONObject jsonObject, final ViewGroup viewGroup, final Activity activity){
        try{
            String name = jsonObject.getString("name");
            String title = jsonObject.getString("title");
            String type = jsonObject.getString("type");


            if (type.equals("0")){
                textView = new TextView(activity);
                //textView.setText(name);
                textView.setText(title);
                textView.setId(generateIds(textView));

                //editText = new EditText(activity);
                editText = new EditText(new ContextThemeWrapper(activity,R.style.ButtonStyle));
                editText.setId(generateIds(editText));

                viewGroup.addView(textView);
                viewGroup.addView(editText);
            }else if(type.equals("1")){
                System.out.println("BASE400 :"+"Elem");
                textView = new TextView(activity);
                textView.setText(title);
                textView.setId(generateIds(textView));
                textView.setPadding(10,10,10,10);
                int test = generateIds(textView);
                System.out.println("BASE400 :"+String.valueOf(test));


                editText = new EditText(new ContextThemeWrapper(activity,R.style.ButtonStyle));
                editText.setHint("TESTING....");
                editText.setBottom(generateIds(textView));
                editText.setMaxLines(10);
                editText.setId(generateIds(editText));
                System.out.println("BASE500 :"+String.valueOf(generateIds(editText)));

                //Add the views to layout
                viewGroup.addView(textView);
                viewGroup.addView(editText);
            }else if(type.equals("2")){
                checkBox = new CheckBox(activity);
                checkBox.setText(title);
                checkBox.setId(generateIds(radio1));

                viewGroup.addView(checkBox);
            }else if(type.equals("3")){

            }else if(type.equals("4")){
                textView = new TextView(activity);
                textView.setId(generateIds(textView));
                textView.setText(title);
                //Datepicker
                myCalendar = Calendar.getInstance();
                // init - set date to current date
                long currentdate = System.currentTimeMillis();
                String dateString = sdf.format(currentdate);


                final Drawable calendar = activity.getResources().getDrawable(R.drawable.ic_date_range_black_24dp);
                editText = new EditText(activity);
                editText.setCompoundDrawablesWithIntrinsicBounds(null,null,calendar,null);
                //editText.setText(dateString);
                editText.setFocusable(false);

                // set calendar date and update editDate
                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        myCalendar.set(Calendar.YEAR,i);
                        myCalendar.set(Calendar.MONTH,i1);
                        myCalendar.set(Calendar.DAY_OF_MONTH,i2);
                        updateDate(editText);
                    }
                };

                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(activity,date,myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                viewGroup.addView(textView);
                viewGroup.addView(editText);
            }else if(type.equals("5")){

            }else if(type.equals("6")){
                textView = new TextView(activity);
                textView.setText(title);


                timePicker = new TimePicker(activity);
                timePicker.setPadding(10,10,10,10);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                        String format = "";
                        if (i==0){
                            i+=12;
                            format = "AM";
                        }else if(i==12){
                            format = "PM";
                        }else if(i>12){
                            i -= 12;
                            format = "PM";
                        }else {
                            format = "AM";
                        }
                        if (textView!=null){
                            String hour = String.valueOf(i < 10 ? "0" + i : i);
                            String min = String.valueOf(i1 < 10 ? "0" + i1 : i1);
                            String text = hour+" : "+min +" "+format;
                            textView.setText(text);
                        }
                    }
                });
                viewGroup.addView(textView);
                viewGroup.addView(timePicker);
            }else if(type.equals("7")){

            }else if(type.equals("8")){

            }else if(type.equals("9")){

            }else if(type.equals("10")){



            }else if(type.equals("11")){
               /* String list = jsonObject.getString("list");
                JSONArray jsonArray = new JSONArray(list);
                JSONObject json = new JSONObject();
                for (int i=0;i<jsonArray.length();i++){
                    json = jsonArray.getJSONObject(i);
                    JSONArray array = json.names();
                    System.out.println("BASE1000 "+array.toString());
                    System.out.println("BASE1100 "+array.get(1).toString());
                    //Get the key at position 1
                    jsonKey = array.get(1).toString();
                    spinnerItem = json.getString(jsonKey);
                    System.out.println("BASE1200 "+spinnerItem);

                    arrayList.add(spinnerItem);
                }

                textView = new TextView(activity);
                textView.setText(title);

                autoCompleteTextView = new AutoCompleteTextView(activity);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,R.layout.support_simple_spinner_dropdown_item,arrayList);
                autoCompleteTextView.setAdapter(arrayAdapter);

                viewGroup.addView(textView);
                viewGroup.addView(autoCompleteTextView);*/


                String list = jsonObject.getString("list");
                System.out.println("BASE700 "+list);
                JSONArray jsonArray = new JSONArray(list);
                JSONObject json = new JSONObject();

                for (int i=0;i<jsonArray.length();i++){
                    json = jsonArray.getJSONObject(i);
                    JSONArray array = json.names();
                    System.out.println("BASE1000 "+array.toString());
                    System.out.println("BASE1100 "+array.get(1).toString());
                    //Get the key at position 1
                    jsonKey = array.get(1).toString();
                    spinnerItem = json.getString(jsonKey);
                    System.out.println("BASE1200 "+spinnerItem);

                    arrayList.add(spinnerItem);
                }

                System.out.println("BASE1300 "+arrayList);
                spinner = new Spinner(activity);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,R.layout.support_simple_spinner_dropdown_item,arrayList);
                spinner.setAdapter(arrayAdapter);

                textView = new TextView(activity);
                textView.setText(title);

                viewGroup.addView(textView);
                viewGroup.addView(spinner);

            }else if(type.equals("12")){



            }else if(type.equals("13")){

            }else if(type.equals("14")){

            }else if(type.equals("15")){

            }else if(type.equals("16")){

            }else if(type.equals("17")){

            }else if(type.equals("18")){

            }
        }catch (JSONException e){
            Log.e("JSONError",e.toString());
        }
    }

    //Method to generate Ids for different views
    public static int generateIds(View view){
        int view_id = view.generateViewId();
        return view_id;
    }

    public void updateDate(EditText editText){
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    //Method to save data
    public static JSONObject saveData(ViewGroup viewGroup, Context context){
        ArrayList<String> tvData = new ArrayList<>();
        ArrayList<String> etData = new ArrayList<>();
        JSONObject jsonData = null;
        String textViewData;
        String editTextData;

        for (int j=0;j<viewGroup.getChildCount();j++){
            View child = viewGroup.getChildAt(j);
            if (child.getClass().equals(TextView.class)){
                System.out.println("BASE1300 "+String.valueOf(child.getId()));
                textViewData = ((TextView) child).getText().toString();
                String textData = EditViews.removeSpaces(textViewData);
                tvData.add(textData);
            }else if (child.getClass().equals(EditText.class)){
                System.out.println("BASE1100 "+String.valueOf(child.getId()));
                editTextData = ((EditText) child).getText().toString();
                etData.add(editTextData);
            }
            System.out.println("BASE2100 "+etData.toString());
            System.out.println("BASE2200 "+etData.toString());
        }
        if (etData.contains("")){
            System.out.println("BASE1800 ");
            etData.removeAll(Collections.singleton(""));
            System.out.println("BASE2000 "+etData.toString());
            Toast.makeText(context,"Empty fields",Toast.LENGTH_LONG).show();
        }else {
            jsonData = new JSONObject();
            Set<String> s = new LinkedHashSet<>(etData);
            Set<String> set = new LinkedHashSet<>(tvData);

            etData.clear();
            tvData.clear();
            etData.addAll(s);
            tvData.addAll(set);

            for (int i=0;i<tvData.size();i++){
                try {
                    jsonData.put(tvData.get(i),etData.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("baseJSON",jsonData.toString());
        return jsonData;
    }
}
