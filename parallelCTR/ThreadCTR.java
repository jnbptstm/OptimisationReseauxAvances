package parallelCTR;

import gnu.crypto.cipher.Rijndael;

public class ThreadCTR implements Runnable{

	private byte[] plainTextBloc;
	private byte[] initializationVectorIncremented;
	private byte[] cipheredBlock = new byte[16];
	private Object secretKey;
	private Rijndael rijndael;
	
	public ThreadCTR(){}
	
	public ThreadCTR(Rijndael rijndael, byte[] plainTextBloc, Object secretKey, byte[] initializationVectorIncremented) {
		this.plainTextBloc = plainTextBloc;
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
		
		// Chiffrement par block :
		rijndael.encrypt(initializationVectorIncremented, 0, cipheredBlock, 0, secretKey, 16);
		assert(cipheredBlock.length > 0);
		assert(cipheredBlock.length == plainTextBloc.length);
		
		// XOR :
		for(int i = 0 ; i < plainTextBloc.length ; i++){
			cipheredBlock[i] = (byte) (cipheredBlock[i] ^ plainTextBloc[i]);
		}
	}
}
