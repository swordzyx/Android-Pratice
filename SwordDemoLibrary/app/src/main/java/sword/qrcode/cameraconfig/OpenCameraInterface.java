package sword.qrcode.cameraconfig;

import android.hardware.Camera;

import sword.logger.SwordLog;

public interface OpenCameraInterface {
    public static final int NO_REQUEST_CAMERA = -1;

    //打开相机
    public static OpenCamera openCamera(int cameraId) {
        //获取当前设备的相机数量，检测 cameraId 是否有效，获取要打开的相机的 cameraId
        int cameraNums = Camera.getNumberOfCameras();
        if (cameraNums == 0) {
            SwordLog.warn("could not found camera on device");
            return null;
        }

        if (cameraId >= cameraNums) {
            SwordLog.warn("Requested camera not exist: " + cameraId);
            return null;
        }

        if (cameraId == NO_REQUEST_CAMERA) {
            cameraId = 0;
            while (cameraId < cameraNums) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
                ++cameraId;
            }
            if (cameraId == cameraNums) {
                SwordLog.warn("No camera facing Back, return camera #0");
                cameraId = 0;
            }
        }

        //通过 Camera.open(cameraId) 打开相机
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        Camera camera = Camera.open(cameraId);
        if(camera == null) {
            SwordLog.warn("camera open failed, return null");
            return null;
        }

        //返回 OpenCamera 对象
        return new OpenCamera(camera, cameraInfo.facing, cameraInfo.orientation);
    }
}
