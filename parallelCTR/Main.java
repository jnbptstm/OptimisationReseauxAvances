package parallelCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Main{

	public static void main (String args[]) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException{
		
		int N=4; // Number of thread
		Thread tabThread[] = new Thread[N];
		
		// Plaintext is cut in 128-bits parts
		byte[][] blocs128;
		File plainText = new File("test.txt");
		byte[] blocInf128 = new byte[new FileInputStream(plainText).available() % 16];
		blocs128 = Utils.splitFile(plainText, blocInf128);
		// byte[] blocInf128 = new byte[new FileInputStream(file).available() % 16];
		System.out.println("Fichier decoupé en "+blocs128.length+" blocs de 128 bits (=16 bytes) + 1 bloc de "+ blocInf128.length+" bytes");		
		
		// TODO: next step
		// Random 128-bits IV

//		initializationVector[0] = 0;
//		initializationVector[1] = 0;
//		initializationVector[2] = 0;
//		initializationVector[3] = 0;
//		initializationVector[4] = 0;
//		initializationVector[5] = 0;
//		initializationVector[6] = 0;
//		initializationVector[7] = 1;
//		initializationVector[8] = 0;
//		initializationVector[9] = 0;
//		initializationVector[10] = 0;
//		initializationVector[11] = 0;
//		initializationVector[12] = 0;
//		initializationVector[13] = 0;
//		initializationVector[14] = 0;
//		initializationVector[15] = 0;
		
		// Fuuu la conversion avec byte[16]
		byte[][] initializationVectorIncremented = Utils.ivsIncremented(blocs128.length + 1, 8); // Contiendra tous les IV incrémentés.
		
		System.out.println("Les 23 U:");
		for(int i = 0 ; i < initializationVectorIncremented.length ; i++){
			for(int j = 0 ; j < initializationVectorIncremented[i].length ; j++){
				System.out.print(initializationVectorIncremented[i][j]+" ");
			}
			System.out.println();
		}
				
		
		// AES key
		KeyGenerator keyGen = KeyGenerator.getInstance("AES/CTR/NoPadding");
		keyGen.init(128);
		SecretKey aesSecretKey = keyGen.generateKey();
		byte aesSecretKeyEncoded[] = aesSecretKey.getEncoded();
		File cipheredText = new File("cipheredText.txt");
		Cipher encryptCipher = Cipher.getInstance("AES/CTR/NoPadding");
		
		// Executing threads
		int nombreThreadEnCours = 0;
		int numBlocEncours = 0;
		int indiceThreadNotAlive = 0;
		for(byte[] unBloc : blocs128){
			/** 
			 * TODO : on lance un nouveau thread.
			 * N threads peuvent être executés en meme temps, il faut attendre la fin des autres sinon.
			 */
			if(nombreThreadEnCours < N){
				for(indiceThreadNotAlive = 0 ; tabThread[indiceThreadNotAlive].isAlive() ; indiceThreadNotAlive++){}
				
				tabThread[indiceThreadNotAlive] = new Thread(new ThreadCTR( blocs128[numBlocEncours], numBlocEncours, cipheredText, aesSecretKey, encryptCipher ));
				tabThread[indiceThreadNotAlive].run();
				numBlocEncours++;
				
			}
		}
	}
}
