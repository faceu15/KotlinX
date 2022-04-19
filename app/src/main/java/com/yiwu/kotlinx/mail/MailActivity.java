package com.yiwu.kotlinx.mail;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.excellence.transfer.CommandService;
import com.excellence.transfer.ui.DebugActivity;
import com.yiwu.kotlinx.Manifest;
import com.yiwu.kotlinx.R;
import com.yiwu.kotlinx.base.BaseActivity;
import com.yiwu.kotlinx.databinding.ActivityMailBinding;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import static com.excellence.transfer.CommandService.CODE;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MailActivity extends BaseActivity<ActivityMailBinding> {


    private static final String TAG = MailActivity.class.getSimpleName();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_mail;
    }

    @Override
    protected void init() {
        int granted = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.i("DDDD", "[40]->init: " + granted);

        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        mViewDataBinding.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.btn_update:
                    Intent intent2 = new Intent(this, CommandService.class);
                    intent2.putExtra(CODE, 1);
                    startService(intent2);
                    break;
                case R.id.btn_logcat:
                    Intent intent = new Intent(this, CommandService.class);
                    intent.putExtra(CODE, 2);
                    startService(intent);
                    break;
                case R.id.btn_stop_logcat:
                    Intent intent1 = new Intent(this, CommandService.class);
                    intent1.putExtra(CODE, 3);
                    startService(intent1);
                    break;
                case R.id.btn_remote_debug:
                    Intent intentR = new Intent(this, DebugActivity.class);
                    startActivity(intentR);
                    break;

                default:
                    break;
            }
        });

    }


    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void sendMail() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "my-mail-server");
        Session session = Session.getInstance(properties);
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom("me@qq.com");
            mimeMessage.setRecipients(Message.RecipientType.TO,
                    "you@example.com");
            mimeMessage.setSubject("Log collection");
            mimeMessage.setSentDate(new Date());
            mimeMessage.setText("Hello, world!\n");

            Transport.send(mimeMessage, "me@example.com", "my-password");

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.toString());
        }
    }

    private void connect() {


    }
}