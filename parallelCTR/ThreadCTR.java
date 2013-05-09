package parallelCTR;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class ThreadCTR implements Runnable{

	private byte[] plainTextBloc;
	private int numBloc128;
	private File cipheredText;
	private SecretKey aesSecretKey;
	private Cipher encryptCipher;
	
	public ThreadCTR(){}
	
	public ThreadCTR(byte[] plainTextBloc, int numBloc128, File cipheredText, SecretKey aesSecretKey, Cipher encryptCipher){
		this.plainTextBloc = plainTextBloc;
		this.numBloc128 = numBloc128;
		this.cipheredText = cipheredText;
		this.aesSecretKey = aesSecretKey;
		this.encryptCipher = encryptCipher;
	}
	
	@Override
	public void run() {
		
	}

}
