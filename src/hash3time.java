package src;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class hash3time {


    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    { 
        // Static getInstance method is called with hashing SHA 
        MessageDigest md = MessageDigest.getInstance("SHA-256"); 

        // digest() method called 
        // to calculate message digest of an input 
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8)); 
    }
    
    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation 
        BigInteger number = new BigInteger(1, hash); 

        // Convert message digest into hex value 
        StringBuilder hexString = new StringBuilder(number.toString(16)); 

        // Pad with leading zeros
        while (hexString.length() < 32) 
        { 
            hexString.insert(0, '0'); 
        } 
        return hexString.toString(); 
    }

            //H3: {0,1}*→G1*
            static Element hash3(String str,Pairing pairing)
            {
                String shastring = "";
                try {
                  shastring = toHexString(getSHA(str));
                } catch (NoSuchAlgorithmException e) {
                  System.out.println(" No such Algorithm exception occurred ");
                  e.printStackTrace();
                }
                byte [] shatringbyte = shastring.getBytes();
        
                Element g1 = pairing.getG1().newElement().setFromHash(shatringbyte, 0, shatringbyte.length);
                return g1.duplicate();
            }

    public static void main(String[] args) throws Exception {
        long hash3time=0;


        System.out.println(" time for generating random number \n");
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);


      String str = "radomword";
      
        long start, end ;
        for(int i = 0;i <1000;i++)
        {
            
           start = System.currentTimeMillis();
            Element h1 = hash3(str, pairing);
            end = System.currentTimeMillis();

            hash3time += (end - start);
        }

        System.out.println("hash 3 time "+ hash3time);

    }
}
