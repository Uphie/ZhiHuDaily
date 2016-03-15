package studio.uphie.zhihudaily.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by Uphie on 2015/9/7.
 * Email: uphie7@gmail.com
 */
public class TextToast {

    private static Context context;

    public static void init(Context ctx){
        context=ctx;
    }

    public static void shortShow(String content){
        if (context==null){
            throw new IllegalStateException("TextToast is not initialized");
        }
        Toast toast= Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.show();
    }
    public static void shortShow(int strRes){
        if (context==null){
            throw new IllegalStateException("TextToast is not initialized");
        }
        Toast toast= Toast.makeText(context, context.getResources().getText(strRes), Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void longShow(String content){
        if (context==null){
            throw new IllegalStateException("TextToast is not initialized");
        }
        Toast toast= Toast.makeText(context, content, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void longShow(int strRes){
        if (context==null){
            throw new IllegalStateException("TextToast is not initialized");
        }
        Toast toast= Toast.makeText(context, context.getResources().getText(strRes), Toast.LENGTH_LONG);
        toast.show();
    }
}
