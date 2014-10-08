package com.parser.xml.output;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("poslanec")
public class Poslanec {
    //@XStreamAsAttribute
    @XStreamAlias("jmeno")
    private String jmeno;

    //@XStreamAsAttribute
    @XStreamAlias("prislusnnost")
    private String prislusnnost;

    //@XStreamAsAttribute
    @XStreamAlias("hlasoval")
    private String hlasoval;

    public Poslanec(String jmeno, String prislusnnost, String hlasoval) {
        if (hlasoval.equals("AYE")) {
            hlasoval = "Pro";
        } else if (hlasoval.equals("NO")) {
            hlasoval = "Ne";
        } else if (hlasoval.equals("ABSTAINED")) {
            hlasoval = "Zdržel se";
        } else if (hlasoval.equals("NOT_VOTING")) {
            hlasoval = "Nehlasoval";
        }

        this.jmeno = jmeno;
        this.prislusnnost = prislusnnost;
        this.hlasoval = hlasoval;
    }
}
