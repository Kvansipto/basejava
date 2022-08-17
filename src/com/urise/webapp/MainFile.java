package com.urise.webapp;

import java.io.File;
import java.util.Objects;

public class MainFile {
    public static void main(String[] args) {
        File dir = new File("/Users/sheldor/IdeaProjects/basejava/src/com/urise/webapp");
        getFilesName(dir);
    }
    
    public static void getFilesName(File dir) {
        System.out.println(dir.getName());
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                getFilesName(file);
            }
        }
    }
}
