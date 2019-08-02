package com.dewcis.baraza.Utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dewcis.baraza.R;

import java.lang.reflect.Field;

/**
 * Created by Faith on 4/9/2018.
 */

public class EditViews {

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static String removeSpaces(String string){
        String test = string.toLowerCase();
        String test1 = test.replace(" ","_");
        Log.e("BASE1112",test1);
        return test1;
    }

    public static void changeLineColor(EditText editText, ViewGroup viewGroup){
        editText.setTextColor(viewGroup.getResources().getColor(R.color.buttonColor));
        //EditViews.setCursorColor(editText,getResources().getColor(R.color.buttonColor));
        editText.getBackground().setColorFilter(viewGroup.getResources().getColor(R.color.buttonColor),
                PorterDuff.Mode.SRC_ATOP);
    }
}
