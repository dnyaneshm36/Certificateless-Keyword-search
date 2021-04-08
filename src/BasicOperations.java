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

public class BasicOperations {
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
        //H1: {0,1}*→G1*
        static Element hash1(String str,Pairing pairing)
        {
            String shastring = "";
            try {
              shastring = toHexString(getSHA(str));
            } catch (NoSuchAlgorithmException e) {
              System.out.println(" No such Algorithm exception occurred ");
              e.printStackTrace();
            }
            byte [] shatringbyte = shastring.getBytes();
    
            Element g1 = pairing.getG1().newElementFromBytes(shatringbyte);
            return g1.duplicate();
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
    
            Element g1 = pairing.getG1().newElementFromBytes(shatringbyte);
            return g1.duplicate();
        }
        // H4:G2→{0,1}f
        static BigInteger hash4(Element G2pair)
        {
              byte [ ] G2pairbyte = G2pair.toBytes();
      
              BigInteger G2pairbiginterger = new BigInteger(G2pairbyte);
              return G2pairbiginterger;
        }


    public static void main(String[] args) throws Exception {
            
        long randomg1=0;
        long randomg2=0,randomzr=0,addtionsg1=0,mulg1=0,mulg1zn=0;
        long pairingbetweeng1s=0,subofg1=0;

        long hash1time=0;
        long hash2time=0;
        long hash3time=0;
        long hash4time=0;

        System.out.println(" time for generating random number \n");
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);


        for(int i = 0;i <1000;i++)
        {
            long start = System.currentTimeMillis();
            Element Raddom  = pairing.getG1().newRandomElement();
            long end = System.currentTimeMillis();
            randomg1 += (end-start);
            start = System.currentTimeMillis();
            Element Raddomg2  = pairing.getG2().newRandomElement();
            end = System.currentTimeMillis();
            randomg2 += (end-start);
            start = System.currentTimeMillis();
            Element Raddomzr  = pairing.getZr().newRandomElement();
            end = System.currentTimeMillis();
            randomzr += (end-start);

            Element opr = pairing.getG1().newRandomElement();

            start = System.currentTimeMillis();
            opr.add(Raddom) ; 
            end = System.currentTimeMillis();
            addtionsg1 += (end-start);

            start = System.currentTimeMillis();
            opr.mul(Raddom) ; 
            end = System.currentTimeMillis();
            mulg1 += (end-start);
            start = System.currentTimeMillis();
            opr.mulZn(Raddomzr) ; 
            end = System.currentTimeMillis();
            mulg1zn += (end-start);
            

            //paring of the two g1 element group 
            start = System.currentTimeMillis();
            pairing.pairing( opr , Raddom );
            end = System.currentTimeMillis();
            pairingbetweeng1s += (end-start);


            // substrationg g1 elements 
            start = System.currentTimeMillis();
            opr.sub(Raddom);
            end = System.currentTimeMillis();
            subofg1 += (end-start);




            String str = "radomword";
            start = System.currentTimeMillis();
            Element h1 = hash1(str, pairing);
            end = System.currentTimeMillis();

            hash1time += (end - start);

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

          start = System.currentTimeMillis();
          BigInteger rethash = hash2_asscii(str, q);
          Element h2 = pairing.getZr().newElement(rethash);
          end = System.currentTimeMillis();

          hash2time += (end - start);
          
          start = System.currentTimeMillis();
          Element h3 = hash1(str, pairing);
          end = System.currentTimeMillis();

          hash3time += (end - start) ;
          
          Element h4pairg2 =  pairing.pairing(h1, h3);

          start = System.currentTimeMillis();

          BigInteger h4big = hash4(h4pairg2);

          end = System.currentTimeMillis();

          hash4time += (end - start);


        }


        

        System.out.println("Time for random g1 element generations is     : "+randomg1);
        System.out.println("Time for random g2 element generations is     : "+randomg2);
        System.out.println("Time for random zr element generations is     : "+randomzr);
        System.out.println("Time for Adding two element from g1  is       : "+addtionsg1);
        System.out.println("Time for Multiplaying two element from g1  is : "+mulg1);
        System.out.println("Time for Scaler multipcation  of g1 and Zr    : "+mulg1zn);
        System.out.println("Time for pairing two g1 element  is           : "+pairingbetweeng1s);
        System.out.println("Time for  Subtracti of two G1 element is      : "+subofg1);
        System.out.println("Time for  runing hash1 algorithms is          : "+hash1time);
        System.out.println("Time for  runing hash2 algorithms is          : "+hash2time);
        System.out.println("Time for  runing hash3 algorithms is          : "+hash3time);
        System.out.println("Time for  runing hash4 algorithms is          : "+hash4time);




    }
}
// Time for random g1 element generations is     : 2.879
// Time for random g2 element generations is     : 2.730
// Time for random zr element generations is     : 0.012
// Time for Adding two element from g1  is       : 0.115
// Time for Multiplaying two element from g1  is : 0.080
// Time for Scaler multipcation  of g1 and Zr    : 18.799
// Time for pairing two g1 element  is           : 9.158
// Time for  Subtracti of two G1 element is      : 0.102
// Time for  runing hash1 algorithms is          : 41.210
// Time for  runing hash2 algorithms is          : 0.060
// Time for  runing hash3 algorithms is          : 40.753
// Time for  runing hash4 algorithms is          : .009