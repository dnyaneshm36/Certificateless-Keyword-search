package src;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class additionofg1 {

    public static void main(String[] args) throws Exception {
        long addtionsg1 = 0;
        long start, end;
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        for(int i = 0;i <1000;i++)
        {
            Element fir = pairing.getG1().newRandomElement();
            Element sec = pairing.getG1().newRandomElement();
            start = System.currentTimeMillis();
            fir.add(sec) ; 
            end = System.currentTimeMillis();
            addtionsg1 += (end-start);
        
        }

    System.out.println("Time for Adding two element from g1  is       : "+addtionsg1);
    }
}
