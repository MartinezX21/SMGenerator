package com.uds.urifia.smgenerator.utils;

import java.util.Date;

public class DateUtil {
    public static Date dateFromString(String dateStr) {
        Date date = null;
        try {
            date = new Date(dateStr);
        } catch (Exception e) {
            // ignore
        }
        return date;
    }

    public static String parse(long millis) {
        String result = "";
        long time = millis;
        if (time >= 86400000) {
            int days = Math.round(time / 86400000);
            result+= (days + "j ");
            time-= (86400000 * days);
        }

        if (time >= 3600000) {
            int hours = Math.round(time / 3600000);
            result+= (hours + "h ");
            time-= (3600000 * hours);
        }

        if (time >= 60000) {
            int minutes = Math.round(time / 60000);
            result+= (minutes + "m");
        }else {
            if (result.equals("")){
                result = "moins d'une minute";
            }
        }

        return result;
    }
}
