package com.parser.controller;

import com.parser.gui.MainFrame;
import com.parser.model.DataModel;

import java.util.ArrayList;

public class ApplicationObjects {
    private static final ApplicationObjects instance = new ApplicationObjects();

    private static String inputFolderLocation;
    //folder for copying files from inputFolderLocation
    private static String backupFolderLocation = "D:\\2014\\VoteParserDataForTesting\\backup_folder";
    //output folder for new xmls
    private static String outputFolderLocation = "D:\\2014\\VoteParserDataForTesting\\output_folder";
    private static ArrayList<DataModel> inputDataFromXML;
    private static boolean isAdminLogged;


    private ApplicationObjects() {
    }

    public static ApplicationObjects getInstance() {
        return instance;
    }

    public String getInputFolderLocation() {
        return inputFolderLocation;
    }

    public void setInputFolderLocation(String inputFolderLocation) {
        this.inputFolderLocation = inputFolderLocation;
        if(MainFrame.DEBUG){
            System.out.println("inputFolderLocation:"+inputFolderLocation);
        }
    }

    public String getBackupFolderLocation() {
        return backupFolderLocation;
    }

    public void setBackupFolderLocation(String backupFolderLocation) {
        this.backupFolderLocation = backupFolderLocation;
    }

    public String getOutputFolderLocation() {
        return outputFolderLocation;
    }

    public void setOutputFolderLocation(String outputFolderLocation) {
        this.outputFolderLocation = outputFolderLocation;
    }

    public boolean isAdminLogged() {
        return isAdminLogged;
    }

    public void setAdminLogged(boolean isAdminLogged) {
        this.isAdminLogged = isAdminLogged;
    }

    public ArrayList<DataModel> getInputDataFromXML() {
        return inputDataFromXML;
    }

    public void setInputDataFromXML(ArrayList<DataModel> inputDataFromXML) {
        this.inputDataFromXML = inputDataFromXML;
    }
}
