package com.microyum.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinYinUtils {

    /**
     * 获取汉字首字母的方法。如： 张三 --> zs
     * 说明：暂时解决不了多音字的问题，只能使用取多音字的第一个音的方案
     *
     * @param hanzi 汉字字符串
     * @return 大写汉子首字母; 如果都转换失败,那么返回null
     */
    public static String getHanziInitials(String hanzi) {

        if (StringUtils.isBlank(hanzi)) {
            return null;
        }

        char[] charArray = hanzi.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char ch : charArray) {
            // 逐个汉字进行转换， 每个汉字返回值为一个String数组（因为有多音字）
            String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(ch);
            if (null != stringArray) {
                builder.append(stringArray[0].charAt(0));
            }
        }

        if (builder.length() == 0) {
            return null;
        }

        return builder.toString().toLowerCase();
    }

    /**
     * 获取汉字拼音的方法。如： 张三 --> zhangsan
     * 说明：暂时解决不了多音字的问题，只能使用取多音字的第一个音的方案
     *
     * @param hanzi 汉子字符串
     * @return 汉字拼音; 如果都转换失败,那么返回null
     */
    public static String getHanziPinYin(String hanzi) {

        if (StringUtils.isBlank(hanzi)) {
            return null;
        }

        char[] charArray = hanzi.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char ch : charArray) {
            // 逐个汉字进行转换， 每个汉字返回值为一个String数组（因为有多音字）
            String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(ch);
            if (null != stringArray) {
                // 把第几声这个数字给去掉
                builder.append(stringArray[0].replaceAll("\\d", ""));
            }
        }

        if (builder.length() > 0) {
            return null;
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(PinYinUtils.getHanziInitials("龙蟒佰利"));
        System.out.println(PinYinUtils.getHanziPinYin("龙蟒佰利"));
    }
}
