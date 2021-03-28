#include <pbc/pbc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <gmp.h>
#include <time.h>
#include <assert.h>
#include<bits/stdc++.h>
using namespace std;
typedef struct setup_output {

	mpz_t k; 

	char * type;
	mpz_t q;
	mpz_t h;
	mpz_t r;
	mpz_t exp2;
	mpz_t exp1;
	mpz_t sign1;
	mpz_t sign0;

	pairing_t pairing; 
	pbc_param_t par;
	element_t identity,identity_gt;
	element_t g1,g2,gt;
	element_t P;
	element_t master_key_lamda;
	element_t PKc,SKc;


}setup_result;



typedef struct Client_key {

	//-------------------
	// this send by KGC 
	char * IDu;  //client_id
	element_t Qu; //hash of id
	element_t Du;  //partial private key


	//------------------------
	// this generated locally

	element_t Su;   	//secret value for client

	element_t SKu1,SKu2,SKu; 

					// Private key of client is SKu
	
	element_t PKu,PKu1,PKu2;

					//Public key of client is PKu
}client;

typedef struct cipher_word {
	element_t Ui;
	mpz_t Vi;
}cipher;

typedef struct trapdoor_word {
	element_t T1;
	element_t T2;
	element_t T3;
	string stringT2;
	string stringT3;	
}trapdoor;

// globle parameters.
setup_result globle_setup;
client sender,receiver;
cipher CPwi;
trapdoor search_word;

void hash2_ascii(string str,mpz_t & ret,mpz_t q)   // 
{
	int l = str.length();
	int convert;
	mpz_t sum;
	mpz_init(sum);
	for (int i = 0; i < l; i++)
	{
		convert = str[i] - NULL;
		mpz_add_ui(sum,sum,convert);
	}	
	mpz_init(ret);
	int exp = 1;
	mpz_powm_ui(ret,sum,exp,q);
}

void generate_keys_sender_receiver(client * user,string userid)
{

	//ePPK       should run by KGC
	{
		
		user->IDu = &userid[0];
  		element_init_G1(user->Qu,globle_setup.pairing);
		int len = userid.size();
		element_from_hash(user->Qu, user->IDu, len);
		printf("\nthe client id is = %s\n",user->IDu);	
		element_printf("client hash of id Qu = %B\n", user->Qu);	
		

		element_init_G1(user->Du,globle_setup.pairing);
		element_mul_zn(user->Du,user->Qu,globle_setup.master_key_lamda);
		element_printf("The  partial private key  is Du = %B\n", user->Du);

	}

	//sSV         Should run by client
	
	{
		element_init_Zr(user->Su,globle_setup.pairing);
		element_random(user->Su);
  		element_printf("The  secret value for client u Su = %B\n", user->Su);
	}
	//gPriK
	{
		
		element_init_Zr(user->SKu1,globle_setup.pairing);
		element_init_G1(user->SKu2,globle_setup.pairing);

		element_set(user->SKu1,user->Su);
		element_mul_zn(user->SKu2,user->Du,user->Su);
		element_printf("The value of  SKu1,SKu2 is = %B , %B\n", user->SKu1,user->SKu2);
		element_printf("The Private key of client is SKu= (%B , %B)\n", user->SKu1, user->SKu2);
	}
	//gPubK
	{
		element_init_G1(user->PKu1,globle_setup.pairing);
		element_init_G1(user->PKu2,globle_setup.pairing);

		element_mul_zn(user->PKu1,globle_setup.P,user->Su);
		element_mul_zn(user->PKu2,globle_setup.PKc,user->Su);
		
		element_printf("The value of  PKu1,PKu2 is = %B , %B\n", user->PKu1,user->PKu2);
		element_printf("The Public key of client is PKu= (%B , %B)\n\n", user->PKu1,user->PKu2);
	}

}

