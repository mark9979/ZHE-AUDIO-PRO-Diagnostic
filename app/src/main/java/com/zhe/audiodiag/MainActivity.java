package com.zhe.audiodiag;

import android.app.Activity;
import android.os.Bundle;
import android.media.AudioManager;
import android.media.AudioDeviceInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.view.Gravity;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.io.File;
import java.util.List;

public class MainActivity extends Activity {
    private StringBuilder report;
    private void line(String s) { report.append(s).append("\n"); }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        report = new StringBuilder();
        buildReport();
        ScrollView scroll = new ScrollView(this);
        TextView tv = new TextView(this);
        tv.setText(report.toString());
        tv.setTextSize(16);
        tv.setTextColor(0xFFECEFF1);
        tv.setPadding(24, 24, 24, 24);
        tv.setTextIsSelectable(true);
        scroll.setBackgroundColor(0xFF101820);
        scroll.addView(tv);
        setContentView(scroll);
    }

    private void buildReport() {
        line("=== ZHE AUDIO PRO Diagnostic v0.5 ===");
        line("Package: " + getPackageName());
        line("Android: " + android.os.Build.VERSION.RELEASE + " (API " + android.os.Build.VERSION.SDK_INT + ")");
        line("Device: " + android.os.Build.MANUFACTURER + " / " + android.os.Build.MODEL);
        line("");
        line("[Permissions]");
        String[] perms = {"android.permission.RECORD_AUDIO","android.permission.MODIFY_AUDIO_SETTINGS","android.permission.ACCESS_WIFI_STATE"};
        for (String p : perms) {
            int state = checkSelfPermission(p);
            line(p + " = " + (state == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "NOT GRANTED"));
        }
        line("");
        line("[AudioManager]");
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            line("mode=" + am.getMode());
            line("musicActive=" + am.isMusicActive());
            line("sampleRate=" + am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));
            line("bufferSize=" + am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER));
            AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo d : devices) {
                line("device id=" + d.getId() + " type=" + d.getType() +
                    " product=" + d.getProductName() +
                    " source=" + d.isSource() + " sink=" + d.isSink());
            }
        } else line("AudioManager unavailable");

        line("");
        line("[Droidlogic package visibility]");
        String[] pkgs = {"com.droidlogic.tv.settings","com.droidlogic.mediacenter","com.android.tv.settings"};
        for (String pkg : pkgs) {
            try {
                PackageInfo pi = getPackageManager().getPackageInfo(pkg, PackageManager.GET_SERVICES |
                    PackageManager.GET_RECEIVERS | PackageManager.GET_PROVIDERS | PackageManager.GET_PERMISSIONS);
                line(pkg + " INSTALLED version=" + pi.versionName);
                if (pi.services != null) for (ServiceInfo s : pi.services)
                    line("  service " + s.name + " exported=" + s.exported + " permission=" + s.permission);
                if (pi.receivers != null) for (ActivityInfo a : pi.receivers)
                    line("  receiver " + a.name + " exported=" + a.exported);
                if (pi.providers != null) for (ProviderInfo p : pi.providers)
                    line("  provider " + p.name + " exported=" + p.exported + " permission=" + p.readPermission);
                if (pi.requestedPermissions != null) for (String p : pi.requestedPermissions)
                    line("  permission " + p);
            } catch (Exception e) {
                line(pkg + " NOT VISIBLE/NOT INSTALLED: " + e.getClass().getSimpleName());
            }
        }

        line("");
        line("[Read-only filesystem metadata]");
        String[] paths = {"/dev/amaudio","/dev/amaudio_ctl","/dev/audio_data_debug",
            "/dev/audiodsp0","/dev/media.audio","/dev/snd","/proc/asound"};
        for (String path : paths) {
            File f = new File(path);
            line(path + " exists=" + f.exists() + " canRead=" + f.canRead() +
                " canWrite=" + f.canWrite() + " directory=" + f.isDirectory());
            if (f.isDirectory()) {
                String[] names = f.list();
                if (names != null) {
                    StringBuilder sb = new StringBuilder("  entries: ");
                    for (int i=0; i<Math.min(names.length, 40); i++) {
                        if (i>0) sb.append(", ");
                        sb.append(names[i]);
                    }
                    line(sb.toString());
                }
            }
        }
        line("");
        line("NOTE: This diagnostic only reads Android metadata and filesystem access flags.");
        line("It does NOT open DVB/ALSA nodes, tune channels, change audio, or call vendor services.");
        line("Share the complete report text for analysis.");
    }
}
