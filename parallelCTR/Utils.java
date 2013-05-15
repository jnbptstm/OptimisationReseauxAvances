package parallelCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
		nombreBloc128 = fileLengthInBytes / 16;
		byte[][] bloc128 = new byte[nombreBloc128][16];
		
		for(int i=0 ; i<nombreBloc128 ; i++){
			fis.read(tmp);
			for(int j=0 ; j<tmp.length ; j++){
				bloc128[i][j] = tmp[j];
				tmp[j] = '\0';
			}
		}
		fis.read(blocInf128);
		fis.close();

		return bloc128;
	}
	
	
	public static byte[][] ivsIncremented(int numberOfIv, int lengthOfIv) throws UnsupportedEncodingException{
		
		byte[][] initializationVectorIncremented = new byte[numberOfIv][lengthOfIv];
		byte initializationVector[] = new byte [lengthOfIv];
		new SecureRandom().nextBytes(initializationVector);
		
		for(int i=0 ; i<initializationVector.length ; i++){ System.out.print(initializationVector[i]+" "); }
		System.out.println();
		BigInteger bigIntegerIV = new BigInteger(initializationVector);
		
		for(int i = 0 ; i < numberOfIv; i++){
			
			bigIntegerIV = bigIntegerIV.add(BigInteger.ONE);
			initializationVector = bigIntegerIV.toByteArray();
			
			for(int j = 0 ; j < initializationVector.length ; j++){
				initializationVectorIncremented[i][j] = initializationVector[j];
			}
		}
		return initializationVectorIncremented;
	}
}
