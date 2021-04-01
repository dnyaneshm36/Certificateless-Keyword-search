package src;


import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
  

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class MainCode {

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
    
            Element g1 = pairing.getG1().newElement().setFromHash(shatringbyte, 0, shatringbyte.length);
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
    
            Element g1 = pairing.getG1().newElement().setFromHash(shatringbyte, 0, shatringbyte.length);
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
   
      long setup=0;
      long senderkey=0;
      long receiverkey = 0;
      long cipher = 0;
      long trapdoor = 0;
      long test = 0;
      // for ( int times  = 0 ; times<1000 ; times++){


        // int rBits = 7;
        // int qBits = 20;
        // PairingParametersGenerator pg = new TypeACurveGenerator(rBits, qBits);
        // PairingParameters params = pg.generate();

        // try {
        //     FileWriter fw = new FileWriter("params1.txt");
        //     String paramsStr = params.toString();
        //     fw.write(paramsStr);
        //     fw.flush();
        //     fw.close();

        // } catch (IOException e) {
        //     System.out.println("the we get problem in writering ");
        //     e.printStackTrace();
        // }


        // //Implamenting the pairing   

        // Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        // //use pbc wrapper
        // PairingFactory.getInstance().setUsePBCWhenPossible(true);


        //Implamenting the pairing   
        long KeyGen_server_start = System.currentTimeMillis();
        Pairing pairing = PairingFactory.getPairing("params1.txt"); 
        //use pbc wrapper
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        
        /* Return Zr */
        Field Zr = pairing.getZr();

        // /* Return G1 */
        // Field G1 = pairing.getG1();

        // /* Return G2 */
        // Field G2 = pairing.getG2();

        // /* Return GT */
        // Field GT = pairing.getGT();
        
        Element P = pairing.getG1().newRandomElement();

        // try {
        //     FileWriter fw = new FileWriter("P.txt");
        //     String PStr = Base64.encodeBytes(P.toBytes());
        //     fw.write(PStr);
        //     fw.flush();
        //     fw.close();

        // } catch (IOException e) {
        //     System.out.println("the we are genpr wrigi in P ");
        //     e.printStackTrace();
        // }
        // System.out.println("P is ---- "+P);
        // try {                 // taking q from param
        //   File myObj = new File("P.txt");
        //   Scanner myReader = new Scanner(myObj);

        //   String Pbytestr;
        //   Pbytestr = myReader.nextLine();
          
        //   P = pairing.getG1().newElementFromBytes(Base64.decode(Pbytestr));
        //   myReader.close();
        // } catch (FileNotFoundException e) {
        //   System.out.println("An error occurred.");
        //   e.printStackTrace();
        // }
        
        // System.out.println("P is --2-- "+P);

        Element master_key_lamda = pairing.getZr().newRandomElement();
        Element PKc = P.duplicate();
        PKc.mulZn(master_key_lamda);
        Element SKs = master_key_lamda.duplicate();
        long KeyGen_server_end = System.currentTimeMillis();
        SetupParamter r = new SetupParamter(P,master_key_lamda,PKc,SKs,(KeyGen_server_end-KeyGen_server_start)) ;
        
        
        // System.out.println("--------------  object "+r);
         
        
        long  time_generater_key_start  = System.currentTimeMillis();
        ClientKey sender;
        String senderId = "senderid";

        Element Qus = hash1(senderId, pairing).duplicate();
        Element Dus = Qus.duplicate();
        Dus.mulZn(master_key_lamda);

        sender = new ClientKey(senderId,Qus , Dus);
        
        Element Sus = pairing.getZr().newRandomElement(); //clent id
        Element SKu1s = Sus.duplicate();
        Element SKu2s = Dus.duplicate();
        SKu2s.mulZn(Sus);   

        Element PKu1s = r.getP().duplicate();
        PKu1s.mulZn(Sus);
        Element PKu2s = r.getPKc().duplicate();
        PKu2s.mulZn(Sus);      
        sender.setPKu1(PKu1s);
        sender.setPKu2(PKu2s);
        sender.setSKu1(SKu1s);
        sender.setSKu2(SKu2s);
        sender.setSu(Sus);
        long  time_generater_key_end  = System.currentTimeMillis();
        
        sender.setRequredTime((time_generater_key_end-time_generater_key_start));
        
        time_generater_key_start  = System.currentTimeMillis();
        
        ClientKey receiver;
        String receiverId = "receiverid";
        Element Qur = hash1(receiverId, pairing);
        Element Dur = Qur.duplicate();
        Dur.mulZn(master_key_lamda);
        receiver = new ClientKey(receiverId, Qur, Dur);

        Element Sur = pairing.getZr().newRandomElement();    //client secrete.
        Element SKu1r = Sur.duplicate();
        Element SKu2r = Dur.duplicate();
        SKu2r.mulZn(Sur);

        Element PKu1r = r.getP().duplicate();
        PKu1r.mulZn(Sur);

        Element PKu2r = r.getPKc().duplicate();
        PKu2r.mulZn(Sur);

        receiver.setPKu1(PKu1r);
        receiver.setPKu2(PKu2r);
        receiver.setSKu1(SKu1r);
        receiver.setSKu2(SKu2r);
        receiver.setSu(Sur);


        time_generater_key_end  = System.currentTimeMillis();
        receiver.setRequredTime((time_generater_key_end-time_generater_key_start));

        // CLPKES

        //checking pair is equal.

        time_generater_key_start  = System.currentTimeMillis();

        Element pair_sender_PKc,pair_sender_P;
        // System.out.println("pku1    "+sender.getPKu1());
        // System.out.println("PKC   "+  r.getPKc() );
        // System.out.println("getPKu2  "+sender.getPKu2());
        // System.out.println("getp   "+r.getP());
        pair_sender_PKc =  pairing.pairing(sender.getPKu1(), r.getPKc());
        pair_sender_P  = pairing.pairing(sender.getPKu2(), r.getP());

        if( pair_sender_PKc.isEqual(pair_sender_P) )
        {
          System.out.println("\nPairing equal for receiver ");
        }
        else
        {
          System.out.println("\nfail turn ⊥ and about\n");
        }

        //checking pair is equal.
        Element pair_receiver_PKc,pair_receiver_P;

        pair_receiver_PKc =  pairing.pairing(receiver.getPKu1(), r.getPKc());
        pair_receiver_P  = pairing.pairing(receiver.getPKu2(), r.getP());


        if( pair_receiver_PKc.isEqual(pair_receiver_P) )
        {
          System.out.println("\nPairing equal for receiver ");
        }
        else
        {
          System.out.println("\nfail turn ⊥ and about\n");
        }

        String word = "wordto_encrption";
        Element Ri;
        Ri = pairing.getZr().newRandomElement();

        
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
          String str = data;
          BigInteger q = new BigInteger(str);
          BigInteger rethash = hash2_asscii(word, q);
          Element hash = Zr.newElement(rethash);
          Element first = receiver.getQu().duplicate();
          first.mulZn(hash);
          first.mulZn(Ri);
          Element pair1 = pairing.pairing( first , receiver.getPKu2() );
          Element QsRi = sender.getQu().duplicate();
          QsRi.mulZn(Ri);    
          Element pair2 = pairing.pairing( QsRi , sender.getPKu2() );
          
          
          Element hash3_word = hash3(word, pairing).duplicate();
          hash3_word.mulZn(Ri);

          Element pair3 = pairing.pairing( hash3_word , r.getP() );

          Element Ti = pair1.duplicate();
          Ti.mul(pair2);
          Ti.mul(pair3);

          Element Ui =  r.getP().duplicate();
          Ui.mulZn(Ri);
          BigInteger Vi = hash4(Ti);
          CipherWord cipherword = new CipherWord(Ui,Vi);

          time_generater_key_end  = System.currentTimeMillis();

          cipherword.setRequiredTime((time_generater_key_end-time_generater_key_start));

          // Trapdoor()

          time_generater_key_start  = System.currentTimeMillis();

          String wordchecking = "wordto_encrption";
          BigInteger hash2big = hash2_asscii(wordchecking, q);
          Element hash2 = Zr.newElement(hash2big);
          Element T1 = r.getP().duplicate();
          T1.mulZn(r.getMaster_key_lamda());
          Element H2wSKR = receiver.getSKu2().duplicate();
          H2wSKR.mulZn(hash2);
          Element lambdaPKs = sender.getPKu1().duplicate() ;
        //   System.out.println("lamsdf  H2wSKR  t2 -dash-"+H2wSKR);
          lambdaPKs.mulZn(r.getMaster_key_lamda());
        //  System.out.println("lamsdf  2 "+lambdaPKs);
          byte [] H2wSKRByte = H2wSKR.toBytes();
        //   Element e = pairing.getG1().newElement();
        //   int bythread = e.setFromBytes(H2wSKRByte);
        //   System.out.println("e--- "+e);
        //   System.out.println("H2wSKR--- "+H2wSKR);
          byte [] lambdaPKsByte = lambdaPKs.toBytes();
          int lenFinal = Math.max(H2wSKRByte.length, lambdaPKsByte.length);
          byte[] T2bytexor = new byte[lenFinal];
          for( int  i = 0 ; i < lenFinal ; i++ )
          {
              byte x =  H2wSKRByte[i];
              byte y =  lambdaPKsByte[i];
              int xint = x;
              int yint = y;
              int ans = ( xint ^ yint );
              T2bytexor[i] = (byte) ans;
          }
          
          Element T2 = pairing.getG1().newElement();
          int bythread = T2.setFromBytes(T2bytexor);
          System.out.println(bythread);

          Element hash3_word2 = hash3(wordchecking, pairing).duplicate();
         // System.out.println("hash3_word2 should be equal to t3 dash "+hash3_word2);
          byte [] hash3WordByte = hash3_word2.toBytes();
          lenFinal = Math.max(hash3WordByte.length, lambdaPKsByte.length);
          byte[] T3bytexor = new byte[lenFinal];
          for( int  i = 0 ; i < lenFinal ; i++ )
          {
              byte x =  hash3WordByte[i];
              byte y =  lambdaPKsByte[i];
              int xint = x;
              int yint = y;
              int ans = ( xint ^ yint );
              T3bytexor[i] = (byte) ans;
          }
          Element T3 = pairing.getG1().newElement();
          bythread = T3.setFromBytes(T3bytexor);

          TrapdoorWord wordSearch = new TrapdoorWord();
          wordSearch.setT1(T1);
          wordSearch.setT2(T2);
          wordSearch.setT3(T3);
          wordSearch.setByteArrayT2(T2bytexor);
          wordSearch.setBythArrayT3(T3bytexor);
          time_generater_key_end  = System.currentTimeMillis();

          wordSearch.setRequiredTime((time_generater_key_end-time_generater_key_start));
          // TEST()

          Element SKsT1 = wordSearch.getT1().duplicate();
          SKsT1.mulZn(sender.getSKu1());
          byte [] SKsT1byte = SKsT1.toBytes();
          // byte [] T2byte = wordSearch.getT2().toBytes();
          // byte [] T3byte = wordSearch.getT3().toBytes();
             byte [] T2byte = wordSearch.getByteArrayT2().clone();
             byte [] T3byte = wordSearch.getBythArrayT3().clone();
        //   int [] T2byte = T2bytexor;
        //   int [] T3byte = T3bytexor;
          lenFinal = Math.max(T2byte.length, SKsT1byte.length);
          byte [] T2dashintobyte = new byte[lenFinal];
          for( int  i = 0 ; i < lenFinal ; i++ )
          {
              byte x =  T2byte[i];
              byte y =  SKsT1byte[i];
              int xint = x;
              int yint = y;
              int ans = ( xint ^ yint );
              T2dashintobyte[i] = (byte) ans;
              
              // if(H2wSKRByte[i]!=T2dashbyte[i])
              //   {
              //     System.out.println(i+"   this    "+T2dashbyte[i]+"   --==--   "+T2byte[i]+"   ^   "+SKsT1byte[i] +"  ---  "+H2wSKRByte[i]);
              //   }
          }

          lenFinal = Math.max(T3byte.length, SKsT1byte.length);
          byte [] T3dashintobyte = new byte[lenFinal];
          for( int  i = 0 ; i < lenFinal ; i++ )
          {
              byte x =  T3byte[i];
              byte y =  SKsT1byte[i];
              int xint = x;
              int yint = y;
              int ans = ( xint ^ yint );
              T3dashintobyte[i] = (byte) ans;
                // if(hash3WordByte[i]!=T3dashbyte[i])
                // {
                //   System.out.println(i+"   1111    "+T3dashbyte[i]+"   --==--   "+T3byte[i]+"   ^   "+SKsT1byte[i] +"  ---  "+hash3WordByte[i]);
                // }
          }
          // if you comment bellow fourlines uncommment  t2dash line then it run perfect
          
          Element T2dash = pairing.getG1().newElement();
          bythread = T2dash.setFromBytes(T2dashintobyte);
          Element T3dash = pairing.getG1().newElement();
          bythread = T3dash.setFromBytes(T3dashintobyte);   
                                                                        //-------------------------------------
          
          // Element T2dash = H2wSKR.duplicate();
          // Element T3dash = hash3_word2.duplicate();

        //   System.out.println(" -----------T2dash --- "+ T2dash);
        //   System.out.println(" -----------T3dash --- "+ T3dash);
          Element firstElementPair = T2dash.duplicate();
        //   System.out.println("firstElementPair "+firstElementPair);
          firstElementPair.add(sender.getSKu2()); 
        //   System.out.println("firstElementPair  1  "+firstElementPair);
          firstElementPair.add(T3dash);
        //   System.out.println("firstElementPair 2  "+firstElementPair);
          Element hash4pair =  pairing.pairing(firstElementPair, cipherword.getUi());
  
          BigInteger hash4pairbig = hash4(hash4pair);
          String equality;

          // System.out.println("------------------H2wSKRByte-----------------"+H2wSKRByte.length);
          // for(int i=0; i< H2wSKRByte.length ; i++) {
          //   System.out.print(H2wSKRByte[i] +" ");
          // }
          // System.out.println("\n------------------lambdaPKsByte-----------------"+lambdaPKsByte.length);
          // for(int i=0; i< lambdaPKsByte.length ; i++) {
          //   System.out.print(lambdaPKsByte[i] +" ");
          // }
          // System.out.println("\n------------------T2bytexor-----------------"+T2bytexor.length);
          // for(int i=0; i< T2bytexor.length ; i++) {
          //   System.out.print(T2bytexor[i] +" ");
          // }
          
          // System.out.println("\n------------------T2dashintobyte-----------------"+T2dashintobyte.length);
          // for(int i=0; i< T2dashintobyte.length ; i++) {
          //   System.out.print(T2dashintobyte[i] +" ");
          // }
          // System.out.println("\n------------------hash3WordByte-----------------"+hash3WordByte.length);
          // for(int i=0; i< hash3WordByte.length ; i++) {
          //   System.out.print(hash3WordByte[i] +" ");
          // }
          // System.out.println("\n------------------SKsT1byte-----------------"+SKsT1byte.length);
          // for(int i=0; i< SKsT1byte.length ; i++) {
          //   System.out.print(SKsT1byte[i] +" ");
          // }
          // System.out.println("\n------------------T3bytexor-----------------"+T3bytexor.length);
          // for(int i=0; i< T3bytexor.length ; i++) {
          //   System.out.print(T3bytexor[i] +" ");
          // }
          // System.out.println("\n------------------T3dashintobyte-----------------"+T3dashintobyte.length);
          // for(int i=0; i< T3dashintobyte.length ; i++) {
          //   System.out.print(T3dashintobyte[i] +" ");
          // }



          if( hash4pairbig.equals(Vi) )
          {
              System.out.println("\nYes !!! \nsuccessfully  find the word\n");
              equality="\nYes !!! \nsuccessfully  find the word\n"+"\n hash4pairbig  : "+hash4pairbig+"\n Vi()          : "+cipherword.getVi();
          }
          else
          {
                System.out.println("\n oh No oh NO oh No !!! \nsuccessfully didn't find  the word\n");
                equality = "\n oh No oh NO oh No !!! \nsuccessfully didn't find  the word\n"+"\n hash4pairbig  : "+hash4pairbig+"\n Vi()          : "+cipherword.getVi();
          }

          
          System.out.println("hash4pairbig     q---   "+hash4pairbig);
          System.out.println("cipherword.getVi()---   "+cipherword.getVi());
          
          time_generater_key_end  = System.currentTimeMillis();
          String ret="";
          // ret = r.toString();
          // ret +=" \n sender key------------- \n\n"+sender.toString();
          // ret +=" \n receiver key------------- \n\n"+receiver.toString();
          // ret +=" \n cipher of word ------------- \n\n"+cipherword.toString();
          // ret +=" \n Trapdoor of word ------------- \n\n"+wordSearch.toString();
          // ret += "\n Checking Trapdoor gives same reasult or not \n\n";
          ret += equality ;
        
        
        
          System.out.println(ret);
          setup+= r.time;
          senderkey += sender.requredTime;
          receiverkey += receiver.requredTime;
          cipher += cipherword.requiredTime;
          trapdoor += wordSearch.requiredTime;
          test += (time_generater_key_end-time_generater_key_start);
        
        // }


        // setup /= 1000;
        // senderkey /= 1000;
        // receiverkey /= 1000;
        // cipher /=1000;
        // trapdoor /= 1000;
        // test /= 1000;
      
        System.out.println("\nTime required for milli sec iterations.");
        System.out.println("setup "+setup);
        System.out.println("senderkey "+senderkey);
        System.out.println("receiverkey "+receiverkey);
        System.out.println("cipher "+cipher);
        System.out.println("trapdoor "+trapdoor);
        System.out.println("test "+test);


    }
    
}


// setup 1.218
// senderkey 2.787
// receiverkey 2.634
// cipher 7.679
// trapdoor 1.940
// test 1.197