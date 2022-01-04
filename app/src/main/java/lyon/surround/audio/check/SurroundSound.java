package lyon.surround.audio.check;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;


import java.util.List;

/**
 * 20211221  音訊輸出通道切換
 *     <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
 *
 *     https://www.itread01.com/content/1548400346.html
 *
 */
public class SurroundSound {
    String TAG = SurroundSound.class.getSimpleName();
    Context context;
    boolean isSupporSurroundSound = false;
    TextView textView;
    public SurroundSound(Context context, TextView textView){
        this.context=context;
        this.textView=textView;
        onAudioDeviceCallback();//hdmi檢測
    }

    public boolean getChannel(){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for(int i =0;i<devices.length;i++){
                AudioDeviceInfo audioDevice = devices[i];
                int id =  audioDevice.getId();
                boolean isInput = audioDevice.isSource();
                boolean isOutput = audioDevice.isSink();
                int type = audioDevice.getType();
                int formast[] = audioDevice.getEncodings();
//                Log.d(TAG,"["+i+"] 20211221 audio formast:"+intToString(formast));
                int[] sampleRates = audioDevice.getSampleRates();
//                Log.d(TAG,"["+i+"] 20211221 audio sampleRates:"+intToString(sampleRates));
                Log.d(TAG,"["+i+"] 20211221 audio ,Device:"+audioTypeToReadableString(type));
                int channelCounts[] = audioDevice.getChannelCounts();
                int channelMasks[] = audioDevice.getChannelMasks();
                int channelMaskIndex[] = audioDevice.getChannelIndexMasks();
                String sss = "\n["+i+"]20211221 channelCounts:"+intToString(channelCounts);
                Log.d(TAG,sss);
                textView.append(sss);
//                Log.d(TAG,"["+i+"]20211221 channelMaskIndex:"+intToString(channelMaskIndex));
//                        Log.d(TAG,"["+i+"]20211221 channelMasks:"+intToString(channelMasks));
//                        Log.d(TAG, "["+i+"]20211221 getProductName:" + audioDevice.getProductName());
                if(type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER){
                    //內置揚聲器
                    try {
                        for (int j = 0; j < channelCounts.length; j++) {
                            int channelCount = channelCounts[j];
                            int channelConfig = reconfigure(channelCount);
                            if(channelCount>6) {
                                sss = "\n20211221 內置揚聲器 channelCount:" + channelCount + " / channelConfig:" + channelConfig + ",Device:" + audioTypeToReadableString(type);
                                Log.d(TAG,sss);
                                textView.append(sss);
                                break;
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG,"20211221 Exception:"+ e);
                    }
                }
                else if(isOutput){//&& isHDMIPlugin){
                    try {
                        for (int j = 0; j < channelCounts.length; j++) {
                            int channelCount = channelCounts[j];
                            int channelConfig = reconfigure(channelCount);
                            if(channelCount>=6) {
                                sss = "\n20211221 channelCount:" + channelCount + " / channelConfig:" + channelConfig + ",Device:" + audioTypeToReadableString(type);
                                Log.d(TAG,sss);
                                textView.append(sss);
                                break;
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG,"20211221 Exception:"+ e);
                    }
                }
            }
        }
        return isSupporSurroundSound;
    }

    public boolean isFormstSupportSurround(){
        boolean isFormstSupportSurround = false;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for(int i =0;i<devices.length;i++) {
                AudioDeviceInfo audioDevice = devices[i];
                int formasts[] = audioDevice.getEncodings();
                int type = audioDevice.getType();
                String sss = "\n["+i+"] 20211221 audio formast:"+intToString(formasts)+" , type:"+audioTypeToReadableString(type);
                Log.d(TAG,sss);
                textView.append(sss);

                for (int formast : formasts) {
                    switch (formast) {
                        case AudioFormat.ENCODING_INVALID: //0
                        case AudioFormat.ENCODING_DEFAULT://1
                        case AudioFormat.ENCODING_PCM_16BIT://2 雙聲道
                        case AudioFormat.ENCODING_PCM_8BIT://3 單聲道
                        case AudioFormat.ENCODING_PCM_FLOAT://4
                        case AudioFormat.ENCODING_AC3: // 5 Dolby Digital
                        case AudioFormat.ENCODING_DTS://7
                        case AudioFormat.ENCODING_MP3://9
                        case AudioFormat.ENCODING_AAC_LC:// 10
                        case AudioFormat.ENCODING_AAC_HE_V1:// 11
                        case AudioFormat.ENCODING_AAC_HE_V2://12
                        case AudioFormat.ENCODING_AAC_XHE://16
                        case AudioFormat.ENCODING_AC4://17
                        case AudioFormat.ENCODING_AAC_ELD://15 支持单声道/立体声内容
                        case AudioFormat.ENCODING_DOLBY_MAT:// 19 杜比全景聲內容
                        case AudioFormat.ENCODING_OPUS://20 有損音訊壓縮的數位音訊編碼格式
                        case AudioFormat.ENCODING_IEC61937://13 用于PCM或S / PDIF直通的压缩音频。
                            isFormstSupportSurround = false;
                            break;
                        case AudioFormat.ENCODING_E_AC3:// 6 E-AC-3 壓縮，也稱為 Dolby Digital Plus 或 DD+
                        case AudioFormat.ENCODING_DTS_HD:// 8
                        case AudioFormat.ENCODING_DOLBY_TRUEHD:// 14 無損多聲道音頻編解碼器
                        case AudioFormat.ENCODING_E_AC3_JOC:// 18 E-AC-3 壓縮，也稱為 Dolby Digital Plus 或 DD+
                            isFormstSupportSurround = true;
                            return isFormstSupportSurround;
                    }
                }
            }
        }

        return isFormstSupportSurround;
    }

