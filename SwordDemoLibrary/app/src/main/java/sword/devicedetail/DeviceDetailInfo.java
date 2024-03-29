package sword.devicedetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.UUID;

public class DeviceDetailInfo {
    
    
    public String getDeviceId(Context context) {
        String uuid = loadDeviceUUID(context);
        if (uuid == null) {
            uuid = buildDeviceUUID(context);
            saveDeviceUUID(context, uuid);
        }
        return uuid;
    }

    private static String loadDeviceUUID(Context context) {
        return context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE).getString("uuid", null);
    }

    private static void saveDeviceUUID(Context context, String uuid) {
        context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE).edit().putString("uuid", uuid).apply();
    }

    private static String buildDeviceUUID(Context context) {
        String androidId = getAndroidId(context);
        return new UUID(androidId.hashCode(), getBuildInfo().hashCode()).toString();
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getBuildInfo() {
        return "Brand" + Build.BRAND + "/" +
            Build.PRODUCT + "/" +
            Build.DEVICE + "/" +
            Build.ID + "/";
    }

}
