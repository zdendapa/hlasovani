package com.parser.gui;

import com.parser.controller.ApplicationObjects;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.regex.Pattern;

public class FileManager {
    public static final String matchingInputString = "[0-9]{4}.xml";

    public void copyAllFilesAndFolders() throws IOException {
        File inputDir = new File(ApplicationObjects.getInstance().getInputFolderLocation());
        File outputDir = new File(ApplicationObjects.getInstance().getBackupFolderLocation());
        if(MainFrame.DEBUG){
        System.out.println("input folder: "+ApplicationObjects.getInstance().getInputFolderLocation());
            System.out.println("output folder: "+ApplicationObjects.getInstance().getBackupFolderLocation());
        }
       FileUtils.copyDirectory(inputDir, outputDir);


    }

    public static File[] listFilesMatching(File root, String regex) {
        if (!root.isDirectory()) {
            throw new IllegalArgumentException(root + " is no directory.");
        }
        final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
        return root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return p.matcher(file.getName()).matches();
            }
        });
    }
}
