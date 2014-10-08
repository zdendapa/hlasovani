package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Session")
public class Session {
    //@XStreamAsAttribute
    @XStreamAlias("number")
    private String number;//Nazev usneseni VotingResult/Session content:  <Session number="201401" election_period="1">22. Zasedání ZMČ Praha 3</Session>


}
