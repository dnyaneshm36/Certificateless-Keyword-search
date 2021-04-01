

import it.unisa.dia.gas.jpbc.Element;

public class TrapdoorWord {
        Element T1;
        Element T2;
        Element T3;
        byte [] byteArrayT2;
        byte [] bythArrayT3;
        long requiredTime;
        public TrapdoorWord(Element t1, Element t2, Element t3, byte[] byteArrayT2, byte[] bythArrayT3,
                long requiredTime) {
                    super();
                    this.T1 = t1.duplicate();
                    this.T2 = t2.duplicate();
                    this.T3 = t3.duplicate();
                    this.byteArrayT2 = byteArrayT2.clone();
                    this.bythArrayT3 = bythArrayT3.clone();
                    this.requiredTime = requiredTime;
        }
        public TrapdoorWord() {
            super();
        }
        public Element getT1() {
            return T1;
        }
        public void setT1(Element t1) {
            this.T1 = t1.duplicate();
        }
        public Element getT2() {
            return T2;
        }
        public void setT2(Element t2) {
            this.T2 = t2.duplicate();
        }
        public Element getT3() {
            return T3;
        }
        public void setT3(Element t3) {
            this.T3 = t3.duplicate();
        }
        public long getRequiredTime() {
            return requiredTime;
        }
        public byte[] getByteArrayT2() {
            return byteArrayT2;
        }
        public void setByteArrayT2(byte[] byteArrayT2) {
            this.byteArrayT2 = byteArrayT2.clone();
        }
        public byte[] getBythArrayT3() {
            return bythArrayT3;
        }
        public void setBythArrayT3(byte[] bythArrayT3) {
            this.bythArrayT3 = bythArrayT3.clone();
        }
        public void setRequiredTime(long requiredTime) {
            this.requiredTime = requiredTime;
        }
        @Override
        public String toString() {
            return "TrapdoorWord: \n{ \n  T1 = [ " + T1 + " ] , \n  T2 = [ " + T2 + " ] , \n  T3 = [ " + T3 + " ] , \n  requiredTime = " + requiredTime
                    + " , \n  \n}";
        }

        

        
        
}
