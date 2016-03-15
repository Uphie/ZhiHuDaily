package studio.uphie.zhihudaily.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Uphie on 2016/3/14 0014.
 * Email:uphie7@gmail.com
 */
public class TimeUtil {
    private static SimpleDateFormat format=new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public static String getTime(long time){
        Date date=new Date();
        date.setTime(time);
        return format.format(date);
    }
}
