package com.example.fy071.floatingwidget.pet;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.fy071.floatingwidget.util.Key;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RandomDialogService extends Service {
    private static final String TAG = "RandomDialogService";

    public boolean flag = true;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        flag = true;
        Log.w(TAG, "onCreate: called");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final String[] dialog = returnDialog();
                int min = 0;
                int max = 44;
                Random random = new Random();
                final int num = random.nextInt(max) % (max - min + 1) + min;
                if (flag) {
                    Intent intent = new Intent();
                    // 与清单文件的receiver的anction对应
                    intent.setAction("com.tofloatingpet.message");
                    intent.putExtra("content", dialog[num]);
                    //发送广播
                    sendBroadcast(intent);
                }
            }
        };
        timer.schedule(task, 1000, 36000);

    }

    @Override
    public void onDestroy() {

        Log.w(TAG, "destory: called");
        flag = false;
        super.onDestroy();
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private String[] returnDialog() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return new String[]{"幸福就是猫吃鱼，狗吃肉，奥特曼打小怪兽," + sharedPreferences.getString(Key.PET_NAME, "") + "每天都变瘦",
                "每天午睡15到30分钟对身体很有好处，" + sharedPreferences.getString(Key.USER_NAME, "") + "要注意多休息哦",
                sharedPreferences.getString(Key.USER_NAME, "") + ",让我给你跳个舞好不好呀,吃饱了不运动很容易长胖的呢",
                sharedPreferences.getString(Key.USER_NAME, "") + ",我已经下定决心跟你过一辈子了,让我多陪陪你吧",
                "人是铁,饭是钢,一顿不吃饿的慌," + sharedPreferences.getString(Key.USER_NAME, "") + ",按时吃饭了吗",
                "我是很笨，但是我很认真，幻想和主人过一生！",
                "多多微笑，阴天谨防情绪感冒！",
                "生命在于运动，" + sharedPreferences.getString(Key.USER_NAME, "") + "你今天运动了吗",
                "我现在浑身充满力量，要不要跟" + sharedPreferences.getString(Key.USER_NAME, "") + "拔河",
                "踮起脚尖我们就能离幸福更近一点哦",
                "你说小白兔吃胡萝卜，是因为它买不起肉么",
                "他们都说我丑，其实我只是美得不明显",
                "我昨晚可是想了你好多次呢,抱抱嘛",
                "刚出炉的面包不宜马上食用哦，因为酵母还没有完全消失",
                "得之泰然，失之淡然，争其必然，顺气自然，我觉得这句话很有道理耶",
                "保护生命之水，需要从节约用水做起，" + sharedPreferences.getString(Key.USER_NAME, "") + "你说对吗？",
                "酸梅具有减缓老化的作用，我要多吃点，保持青春永驻!",
                "红灯停，绿灯行，不论是开车还是走路，我们都要遵守交通规则哦",
                sharedPreferences.getString(Key.USER_NAME, "") + ",生活这么美好，我们是不是该微笑面对每一天呢",
                "人家都说饭后吃水果，其实饭前一小时吃有利于人体免疫系统哦",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢的运动是游泳和跑步",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢吃的零食是薯片和杨梅",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢吃的蔬菜是洋葱和西红柿",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢吃的水果是樱桃和橘子",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢喝的饮料是雪碧",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢的歌手是周杰伦",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢的电影是当幸福来敲门",
                sharedPreferences.getString(Key.PET_NAME, "") + "最讨厌的颜色是棕色，最喜欢的颜色是蓝色",
                sharedPreferences.getString(Key.PET_NAME, "") + "最喜欢的演员是成龙",
                "三个金叫“鑫”，三个人叫“众”，那么三个鬼应该叫什么?",
                "猴子每分钟能掰一个玉米，在果园里，一只猴子5分钟能掰几个玉米?",
                "有种动物，大小像只猫，长相又像虎，这是什么动物? ",
                sharedPreferences.getString(Key.USER_NAME, "") + ",什么时候四减三会等于五?",
                sharedPreferences.getString(Key.USER_NAME, "") + ",什么人是不用电的?",
                sharedPreferences.getString(Key.USER_NAME, "") + ",什么时候时钟会响13下？",
                sharedPreferences.getString(Key.USER_NAME, "") + ",什么情况一山可容二虎？",
                "王先生在打太极拳时金鸣独立，站多久看上去都那么轻松，为什么？",
                sharedPreferences.getString(Key.USER_NAME, "") + ",什么人每天靠运气赚钱？",
                "一只鸡，一只鹅，放冰箱里，鸡冻死了，鹅却活着，为什么？",
                sharedPreferences.getString(Key.USER_NAME, "") + "什么东西往上升永远掉不下来？",
                sharedPreferences.getString(Key.USER_NAME, "") + ",多可爱的名字呀!",
                sharedPreferences.getString(Key.USER_NAME, "") + ",你的英文名字叫什么呀？",
                sharedPreferences.getString(Key.USER_NAME, "") + "和" + sharedPreferences.getString(Key.PET_NAME, "") + "永远是好朋友！",
                sharedPreferences.getString(Key.USER_NAME, "") + "和" + sharedPreferences.getString(Key.PET_NAME, "") + "会一直在一起吗？",
                sharedPreferences.getString(Key.PET_NAME, "") + "最开心的时候就是和" + sharedPreferences.getString(Key.USER_NAME, "") + "在一起！"};
    }
}
