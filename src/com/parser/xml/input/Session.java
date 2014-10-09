package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Session")
public class Session {

    @XStreamAlias("Session")
    private String session;

    @XStreamAsAttribute
    @XStreamAlias("number")
    private String number;//Nazev usneseni VotingResult/Session content:  <Session number="201401" election_period="1">22. Zasedání ZMČ Praha 3</Session>

    public String getNumber() {
        return number;
    }

    public String getSession() {
        return session;
    }
}
