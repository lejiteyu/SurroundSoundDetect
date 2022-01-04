package lyon.surround.audio.check;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;


public class DisplayScreen {
    String TAG = DisplayScreen.class.getSimpleName();
    Context context;
    public DisplayScreen(Context context){
        this.context=context;
    }

    public void getDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                Log.d(TAG,"display:"+display.getName()+" isHdr:"+display.isHdr());
                int state = display.getState();
                if (display.getState() != Display.STATE_OFF){
                    int width = display.getMode().getPhysicalWidth();
                    int hight = display.getMode().getPhysicalHeight();
                    Log.d(TAG,"20220103 real screen width:"+width+", hight:"+hight+" RefreshRate:"+display.getRefreshRate());
                }

            }

        }
    }
}
