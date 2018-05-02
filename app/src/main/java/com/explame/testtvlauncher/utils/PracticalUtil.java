package com.explame.testtvlauncher.utils;

import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XQ on 2017/4/5.
 * 实用工具类
 */
public class PracticalUtil {
    /**
     * 防止按钮多次点击方法
     */
    private static long lastClickTime;// 最后一次点击的时间

    public synchronized static boolean isFastClick() {
        // 获得当前时间
        long time = System.currentTimeMillis();
//        LogUtils.i("isFastClick---->" + (time - lastClickTime));
        if ((time - lastClickTime) < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * editext 要处理金融业务的输入 ,输入金额 小数点后两位 ，首位输入小数点
     */
    public static void formatDot(CharSequence sequence, EditText editText) {
        String s = sequence.toString();
        if (s.contains(".")) {
            /**
             * 如果小数点位数大于两位 截取后两位
             */
            if (s.length() - 1 - s.indexOf(".") > 2) {
                s = s.substring(0, (s.indexOf(".") + 3));
                editText.setText(s);
                editText.setSelection(s.length());
            }

            /**
             * 如果第一个输入为小数点 ，自动补零
             */
            if (s.trim().substring(0).equals(".")) {
                s = "0" + s;
                editText.setText(s);
                editText.setSelection(s.length());
            }

            /**
             * 如果第一个第二个均为0
             */
            if (s.startsWith("0") && s.trim().length() > 1) {
                if (!s.substring(1, 2).equals(".")) {
                    editText.setText(s.substring(0, 1));
                    editText.setSelection(1);
                    return;
                }
            }
        }
    }

    /**
     * object类型转化为Json
     */
    public static String object2json(Object obj) {
        StringBuilder json = new StringBuilder();
        if (obj == null) {
            json.append("\"\"");
        } else if (obj instanceof String || obj instanceof Integer
                || obj instanceof Float || obj instanceof Boolean
                || obj instanceof Short || obj instanceof Double
                || obj instanceof Long || obj instanceof BigDecimal
                || obj instanceof BigInteger || obj instanceof Byte) {
            json.append("\"").append(string2json(obj.toString())).append("\"");
        } else if (obj instanceof Object[]) {
            // json.append(array2json((Object[]) obj));
        } else if (obj instanceof List) {
            json.append(list2json((List<?>) obj));
        } else if (obj instanceof Map) {
            json.append(map2json((Map<?, ?>) obj));
        } else if (obj instanceof Set) {
            json.append(set2json((Set<?>) obj));
        } else {
            // json.append(bean2json(obj));
        }
        return json.toString();
    }

    public static String list2json(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String map2json(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (map != null && map.size() > 0) {
            for (Object key : map.keySet()) {
                json.append(object2json(key));
                json.append(":");
                json.append(object2json(map.get(key)));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    public static String set2json(Set<?> set) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (set != null && set.size() > 0) {
            for (Object obj : set) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String string2json(String s) {
        if (s == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if (ch >= '\u0000' && ch <= '\u001F') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * 判定只输入汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 检测String是否全是中文
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * double转String保留两位小数
     */
    public static String StringtoDouble(Double cny) {
        String CNY = "";
        DecimalFormat df = new DecimalFormat("0.00");
        CNY = df.format(cny);
        return CNY;
    }


    /**
     * 使用序列化方法深度复制list
     * 这个方法有个缺点 实体类是实现了Serializable接口，那么使用上面方法可以实现深度复制，但是如果是实现了Android中的Parcelable接口，上面方法就不行了
     */

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /**
     * 比较统一的且可以实现深度复制list的方法就是：将我们的List<T>转换为json，再把json转回List<T>
     */
    public static <T> ArrayList<T> jsonToArrayList(List<T> sourceList, Class<T> clazz) {
        Gson gson = new Gson();
        String json = gson.toJson(sourceList);
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    /**
     * 一至十二月的英文缩写翻译
     */
    public static String twelveMonthsTranslation(int month) {
        String str = "";
        if (1 == month) {
            str = "Jan";
        } else if (2 == month) {
            str = "Feb";
        } else if (3 == month) {
            str = "Mar";
        } else if (4 == month) {
            str = "Apr";
        } else if (5 == month) {
            str = "May";
        } else if (6 == month) {
            str = "Jun";
        } else if (7 == month) {
            str = "Jul";
        } else if (8 == month) {
            str = "Aug";
        } else if (9 == month) {
            str = "Sept";
        } else if (10 == month) {
            str = "Oct";
        } else if (11 == month) {
            str = "Nov";
        } else if (12 == month) {
            str = "Dec";
        }
        return str;
    }


}
