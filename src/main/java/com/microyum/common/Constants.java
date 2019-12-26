package com.microyum.common;

public class Constants {

    public static final String COMMON_MD5 = "MD5";
    public static final String COMMON_DEFAULT_ENCODE = "UTF-8";

    public static final String ALBUM_FILE_ACTUAL_PICTURE_DIR = "/data/album/";
    public static final String ALBUM_FILE_VIRTUAL_PICTURE_DIR = "/album/image/";

    public static final Byte BLOG_STATUS_DELETED = 0;
    public static final Byte BLOG_STATUS_ACTIVE = 1;
    public static final Byte BLOG_STATUS_TOP = 1;
    public static final Byte BLOG_STATUS_TEMPORARY = 3;
    public static final String BLOG_LIST_SUMMARY = "<div class=\"item\"><div class=\"layui-fluid\"><div class=\"layui-row\"><div class=\"layui-col-xs12 layui-col-sm4 layui-col-md5\"><div class=\"img\"><img src=\"{#topicImg#}\" style=\"width:300;height:250px;margin-top:20px;\"></div></div><div class=\"layui-col-xs12 layui-col-sm8 layui-col-md7\"><div class=\"item-cont\"><h3>{#title#}{#newButton#}</h3><p>{#summary#}</p><p>{#createTime#}</p><a href=\"/public/blog/{#blodId#}/detail\" class=\"go-icon\"></a></div></div></div></div></div>";
    public static final String BLOG_NO_PICTURE = "/static/images/no_pic.jpg";
    public static final String BLOG_NEW_BUTTON = "<button class=\"layui-btn layui-btn-danger new-icon\">new</button>";
    public static final String BLOG_FILE_ACTUAL_PICTURE_DIR = "/data/image/";
    public static final String BLOG_FILE_VIRTUAL_PICTURE_DIR = "/blog/image/";
    public static final String BLOG_DETAIL_KBN_PRE = "pre";
    public static final String BLOG_DETAIL_KBN_POST = "post";

    public static final String STOCK_SINA_URL = "http://hq.sinajs.cn/list=";
    public static final Integer STOCK_BASE_COUNT = 500;

    public static final String MAIL_NAME_BUYING_STOCK = "BUYING_STOCK";
    public static final String MAIL_NAME_BUYING_STOCK_TABLE = "BUYING_STOCK_TABLE";
}
