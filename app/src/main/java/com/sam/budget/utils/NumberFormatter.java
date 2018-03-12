package com.sam.budget.utils;

import java.text.DecimalFormat;

/**
 * Created by SAcevedoM on 29/12/2017.
 */

public class NumberFormatter {

   public static String formatFloat(float number) {
       DecimalFormat formatter = new DecimalFormat("###,###.###");
       return formatter.format(number);
   }

    public static String formatAvailable(float amount1, float amount2) {
        String total = formatFloat(amount1);
        String available = formatFloat(amount1 - amount2);

        return total + " / " + available;
    }
}
