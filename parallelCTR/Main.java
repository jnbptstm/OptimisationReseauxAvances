package parallelCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Main{

	public static byte[][] cipheredBloc128;
	
	public static void main (String args[]) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException{
		
		int numberOfThread; // Number of thread
		System.out.println("Nombre de threads désirés? ("+Runtime.getRuntime().availableProcessors()+" conseillés)");
		Scanner sc = new Scanner(System.in);
		numberOfThread = sc.nextInt();
		Thread tabThread[] = new Thread[numberOfThread];
		for(int i=0 ; i<tabThread.length ; i++) {
			tabThread[i] = new Thread();
		}
		
		// Plaintext is cut in 128-bits parts
		byte[][] blocs128;
		File plainText = new File("test.txt");
		byte[] blocInf128 = new byte[new FileInputStream(plainText).available() % 16];
		blocs128 = Utils.splitFile(plainText, blocInf128);
		cipheredBloc128 = new byte[blocs128.length][blocs128[0].length];
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
		File cipheredText = new File("cipheredText.txt");
		
		//Anubis anubis = new Anubis(); // Cipher block algorithm.
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
						threadIsFree = true;
					}
				}
			}
			indiceThreadNotAlive--;
			System.out.println("indiceThreadNotAlive: "+indiceThreadNotAlive);
			tabThread[indiceThreadNotAlive] = new Thread(new ThreadCTR( unBloc, 
																		numBlocEncours, 
																		secretKey,
																		initializationVectorIncremented[numBlocEncours] ));
			tabThread[indiceThreadNotAlive].start();
			
			System.out.println("Num bloc en cours: "+numBlocEncours++);
			threadIsFree = false;
		}
		
		// On attend que tous les threads se terminent
		for(int i=0 ; i<tabThread.length ; i++){
			try {
				tabThread[i].join();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		System.out.println("FIN ENCRYPTION, AFFICHAGE DONNEES CRYPTEES:");
		Cipher deciph = Cipher.getInstance("AES/CTR/NoPadding");
		
		FileOutputStream fos = new FileOutputStream(cipheredText);
		for(int i=0 ; i<cipheredBloc128.length ; i++){
			
			try {deciph.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(initializationVectorIncremented[i]));
			} catch (InvalidKeyException e) {}
			catch (InvalidAlgorithmParameterException e) {}
			try {
				System.out.println("Decrypted: "+new String(deciph.doFinal(cipheredBloc128[i])).toString());
			} catch (IllegalBlockSizeException e) {
			} catch (BadPaddingException e) {}
			fos.write(cipheredBloc128[i]);
		}
	}
	
	public static void writeCipheredBloc(byte[] cipheredBlock, int numBloc128){
		System.out.println("Ecriture du chiffré...");
		cipheredBloc128[numBloc128] = cipheredBlock;
		System.out.println("Fin écriture du chiffré...");
	}
}
