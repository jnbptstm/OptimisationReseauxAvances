package parallelCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class Utils {

	/**
	 * @param file the file to split in 128-bits (16 bytes) parts.
	 * @param blocInf128 will contain the last bytes (<16) of the file.
	 * @return A byte[][] that contains n parts of the split file.
	 * @throws IOException
	 */
	public static byte[][] splitFile(File file, byte[] blocInf128) throws IOException{
		
		int fileLengthInBytes, nombreBloc128;
		byte tmp[] = new byte[16];
		FileInputStream fis = new FileInputStream(file);
		fileLengthInBytes = fis.available();
		//System.out.println("File length: "+fileLengthInBytes+" bytes");
		nombreBloc128 = fileLengthInBytes / 16;
		//System.out.println("Number of 128-bits blocs: "+nombreBloc128);
		byte[][] bloc128 = new byte[nombreBloc128][16];
		
		for(int i=0 ; i<nombreBloc128 ; i++){
			fis.read(tmp);
			for(int j=0 ; j<tmp.length ; j++){
				bloc128[i][j] = tmp[j];
				//System.out.print(bloc128[i][j]+" ");
				tmp[j] = '\0';
			}
			//System.out.println("\n"+ bloc128[i].length+" "+new String(bloc128[i], "UTF-8")+"\n");
		}
		fis.read(blocInf128);
		fis.close();
		//System.out.println("\n"+blocInf128.length+" "+ new String(blocInf128, "UTF-8")+"\n");
		return bloc128;
	}
	
	
	public static byte[][] ivsIncremented(int numberOfIv, int lengthOfIv){
		
		byte[][] initializationVectorIncremented = new byte[numberOfIv][lengthOfIv];
		byte initializationVector[] = new byte [lengthOfIv];
		new SecureRandom().nextBytes(initializationVector);
		
		for(int i=0 ; i<initializationVector.length ; i++){ System.out.print(initializationVector[i]+" "); }
		
		// Byte[] to Long
		long longInitializationVector = ByteBuffer.wrap(initializationVector).getLong(); // On stocke l'IV dans un long pour pouvoir l'incrémenter.
		for(int i = 0 ; i < numberOfIv; i++){
			//Long to Byte[]
			ByteBuffer.wrap(initializationVector).putLong(longInitializationVector++);
			for(int j = 0 ; j < initializationVector.length ; j++){
				initializationVectorIncremented[i][j] = initializationVector[j];
			}
		}
		return initializationVectorIncremented;
	}
}