void setup( ) 
{
	// mpz_t k;
	// mpz_init(k);

	// mpz_set(k, security_parameter);
	// mpz_t q; 
	// mpz_init(q);
	// random_prime_bits(q, k);
	// gmp_printf("Order of group G1, GT is q: %Zd\n", q);
	
	pairing_t pairing; 
	pbc_param_t par;
	// pbc_param_init_a1_gen(par, q);
	mpz_t rb; 
	mpz_init(rb);
	int rbits=7;
	mpz_set_ui(rb,rbits);
	int qbits=20;
	pbc_param_init_a_gen(globle_setup.par,rbits,qbits);
	pairing_init_pbc_param(globle_setup.pairing, globle_setup.par);
	pairing_init_pbc_param(pairing, globle_setup.par);
	pbc_param_out_str(stdout, globle_setup.par);


	// writing in the file.

	FILE *stream;
	stream = fopen("a_param.txt", "w+");
	pbc_param_out_str(stream, globle_setup.par);
	fclose(stream);


	//reading from the file.
	FILE *reading;
   	char buff[1024];

	reading = fopen("a_param.txt", "r");
    fscanf(reading, "%s", buff);
    fgets(buff, 1024, (FILE*)reading);
	globle_setup.type= buff;
	fgets(buff, 1024, (FILE*)reading);
	char * q= buff+2;
	mpz_init(globle_setup.q);
	mpz_init_set_str(globle_setup.q,q,10);

	fgets(buff, 1024, (FILE*)reading);
	char * h= buff+2;
	mpz_init(globle_setup.h);
	mpz_init_set_str(globle_setup.h,h,10);

	fgets(buff, 1024, (FILE*)reading);
	char * r= buff+2;
	mpz_init(globle_setup.r);
	mpz_init_set_str(globle_setup.r,r,10);

	fgets(buff, 1024, (FILE*)reading);
	char * exp2= buff+5;
	mpz_init(globle_setup.exp2);
	mpz_init_set_str(globle_setup.exp2,exp2,10);

	fgets(buff, 1024, (FILE*)reading);
	char * exp1= buff+5;
	mpz_init(globle_setup.exp1);
	mpz_init_set_str(globle_setup.exp1,exp1,10);

	fgets(buff, 1024, (FILE*)reading);
	char * sign1= buff+6;
	mpz_init(globle_setup.sign1);
	mpz_init_set_str(globle_setup.sign1,sign1,10);

	fgets(buff, 1024, (FILE*)reading);
	char * sign0= buff+6;
	mpz_init(globle_setup.sign0);
	mpz_init_set_str(globle_setup.sign0,sign0,10);


	element_t g1, g2, gt,temp1,temp2,p, e, identity, identity_gt,store_gen;

	element_init_G1(g1, pairing);
	element_init_G1(temp1, pairing);
	element_init_G1(temp2, pairing);
	element_init_G2(g1, pairing);
	element_init_GT(g2, pairing);
	element_init_GT(gt, pairing);
	element_init_G1(identity, pairing);
	element_init_GT(identity_gt, pairing);
	element_init_GT(store_gen, pairing);
	element_set0(identity);
	element_set0(identity_gt);
	element_random(g1);


	element_init_G1(globle_setup.g1,pairing);
	element_set(globle_setup.g1,g1);

	element_printf("Element of G1 group g1: %B\n", g1);

	element_init_GT(globle_setup.g2,pairing);
	element_set(globle_setup.g2,g2);

	element_printf("Element of G2 group g2: %B\n", g2);

	element_pairing(gt,g1,g1);
	
	element_init_GT(globle_setup.gt,pairing);
	element_set(globle_setup.gt,gt);

	element_printf("Applying bilinear pairing on g1 and g1, g2: %B\n", gt);
	// element_to_mpz(rb,gt);
	// gmp_printf("the depcption --- of the gt is  q: %Zd\n", rb);

	// element_t temp;
	// element_init_GT(temp,pairing);
	// long int tempint = 12;
	// element_set_si(temp,tempint);
	// element_printf("-------------------Tempeture : %B\n", temp);

	/*do {
		element_random(temp1);	
		element_pow_mpz(temp2, temp1, rb);
		if (element_cmp(temp2, identity) == 0) {
			//element_sword wet(store_gen,temp2);
			break;
		}
	} while(1);*/ 
	//element_printf("Applying bilinear pairing on g1 and g1, g2: %B\n temp1 = %B\n temp2= %B", gt,temp1,temp2);
	if (pairing_is_symmetric(pairing) )
	{
		printf("pairing is symmetric G1 AND G2 are same \n");
	}

  	element_t P,master_key_lamda,PKc,SKc;

	element_init_G1(P,pairing);
	element_init_G1(PKc,pairing);
	element_init_Zr(SKc,pairing);
	element_random(P);

	element_init_G1(globle_setup.P,pairing);
	element_set(globle_setup.P,P);

	element_init_Zr(master_key_lamda, pairing);
	element_random(master_key_lamda);

	element_init_Zr(globle_setup.master_key_lamda,pairing);
	element_set(globle_setup.master_key_lamda,master_key_lamda);
	
	element_mul_zn(PKc,P,master_key_lamda);

	element_set(SKc,master_key_lamda);

	element_init_G1(globle_setup.PKc,pairing);
	element_set(globle_setup.PKc,PKc);
	element_init_Zr(globle_setup.SKc,pairing);
	element_set(globle_setup.SKc,master_key_lamda);


	element_printf("Random element from the group g1 P is : %B\n", P);
	element_printf("Master key lamda is : %B\n", master_key_lamda);
	element_printf(" PKc is : %B\n", PKc);
	element_printf(" SKc is : %B\n", SKc);
	


	printf("\n printing sender key \n");
	generate_keys_sender_receiver(&sender,"senderid");
	printf("\n printing receiver key \n");
	generate_keys_sender_receiver(&receiver,"reciverid");


	// printing whole group

	// element_t addgroup;
	// element_init_G1(addgroup,pairing);
	// printf("elements in G1 :\n");
	// int i=0;
	// element_add(addgroup,addgroup,g1);
	// while(element_cmp(addgroup, identity)!=0){
	// 	element_printf("i= %d the element = %B  \n",i++, addgroup);
	// 	element_pow_mpz(temp2, addgroup, rb);
	// 	if (element_cmp(temp2, identity_gt) == 0) {
	// 		element_pairing(gt,addgroup,g1);
	// 	}
	// 	element_add(addgroup,addgroup,g1);

	// }
	// i=0;
	// //element_pairing(gt,temp1,g1);
	// element_set(store_gen, gt);
	//  element_printf(" Group GT- \n i= %d the element = %B  \n",i++, gt);
	//  element_mul(gt, store_gen, gt);
	//  while(element_cmp(gt, identity_gt)) {
	//  	element_printf("i= %d the element = %B  \n",i++, gt);
	//  	element_mul(gt, store_gen, gt);
	//  }	


	// for( int  i= 0 ;i<100;i++)
	// {
	// 	//pairing elment is g1
	// 	element_add(addgroup,addgroup,g1); 

	// 	element_printf("i= %d the element = %B  \n",i, addgroup);
	// }

}

