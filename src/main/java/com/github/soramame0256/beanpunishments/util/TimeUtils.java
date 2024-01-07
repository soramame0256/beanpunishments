package com.github.soramame0256.beanpunishments.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    private static final Pattern TIME_FORMAT = Pattern.compile(
            "^((?<year>\\d+)y)?((?<month>\\d+)M)?((?<day>\\d+)d)?((?<hour>\\d+)h)?((?<minute>\\d+)m)?((?<second>\\d+)s)?$"
    );
    public static boolean isTimeFormat(String format){
        return TIME_FORMAT.matcher(format).matches() && format.length()!=0;
    }
    public static long getSecond(String format){
        Matcher m;
        long second=0;
        if((m=TIME_FORMAT.matcher(format)).matches()){
            second+=m.group("year") != null ? Long.parseLong(m.group("year"))*60*60*24*365 : 0;
            second+=m.group("month") != null ? Long.parseLong(m.group("month"))*60*60*24*30 : 0;
            second+=m.group("day") != null ? Long.parseLong(m.group("day"))*60*60*24 : 0;
            second+=m.group("hour") != null ? Long.parseLong(m.group("hour"))*60*60 : 0;
            second+=m.group("minute") != null ? Long.parseLong(m.group("minute"))*60 : 0;
            second+=m.group("second") != null ? Long.parseLong(m.group("second")) : 0;
        }
        return second;
    }
    public static String getFormattedTime(long l){
        StringBuilder sb = new StringBuilder();
        if(l/60/60/24/365>=1) {
            sb.append(l/60/60/24/365)
              .append("year");
            if(l/60/60/24/365>1)sb.append("s");
            sb.append(" ");
            l%=60*60*24*365;
        }
        if(l/60/60/24/30>=1) {
            sb.append(l/60/60/24/30)
                    .append("month");
            if(l/60/60/24/30>1)sb.append("s");
            sb.append(" ");
            l%=60*60*24*30;
        }
        if(l/60/60/24>=1) {
            sb.append(l/60/60/24)
                    .append("day");
            if(l/60/60/24>1)sb.append("s");
            sb.append(" ");
            l%=60*60*24;
        }
        if(l/60/60>=1) {
            sb.append(l/60/60)
                    .append("hour");
            if(l/60/60>1)sb.append("s");
            sb.append(" ");
            l%=60*60;
        }
        if(l/60>=1) {
            sb.append(l/60)
                    .append("minute");
            if(l/60>1)sb.append("s");
            sb.append(" ");
            l%=60;
        }
        if(l>=1) {
            sb.append(l)
                    .append("second");
            if(l>1)sb.append("s");
            sb.append(" ");
            l=0;
        }
        return sb.toString().trim();
    }
}
