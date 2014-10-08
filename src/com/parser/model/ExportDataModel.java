package com.parser.model;

public class ExportDataModel {

    private String name;
    private String party;
    private String vote;

    private int yes; // ANO = VotingResult attr: „aye“
    private int no; //NE = VotingResult attr: „no“
    private int abstained; //ZDR = VotingResult attr: „abstained“
    private int notVoting; // NEH = VotingResult attr: „not_voting“
    private int orderNumber;
    //additional helpful info
    private String fileName;

    public ExportDataModel(String name, String party, String vote, int yes, int no, int abstained, int notVoting, int orderNumber, String fileName) {
        this.name = name;
        this.party = party;
        this.vote = vote;
        this.yes = yes;
        this.no = no;
        this.abstained = abstained;
        this.notVoting = notVoting;
        this.orderNumber = orderNumber;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }


    public String getParty() {
        return party;
    }

    public String getVote() {
        return vote;
    }

    public int getYes() {
        return yes;
    }

    public int getNo() {
        return no;
    }

    public int getAbstained() {
        return abstained;
    }

    public int getNotVoting() {
        return notVoting;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getFileName() {
        return fileName;
    }


    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataModel{");
        sb.append("name=").append(name);
        sb.append(", party='").append(party);
        sb.append(", vote='").append(vote);
        sb.append(", yes=").append(yes);
        sb.append(", no=").append(no);
        sb.append(", abstained=").append(abstained);
        sb.append(", notVoting=").append(notVoting);
        sb.append(", orderNumber=").append(orderNumber);
        sb.append(", fileName='").append(fileName);
        sb.append('}');
        return sb.toString();
    }
}
