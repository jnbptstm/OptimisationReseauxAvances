package parallelCTR;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Main{

	public static void main (String args[]) throws NoSuchAlgorithmException{
		
		int N=4; // Number of thread
		Thread tabThread[] = new Thread[N];
		
		// Plaintext is cut in 128-bits parts
		ArrayList<char[]> blocs128 = new ArrayList<char[]>();
		
		
		
		// Random 128-bits IV
		byte initializationVector[] = new byte [16];
		new SecureRandom().nextBytes(initializationVector);
		
		// AES key
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecretKey aesSecretKey = keyGen.generateKey();
		byte aesSecretKeyEncoded[] = aesSecretKey.getEncoded();
		
		// Executing threads
		int i=1;
		int nombreThreadEnCours=0;
		for(char[] unBloc : blocs128){
			/** 
			 * TODO : on lance un nouveau thread.
			 * N threads peuvent être executés en meme temps, il faut attendre la fin des autres sinon.
			 */
		}
	}
}
