import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.shubham.emergencyapplication.R

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val message = intent?.getBooleanExtra("message", false)
        Log.d("OverlayService", "Received message:")
//        if (message!!) {
//            showOverlay()
//        }
//        showOverlay()
        return START_NOT_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
