package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;

@XStreamAlias("Parties")
public class Parties {
    @XStreamImplicit(itemFieldName = "Party")
    public ArrayList<Party> party;
}
