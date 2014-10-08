package com.parser.xml.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;

@XStreamAlias("Party")
public class Party {
    @XStreamImplicit(itemFieldName = "Member")
    public ArrayList<Member> member;
}
