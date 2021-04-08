package src;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class sclarmul {
    public static void main(String[] args) throws Exception {
            long mulg1zn = 0;
            long start, end;
            Pairing pairing = PairingFactory.getPairing("params1.txt"); 
            //use pbc wrapper
            PairingFactory.getInstance().setUsePBCWhenPossible(true);
            
            for(int i = 0;i <1000;i++)
            {
                Element fir = pairing.getG1().newRandomElement();
                Element sec = pairing.getZr().newRandomElement();
                start = System.currentTimeMillis();
                fir.mulZn(sec) ; 
                end = System.currentTimeMillis();
                mulg1zn += (end-start);
            
            }
            
            System.out.println("Time for Scaler multipcation  of g1 and Zr    : "+mulg1zn);
    }
}
