package lyon.surround.audio.check;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {
    String TAG = MainActivity.class.getSimpleName();
    SurroundSound surroundSound;
    TextView textView;
    boolean isFirst = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        textView = findViewById(R.id.textView);
        surroundSound = new SurroundSound(this,textView){
            @Override
            public void onResum() {
                if(isFirst){
                    onResume();
                }
                isFirst = true;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isSupport = getIsSurroundSound();
        if(textView!=null)
            textView.append("\n是否支援5.1聲道："+isSupport);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(surroundSound!=null)
            surroundSound.onStop();
    }


    public boolean getIsSurroundSound(){
        new DisplayScreen(this).getDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(surroundSound!=null){
                boolean channel = surroundSound.getChannel();
                boolean format = surroundSound.isFormstSupportSurround();
                return channel && format;
            }else{
                return false;
            }
        }
        else
            return false;
    }
}
