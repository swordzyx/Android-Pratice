package com.sword.utilapk

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.utilclass.LogUtil
import com.example.utilclass.PermissionUtil
import com.example.utilclass.ShellAdbUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var logFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!PermissionUtil.isPermissionGranted(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtil.requestSpecialSinglePermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        logFilePath = "$filesDir${File.separator}XlcwLog.log"

        ShellAdbUtil.execShellCommand(false, "logcat -c")

        LogUtil.debug("logFilePath: $logFilePath")
    }

    fun onClick(view: android.view.View) {
        ShellAdbUtil.execShellCommand(false, "logcat -d > $logFilePath")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish()
        }
    }
}