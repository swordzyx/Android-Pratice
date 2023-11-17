package sword

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.swordlibrary.R
import sword.logger.SwordLog
import sword.pages.HomePage
import sword.view.*

class MainActivity : AppCompatActivity() {
  private val tag = "MainActivity"
  private lateinit var rootView: View

  @SuppressLint("SetTextI18n", "InflateParams")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initWindowSize(this)
    
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
      fullScreenByFlag(window)
    }

    rootView = HomePage(this).rootView
    setContentView(rootView)
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      fullScreenByInsetController(window)
    }
  }
  
  
  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    super.onBackPressed()
    SwordLog.debug(tag, "onBackPress")
  }
}