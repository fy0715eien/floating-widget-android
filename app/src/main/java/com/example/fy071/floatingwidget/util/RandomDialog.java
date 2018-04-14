package com.example.fy071.floatingwidget.util;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RandomDialog extends Service {
    @Override
    public IBinder onBind(Intent intent)
    {throw new UnsupportedOperationException("Not yet implemented");}
    @Override
    public  int onStartCommand(Intent intent,int flags,int startId){
       Timer timer = new Timer();
     TimerTask task = new TimerTask() {
         @Override
          public void run() {
           final   String[] dialog = {"幸福就是猫吃鱼，狗吃肉，奥特曼打小怪兽", "每天午睡15到30分钟对身体很有好处，你要注意多休息哦", "主人,让我给你跳个舞好不好呀,吃饱了不运动很容易长胖的呢",
                      "主人,我已经下定决心跟你过一辈子了,让我多陪陪你吧", "人是铁,饭是钢,一顿不吃饿的慌,主人,按时吃饭了吗", "我是很笨，但是我很认真，幻想和主人过一生！"
                      , "多多微笑，阴天谨防情绪感冒！", "生命在于运动，主人你今天运动了吗", "我现在浑身充满力量，要不要跟我拔河", "踮起脚尖我们就能离幸福更近一点哦",
                      "你说小白兔吃胡萝卜，是因为它买不起肉么", "他们都说我丑，其实我只是美得不明显", "我昨晚可是想了你好多次呢,抱抱嘛",
                      "刚出炉的面包不宜马上食用哦，因为酵母还没有完全消失", "得之泰然，失之淡然，争其必然，顺气自然，我觉得这句话很有道理耶", "保护生命之水，需要从节约用水做起，主人你说对吗？",
                      "酸梅具有减缓老化的作用，我要多吃点，保持青春永驻!", "红灯停，绿灯行，不论是开车还是走路，我们都要遵守交通规则哦", "主人，生活这么美好，我们是不是该微笑面对每一天呢", "人家都说饭后吃水果，其实饭前一小时吃有利于人体免疫系统哦"};
              int min = 0;
              int max = 19;
              Random random = new Random();
             final int num = random.nextInt(max) % (max - min + 1) + min;
             Handler handler = new Handler(Looper.getMainLooper());
                     handler.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          //放在UI线程弹Toast
                                          Toast.makeText(RandomDialog.this, dialog[num], Toast.LENGTH_LONG).show();

                                      }

                                  });
         }
     };


       timer.schedule(task, 1000, 30000);

        return super.onStartCommand(intent, flags, startId);
    }
}
