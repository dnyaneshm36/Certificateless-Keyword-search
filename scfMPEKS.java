import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;


public class scfMPEKS {

        public static void main(String[] args) throws Exception{
            System.out.println("the dnyanesh ");
            int rBits = 160;
            int qBits = 512;
            // PairingParametersGenerator parg = new PairingParametersGenerator(
            //     3,  // the number of primes
            //     517 // the bit length of each prime
            // );
            // PairingParameters params = parg.generate();
            PairingParametersGenerator pg = new TypeACurveGenerator(rBits, qBits);
            PairingParameters params = pg.generate();
    
            try {
                FileWriter fw = new FileWriter("params1.txt");
                String paramsStr = params.toString();
                fw.write(paramsStr);
                fw.flush();
                fw.close();
    
            } catch (IOException e) {
                System.out.println("the we get problem in writering ");
                e.printStackTrace();
            }
    
    
            //Implamenting the pairing   
    
            Pairing pairing = PairingFactory.getPairing("params1.txt"); 
            //use pbc wrapper
            PairingFactory.getInstance().setUsePBCWhenPossible(true);

            long Sum_KeyGen_Server=0;

            int j = 0;
            Element P = pairing.getG1().newRandomElement();
            //keyGen -Server 
            //sks = a pks = (A,B)\
            long KeyGen_server_start = System.currentTimeMillis();
            Element a = pairing.getZr().newRandomElement(); //sks
            Element A = P.duplicate();
            A.mulZn(a); //A

            PairingPreProcessing pA = pairing.getPairingPreProcessingFromElement(A);
            Element B = pairing.getG1().newRandomElement(); // B
            PairingPreProcessing pB = pairing.getPairingPreProcessingFromElement(B);

            long KenyGen_server_end = System.currentTimeMillis();
            System.out.println("SKF - MPEKS KenyGen _server [" +j+"]: "+(KenyGen_server_end - KeyGen_server_start) );

            Sum_KeyGen_Server =   (Sum_KeyGen_Server + (KenyGen_server_end-KeyGen_server_start));

            long KeyGen_receiver_start = System.currentTimeMillis();
            Element c = pairing.getZr().newRandomElement();  //skr\
            Element C = P.duplicate();
            C.mulZn(c);

            PairingPreProcessing pC = pairing.getPairingPreProcessingFromElement(C);
            long KenyGen_receiver_end = System.currentTimeMillis();

            //SCF-MPEKS
            // S = (M,N1,N2, .... Nn) = (tA, H*[e(H(w1),C)^t]
            //SCF-MPEKS 

            //S = (M,N1,N2,...,Nn)-(tA,H* [eCH(w1),C)At],H*[e(H(w2),C)^t], ...,H*[e(HCwn),C)^t]) //let n=3, w1=Barclays, w2=Finance, w3-2017

            long SCF_MPEKS_start = System.currentTimeMillis();
            Element t_MPEKS = pairing.getZr().newRandomElement(); //t
            Element M = A.duplicate(); 
            M.mulZn(t_MPEKS); //M

                //H(w1)

        byte[] b1_MPEKS = new byte[10];
        String w1_MPEKS = "Barclays";
        b1_MPEKS = w1_MPEKS.getBytes();
        Element H_w1_MPEKS = pairing.getG1().newElement().setFromHash(b1_MPEKS, 0,b1_MPEKS.length); //H(w1)=H_w1
        //HCw2)

        byte[] b2_MPEKS = new byte[10]; 
        String w2_MPEKS = "Finance";
        b2_MPEKS = w2_MPEKS.getBytes(); 
        Element H_w2_MPEKS = pairing.getG1().newElement().setFromHash(b2_MPEKS, 0, b2_MPEKS.length);
        //H(w3)

            byte[] b3_MPEKS = new byte[10];
            String w3_MPEKS = "2017";
            b3_MPEKS = w3_MPEKS.getBytes();
            Element H_w3_MPEKS = pairing.getG1().newElement().setFromHash(b3_MPEKS, 0, b3_MPEKS.length);//H(w3)=H_w3

            //D1=e(H(w1), C)^t; D2=e(H(W2),C)^t; D3=e(H(W3),C)^t, 
            //D1, D2, D3

            Element D1_MPEKS = pC.pairing(H_w1_MPEKS).powZn(t_MPEKS);
            Element D2_MPEKS = pC.pairing(H_w2_MPEKS).powZn(t_MPEKS);
            Element D3_MPEKS = pC.pairing(H_w3_MPEKS). powZn(t_MPEKS);
             //System.out.println("D3_MPEKS: + D3_MPEKS);

        //System.out.println("D1_MPEKS: " + 01_MPEKS); 
        //System.out.println("D2_MPEKS: + D2_MPEKS);

        //N1, N2, N3

        //Element D1 MPEG1 = D1_MPEKS. duplicate();
        //Element D2_MPEKS1 = D2_MPEKS.duplicate();
        //Element D3_MPEKS1 = D3_MPEKS.duplicate(); 
        byte[] N1_MPEKS = D1_MPEKS.toBytes(); //N1 
        byte[] N2_MPEKS = D2_MPEKS.toBytes(); //N2
        byte[] N3_MPEKS = D3_MPEKS.toBytes(); //N3 
        
        long SCF_MPEKS_end = System.currentTimeMillis();

        System.out.println("SCF-MPEKS(" + j +"]: " + (SCF_MPEKS_end-SCF_MPEKS_start)); 
        int Sum_SCF_MPEKS=0;
        Sum_SCF_MPEKS = (int) (Sum_SCF_MPEKS + (SCF_MPEKS_end-SCF_MPEKS_start));
        
    
    
        //Trapdoor
        //TW = (T1,T2)=[CH(w) XOR e(A, B)^(t*+c), eCA, t*B)]
        // w Barclays

        long Trapdoor_start = System.currentTimeMillis();
        Element t_Trapdoor = pairing.getZr().newRandomElement(); //t* 
        Element tB_Trapdoor = B.duplicate();
        tB_Trapdoor.mulZn(t_Trapdoor); //t*B 
        Element T2 = pA.pairing(tB_Trapdoor); //T2



        //H(w*)
        byte[] b1_Trapdoor = new byte[10];
        String w1_Trapdoor = "2017";
        b1_Trapdoor = w1_Trapdoor.getBytes();
        Element H_w1_Trapdoor = pairing.getG1().newElement().setFromHash(b1_Trapdoor, 0, b1_Trapdoor.length);


        //e(A,B)^(t*+c)
        Element tc = c. duplicate(); 
        tc.add(t_Trapdoor); //t*+c
        Element T1_right = pA.pairing(B).powZn(tc); //e(A,B)^Ct++c) 
            //System.out.println("T1 right " + T1_right);


            //cH(w*)
        Element cHw1 = H_w1_Trapdoor.duplicate();
        cHw1.mulZn(c); //cH(w1*)
        Element chhw1 = cHw1.duplicate();

        //T1=cH(W) XOR e(A,B)^(t*+c) //Element cHw_Trapdoor = cHw.duplicate();
        //Element T1_right_Trapdoor = T1_right.duplicate(); //T1_right_Trapdoor=e(A,B)^(t*+c)

        byte[] tt1 = cHw1.toBytes();
        byte[] tt2 = T1_right.toBytes();
        int temp = 0;
        if(tt1. length <= tt2.length) {
            temp = tt2.length;
        } else {
            temp = tt1.length;
        }

        int[] array = new int[temp]; //T1=cH(w*) XOR e(A,B)^(t*+c) ------array

        for(int i = 0; i < temp; i++){
             int x = (int)tt1[i] ^ (int)tt2[i];
            array[i] = x;
        }

        //Test
        //The server first calculates Twi - T1 XOR T2*e(aB,C)

        long Test_start = System.currentTimeMillis(); 
        Element Twi_right = T2.duplicate();
        //T = e(aB, C)
        Element T = pC.pairing(B).mulZn(a);
        Twi_right.mul(T); //T2*e(B,C) 
        //System.out.println("T2*e(aB,C): " + Twi_right); 
        //Tw1 - T1 XOR T2*e(aB,C)
        //XOR
        byte[] tt5 = Twi_right.toBytes();
        byte[] tt6 = Twi_right.toBytes();
        int temp2 = 0;
        if(tt5.length <= array.length)
        {
            temp2 = array.length;
        }
        else 
        {
            temp2 = tt5.length;
        }
        int[] array2 = new int[temp2]; // T1 = T1 XOR T2*e(aB,C) - cH(w*) ---------array2=cH(w*)
            //System.out.println("array2.length:" + array2.length);       
            
        for(int i = 0; i < temp2; i++)
        {

            int x1 =(int)tt5[i] ^ array[i];    
            array2[i] = x1;    
            //System.out.print(array2[i]);
        }
            //System.out.println();
                
            byte[] cHw1_test1 = tt1;     
        //System.out.println("chwi_test.length: " + chwi_testi.length); 
        for(int i= 0; i < temp2; i++)        
        {
                
            if(cHw1_test1[i]!=array2[i]){
                System.out.println("Wrong!...exit!");
                System.exit(0);
                }        
        }        
        //System.out.println();        
        System.out.println("OK!"); //It means T1=cH(w*)="Barclays"

        //T1-CH(**)="Barclays"

        //Then, the server tests H*[e(Tw1, M/a)]=N1

        //H [e(T1,M/a)]-N1

        Element Tw1_test = chhw1.duplicate();

        Element P_test1 = P.duplicate();
        //Element a_test = a. duplicate(); 
        Element out1 = pairing.pairing(Tw1_test, P_test1.mulZn(t_MPEKS));
            //System.out.println("out1: " + out1); 
        byte[] out1_b = out1.toBytes();
        //If true, H*[e(T1,M/a))=N1

        if(Arrays.equals(out1_b,N2_MPEKS) || Arrays.equals(out1_b,N1_MPEKS) || Arrays.equals(out1_b,N3_MPEKS))
        { 
            System.out.println(" -end");
            System.out.println("--------------N1_MPEKS--------------");
            for(int k = 0; k< N1_MPEKS.length; k++)
            {
                System.out.print(" "+N1_MPEKS[k]);
            }
            System.out.println(" -end");
            System.out.println("--------------N2_MPEKS--------------");
            for(int k = 0; k< N2_MPEKS.length; k++)
            {
                System.out.print(" "+N2_MPEKS[k]);
            }
            System.out.println(" -end");
            System.out.println("--------------N3_MPEKS--------------");
            for(int k = 0; k< N3_MPEKS.length; k++)
            {
                System.out.print(" "+N3_MPEKS[k]);
            }
            System.out.println(" -end");
            System.out.println("--------------out1_b--------------");
            for(int k = 0; k< out1_b.length; k++)
            {
                System.out.print(" "+out1_b[k]);
            }
            System.out.println(" -end");
            System.out.println("  true \n string is matched -----");
        }
        else{
        System.out.println("false\n string is not matched -----");
        }
    }

}
