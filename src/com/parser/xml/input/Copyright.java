package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Copyright")
public class Copyright {


    @XStreamAsAttribute
    @XStreamAlias("author")
    private String author;//Nazev usneseni VotingResult/Session content:  <Session number="201401" election_period="1">22. Zasedání ZMČ Praha 3</Session>

    public String getAuthor() {
        return author;
    }
}
