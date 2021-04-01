package src;



import it.unisa.dia.gas.jpbc.Element;

public class SetupParamter {

        Element P;
        Element master_key_lamda;
        Element PKc;
        Element SKc;
        long  time;
        public SetupParamter(Element p, Element master_key_lamda, Element pKc, Element sKc,long time) {
            super();
            this.P = p.duplicate();
            this.master_key_lamda = master_key_lamda.duplicate();
            this.PKc = pKc.duplicate();
            this.SKc = sKc.duplicate();
            this.time = time;
        }
        public SetupParamter() {
            super();
        }
        public Element getP() {
            return P;
        }
        public void setP(Element p) {
            P = p.duplicate();
        }
        public Element getMaster_key_lamda() {
            return master_key_lamda;
        }
        public void setMaster_key_lamda(Element master_key_lamda) {
            this.master_key_lamda = master_key_lamda;
        }
        public Element getPKc() {
            return PKc;
        }
        public void setPKc(Element pKc) {
            PKc = pKc.duplicate();
        }
        public Element getSKc() {
            return SKc;
        }
        public void setSKc(Element sKc) {
            SKc = sKc.duplicate();
        }
        @Override
        public String toString() {
            return "SetupParamters: \n { \nP = [ " + P + " ] , \nmaster_key_lamda = [ " + master_key_lamda+ " ] , \nPKc = [ " + PKc + " ] , \nSKc = [ " + SKc + 
                " ] , "+"\n requredTime = " + time + " ( milli second )\n}";
        }
        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }
        
    
}
