package com.parser.xml.output;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;

@XStreamAlias("hlasovani")
public class Hlasovani {
    @XStreamImplicit(itemFieldName = "poslanec")
    public ArrayList<Poslanec> poslanec;

    //@XStreamAsAttribute
    @XStreamAlias("cislo_usneseni")
    public int orderNumber; // ANO = VotingResult attr: „aye“

    //@XStreamAsAttribute
    @XStreamAlias("celkem_pro")
    public int yes; // ANO = VotingResult attr: „aye“

    //@XStreamAsAttribute
    @XStreamAlias("celkem_proti")
    public int no; //NE = VotingResult attr: „no“

    //@XStreamAsAttribute
    @XStreamAlias("celkem_zdrzel")
    public int abstained; //ZDR = VotingResult attr: „abstained“

    //@XStreamAsAttribute
    @XStreamAlias("celkem_nehlasoval")
    public int notVoting; // NEH = VotingResult attr: „not_voting“

    public Hlasovani(ArrayList<Poslanec> poslanec, int orderNumber, int yes, int no, int abstained, int notVoting) {
        this.poslanec = poslanec;
        this.orderNumber = orderNumber;
        this.yes = yes;
        this.no = no;
        this.abstained = abstained;
        this.notVoting = notVoting;
    }
}
