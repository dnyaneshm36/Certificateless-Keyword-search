

import it.unisa.dia.gas.jpbc.Element;

public class ClientKey {
            //  this is send KGC
            
            String IDu;     //client
            Element Qu;     // hash of id
            Element Du;     // partial private key
            //-------------------

            // this is generated locally
            Element Su;
            Element SKu1;
            Element SKu2;
            Element SKu;
            // Private keky of client  SKu

            Element PKu;      // Public ke of client PKu
            Element PKu1;
            Element PKu2;

            long requredTime;

                public ClientKey(String iDu, Element qu, Element du) {
                    this.IDu = iDu;
                    this.Qu = qu.duplicate();
                    this.Du = du.duplicate();
                }
                public String getIDu() {
                    return IDu;
                }
                public void setIDu(String iDu) {
                    IDu = iDu;
                }
                public Element getQu() {
                    return Qu;
                }
                public void setQu(Element qu) {
                    Qu = qu;
                }
                public Element getDu() {
                    return Du;
                }
                public void setDu(Element du) {
                    Du = du.duplicate();
                }
                public Element getSu() {
                    return Su;
                }
                public void setSu(Element su) {
                    Su = su.duplicate();
                }
                public Element getSKu1() {
                    return SKu1;
                }
                public void setSKu1(Element sKu1) {
                    SKu1 = sKu1.duplicate();
                }
                public Element getSKu2() {
                    return SKu2;
                }
                public void setSKu2(Element sKu2) {
                    SKu2 = sKu2.duplicate();
                }
                public Element getSKu() {
                    return SKu;
                }
                public void setSKu(Element sKu) {
                    SKu = sKu.duplicate();
                }
                public Element getPKu() {
                    return PKu;
                }
                public void setPKu(Element pKu) {
                    PKu = pKu.duplicate();
                }
                public Element getPKu1() {
                    return PKu1;
                }
                public void setPKu1(Element pKu1) {
                    PKu1 = pKu1.duplicate();
                }
                public Element getPKu2() {
                    return PKu2;
                }
                public void setPKu2(Element pKu2) {
                    PKu2 = pKu2.duplicate();
                }
                public long getRequredTime() {
                    return requredTime;
                }
                public void setRequredTime(long requredTime) {
                    this.requredTime = requredTime;
                }
                @Override
                public String toString() {
                    return "ClientKey: \n{ \n Du = [ " + Du + " ] , \n IDu = [ " + IDu + " ] , \n PKu = [ " + PKu + " ] , \n PKu1 = [ " + PKu1 + " ] , \n PKu2 = [ " + PKu2
                            + " ] , \n Qu = [ " + Qu + " ] , \n SKu = [ " + SKu + " ] , \n SKu1 = [ " + SKu1 + " ] , \n SKu2 = [ " + SKu2 + " ] , \n Su = [ " + Su
                            + " ] , \n requredTime = " + requredTime + " ( milli second )\n}\n";
                }
                

}
