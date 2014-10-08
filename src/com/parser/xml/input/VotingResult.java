package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;

@XStreamAlias("VotingResult")
public class VotingResult {
    @XStreamAsAttribute
    @XStreamAlias("number")
    private int number; //# = VotingResult
    @XStreamAsAttribute
    @XStreamAlias("time")
    private String time; //Datum = VotingResult

    @XStreamAsAttribute
    @XStreamAlias("aye")
    private int yes; // ANO = VotingResult attr: „aye“

    @XStreamAsAttribute
    @XStreamAlias("no")
    private int no; //NE = VotingResult attr: „no“

    @XStreamAsAttribute
    @XStreamAlias("abstained")
    private int abstained; //ZDR = VotingResult attr: „abstained“

    @XStreamAsAttribute
    @XStreamAlias("not_voting")
    private int notVoting; // NEH = VotingResult attr: „not_voting“

    @XStreamAlias("Session")
    public String session;

    @XStreamAlias("Topic")
    public String topic;
    @XStreamAlias("Comment")
    public Comment comment;
    @XStreamImplicit(itemFieldName = "Deputy")
    public ArrayList<Deputy> deputy;
    @XStreamAlias("Parties")
    public Parties parties;
    @XStreamAlias("Copyright")
    public Copyright copyright;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
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


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ArrayList<Deputy> getDeputy() {
        return deputy;
    }

    public void setDeputy(ArrayList<Deputy> deputy) {
        this.deputy = deputy;
    }

    public Parties getParties() {
        return parties;
    }

    public void setParties(Parties parties) {
        this.parties = parties;
    }

    public Copyright getCopyright() {
        return copyright;
    }

    public void setCopyright(Copyright copyright) {
        this.copyright = copyright;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VotingResult{");
        sb.append("number=").append(number);
        sb.append(", session=").append(session);
        sb.append(", topic=").append(topic);
        sb.append(", comment=").append(comment);
        sb.append(", deputy=").append(deputy);
        sb.append(", parties=").append(parties);
        sb.append(", copyright=").append(copyright);
        sb.append('}');
        return sb.toString();
    }
}