    private int reconfigure(int channelCount) {
       //int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int channelConfig;
        String sss;
        switch (channelCount) {
            case 1:
                channelConfig = AudioFormat.CHANNEL_OUT_MONO;
                //Log.d(TAG,"20211221 支援單聲道");
                isSupporSurroundSound = false;
                break;
            case 2:
                channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
                //Log.d(TAG,"20211221 支援雙聲道");
                isSupporSurroundSound = false;
                break;
            case 4:
                channelConfig = AudioFormat.CHANNEL_OUT_QUAD;
                //Log.d(TAG,"20211221 支援2.1聲道");
                isSupporSurroundSound = false;
                break;
            case 6:
                channelConfig = AudioFormat.CHANNEL_OUT_5POINT1;

                sss = "\n20211221 支援5.1聲道";
                Log.d(TAG,sss);
                textView.append(sss);
                isSupporSurroundSound = true;
                break;
            case 8:
                channelConfig = AudioFormat.CHANNEL_OUT_7POINT1_SURROUND;

                sss = "\n20211221 支援7.1聲道";
                Log.d(TAG,sss);
                textView.append(sss);
                isSupporSurroundSound = true;
                break;
            default:
                sss = "\nUnsupported channel count: " + channelCount;
                Log.d(TAG,sss);
                textView.append(sss);
                channelConfig = 0;
                isSupporSurroundSound = false;
                Log.d(TAG,"20211221 未知channel count");
        }
        return channelConfig;
    }

    private  String audioTypeToReadableString(int type){
        switch (type){
            case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                return "TYPE_BUILTIN_EARPIECE";
            case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                return "TYPE_BUILTIN_SPEAKER";
            case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                return "TYPE_WIRED_HEADSET";
            case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                return "TYPE_WIRED_HEADPHONES";
            case AudioDeviceInfo.TYPE_LINE_ANALOG:
                return "TYPE_LINE_ANALOG";
            case AudioDeviceInfo.TYPE_LINE_DIGITAL:
                return "TYPE_LINE_DIGITAL";
            case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                return "TYPE_BLUETOOTH_SCO";
            case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                return "TYPE_BLUETOOTH_A2DP";
            case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                return "TYPE_BUILTIN_MIC";
            case AudioDeviceInfo.TYPE_FM_TUNER:
                return "TYPE_FM_TUNER";
            case AudioDeviceInfo.TYPE_TV_TUNER:
                return "TYPE_TV_TUNER";
            case AudioDeviceInfo.TYPE_TELEPHONY:
                return "TYPE_TELEPHONY";
            case AudioDeviceInfo.TYPE_HDMI:
                return "HDMI";
            case AudioDeviceInfo.TYPE_HDMI_ARC:
                return "HDMI ARC"; // HDMI 支援音頻回朔
            //～ omitted ～
            default:    //0
                return"TYPE_UNKNOWN";
        }
    }


    private String intToString(int[] i){
        String s = "[";
        for(int j=0;j<i.length;j++){
            s = s+i[j]+",";
        }
        s =s+"]";
        return s;
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // pause video
            String action = intent.getAction();
            Log.d(TAG,"action:"+action);
            switch (action) {
                case AudioManager.ACTION_HDMI_AUDIO_PLUG :
                    // EXTRA_AUDIO_PLUG_STATE: 0 - UNPLUG, 1 - PLUG
                    int plug = intent.getIntExtra(AudioManager.EXTRA_AUDIO_PLUG_STATE, -1);
                    if(plug==1){
                        String sss = "\nHDMI 插入";
                        Log.d(TAG,sss);
                        textView.append(sss);
                        isHDMIPlugin = true;
                        onResum();
                    }else{
                        String sss = "\nHDMI 沒有插入";
                        Log.d(TAG,sss);
                        textView.append(sss);
                        isHDMIPlugin = false;
                    }
                    Log.d(TAG, "ACTION_HDMI_AUDIO_PLUG " + plug);
                    break;
            }
        }
    };
    boolean isHDMIPlugin = false;

    public void onResum(){

    }

    public void onStop(){
        context.unregisterReceiver(eventReceiver);
    }

    private void onAudioDeviceCallback(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

            //檢查HDMI是否插入
            IntentFilter filter = new IntentFilter();
            filter.addAction(AudioManager.ACTION_HDMI_AUDIO_PLUG);
            context.registerReceiver(eventReceiver, filter);

            AudioDeviceCallback audioDeviceCallback = new AudioDeviceCallback() {
                @Override
                public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                    super.onAudioDevicesAdded(addedDevices);
                    for(AudioDeviceInfo device :addedDevices){
                        int type = device.getType();
                        int formasts[] = device.getEncodings();
                        if(type == AudioDeviceInfo.TYPE_HDMI || type == AudioDeviceInfo.TYPE_HDMI_ARC)
                            Log.d(TAG,"add audio Device:"+device+" "+intToString(device.getChannelCounts())+", formasts:"+intToString(formasts)+", type:"+audioTypeToReadableString(type));
                    }
                }

                @Override
                public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                    super.onAudioDevicesRemoved(removedDevices);
                    for(AudioDeviceInfo device :removedDevices){
                        int type = device.getType();
                        if(type == AudioDeviceInfo.TYPE_HDMI || type == AudioDeviceInfo.TYPE_HDMI_ARC)
                            Log.d(TAG,"remove audio Device:"+device+" "+intToString(device.getChannelCounts()));
                    }
                }
            };
            audioManager.registerAudioDeviceCallback(audioDeviceCallback,null);
        }
    }



}
