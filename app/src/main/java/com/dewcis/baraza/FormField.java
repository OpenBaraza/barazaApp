package com.dewcis.baraza;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;

import android.text.InputType;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Faith Mandela on 3/29/2018.
 * Update by Dennis Gichangi and Joseph Onalo
 */

public class FormField {
    int typeId;
    String fieldName;

    EditText editText = null;
    TextView textView = null;
    AutoCompleteTextView autoCompleteTextView = null;
    Button button = null;
    Spinner spinner = null;
    CheckBox checkBox;
    DatePicker datePicker = null;
    TimePicker timePicker = null;
    ImageView imageView = null;
    Calendar myCalendar = null;
    String dateFormat = "dd/MM/yyyy";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    String jsonKey;
    String spinnerItem;
    List<String> arrayKeys = null;
    List<String> arrayValues = null;
    boolean edit;
    String defaultValue;

    //Constructor to create views
    public FormField(JSONObject jsonObject, final ViewGroup viewGroup, final Activity activity) {
        edit=false;
        defaultValue=null;
        try{
            fieldName = jsonObject.getString("name");
            typeId = jsonObject.getInt("type");
            String title = "";
            if(jsonObject.has("title")) { title = jsonObject.getString("title"); }
            else if(jsonObject.has("tab")) { title = jsonObject.getString("tab"); }
            if(jsonObject.has("data")){
                edit=true;
                defaultValue=jsonObject.getString("data");
            }


		    switch(typeId) {
		        case 0:     // TEXTFIELD
		            textView = new TextView(activity);
		            textView.setText(title);

		            editText = new EditText(new ContextThemeWrapper(activity, R.style.ButtonStyle));
                    editText.setMinWidth(500);
                    editText.setMaxWidth(700);
                    if(edit){editText.setText(defaultValue);}
		            viewGroup.addView(textView);
		            viewGroup.addView(editText);
		            break;
		        case 1:     // TEXTAREA
					textView = new TextView(activity);
		            textView.setText(title);
		            textView.setPadding(10,10,10,10);

		            editText = new EditText(new ContextThemeWrapper(activity, R.style.ButtonStyle));
                    editText.setMinWidth(500);
                    editText.setMaxWidth(700);
		            editText.setMaxLines(10);

		            //Add the views to layout
                    if(edit){editText.setText(defaultValue);}
		            viewGroup.addView(textView);
		            viewGroup.addView(editText);
		            break;
		        case 2:     // CHECKBOX
		            checkBox = new CheckBox(activity);
		            checkBox.setText(title);
		            System.out.println("Check box default value on edit-----"+defaultValue);
		            viewGroup.addView(checkBox);
                    if(edit){
                        if(defaultValue.equals("Yes")){
                            checkBox.setChecked(true);
                        }
                        if(defaultValue.equals("No")){
                            checkBox.setChecked(false);
                        }
                    }
		            break;
		        case 3:     // TEXTTIME
		            break;
		        case 4:     // TEXTDATE
		            textView = new TextView(activity);
		            textView.setText(title);

	                editText = new EditText(activity);
                    editText.setMinWidth(500);
                    editText.setMaxWidth(700);
					makeCalendar(activity);

                  //  if(edit){editText.setText(defaultValue);}

		            viewGroup.addView(textView);
		            viewGroup.addView(editText);
		            break;
		        case 5:     // TEXTTIMESTAMP
		            break;
		        case 6:     // SPINTIME
		            textView = new TextView(activity);
		            textView.setText(title);

					makeTimePicker(activity);

		            viewGroup.addView(textView);
		            viewGroup.addView(timePicker);
		            break;
		        case 7:     // SPINDATE
		            break;
		        case 8:     // SPINTIMESTAMP
		            break;
		        case 9:     // TEXTDECIMAL
                    textView = new TextView(activity);
                    textView.setText(title);

                    editText = new EditText(new ContextThemeWrapper(activity, R.style.ButtonStyle));
                    editText.setMinWidth(500);
                    editText.setMaxWidth(700);
                    if(edit){editText.setText(defaultValue);}
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    viewGroup.addView(textView);
                    viewGroup.addView(editText);
		            break;
		        case 10:     // COMBOLIST
		        case 11:     // COMBOBOX
		            textView = new TextView(activity);
		            textView.setText(title);

					makeComboBox(activity, jsonObject);

		            viewGroup.addView(textView);
		            viewGroup.addView(spinner);
		            break;
		        case 12:     // GRIDBOX
		            break;
		        case 14:     // EDITOR
		            break;
		        case 18:     // PICTURE
		            break;
		    }
        } catch (JSONException ex) {
            Log.e("JSONError", ex.toString());
        }
    }

