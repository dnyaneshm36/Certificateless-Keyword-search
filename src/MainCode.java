package src;


import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
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
              shastring = toHexString(getSHA(str));                                      //generate the sha256 output for string 
            } catch (NoSuchAlgorithmException e) {
              System.out.println(" No such Algorithm exception occurred ");
              e.printStackTrace();
            }
            byte [] shatringbyte = shastring.getBytes();                                // converting it into bytes.
            
            Element g1 = pairing.getG1().newElement().setFromHash(shatringbyte, 0, shatringbyte.length);             // setting g1 element from bytes.
            return g1.duplicate();                                                      // to make deep copy used duplicate.
        }
        //H2: {0,1}*→Zq*
        static BigInteger hash2_asscii(String str, BigInteger q)   
        {

            String shastring = "";
            try {
              shastring = toHexString(getSHA(str));                                         //generate the sha256 output for string 
            } catch (NoSuchAlgorithmException e) {            
              System.out.println(" No such Algorithm exception occurred ");
              e.printStackTrace();
            }
            int l = shastring.length();
            int convert;
            BigInteger sum = new BigInteger("0");
            for ( int i = 0 ; i < l ; i++ )
            {
                convert = (int) shastring.charAt(i) ;                                       //convert each char to ascii value  
                // convert int to BigInteger
                BigInteger bigconv = BigInteger.valueOf(convert);                           // make it biginteger type.
                sum = sum.add(bigconv);                                                      // add it.
            }
            sum = sum.mod(q);                                                               // mod oprations.

            return sum;
        }
        //H3: {0,1}*→G1*
        static Element hash3(String str,Pairing pairing)                                    // this hash function is similliar to hash1.
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
              byte [ ] G2pairbyte = G2pair.toBytes();                                     // group g2 element to bytes.
              BigInteger G2pairbiginterger = new BigInteger(G2pairbyte);                  // converting it into biginterger.
              return G2pairbiginterger;
        }
    public static void main(String[] args) throws Exception {
   
      long setup=0;
      long senderkey=0;
      long receiverkey = 0;
      long cipher = 0;                                                  //time to calculate for each algo.
      long trapdoor = 0;
      long test = 0;
      // for ( int times  = 0 ; times<1000 ; times++){


        // int rBits = 7;                                                                      // this code generater params1.txt file
        // int qBits = 20;                                                                      // no need to generated again.
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
            
        Element P = pairing.getG1().newRandomElement();                              //generate the ramdom p element g1 type.
 
        // try {                                                                      // when you have to make p constatant.
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

        Element master_key_lamda = pairing.getZr().newRandomElement();                   // random value of lambda.
        Element PKc = P.duplicate();
        PKc.mulZn(master_key_lamda);                                                          // PKc = lambda * P
        Element SKs = master_key_lamda.duplicate();
        long KeyGen_server_end = System.currentTimeMillis();
        SetupParamter r = new SetupParamter(P,master_key_lamda,PKc,SKs,(KeyGen_server_end-KeyGen_server_start)) ;
        
        
        // System.out.println("--------------  object "+r);
         
        
        long  time_generater_key_start  = System.currentTimeMillis();                   //generating sender keys.
        ClientKey sender; 
        String senderId = "senderid";

        Element Qus = hash1(senderId, pairing).duplicate();
        Element Dus = Qus.duplicate();                                                  //multiply the du by lamda.
        Dus.mulZn(master_key_lamda);

        sender = new ClientKey(senderId,Qus , Dus);
        
        Element Sus = pairing.getZr().newRandomElement(); //clent id
        Element SKu1s = Sus.duplicate();
        Element SKu2s = Dus.duplicate();
        SKu2s.mulZn(Sus);                                      //multiply the SKu2s by Sus.

        Element PKu1s = r.getP().duplicate();
        PKu1s.mulZn(Sus);
        Element PKu2s = r.getPKc().duplicate();
        PKu2s.mulZn(Sus);      
        sender.setPKu1(PKu1s);                                                   //storing in sender keys objects.
        sender.setPKu2(PKu2s);
        sender.setSKu1(SKu1s);
        sender.setSKu2(SKu2s);
        sender.setSu(Sus);
        long  time_generater_key_end  = System.currentTimeMillis();
        
        sender.setRequredTime((time_generater_key_end-time_generater_key_start));
        
        time_generater_key_start  = System.currentTimeMillis();                                        //generating receivers keys.
        
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

        

        Element pair_sender_PKc,pair_sender_P;
        pair_sender_PKc =  pairing.pairing(sender.getPKu1(), r.getPKc());
        pair_sender_P  = pairing.pairing(sender.getPKu2(), r.getP());

        if( pair_sender_PKc.isEqual(pair_sender_P) )                       // checking pairing for sender.
        {
          System.out.println("\nPairing equal for Sender ");
        }
        else
        {
          System.out.println("\nfail turn ⊥ and about\n");
        }

        //checking pair is equal.
        Element pair_receiver_PKc,pair_receiver_P;                                          //checing pairing for receiver.
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

           String data = "";                                                   // read the value of q from the param1.txt
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


        String word1 = "abc";                                                   // words what we are encypting 
        String word2= "wordsecend";
        String word3 = "ramleela";
        String word4 = "vnit_collage";
        String word5 = "welcome";
        String word6 = "corona";
        ArrayList<String> words                   = new ArrayList();                //encrptions of words.
        ArrayList<CipherWord> cipherwords         = new ArrayList();                // array list of encripted data.
        words.add(word1);
        words.add(word2);
        words.add(word3);
        words.add(word4);
        words.add(word5);
        words.add(word6);
        int m = 6;


        for( int i=0 ;i< m;i++)
        {
            time_generater_key_start  = System.currentTimeMillis();
            Element Ri;
            Ri = pairing.getZr().newRandomElement();     
            BigInteger rethash = hash2_asscii(words.get(i), q);                            //apply hash2 word get bigint.
            Element hash = pairing.getZr().newElement(rethash);                            //convert bigint to zr element format.
            Element first = receiver.getQu().duplicate();
            first.mulZn(hash);                                                              //multiply hash2 ri and qu.
            first.mulZn(Ri);                                              
            Element pair1 = pairing.pairing( first , receiver.getPKu2() );                   //first pair int c) part                     
            
            Element QsRi = sender.getQu().duplicate();
            QsRi.mulZn(Ri);    
            Element pair2 = pairing.pairing( QsRi , sender.getPKu2() );                       //second pair in the c) part

            Element hash3_word = hash3(words.get(i), pairing).duplicate();
            hash3_word.mulZn(Ri);
            Element pair3 = pairing.pairing( hash3_word , r.getP() );                           // third pair.
  
            Element Ti = pair1.duplicate();
            Ti.mul(pair2);
            Ti.mul(pair3);                                                                      //multiplying pairs.

            Element Ui =  r.getP().duplicate();
            Ui.mulZn(Ri);
            BigInteger Vi = hash4(Ti);                                                          // generate vi from hash4
            CipherWord cipherword = new CipherWord(Ui,Vi);
            time_generater_key_end  = System.currentTimeMillis();
            cipherword.setRequiredTime((time_generater_key_end-time_generater_key_start));
            cipherwords.add(cipherword);

        }
        

          // Trapdoor()


          System.out.println(" Below words we are encripting.\n");                      //print words just to know.
          for( int i = 0 ; i< m ; i++)
          {
               System.out.println(words.get(i));
          }
          System.out.println("Enter the word for checking. \n ");                         // taking word from cmd 
          Scanner scread = new Scanner(System.in);
          
          String wordchecking = scread.nextLine();

          time_generater_key_start  = System.currentTimeMillis();
          BigInteger hash2big = hash2_asscii(wordchecking, q);
          Element hash2 = pairing.getZr().newElement(hash2big);                       //apply hash2 and convert it to zr format.
          Element T1 = r.getP().duplicate();
          T1.mulZn(r.getMaster_key_lamda());                                            // Ti = lambda * P
          Element H2wSKR = receiver.getSKu2().duplicate();                          
          H2wSKR.mulZn(hash2);
          Element lambdaPKs = sender.getPKu1().duplicate() ;                           //calating lambda * PKs.
          lambdaPKs.mulZn(r.getMaster_key_lamda());

          byte [] H2wSKRByte = H2wSKR.toBytes();
          byte [] lambdaPKsByte = lambdaPKs.toBytes();
          int lenFinal = Math.max(H2wSKRByte.length, lambdaPKsByte.length);             // binary xor oprations.
          byte[] T2bytexor = new byte[lenFinal];
          for( int  i = 0 ; i < lenFinal ; i++ )
          {
              byte x =  H2wSKRByte[i];
              byte y =  lambdaPKsByte[i];
              int xint = x;                                                             //converted to int and apply xor bytes wise.
              int yint = y;
              int ans = ( xint ^ yint );
              T2bytexor[i] = (byte) ans;
          }
          
          Element T2 = pairing.getG1().newElement();
          int bythread = T2.setFromBytes(T2bytexor);
          System.out.println(bythread);

          Element hash3_word2 = hash3(wordchecking, pairing).duplicate();
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
          wordSearch.setByteArrayT2(T2bytexor);                                               //setting trapdoor value.
          wordSearch.setBythArrayT3(T3bytexor);
          time_generater_key_end  = System.currentTimeMillis();
          wordSearch.setRequiredTime((time_generater_key_end-time_generater_key_start));


          // TEST()

             Element SKsT1 = wordSearch.getT1().duplicate();
             SKsT1.mulZn(sender.getSKu1());                                                 //calucating T1 * SKs1
             byte [] SKsT1byte = SKsT1.toBytes();
             byte [] T2byte = wordSearch.getByteArrayT2().clone();
             byte [] T3byte = wordSearch.getBythArrayT3().clone();                           // taking byte array gerated in trapdoor funtions.
                                                                                              

          lenFinal = Math.max(T2byte.length, SKsT1byte.length);
          byte [] T2dashintobyte = new byte[lenFinal];
          for( int  i = 0 ; i < lenFinal ; i++ )                                            // bytewise xor
          {
              byte x =  T2byte[i];
              byte y =  SKsT1byte[i];
              int xint = x;
              int yint = y;
              int ans = ( xint ^ yint );
              T2dashintobyte[i] = (byte) ans;
          }

          lenFinal = Math.max(T3byte.length, SKsT1byte.length);
          byte [] T3dashintobyte = new byte[lenFinal];                                          //byte wise xor
          for( int  i = 0 ; i < lenFinal ; i++ )
          {
              byte x =  T3byte[i];
              byte y =  SKsT1byte[i];
              int xint = x;
              int yint = y;
              int ans = ( xint ^ yint );
              T3dashintobyte[i] = (byte) ans;
          }          
          Element T2dash = pairing.getG1().newElement();                        //convert byte array back to g1 type 
          bythread = T2dash.setFromBytes(T2dashintobyte);
          Element T3dash = pairing.getG1().newElement();
          bythread = T3dash.setFromBytes(T3dashintobyte);   
                                                                        //-------------------------------------

          Element firstElementPair = T2dash.duplicate();                              //perfom additions.
          firstElementPair.add(sender.getSKu2()); 
          firstElementPair.add(T3dash);                                           
          PairingPreProcessing firstelementpre_pairing = pairing.getPairingPreProcessingFromElement(firstElementPair);
                                                                          // generated prepocess pair contain $firstElementPair
          for ( int i = 0 ; i < cipherwords.size() ; i++)
          {
                  Element Ui = cipherwords.get(i).getUi();
                  BigInteger Vi = cipherwords.get(i).getVi();
                  Element hash4pair =  firstelementpre_pairing.pairing(Ui);         // pair with preprocess pair.
                  BigInteger hash4pairbig = hash4(hash4pair);
                  if( hash4pairbig.equals( Vi ) )
                  {
                      System.out.println("\nYes !!! \n\n");  
                  }
                  else
                  {
                        System.out.println("\n No !!! \n ");
                  }
          }

          
          time_generater_key_end  = System.currentTimeMillis();         //time calcucations.

          setup+= r.time;
          senderkey += sender.requredTime;
          receiverkey += receiver.requredTime;
          for(int i = 0 ; i< m ; i++)
          {
              cipher+= cipherwords.get(i).requiredTime;
          }
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