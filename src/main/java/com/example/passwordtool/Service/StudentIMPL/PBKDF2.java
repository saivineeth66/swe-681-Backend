package com.example.passwordtool.Service.StudentIMPL;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * SWE 681 Final Project.
 * <br/>Group 12
 * <br/>Generates the cryptographic key for Scrypt encryption.
 * Algorithm referenced from wikipedia.
 * <a href="https://en.wikipedia.org/wiki/PBKDF2#Key_derivation_process"><br/>Algorithm Link</a>
 *
 * @author Mani Shah
 */
public class PBKDF2
{


	/**
	 * Password
	 */
	private byte[] passphraseBytes;
	/**
	 * Unaltered original password
	 */
	private String orgPassphrase;
	/**
	 * SecureRandom Salt
	 */
	private byte[] salt;
	/**
	 * Number of iterations
	 */
	private int iterations;
	/**
	 * Length of the derived key
	 */
	private int dkLen;
	/**
	 * Derived Key
	 */
	private byte[] derivedKey;
	/**
	 * HMAC-SHA256 pseudorandom algorithm
	 */
	private String prfAlgo;


	private Mac hmacSha256;


	/**
	 * Validates and inits the object
	 *
	 * @param password password
	 * @param salt byte array of salt
	 * @param iterations number of iterations
	 * @param dkLen length of the derived key
	 * @param prfAlgorithm pseudorandom algorithm
	 */
	public PBKDF2(String password, byte[] salt, int iterations, int dkLen, String prfAlgorithm)
	{
		if(validInputParams(password,salt,iterations,dkLen,prfAlgorithm)) {
			init(password,salt,iterations,dkLen,prfAlgorithm);
			try{
				hmacSha256 = Mac.getInstance(prfAlgo);
				hmacSha256.init(new SecretKeySpec(orgPassphrase.getBytes(), prfAlgo));
			}catch (NoSuchAlgorithmException | InvalidKeyException e){
				e.printStackTrace();;
			}
		}else{
			System.err.println("Invalid Params.");
			System.exit(-1);
		}
	}

	private void init(String password, byte[] salt, int iterations, int dkLen, String prfAlgorithm)
	{
		orgPassphrase = password;
		this.salt = salt;
		this.iterations = iterations;
		this.dkLen = dkLen;
		this.prfAlgo = prfAlgorithm;
	}


	/**
	 *
	 * Validates all the provided inputs
	 *
	 * @param password char array of the password
	 * @param salt byte array of salt
	 * @param iterations number of iterations
	 * @param keyLength length of the derived key
	 * @param prfAlgorithm pseudorandom algorithm
	 * @return true if all inputs are valid. False otherwise.
	 */
	private boolean validInputParams(String password, byte[] salt, int iterations, int keyLength, String prfAlgorithm)
	{
		return !password.isEmpty() && salt.length > 0
				&& iterations > 0 && keyLength > 0 && prfAlgorithm != null && !prfAlgorithm.isEmpty();
	}



	/**
	 * DK = T<sub>1</sub> + T<sub>2</sub> + ⋯ + T<sub>dklen/hlen</sub>
	 * <br/>T<sub>i</sub> = F(Password, Salt, c, i)
	 * @return derivedKey
	 */
	public byte[] createExpensiveSalt() {

			int hLen = hmacSha256.getMacLength();
			int blocks = (int) Math.ceil((double) dkLen / hLen);
			derivedKey = new byte[dkLen];
			byte[] currentBlock = new byte[hLen];

			computePRFBlocks(blocks,currentBlock, hLen);
			return derivedKey;

	}

	/**
	 * F(Password, Salt, c, i) = U<sub>1</sub> ^ U<sub>2</sub> ^ ⋯ ^ U<sub>c</sub>
	 * <br/>where:
	 * <br/>U1 = PRF(Password, Salt + INT_32_BE(i))
	 * <br/>U2 = PRF(Password, U<sub>1</sub>)
	 * <br/>....
	 * <br/>Uc = PRF(Password, U<sub>c−1</sub>)
	 * @param blocks salt blocks
	 * @param currentBlock current salt block
	 * @param hLen hash length
	 */
	private void computePRFBlocks(int blocks, byte[] currentBlock, int hLen)
	{
		for (int i = 1; i <= blocks; i++) {
			byte[] initialPRFSalt = concatArrays(salt, intToBytes(i));
			Arrays.fill(currentBlock, (byte) 0);

			byte[] ui = hmacSha256.doFinal(initialPRFSalt);
			int copyLen = Math.min(ui.length, currentBlock.length);
			System.arraycopy(ui, 0, currentBlock, 0,copyLen);

			for (int j = 1; j < iterations; j++) {
				ui = hmacSha256.doFinal(ui);
				xorByteArrays(currentBlock, ui);
			}
			int copyLenFin = Math.min(hLen, dkLen - (i - 1) * hLen);
			int destPos = (i - 1) * hLen;
			System.arraycopy(currentBlock, 0, derivedKey, destPos , copyLenFin);
		}
	}

	/**
	 * U<sub>i</sub> ^ U<sub>i+1</sub> ^ ⋯ ^ U<sub>i+n</sub>
	 * @param u1 U<sub>i</sub>
	 * @param u2 U<sub>i+1</sub>
	 *
	 */
	private static void xorByteArrays(byte[] u1, byte[] u2) {
		for (int i = 0; i < u1.length && i < u2.length; i++) {
			u1[i] ^= u2[i];
		}
	}

	/**
	 * Converts integer to byte array
	 * @param value Integer value
	 * @return byte array of the int values
	 */
	private static byte[] intToBytes(int value) {
		byte[] intByteArr = new byte[4];

		intByteArr[3]=(byte)(value >>> 24);
		intByteArr[2]=(byte)(value >>> 16);
		intByteArr[1]=(byte)(value >>> 8);
		intByteArr[0]=(byte)value;

		return intByteArr;

	}

	/**
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	private static byte[] concatArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
	public static byte[][] divideArray(byte[] source, int chunksize) {
	     
        byte[][] ret = new byte[(int)Math.ceil(source.length / (double)chunksize)][chunksize];
 
        int start = 0;
 
        for(int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source,start, start + chunksize);
            start += chunksize ;
        }
 
        return ret;
	}

	public static void main(String[] args) {
		// Example usage
		String passphrase = "myPassword";
		byte[] salt = new byte[16]; // Replace with a secure random generator
		new SecureRandom().nextBytes(salt);
		System.out.println(Arrays.toString(salt));
		int iterations = 10000;
		int keyLength = 32;

		PBKDF2 pbkdf2 = new PBKDF2(passphrase, salt, iterations, keyLength, "HmacSHA256" );
		byte[] derivedKey = pbkdf2.createExpensiveSalt();
		System.out.println(Arrays.toString(derivedKey));
	}


}