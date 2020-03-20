package com.cst.bookcoffee.utils;

import android.content.Context;

import java.io.File;

public class PicUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }
}
