package demo.treasure.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by dx on 2023/12/19.
 */
public class UserMockUtils {
    private UserMockUtils() {
    }

    public static void register(Context context, String name, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("demo_treasure", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("_user_" + name, encrypt(password)).apply();
    }

    private static String encrypt(String password) {
        // todo懒得加密了
        return password;
    }

    private static String decrypt(String password) {
        return password;
    }

    private static boolean isRight(Context context, String name, String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("demo_treasure", Context.MODE_PRIVATE);
        String passwordInSp = sharedPreferences.getString("_user_" + name, "");
        return passwordInSp.equals(decrypt(password));
    }

    public static boolean login(Context context, String name, String password) {
        boolean isOk = isRight(context, name, password);
        if (!isOk) {
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("demo_treasure", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString("current_user", name) // 当前用户
                .putLong("current_time", System.currentTimeMillis()) // 登录时间，假设登录状态只能维持12h
                .apply();
        return true;
    }

    public static boolean isLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("demo_treasure", Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("current_user", "");
        if (TextUtils.isEmpty(currentUser)) {
            return false;
        }
        long time = sharedPreferences.getLong("current_time", 0);
        return System.currentTimeMillis() - time < 12 * 3600 * 1000;
    }
}
