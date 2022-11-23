package com.urise.webapp;

import java.io.File;

public class MainFile {
    public static void main(String[] args) {
        File dir = new File("/Users/sheldor/IdeaProjects/basejava/src/com/urise/webapp");
        getFilesName(dir, "");
    }

    public static void getFilesName(File dir, String offset) {
        File[] dir1 = dir.listFiles();
        if (dir1 != null) {
            for (File file : dir1) {
                if (file.isDirectory()) {
                    System.out.println(offset + file.getName());
                    getFilesName(file, offset + "   ");
                } else if (file.isFile()) {
                    System.out.println(offset + file.getName());
                }
            }
        }
    }
}
