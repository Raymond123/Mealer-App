package com.mealer.ui;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpPage {

    // check if first and last name are empty
    public static boolean validateName(String fName, String lName, Context context){
        if(fName.equals("")){
            if(context!=null) {
                Toast.makeText(context, "First name cannot be empty", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        if(lName.equals("")){
            if(context!=null) {
                Toast.makeText(context, "Last name cannot be empty", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    // check if address is empty
    public static boolean validateAddress(String address, Context context){
        if(address.equals("")){
            if(context!=null){
                Toast.makeText(context, "Address cannot be empty!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

}
