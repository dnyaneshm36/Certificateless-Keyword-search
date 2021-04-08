package src;


import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class hash2time {


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

            //H2: {0,1}*→Zq*
            static BigInteger hash2_asscii(String str, BigInteger q)
            {
    
                String shastring = "";
                try {
                  shastring = toHexString(getSHA(str));
                } catch (NoSuchAlgorithmException e) {
                  System.out.println(" No such Algorithm exception occurred ");
                  e.printStackTrace();
                }
                int l = shastring.length();
                int convert;
                BigInteger sum = new BigInteger("0");
                for ( int i = 0 ; i < l ; i++ )
                {
                    convert = (int) shastring.charAt(i) ;
    
                    // convert int to BigInteger
                    BigInteger bigconv = BigInteger.valueOf(convert);
                    sum = sum.add(bigconv);
                }
                sum = sum.mod(q);
    
                // // converting String to ASCII value in Java 
                // try {
                //      String text = "ABCDEFGHIJKLMNOP"; 
                // // translating text String to 7 bit ASCII encoding 
                // byte[] bytes = text.getBytes("US-ASCII"); 
                // System.out.println("ASCII value of " + text + " is following"); 
                // System.out.println(Arrays.toString(bytes)); 
                // } catch (java.io.UnsupportedEncodingException e)
                //  {
                //       e.printStackTrace(); 
                // }
                return sum;
            }

    public static void main(String[] args) throws Exception {



        System.out.println(" time for generating random number \n");
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        long hash2time=0;
        String data = "";

        try {                 // taking q from param
                File myObj = new File("params1.txt");
                Scanner myReader = new Scanner(myObj);

            
                data = myReader.nextLine();
                data = myReader.nextLine();
                data = data.substring(2);
                
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
      }
      String qstr = data;
      BigInteger q = new BigInteger(qstr);
      String str = "radomword";
      
        long start, end ;
        for(int i = 0;i <1000;i++)
        {
            
           start = System.currentTimeMillis();
          BigInteger rethash = hash2_asscii(str, q);
          Element h2 = pairing.getZr().newElement(rethash);
          end = System.currentTimeMillis();

          hash2time += (end - start);
        }

        System.out.println("hash 2 time "+ hash2time);

    }
}
