package parallelCTR;

import gnu.crypto.cipher.Rijndael;

public class ThreadCTR implements Runnable{

	private byte[] plainTextBloc;
	private byte[] initializationVectorIncremented;
	private int numBloc128;
	private byte[] cipheredBlock = new byte[16];
	private Object secretKey;
	private Rijndael rijndael;
	
	public ThreadCTR(){}
	
	public ThreadCTR(Rijndael rijndael, byte[] plainTextBloc, int numBloc128, Object secretKey, byte[] initializationVectorIncremented) {
		this.plainTextBloc = plainTextBloc;
		this.numBloc128 = numBloc128;
		this.secretKey = secretKey;
		this.initializationVectorIncremented = initializationVectorIncremented;
		this.rijndael = rijndael;
	}
	
	@Override
	public void run() {
		assert(rijndael != null);
		assert(plainTextBloc != null);
		assert(secretKey != null);
		assert(cipheredBlock != null);
		
		// Cipher block:
		rijndael.encrypt(initializationVectorIncremented, 0, cipheredBlock, 0, secretKey, 16);
		assert(cipheredBlock.length > 0);
		assert(cipheredBlock.length == plainTextBloc.length);
		
		// XOR:
		for(int i = 0 ; i < plainTextBloc.length ; i++){
			cipheredBlock[i] = (byte) (cipheredBlock[i] ^ plainTextBloc[i]);
		}
		
//		System.out.println("Fin encryption");
//		System.out.println("Plaintext: ");
//		for(int i=0 ; i<plainTextBloc.length ; i++){
//			System.out.print(plainTextBloc[i]);
//		}
//		System.out.println("\nCiphertext: ");
//		for(int i=0 ; i<cipheredBlock.length ; i++){
//			System.out.print(cipheredBlock[i]);
//		}
//		System.out.println("\nSauvegarde...");
//		Main.writeCipheredBloc(cipheredBlock, numBloc128);
//		System.out.println("Fin svg!");
		
	}

}
