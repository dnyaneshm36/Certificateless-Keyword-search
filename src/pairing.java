package src;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class pairing {
    
    public static void main(String[] args) throws Exception {

        long pairingbetweeng1s=0;
        long start, end;
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);


        for(int i = 0;i <1000;i++)
        {
            Element fir = pairing.getG1().newRandomElement();
            Element sec = pairing.getG1().newRandomElement();
            start = System.currentTimeMillis();
            pairing.pairing( fir , sec );
            end = System.currentTimeMillis();
            pairingbetweeng1s += (end-start);
        
        }

        System.out.println("Time for pairing two g1 element  is           : "+pairingbetweeng1s);
    }
}
