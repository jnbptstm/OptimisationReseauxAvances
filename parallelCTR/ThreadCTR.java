package parallelCTR;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class ThreadCTR implements Runnable{

	private byte[] plainTextBloc;
	private byte[] initializationVectorIncremented;
	private int numBloc128;
	private byte[] cipheredBlock;
	private SecretKey secretKey;
	private Cipher encryptCipher;
	
	public ThreadCTR(){}
	
	public ThreadCTR(byte[] plainTextBloc, int numBloc128, SecretKey secretKey, byte[] initializationVectorIncremented) throws NoSuchAlgorithmException, NoSuchPaddingException{
		this.plainTextBloc = plainTextBloc;
		this.numBloc128 = numBloc128;
		this.secretKey = secretKey;
		this.initializationVectorIncremented = initializationVectorIncremented;
		this.encryptCipher = Cipher.getInstance("AES/CTR/NoPadding");
	}
	
	@Override
	public void run() {
		System.out.println("Initialisation du cipher...");
		try {
			encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(initializationVectorIncremented));
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
		} catch (InvalidAlgorithmParameterException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Encryption...");
		try {
			cipheredBlock = encryptCipher.doFinal(plainTextBloc);
			System.out.println("cipheredBloc: "+new String(cipheredBlock).toString());
		} catch (IllegalBlockSizeException e1) {
			e1.printStackTrace();
		} catch (BadPaddingException e1) {
			e1.printStackTrace();
		}
		System.out.println("Fin encryption");
		System.out.println("Sauvegarde...");
		Main.writeCipheredBloc(cipheredBlock, numBloc128);
		System.out.println("Fin svg!");
		
	}

}
