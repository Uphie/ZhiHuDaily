package studio.uphie.zhihudaily.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ImageUtil {

    public static final String TEMP_IMG_CACHE_FOLDER = "img";
    /**
     * max image cache size，200M
     */
    private static final long MAX_SIZE_CACHE = 1024 * 1024 * 200;

    public static void init(Context context, String cachePath) {
        File cacheDir = new File(cachePath);

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(cacheDir)
                .setBaseDirectoryName(TEMP_IMG_CACHE_FOLDER)
                .setMaxCacheSize(MAX_SIZE_CACHE)
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(context).setMainDiskCacheConfig(diskCacheConfig).build();
        Fresco.initialize(context, imagePipelineConfig);
    }

    /**
     * show local images
     *
     * @param path path of target image
     * @param view SimpleDraweeView
     */
    public static void showLocalImage(String path, SimpleDraweeView view) {
        view.setImageURI(Uri.parse("file://" + path));
    }

    /**
     * show images in drawables
     *
     * @param resId
     * @param view
     */
    public static void showDrawableImage(int resId, SimpleDraweeView view) {
        view.setImageURI(Uri.parse("res://cn.tianyilm.client/" + resId));
    }

    /**
     * show images in assets
     *
     * @param path
     * @param view
     */
    public static void showAssetsImage(String path, SimpleDraweeView view) {
        view.setImageURI(Uri.parse("asset://" + path));
    }

    public static void displayImage(String url, SimpleDraweeView view) {
        view.setImageURI(Uri.parse(url));
    }

    public static void clearFrescoDiscCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//      imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();
//      combines above two lines
//      imagePipeline.clearCaches();
    }

}
