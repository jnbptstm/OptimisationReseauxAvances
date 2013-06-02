package parallelCTR;

import gnu.crypto.cipher.Rijndael;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.NoSuchPaddingException;

public class Main{

	public static byte[][] cipheredBloc128;
	
	public static void main (String args[]) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InterruptedException{
		
		// Nombre de thread :
		int numberOfThread;
		System.out.println("Nombre de threads désirés? ("+Runtime.getRuntime().availableProcessors()+" conseillés)");
		Scanner sc = new Scanner(System.in);
		numberOfThread = sc.nextInt();
		
		// Initialisation du tableau contenant les threads :
		Thread tabThread[] = new Thread[numberOfThread];
		for(int i=0 ; i<tabThread.length ; i++) {
			tabThread[i] = new Thread();
		}
		
		// Découpage du texte en blocs de 128 bits :
		File plainText = new File("test.txt");
		byte[] blocInf128 = new byte[new FileInputStream(plainText).available() % 16];
		byte[][] blocs128 = Utils.splitFile(plainText, blocInf128);
		cipheredBloc128 = new byte[blocs128.length][blocs128[0].length];
		System.out.println("Fichier decoupé en "+blocs128.length+" blocs de 128 bits + 1 bloc de "+ blocInf128.length+" bytes");
		System.out.println("Soit une taille de " + (blocs128.length * 16 + blocInf128.length) + " octets");
		
		// Contiendra tous les IV incrémentés :
		byte[][] initializationVectorIncremented;
		if(blocInf128.length == 0){ initializationVectorIncremented = Utils.ivsIncremented(blocs128.length, 16); }
		else{initializationVectorIncremented = Utils.ivsIncremented(blocs128.length + 1, 16);}			
		
		/*
		 * On utilise l'algorithme de Rijndael puis on XOR  le résultat avec le 
		 * texte en clair pour obtenir le chiffré en mode CTR.
		 */
		
		// Génération de la clé :
		byte[] secretKeyInByte = new byte[16];
		new SecureRandom().nextBytes(secretKeyInByte);
		
		// Algorithme de chiffrement par bloc :
		Rijndael rijndael = new Rijndael();
		
		Object secretKey = null;
		try {
			secretKey = rijndael.makeKey(secretKeyInByte, 32);
		} catch (InvalidKeyException e1) { e1.printStackTrace(); }

		assert(secretKey != null);
		
		// Lancement des threads de chiffrement :
		boolean threadIsFree = false;
		int numBlocEncours = 0;
		int indiceThreadNotAlive = 0;
		long t1, t2;
		t1 = System.currentTimeMillis();
		
		for(byte[] unBloc : blocs128){
			/* 
			 * 'numberOfThread' threads peuvent être executés en meme temps, il faut attendre la fin des autres sinon.
			 */
			
			/** 
			 * TODO: Cette partie de la gestion des threads est mal optimisée.
			 */
			while(!threadIsFree){
				for(indiceThreadNotAlive = 0 ; indiceThreadNotAlive < tabThread.length && !threadIsFree ; indiceThreadNotAlive++){
					
					if(tabThread[indiceThreadNotAlive].getState() != Thread.State.RUNNABLE && tabThread[indiceThreadNotAlive].getState() != Thread.State.TIMED_WAITING ){
						threadIsFree = true;
					}
				}
			}
			tabThread[indiceThreadNotAlive -1] = new Thread(new ThreadCTR( rijndael,
																		   unBloc,  
																		   secretKey,
																		   initializationVectorIncremented[numBlocEncours] ));
			tabThread[indiceThreadNotAlive - 1].start();
			
			threadIsFree = false;
		}
		
		// On attend que tous les threads se terminent
		for(int i=0 ; i<tabThread.length ; i++){
			try {
				tabThread[i].join();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		t2 = System.currentTimeMillis();
		t2 = t2 - t1;
		System.out.println("Temps chiffrement multi-thread : "+t2);
	}
}
