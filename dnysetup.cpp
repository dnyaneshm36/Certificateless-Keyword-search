#include <pbc/pbc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <gmp.h>
#include <time.h>
#include <assert.h>
#include <fstream>
using namespace std;
typedef struct setup_output {

	mpz_t k; 

	mpz_t q; //prime number



	pairing_t pairing; 
	pbc_param_t par;
	element_t identity,identity_gt;
	element_t g1,g2;
	element_t P,PKc,SKc;

	char * IDu;  //client_id
	element_t Qu; //hash of id
	element_t Du;  //partial private key

	element_t Su;   	//secret value for client

	element_t SKu1,SKu2,SKu; 

					// Private key of client is SKu
	
	element_t PKu,PKu1,PKu2;

					//Public key of client is PKu

}setup_result;

void random_prime_bits(mpz_t result, mpz_t n) 
{
	mpz_t bits;
	mpz_init(bits);
	gmp_randstate_t state;
	gmp_randinit_default(state);
	gmp_randseed_ui(state, (rand()+1)*(rand()+1));
	if (mpz_cmp_ui(n,1) <= 0) 
	{
		printf("NO PRIME EXISTS\n");
	} 
	else 
	{
		mpz_t lower_limit;
		mpz_init(lower_limit);
		mpz_ui_pow_ui(lower_limit, 2, mpz_get_ui(n)-1);
		while (1)
		{
			mpz_urandomb(bits, state ,mpz_get_ui(n));
			if (mpz_cmp(bits, lower_limit) > 0 && mpz_probab_prime_p(bits,mpz_get_ui(n))) 
			{
				mpz_set(result,bits);	
				break;
			}
		}			
	}
}

void set_globle_variable()
{

}
void setup(mpz_t security_parameter) 
{
	 mpz_t rb; 
	 mpz_init(rb);
	// mpz_set(k, security_parameter);
	 mpz_t cl; 
	 mpz_init(cl);
	// random_prime_bits(q, k);
	// gmp_printf("Order of group G1, GT is q: %Zd\n", q);
	
	pairing_t pairing; 
	pbc_param_t par;
	// pbc_param_init_a1_gen(par, q);
	int rbits=5;
	mpz_set_ui(rb,rbits);
	int qbits=20;
	pbc_param_init_a_gen(par,rbits,qbits);
	pairing_init_pbc_param(pairing, par);
	
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

	element_printf("Element of G1 group g1: %B\n", g1);

	element_printf("Element of G2 group g2: %B\n", g2);

	element_pairing(gt,g1,g1);
	
	element_printf("Applying bilinear pairing on g1 and g1, g2: %B\n", gt);

	/*do {
		element_random(temp1);	
		element_pow_mpz(temp2, temp1, rb);
		if (element_cmp(temp2, identity) == 0) {
			//element_set(store_gen,temp2);
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

	element_init_Zr(master_key_lamda, pairing);
	element_random(master_key_lamda);
	
	element_mul_zn(PKc,P,master_key_lamda);

	element_set(SKc,master_key_lamda);

	element_printf("Random element from the group g1 P is : %B\n", P);
	element_printf("Master key lamda is : %B\n", master_key_lamda);
	element_printf(" PKc is : %B\n", PKc);
	element_printf(" SKc is : %B\n", SKc);
	
	//ePPK
	element_t Du;
	element_t Qu;
	{
		char * IDu= "idofclient"; //client_id
  		element_init_G1(Qu,pairing);
		element_from_hash(Qu, IDu, 10);
		element_printf("client hash of id = %B\n", Qu);
		element_to_mpz(cl,Qu);
		gmp_printf("cl:  %Zd\n", cl);

		char * IDuu= "1"; //client_id
  		element_init_G1(Qu,pairing);
		element_from_hash(Qu, IDuu, 10);
		element_printf("client hash of id = %B\n", Qu);	
		
		element_init_G1(Du,pairing);

		element_mul_zn(Du,Qu,master_key_lamda);
		element_printf("The  partial private key  is Du = %B\n", Du);
	}
	//sSV
		element_t Su;
		element_init_Zr(Su,pairing);
	{
		element_random(Su);
  		element_printf("The  secret value for client u Su = %B\n", Su);
	}
	//gPriK
	element_t SKu1,SKu2,SKu;
	{
		
		element_init_Zr(SKu1,pairing);
		element_init_G1(SKu2,pairing);

		element_set(SKu1,Su);
		element_mul_zn(SKu2,Du,Su);
		element_printf("The value of  SKu1,SKu2 is = %B , %B\n", SKu1,SKu2);
		element_printf("The Private key of client is SKu= (%B , %B)\n", SKu1, SKu2);
	}
	//gPubK
	element_t PKu,PKu1,PKu2;
	{
		element_init_G1(PKu1,pairing);
		element_init_G1(PKu2,pairing);

		element_mul_zn(PKu1,P,Su);
		element_mul(PKu2,PKc,Su);
		
		element_printf("The value of  PKu1,PKu2 is = %B , %B\n", PKu1,PKu2);
		element_printf("The Public key of client is PKu= (%B , %B)\n\n", PKu1,PKu2);
	}

	// printing whole group
	element_t addgroup;
	element_init_G1(addgroup,pairing);
	printf("elements in G1 :\n");
	int i=0;
	element_add(addgroup,addgroup,g1);
	while(element_cmp(addgroup, identity)!=0){
		element_printf("i= %d the element = %B  \n",i++, addgroup);
		element_pow_mpz(temp2, addgroup, rb);
		if (element_cmp(temp2, identity_gt) == 0) {
			element_pairing(gt,addgroup,g1);
		}
		element_add(addgroup,addgroup,g1);
		
	}
	i=0;
	//element_pairing(gt,temp1,g1);
	element_set(store_gen, gt);
	 element_printf(" Group GT- \n i= %d the element = %B  \n",i++, gt);
	 element_mul(gt, store_gen, gt);
	 while(element_cmp(gt, identity_gt)) {
	 	element_printf("i= %d the element = %B  \n",i++, gt);
	 	element_mul(gt, store_gen, gt);
	 }	
	//to output parameters in a.param file 
	FILE *afile;
   	afile= fopen("a.param", "w");
	pbc_param_out_str(stdout, par);
	pbc_param_out_str(afile, par);


}

int main () 
{
	mpz_t security_parameter;
	mpz_init(security_parameter);
	mpz_set_ui(security_parameter, 32);
	setup(security_parameter);
	return 0;
}