    public String getValue() {



        String value = null;
        switch(typeId) {
            case 0:     // TEXTFIELD
                value = editText.getText().toString();
                break;
            case 1:     // TEXTAREA
                value = editText.getText().toString();
                break;
            case 2:     // CHECKBOX
                if(checkBox.isChecked()) value = "true";
                else value = "false";
                System.out.println("Checkbox output0--------"+value);
                break;
            case 3:     // TEXTTIME
                break;
            case 4:     // TEXTDATE
                value = editText.getText().toString();
                break;
            case 5:     // TEXTTIMESTAMP
                break;
            case 6:     // SPINTIME
                value = editText.getText().toString();
                break;
            case 7:     // SPINDATE
                break;
            case 8:     // SPINTIMESTAMP
                break;
            case 9:     // TEXTDECIMAL
                value = editText.getText().toString();
                break;
            case 10:     // COMBOLIST
            case 11:     // COMBOBOX
                value = arrayKeys.get(spinner.getSelectedItemPosition());
                break;
            case 12:     // GRIDBOX
                break;
            case 14:     // EDITOR
                break;
            case 18:     // PICTURE
                break;
        }

        return value;
    }

	public void makeCalendar(final Activity activity) {
        //Datepicker
        myCalendar = Calendar.getInstance();
        SimpleDateFormat DF=new SimpleDateFormat(   "dd-MMM-yyyy");
        SimpleDateFormat OutputFormat=new SimpleDateFormat("dd/MM/yyyy");
        Date editDate=null;
        // init - set date to current date
        long currentdate = System.currentTimeMillis();
        String dateString = sdf.format(currentdate);

        final Drawable calendar = activity.getResources().getDrawable(R.drawable.ic_date_range_black_24dp);
        editText.setCompoundDrawablesWithIntrinsicBounds(null,null,calendar,null);
        editText.setFocusable(false);

        // set calendar date and update editDate
        if(edit){
            try {
                editDate=DF.parse(defaultValue);
                myCalendar.setTimeInMillis(editDate.getTime());
                String date=OutputFormat.format(editDate);
                editText.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR,i);
                myCalendar.set(Calendar.MONTH,i1);
                myCalendar.set(Calendar.DAY_OF_MONTH,i2);
                editText.setText(sdf.format(myCalendar.getTime()));
            }
        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity,date,myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
	}

	public void makeTimePicker(final Activity activity) {
        timePicker = new TimePicker(activity);
        timePicker.setPadding(10,10,10,10);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                String format = "";
                if (i==0) {
                    i+=12;
                    format = "AM";
                } else if(i==12) {
                    format = "PM";
                } else if(i>12) {
                    i -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
            }
        });
	}

	public void makeComboBox(final Activity activity, JSONObject jsonObject) {
		try {
            arrayKeys = new ArrayList<String>();
            arrayValues = new ArrayList<String>();

		    String list = jsonObject.getString("list"),
                    IdKey=jsonObject.getString("list_id"),
                    ValueKey=jsonObject.getString("list_value");

		    JSONArray jsonArray = new JSONArray(list);

		    for (int i=0;i<jsonArray.length();i++){
                JSONObject json = jsonArray.getJSONObject(i);
                jsonKey = json.getString(IdKey);
                spinnerItem = json.getString(ValueKey);
                arrayKeys.add(jsonKey);
                arrayValues.add(spinnerItem);
                if(edit){
                    if(jsonKey.equals(defaultValue)){
                        defaultValue=spinnerItem;
                    }
                }
		    }
		    spinner = new Spinner(activity);

		    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, arrayValues);
		    spinner.setAdapter(arrayAdapter);
		    spinner.getAdapter();
		    if(edit){
                int index=arrayAdapter.getPosition(defaultValue);
                spinner.setSelection(index);
            }

        }
        catch (JSONException ex) {Log.e("JSONError", ex.toString());}
	}
}
