package com.parser.model;

import com.parser.xml.input.Deputy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DataModel {
    public static final String dateFormat = "dd.MM.yyyy hh:mm:ss";
    private int number; //# = VotingResult
    private String time; //Datum = VotingResult
    private String sessionContent;//Nazev usneseni VotingResult/Session content:  <Session number="201401" election_period="1">22. Zasedání ZMČ Praha 3</Session>
    private String topicContent;
    //BY DEFAULT THIS FIELD EMPTY and mayby required
    private int orderNumber; //C. usneseni = nothing
    private int yes; // ANO = VotingResult attr: „aye“
    private int no; //NE = VotingResult attr: „no“
    private int abstained; //ZDR = VotingResult attr: „abstained“
    private int notVoting; // NEH = VotingResult attr: „not_voting“
    private String note; // Poznamka = nothing

    private ArrayList<Deputy> deputies;


    //additional helpful info
    private long realDate;
    private String fileName;

    public DataModel() {

    }

    public DataModel(int number, String topicContent, String time, String sessionContent, int orderNumber, int yes, int no, int abstained, int notVoting, String note, String fileName, ArrayList<Deputy> deputies) {
        this.number = number;
        this.topicContent = topicContent;
        this.time = time;
        this.sessionContent = sessionContent;
        this.orderNumber = orderNumber;
        this.yes = yes;
        this.no = no;
        this.abstained = abstained;
        this.notVoting = notVoting;
        this.note = note;
        this.deputies = deputies;

        this.fileName = fileName;
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        try {
            this.realDate = dateFormatter.parse(this.time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSessionContent() {
        return sessionContent;
    }

    public void setSessionContent(String sessionContent) {
        this.sessionContent = sessionContent;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getAbstained() {
        return abstained;
    }

    public void setAbstained(int abstained) {
        this.abstained = abstained;
    }

    public int getNotVoting() {
        return notVoting;
    }

    public void setNotVoting(int notVoting) {
        this.notVoting = notVoting;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getRealDate() {
        return realDate;
    }

    public void setRealDate(long realDate) {
        this.realDate = realDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<Deputy> getDeputies() {
        return deputies;
    }

    public void setDeputies(ArrayList<Deputy> deputies) {
        this.deputies = deputies;
    }

    public String getTopicContent() {
        return topicContent;
    }

    public void setTopicContent(String topicContent) {
        this.topicContent = topicContent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataModel{");
        sb.append("number=").append(number);
        sb.append(", time='").append(time).append('\'');
        sb.append(", sessionContent='").append(sessionContent).append('\'');
        sb.append(", orderNumber=").append(orderNumber);
        sb.append(", yes=").append(yes);
        sb.append(", no=").append(no);
        sb.append(", abstained=").append(abstained);
        sb.append(", notVoting=").append(notVoting);
        sb.append(", note='").append(note).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
