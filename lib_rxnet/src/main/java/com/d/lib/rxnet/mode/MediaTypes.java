package com.d.lib.rxnet.mode;

import okhttp3.MediaType;

/**
 * MediaType汇总
 */
public class MediaTypes {
    public static final MediaType APPLICATION_ATOM_XML_TYPE = MediaType.parse("application/atom+xml;charset=utf-8");
    public static final MediaType APPLICATION_FORM_URLENCODED_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
    public static final MediaType APPLICATION_JSON_TYPE = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType APPLICATION_OCTET_STREAM_TYPE = MediaType.parse("application/octet-stream");
    public static final MediaType APPLICATION_SVG_XML_TYPE = MediaType.parse("application/svg+xml;charset=utf-8");
    public static final MediaType APPLICATION_XHTML_XML_TYPE = MediaType.parse("application/xhtml+xml;charset=utf-8");
    public static final MediaType APPLICATION_XML_TYPE = MediaType.parse("application/xml;charset=utf-8");
    public static final MediaType MULTIPART_FORM_DATA_TYPE = MediaType.parse("multipart/form-data;charset=utf-8");
    public static final MediaType TEXT_HTML_TYPE = MediaType.parse("text/html;charset=utf-8");
    public static final MediaType TEXT_XML_TYPE = MediaType.parse("text/xml;charset=utf-8");
    public static final MediaType TEXT_PLAIN_TYPE = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType IMAGE_TYPE = MediaType.parse("image/*");
    public static final MediaType WILDCARD_TYPE = MediaType.parse("*/*");

    public static final MediaType APK_TYPE = MediaType.parse("application/vnd.android.package-archive");
    public static final MediaType PNG_TYPE = MediaType.parse("image/png");
    public static final MediaType JPG_TTYPE = MediaType.parse("image/jpg");
    public static final MediaType TEXT_TYPE = MediaType.parse("text/html; charset=utf-8");
}
