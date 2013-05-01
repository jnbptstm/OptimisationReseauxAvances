package parallelCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Main{

	public static void main (String args[]) throws NoSuchAlgorithmException, IOException{
		
		int N=4; // Number of thread
		Thread tabThread[] = new Thread[N];
		
		// Plaintext is cut in 128-bits parts
		ArrayList<byte[]> blocs128 = new ArrayList<byte[]>();
		ArrayList<Byte> listBlocInf128 = new ArrayList<Byte>();
		byte[] blocInf128;
		byte[] tmp = new byte[16];
		FileInputStream fis = new FileInputStream(new File("test.txt"));
		
		while(fis.read(tmp) == 16){
			blocs128.add(tmp);
			for(int i=0 ; i<tmp.length ; i++)
				tmp[i] = '\0';
		}
		for(int i=0 ; tmp[i] != '\0' ; i++){
			listBlocInf128.add(tmp[i]);
		}
		blocInf128 = new byte[listBlocInf128.size()];
		for(int i=0 ; i<listBlocInf128.size() ; i++){
			blocInf128[i] = listBlocInf128.get(i);
		}
		System.out.println("Fichier decoupé en "+blocs128.size()+" blocs de 128 bits (=16 bytes) + 1 bloc de "+ blocInf128.length+" bytes");
	
		//TODO: next step
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
		for(byte[] unBloc : blocs128){
			/** 
			 * TODO : on lance un nouveau thread.
			 * N threads peuvent être executés en meme temps, il faut attendre la fin des autres sinon.
			 */
		}
	}
}
