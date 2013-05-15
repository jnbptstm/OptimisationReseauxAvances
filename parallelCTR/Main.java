package parallelCTR;

import gnu.crypto.cipher.Anubis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Main{

	public static void main (String args[]) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException{
		
		int numberOfThread = 6; // Number of thread
//		System.out.println("Nombre de threads désirés? ("+Runtime.getRuntime().availableProcessors()+" conseillés)");
//		Scanner sc = new Scanner(System.in);
//		numberOfThread = sc.nextInt();
		Thread tabThread[] = new Thread[numberOfThread];
		for(int i=0 ; i<tabThread.length ; i++) {
			tabThread[i] = new Thread();
		}
		
		// Plaintext is cut in 128-bits parts
		byte[][] blocs128;
		File plainText = new File("test.txt");
		byte[] blocInf128 = new byte[new FileInputStream(plainText).available() % 16];
		blocs128 = Utils.splitFile(plainText, blocInf128);
		// byte[] blocInf128 = new byte[new FileInputStream(file).available() % 16];
		System.out.println("Fichier decoupé en "+blocs128.length+" blocs de 128 bits (=16 bytes) + 1 bloc de "+ blocInf128.length+" bytes");		
		
		// Contiendra tous les IV incrémentés.
		byte[][] initializationVectorIncremented;
		if(blocInf128.length == 0){ initializationVectorIncremented = Utils.ivsIncremented(blocs128.length, 16); }
		else{initializationVectorIncremented = Utils.ivsIncremented(blocs128.length + 1, 16);}
		
		System.out.println("Les "+ initializationVectorIncremented.length+" U:");
		for(int i = 0 ; i < initializationVectorIncremented.length ; i++){
			for(int j = 0 ; j < initializationVectorIncremented[i].length ; j++){
				System.out.print(initializationVectorIncremented[i][j]+" ");
			}
			System.out.println();
		}
				
		
		/*
		 * We use AES algorithm with ECB mode and then XOR the result with plain text to obtain
		 * CTR mode.
		 */
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey secretKey = keyGen.generateKey();
		byte secretKeyEncoded[] = secretKey.getEncoded();
		File cipheredText = new File("cipheredText.txt");
		Cipher encryptCipher = Cipher.getInstance("AES/ECB/NoPadding");
		
		// Generating secret key
//		Anubis anubis = new Anubis(); // Cipher block algorithm.
		
		// Executing threads
		boolean threadIsFree = false;
		int numBlocEncours = 0;
		int indiceThreadNotAlive = 0;
		for(byte[] unBloc : blocs128){
			/** 
			 * on lance les nouveaux threads.
			 * numberOfThread threads peuvent être executés en meme temps, il faut attendre la fin des autres sinon.
			 */
			while(!threadIsFree){
				for(indiceThreadNotAlive = 0 ; indiceThreadNotAlive < tabThread.length && !threadIsFree ; indiceThreadNotAlive++){
					System.out.println("Thread["+indiceThreadNotAlive+"] : "+tabThread[indiceThreadNotAlive].getState());
					if(tabThread[indiceThreadNotAlive].getState() != Thread.State.RUNNABLE && tabThread[indiceThreadNotAlive].getState() != Thread.State.TIMED_WAITING ){
						System.out.println("IF");
						threadIsFree = true;
					}
				}
			}
			indiceThreadNotAlive--;
			System.out.println("indiceThreadNotAlive: "+indiceThreadNotAlive);
			tabThread[indiceThreadNotAlive] = new Thread(new ThreadCTR( unBloc, 
																		numBlocEncours, 
																		cipheredText,
																		secretKey,
																		encryptCipher,
																		initializationVectorIncremented[numBlocEncours] ));
			tabThread[indiceThreadNotAlive].start();
			for(int jjj=0 ; jjj<5 ; jjj++){
				System.out.println("foo");
			}
			System.out.println("Num bloc en cours: "+numBlocEncours++);
			threadIsFree = false;
		}
	}
}