void CLPEKS(){

		//checking

		element_t pair_sender_PKc,pair_sender_P;
		element_init_GT(pair_sender_PKc,globle_setup.pairing);
		element_init_GT(pair_sender_P,globle_setup.pairing);

		element_pairing(pair_sender_PKc, sender.PKu1,globle_setup.PKc);
		element_pairing(pair_sender_P, sender.PKu2,globle_setup.P);

		if(element_cmp(pair_sender_PKc,pair_sender_P)==0)
		{
			printf("pairing equal for sender\n");
		}
		else
		{
			printf("fail turn ⊥ and about\n");
		}

		element_t pair_receiver_PKc,pair_receiver_P;
		element_init_GT(pair_receiver_PKc,globle_setup.pairing);
		element_init_GT(pair_receiver_P,globle_setup.pairing);

		element_pairing(pair_receiver_PKc, sender.PKu1,globle_setup.PKc);
		element_pairing(pair_receiver_P, sender.PKu2,globle_setup.P);

		if(element_cmp(pair_receiver_PKc,pair_receiver_P)==0)
		{
			printf("\npairing equal for receiver\n");
		}
		else
		{
			printf("\nfail turn ⊥ and about\n");
		}
		// mpz_t aa;
		// hash2_ascii("return",aa,globle_setup.q);
		// cout<< "ajlslf"<<aa<<endl;

		string word="wordto_encrption";
		element_t Ri;
		element_init_Zr(Ri,globle_setup.pairing);
		element_random(Ri);
		element_t Qr,Qs;
		element_init_G1(Qr,globle_setup.pairing);
		element_init_G1(Qs,globle_setup.pairing);
		element_set(Qr,receiver.Qu);
		element_set(Qs,sender.Qu);

		element_t Ti,pair1,pair2,pair3,first,hash3_word;
		element_init_GT(Ti,globle_setup.pairing);
		element_init_GT(pair1,globle_setup.pairing);
		element_init_GT(pair2,globle_setup.pairing);
		element_init_GT(pair3,globle_setup.pairing);
		element_init_G1(first,globle_setup.pairing);
		element_init_G1(hash3_word,globle_setup.pairing);

		mpz_t rethash;
		hash2_ascii(word,rethash,globle_setup.q);
		element_t hash;

		element_init_Zr(hash,globle_setup.pairing);
		element_set_mpz(hash,rethash);

		element_mul_zn(first,Qr,hash);
		element_mul_zn(first,first,Ri);

		element_pairing(pair1,first,receiver.PKu2);

		element_init_G1(first,globle_setup.pairing);

		element_mul_zn(first,Qs,Ri);
		element_pairing(pair2,first,sender.PKu2);

		element_init_G1(first,globle_setup.pairing);
		int len_word = word.size();
		char * w = &word[0];
		element_from_hash(hash3_word,w,len_word);
		element_mul_zn(first,hash3_word,Ri);

		element_pairing(pair3,first,globle_setup.P);

		element_mul(Ti,pair1,pair2);
		element_mul(Ti,Ti,pair3);

		element_t Ui;
		element_init_G1(Ui,globle_setup.pairing);
		element_mul_zn(Ui,globle_setup.P,Ri);

		mpz_t Vi;
		mpz_init(Vi);
		element_to_mpz(Vi,Ti);  //hash4
		
		
		element_init_G1(CPwi.Ui,globle_setup.pairing);
		element_set(CPwi.Ui,Ui);

		mpz_set(CPwi.Vi,Vi);

		element_printf("\n cyphter text for word is  : [ %B , %Zd ]\n", Ui, Vi);
}
void Trapdoor()
{

		element_t T1,T2,T3;
		element_init_G1(T1,globle_setup.pairing);
		element_init_G1(T2,globle_setup.pairing);
		element_init_G1(T3,globle_setup.pairing);

		element_t local_master_key_lamda;
		element_init_Zr(local_master_key_lamda,globle_setup.pairing);
		element_set( local_master_key_lamda, globle_setup.master_key_lamda );
		element_mul_zn( T1 , globle_setup.P , local_master_key_lamda );

		element_t first,second;
		element_init_G1(first,globle_setup.pairing);
		element_init_G1(second,globle_setup.pairing);

		string word="wordto_encrption";
		mpz_t rethash;
		hash2_ascii(word,rethash,globle_setup.q);
		
		element_t hash;
		element_init_Zr(hash,globle_setup.pairing);
		element_set_mpz(hash,rethash);
		element_mul_zn(first,receiver.SKu2,hash);
		element_mul_zn(second,sender.PKu1, local_master_key_lamda );
		unsigned char * charfirst;
		int lenghtf = element_to_bytes(charfirst,first);
		unsigned char * charsecond;
		int lenghts = element_to_bytes(charsecond,second);
		unsigned char * result;
		int maxlen = max(lenghtf,lenghts);
		for( int i = 0 ;i< maxlen; i++)
		{
			char ch = (char)charfirst[i] ^ (char) charsecond[i]; 
			result[i] = ch;
		}
		
		element_add(T2,first,second);

		element_t hash3_word;
		element_init_G1(hash3_word,globle_setup.pairing);


		char * w = &word[0];
		int len_word = word.size();
		element_from_hash(hash3_word,w,len_word);
		unsigned char * hash3_char;
		unsigned char * T3char;
		int lenghth = element_to_bytes(hash3_char,hash3_word);
		int len_T3 = max( lenghth , lenghts );
		for( int i = 0 ;i< len_word; i++)
		{
			char ch = (char)hash3_char[i] ^ (char) charsecond[i]; 
			T3char[i] = ch;
		}
		for( int i = 0 ;i< len_word; i++)
		{
			 printf("%02x ,", T3char[i]);
		}
		element_add(T3,hash3_word,second);

		element_init_G1(search_word.T1,globle_setup.pairing);
		element_init_G1(search_word.T2,globle_setup.pairing);
		element_init_G1(search_word.T3,globle_setup.pairing);

		element_set(search_word.T1,T1);
		element_set(search_word.T2,T2);
		string strt2(reinterpret_cast< char const* >(result) );
		string strt3(reinterpret_cast< char const* >(T3char) );
		search_word.stringT2 = strt2;
		search_word.stringT3 = strt3;
		element_set(search_word.T3,T3);

}
void Test()
{
		element_t T2dash;
		element_t T3dash;
		element_t pair;
		element_t first,second;
		element_t SKs1T1;
		

		element_init_G1(SKs1T1,globle_setup.pairing);
		element_init_GT(pair,globle_setup.pairing);
		element_mul_zn(SKs1T1 , search_word.T1, sender.SKu1 );

		element_init_G1(T2dash,globle_setup.pairing);
		element_init_G1(T3dash,globle_setup.pairing);

		unsigned char * SKs1T1char;
		int lengthSKs1T1 = element_to_bytes( SKs1T1char , SKs1T1 );
		int lenT2 = search_word.stringT2.size();
		int lenmax = max( lengthSKs1T1 , lenT2 );
		unsigned char * T2dashchar;
		for( int i = 0 ; i< lenmax ; i++)
		{
				char ch = (char)SKs1T1char[i] ^ (char) search_word.stringT2[i]; 
				T2dashchar[i] = ch;
		}
		int lenT3 = search_word.stringT3.size();
		lenmax = max( lengthSKs1T1 , lenT3 );
		unsigned char * T3dashchar;
		for( int i = 0 ; i< lenmax ; i++)
		{
				char ch = (char)SKs1T1char[i] ^ (char) search_word.stringT3[i]; 
				T3dashchar[i] = ch;
		}
		element_from_bytes( T2dash , T2dashchar );
		element_from_bytes( T3dash , T3dashchar );
		// element_add(T2dash,search_word.T2,SKs1T1);
		// element_add(T3dash,search_word.T3,SKs1T1);
		element_printf("\n T2dash , T3dash : ( %B , %B )\n", T2dash , T3dash );



		element_init_G1(first,globle_setup.pairing);
		
		element_add(first, T2dash , sender.SKu2 );
		element_add(first, first , T3dash );

		element_add(first, T2dash,sender.SKu2);
		element_pairing(pair, first , CPwi.Ui );
		
		mpz_t hash4pair;
		mpz_init(hash4pair);

		element_to_mpz(hash4pair,pair);  //hash4

		cout<< "\n compair with second value of cypher text "<<hash4pair<<endl;
				
		
		if(mpz_cmp( hash4pair, CPwi.Vi )==0 ){
			printf("\n Yes !!! \nsuccessfully  find the word\n");
		}
		else
		{
			printf("\n oh No oh NO oh No !!! \nsuccessfully didn't find  the word\n");
		}

}
int main () 
{
	
	setup();
	CLPEKS();
	Trapdoor();
	Test();
	return 0;
}