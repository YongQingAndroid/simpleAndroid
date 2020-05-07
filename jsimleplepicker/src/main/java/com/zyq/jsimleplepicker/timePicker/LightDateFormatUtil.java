package com.zyq.jsimleplepicker.timePicker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightDateFormatUtil {
    public static String FormatDate(String dateStr) {
        return FormatDate(dateStr, "yyyy-MM-dd HH:mm");
    }

    public static String FormatDate(String dateStr, String outFormat) {
       DateFormat formatter1 =new SimpleDateFormat(outFormat);
        return formatter1.format(praseDateString(dateStr));
    }

    public static Date praseDateString(String sourceDate) {
        String stringTime = "([0-9]{1,2})[:|时|点]([0-9]{1,2})";
        String stringDate = "([0-9]{4})?[年|\\-|/]?([0-9]{1,2})[月|\\-|/]([0-9]{1,2})";

        Calendar calendar = Calendar.getInstance();
        try {
            Matcher timer = Pattern.compile(stringTime).matcher(sourceDate);
            while (timer.find()) {
                if (timer.group(1) != null)
                    calendar.set(Calendar.HOUR_OF_DAY,
                            Integer.parseInt(timer.group(1)));
                if (timer.group(2) != null)
                    calendar.set(Calendar.MINUTE,
                            Integer.parseInt(timer.group(2)));
            }
            Matcher dateMatcher = Pattern.compile(stringDate).matcher(sourceDate);
            while (dateMatcher.find()) {
                if (dateMatcher.group(1) != null)
                    calendar.set(Calendar.YEAR,
                            Integer.parseInt(dateMatcher.group(1)));
                if (dateMatcher.group(2) != null)
                    calendar.set(Calendar.MONTH,
                            Integer.parseInt(dateMatcher.group(2))-1);
                if (dateMatcher.group(3) != null)
                    calendar.set(Calendar.DAY_OF_MONTH,
                            Integer.parseInt(dateMatcher.group(3)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return calendar.getTime();
        }

    }
}