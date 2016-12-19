package cn.hugeterry.updatefun;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import cn.hugeterry.updatefun.config.DownloadKey;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.module.Download;
import cn.hugeterry.updatefun.module.HandleUpdateResult;
import cn.hugeterry.updatefun.utils.GetAppInfo;
import cn.hugeterry.updatefun.utils.InstallApk;
import cn.hugeterry.updatefun.utils.StorageUtils;
import cn.hugeterry.updatefun.view.DownLoadDialog;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/12 16:47
 */
public class UpdateFunGO {

    private static Thread download;
    private static Thread thread_update;

    private static volatile UpdateFunGO sInst = null;

    public static void manualStart(Context context) {
        DownloadKey.ISManual = true;
        if (!DownloadKey.LoadManual) {
            DownloadKey.LoadManual = true;
            new UpdateFunGO(context);
        } else Toast.makeText(context, "正在更新中,请稍等", Toast.LENGTH_LONG).show();
    }

    public static UpdateFunGO init(Context context) {
        UpdateFunGO inst = sInst;
        if (inst == null) {
            synchronized (UpdateFunGO.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new UpdateFunGO(context);
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    private UpdateFunGO(Context context) {
        DownloadKey.FROMACTIVITY = context;
        if (DownloadKey.TOShowDownloadView != 2) {
            thread_update = new Thread(new HandleUpdateResult(context));
            thread_update.start();
        }
    }

    public static void showDownloadView(Context context) {
        if (!checkLocalVersionApk(context)) {
            Toast.makeText(context, "安装包已下载，请安装", Toast.LENGTH_SHORT).show();
            return;
        }
        DownloadKey.saveFileName =
                GetAppInfo.getAppPackageName(context) + ".apk";
        if (UpdateKey.DialogOrNotification == 1) {
            Intent intent = new Intent();
            intent.setClass(context, DownLoadDialog.class);
            ((Activity) context).startActivityForResult(intent, 0);
        } else if (UpdateKey.DialogOrNotification == 2) {
            Notification.Builder builder = notificationInit(context);
            download = new Download(context, builder);
            download.start();
        }
    }

    private static Notification.Builder notificationInit(Context context) {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("开始下载")
                .setContentTitle(GetAppInfo.getAppName(context))
                .setContentText("正在更新")
                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis());
        return builder;
    }

    public static void onResume(Context context) {
        if (DownloadKey.TOShowDownloadView == 2) {
            showDownloadView(context);
        } else {
            if (sInst != null) sInst = null;
        }
    }

    public static void onStop(Context context) {
        if (DownloadKey.TOShowDownloadView == 2 && UpdateKey.DialogOrNotification == 2) {
            download.interrupt();
        }
        if (DownloadKey.FROMACTIVITY != null) {
            DownloadKey.FROMACTIVITY = null;
        }
        if (thread_update != null) {
            thread_update.interrupt();
        }
        if (DownloadKey.ISManual) {
            DownloadKey.ISManual = false;
        }
        if (DownloadKey.LoadManual) {
            DownloadKey.LoadManual = false;
        }
    }

    public static boolean checkLocalVersionApk(Context context) {
        File file = new File(StorageUtils.getCacheDirectory(context), GetAppInfo.getAppPackageName(context) + ".apk");
        if (file.exists()) {
            //存在，校验版本
            PackageInfo apkInfo = GetAppInfo.getAPKInfo(context, file.toString());
            String appName = GetAppInfo.getAppPackageName(context);
            int appVersionCode = GetAppInfo.getAppVersionCode(context);
            assert apkInfo != null;
            if (apkInfo.versionCode > appVersionCode && apkInfo.applicationInfo.packageName.equals(appName)) {
                Log.i("UpdateFun TAG", "apk检验:包名相同,versioncode大于当前版本，开始安装apk");
                if(apkInfo.versionCode >= DownloadKey.versionCode){
                    //判断已下载好的apk的code是否大于等于update获取的code
                    DownloadKey.TOShowDownloadView = 1;
                    InstallApk.startInstall(context, file);
                    return false;
                }else {
                    Log.i("UpdateFun TAG", "本地app版本过旧，需要重新下载");
                    return true;
                }
            } else {
                Log.i("UpdateFun TAG", "本地文件检测不通过，开始下载apk");
            }
        } else {
            Log.i("UpdateFun TAG", "开始下载apk");
            return true;
        }
        return true;
    }
}



