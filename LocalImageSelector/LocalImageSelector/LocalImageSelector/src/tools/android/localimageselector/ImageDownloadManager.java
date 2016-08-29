package tools.android.imagedownloader;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liuchonghui on 16/8/1.
 */
public class ImageDownloadManager {

    protected static ImageDownloadManager instance;
    protected ExecutorService downloadExecutor;
    protected ConcurrentHashMap<String, Collection<ImageLoadListener>> pair = new ConcurrentHashMap<String, Collection<ImageLoadListener>>();
    protected static HashMap<String, String> cache = new HashMap<String, String>();

    public static ImageDownloadManager getInstance() {
        if (instance == null) {
            synchronized (ImageDownloadManager.class) {
                if (instance == null) {
                    instance = new ImageDownloadManager();
                }
            }
        }
        return instance;
    }

    public static void setSingletonInstance(ImageDownloadManager singleton) {
        synchronized (ImageDownloadManager.class) {
            if (instance != null) {
                instance = null;
            }
            instance = singleton;
        }
    }

    protected ImageDownloadManager() {
    }

    public String getDownloadCacheDir(Context context) {
        return getDefaultCachePath(context);
    }

    public void cancelAll() {
        Set<String> keys = pair.keySet();
        for (String key : keys) {
            Collection<?> mess = pair.get(key);
            if (mess != null) {
                mess.clear();
                mess = null;
            }
        }
        pair.clear();

        cache.clear();

        if (downloadExecutor != null) {
            try {
                downloadExecutor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
                downloadExecutor = null;
            }
        }
        downloadExecutor = null;

        instance = null;
    }

    protected void writeCache(String id, String value) {
        if (id == null || id.length() == 0) {
            return;
        }
        if (value == null || value.length() == 0) {
            return;
        }
        if (cache != null) {
            cache.put(id, value);
        }
    }

    public static String get(String key) {
        String value = null;
        if (key == null || key.length() == 0) {
            return value;
        }
        if (cache != null) {
            value = cache.get(key);
        }
        return value;
    }

    public void downloadImage(Context context, String identify,
                              String url, ImageLoadListener listener) {
        if (url == null || url.length() == 0) {
            return;
        }
        ImageLoadListener l = listener;
        if (l == null) {
            l = new ImageLoadAdapter() {};
        }
        Collection<ImageLoadListener> ls = pair.get(url);
        if (ls == null) {
            ls = new ArrayList<ImageLoadListener>();
            ls.add(l);
            pair.put(url, ls);
            ImageDownloadWorker worker = createImageDownloadWorker(url,
                    getDownloadCacheDir(context), identify);
            if (downloadExecutor == null) {
                downloadExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable,
                                "IDM download-worker");
                        thread.setPriority(Thread.MAX_PRIORITY - 1);
                        return thread;
                    }
                });
            }
            downloadExecutor.submit(worker);

        } else {
            ls.add(l);
        }
    }

    protected ImageDownloadWorker createImageDownloadWorker(String url, String cachePath,
                                                            String fileName) {
        return new URLConnectionWorker(url, cachePath, fileName);
    }

    public void notifyDownloadStart(final String url) {
        final Collection<ImageLoadListener> ls = pair.get(url);
        if (ls != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (ImageLoadListener l : ls) {
                        l.onImageLoadStart(url);
                    }
                }
            });
        }
    }

    public void notifyDownloadFailure(final String url, final String message) {
        final Collection<ImageLoadListener> ls = pair.get(url);
        if (ls != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (ImageLoadListener l : ls) {
                        l.onImageLoadFailure(url, message);
                    }
                }
            });
        }
    }

    public void notifyDownloadCancel(final String url) {
        final Collection<ImageLoadListener> ls = pair.remove(url);
        if (ls != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (ImageLoadListener l : ls) {
                        l.onImageLoadCancel(url);
                    }
                }
            });
        }
    }

    public void notifyDownloadProgress(final String url, final int progress) {
        final Collection<ImageLoadListener> ls = pair.get(url);
        if (ls != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (ImageLoadListener l : ls) {
                        l.onImageLoadProgress(url, progress);
                    }
                }
            });
        }
    }

    public void notifyDownloadSuccess(final String url, final String path) {
        writeCache(url, path);
        final Collection<ImageLoadListener> ls = pair.get(url);
        if (ls != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (ImageLoadListener l : ls) {
                        l.onImageLoadSuccess(url, path);
                    }
                }
            });
        }
    }

    public void notifyDownloadClear(final boolean success, final String url, final String path) {
        final Collection<ImageLoadListener> ls = pair.remove(url);
        if (ls != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (ImageLoadListener l : ls) {
                        l.onImageLoadClear(success, url, path);
                    }
                }
            });
        }
    }

    private String getDefaultCachePath(Context context) {
        String path = null;
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir == null) {
            path = context.getFilesDir() + "/download_cache";
        } else {
            path = dir.getAbsolutePath();
        }
        return path;
    }
}
