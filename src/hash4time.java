package src;


import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class hash4time {
            // H4:G2→{0,1}f
            static BigInteger hash4(Element G2pair)
            {
                  byte [ ] G2pairbyte = G2pair.toBytes();
          
                  BigInteger G2pairbiginterger = new BigInteger(G2pairbyte);
                  return G2pairbiginterger;
            }

            public static void main(String[] args) throws Exception {

                System.out.println(" time for generating random number \n");
                Pairing pairing = PairingFactory.getPairing("params1.txt"); 
                //use pbc wrapper
                PairingFactory.getInstance().setUsePBCWhenPossible(true);
                long start , end;
                long hash4time = 0;
                Element h1 = pairing.getG1().newRandomElement();
                Element h3 = pairing.getG1().newRandomElement();

                Element h4pairg2 =  pairing.pairing(h1, h3);

                    for ( int i = 0 ;i< 1000;i++){
                        start = System.currentTimeMillis();
            
                        BigInteger h4big = hash4(h4pairg2);
            
                        end = System.currentTimeMillis();
            
                        hash4time += (end - start);
                    }

                    System.out.println("hash 4 time "+ hash4time);

            }
}
