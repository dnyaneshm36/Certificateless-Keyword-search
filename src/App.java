package src;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");



        long KeyGen_server_start = System.currentTimeMillis();
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        

        
        Element P = pairing.getG1().newRandomElement();

        for ( int i = 0 ; i< 100 ;i++)
        {
            String PStr = Base64.encodeBytes(P.toBytes());

            P = pairing.getG1().newElementFromBytes(Base64.decode(PStr));
            
            System.out.println("pdash"+P);
            System.out.println("pstr   "+PStr);
        }






        // for ( int i  = 0 ;i < 100 ;i++)
        // {
        //     System.out.println("p ====-------------"+P+"\n");
        //     byte [] pbyte = P.toBytes();
        //     for(int j = 0 ; j< pbyte.length ; j++)
        //     {
        //         System.out.print(" "+pbyte[i]);
        //     }
        //     Element pdash = pairing.getG1().newElement();
        //     pdash.setFromHash(pbyte, 0, pbyte.length);
        //     System.out.println("p pdash====-------------"+pdash);
        // }


        // pdash.add(P);
        // System.out.println("p pdash add ====-------------"+pdash);
        // pdash.sub(P);
        // System.out.println("p after sub pdash====-------------"+pdash);

        // Element Q = pairing.getG1().newRandomElement();
        // System.out.println("Q ====-------------"+Q);
        
    
        


        // Q.add(P);
        // System.out.println("p Q add ====-------------"+Q);
        // Q.sub(P);
        // System.out.println("p sub a sub pdash====-------------"+Q);
        // System.out.println("--------------------------------------------------------------------------------------------");


        // byte[] pbyte1 = P.toBytes();
        // byte[] qbyte = Q.toBytes();
        // int maxelemnt = Math.max(pbyte1.length , qbyte.length);
        // int [] xor = new int[maxelemnt];
        // for( int i = 0 ; i < maxelemnt; i++)
        // {
        //     int x = ( int ) pbyte1[i] ^ ( int ) qbyte[i] ;
        //     xor[i] = x ;
        // }

        // for( int i = 0 ; i < maxelemnt; i++)
        // {
        //     int x = ( int ) xor[i] ^ ( int ) qbyte[i] ;
        //     xor[i] = x ;
        // }
        // Element Pfinal = pairing.getG1().newElement();
        // byte [] xorbyte = new byte[maxelemnt];
        // for( int i = 0 ; i < maxelemnt; i++)
        // {
        //     int x = xor[i];
        //      xorbyte[i] = (byte)xor[i];
        // }
        // int bythreadpash1 = Pfinal.setFromBytes(xorbyte);
        // System.out.println("final P is "+Pfinal);    
        
        // int num = -81;
        // int num2 = -65;
        
        // int  x = num ^ num2;
        // System.out.println("print x "+x);

        // int  y = x ^ num2;
        // System.out.println(num+" ===== print y "+y);


    }
}
