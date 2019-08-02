package com.dewcis.baraza;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dewcis.baraza.Utils.DataClient;
import com.dewcis.baraza.Utils.MakeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Faith on 4/5/2018.
 */

public class ParkingDilaog extends AppCompatDialogFragment {

    String token, regNumber;
    boolean plateNo;
    boolean oldPlateNo;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;
    Button button;
    TextView textView;
    View child;
    OnCompleteListener onCompleteListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        token = bundle.getString("token");
        Log.e("BASE8070",token);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rev_base,null);



        constraintLayout = (ConstraintLayout)view.findViewById(R.id.mainLayout);

        relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);


        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);*/
        constraintLayout.addView(relativeLayout);

        button = (Button)view.findViewById(R.id.button);
        textView = (TextView) view.findViewById(R.id.textView);
        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        //.getForm("265:1",token,"form");
        String form = DataClient.makeSecuredRequest(token,"265:1","form","{}");
        Log.e("BASE8080",form);

        try {
            JSONObject jsonObject = new JSONObject(form);
            JSONArray jsonArray = jsonObject.getJSONArray("form");
            System.out.println("BASE500"+jsonArray.toString());

            for (int i=0;i<jsonArray.length();i++){
                JSONObject json = jsonArray.getJSONObject(i);
                MakeViews makeViews = new MakeViews(json,relativeLayout,getActivity());
            }
            for (int j=0;j<relativeLayout.getChildCount();j++){
                child = relativeLayout.getChildAt(j);
                if (child.getClass().equals(TextView.class)){
                    Log.e("base3035","TextView");
                    viewPositions(child, 40,30,30,50);
                }else if (child.getClass().equals(EditText.class)){
                    Log.e("base3035","EditText");
                    setDimensions(child,500);
                    viewPositions(child,40,70,30,30);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.setView(view);
        dialog.setTitle("Add Vehicle");
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return dialog.create();
    }

    private void setDimensions(View view, int width){
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = params.WRAP_CONTENT;
        view.setLayoutParams(params);
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog alertDialog = (AlertDialog)getDialog();
        if (alertDialog!=null){
            Button positiveButton = (Button) alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (child.getClass().equals(EditText.class)){
                        regNumber = ((EditText) child).getText().toString();

                        boolean plateNo = regNumber.matches("K[A-Z]{2}[0-9]{3}[A-Z]");
                        boolean oldPlateNo = regNumber.matches("K[A-Z]{2}[0-9]{3}");

                        if (regNumber.equals("")){
                            ((EditText)child).setError("Fill vehicle registration number");
                        }else if(plateNo==false &&oldPlateNo==false) {
                            ((EditText)child).setError("Wrong plate number format");
                        }else{
                            JSONObject jsonObject = MakeViews.saveData(relativeLayout,getActivity());

                            String sendData = DataClient.makeSecuredRequest(token,"265:1","data",jsonObject.toString());
                            Log.e("base4040",sendData);

                            try {
                                JSONObject json = new JSONObject(sendData);
                                String resultCode = json.getString("ResultCode");

                                if (resultCode.equals("0")){
                                    Toast.makeText(getActivity(),"Successful",Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    //Get list of vehicles
                                    String getData = DataClient.makeSecuredRequest(token,"265:0","read","{}");
                                    onCompleteListener.sendData(getData);
                                    Log.e("base4050",getData);
                                }else {
                                    ((EditText) child).setText("");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    //Communicate with the activity
    public interface OnCompleteListener{
        void sendData(String data);
    }

    //Ensures the container activity implements the listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onCompleteListener = (OnCompleteListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public void viewPositions(View view,int left,int top,int right, int bottom) {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)view.getLayoutParams();
        layoutParams.setMargins(left,top,right,bottom);
        view.setLayoutParams(layoutParams);
    }

}
