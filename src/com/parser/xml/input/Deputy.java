package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Deputy")
public class Deputy {
    @XStreamAsAttribute
    @XStreamAlias("first_name")
    private String first_name;

    @XStreamAsAttribute
    @XStreamAlias("name")
    private String name;

    @XStreamAsAttribute
    @XStreamAlias("party")
    private String party;

    @XStreamAsAttribute
    @XStreamAlias("vote")
    private String vote;

    public String getName() {
        return first_name + " " + name;
    }

    public String getParty() {
        return party;
    }

    public String getVote() {
        return vote;
    }

}
