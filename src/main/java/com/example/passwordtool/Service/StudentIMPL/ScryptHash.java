package com.example.passwordtool.Service.StudentIMPL;

import java.security.SecureRandom;


/**
 * Scrypt is a slow-by-design key derivation function designed to create strong cryptographic keys.
 * Simply put, the purpose of the Scrypt hash is to create a fingerprint of its input data but to do it very slowly.
 * A common use-case is to create a strong private key from a password, where the new private key is longer and more secure.
 *
 * <a href="https://blog.boot.dev/cryptography/very-basic-intro-to-the-scrypt-hash/"><br/>Reference Link</a>
 * @author Joshua Vaz
 */
public class ScryptHash
{

	private final String PRF_ALGORITHM = "HmacSHA256";
	private final String username;
	private final byte[] passphrase; // string of characters to be hashed
	private final String orgPass;
	private byte[] salt;  // random salt
	private byte[] passHash;
	private int costFactor; // CPU/Memory cost, must be power of 2
	private int parallelizationFactor; // Parallelization parameter. (1 .. 2<sup>32</sup>-1 * hLen/MFlen)
	private int desiredKeyLen; // Desired key length in bytes (Intended output length in octets of the derived key; a positive integer satisfying dkLen ≤ (232− 1) * hLen.)
	private int blockSize; // blocksize parameter, which fine-tunes sequential memory read size and performance.
	private final int hLen = 32; // The length in octets of the hash function (32 for SHA256).
	private int MFlen; // The length in octets of the output of the mixing function


	/**
	 * Main Constructor. Use this for a new "user" so to speak
	 * @param username Username
	 * @param plainTextPassword Password
	 */
	public ScryptHash(String username, String plainTextPassword)
	{
		this.orgPass = plainTextPassword;
		this.username = username;
		this.passphrase = plainTextPassword.getBytes();
		init();
		setSalt(getRandomSalt());
		long start=System.currentTimeMillis();
		encryptPassword();
		long end=System.currentTimeMillis();
		System.out.println("Time:"+(end-start));
	}

	/**
	 * Existing users use this
	 * We use this when we already have the salt defined in the database
	 * @param username username
	 * @param plainTextPassword password in plaintext
	 * @param saltHexString salt used to create expensive salt
	 */
	public ScryptHash(String username, String plainTextPassword, String saltHexString)
	{
		this.orgPass = plainTextPassword;
		this.username = username;
		this.passphrase = plainTextPassword.getBytes();
		init();
		setSalt(saltHexString); // reset this so that it uses the parameter byte[] instead
		long start=System.currentTimeMillis();
		encryptPassword();
		long end=System.currentTimeMillis();
		System.out.println("Time:"+(end-start));
	}
	/**
	 * Creates random salt thats used for PBKDF2
	 * @return byte[] salt
	 */
	public byte[] getRandomSalt()
	{
		return createRandomSalt();
	}

	/**
	 * String version of the salt in hex
	 * @return String Salt
	 */
	public String getStringRandomSalt()
	{
		if(this.salt != null && this.salt.length > 0){
			return convertToHexString(this.salt);
		}else{
			return null;
		}
	}

	/**
	 * Sets the salt[]
	 * @param salt byte[]
	 */
	private void setSalt(byte[] salt)
	{
		this.salt = salt;
	}

	/**
	 * Sets the salt based on the hex string
	 * @param hexSalt String
	 */
	public void setSalt(String hexSalt)
	{
		this.salt = convertHexToBytes(hexSalt);
	}


	/**
	 * Inits stuff
	 */
	private void init()
	{
		costFactor = 16384<<3;
		int blockSizeFactor = 8;
		parallelizationFactor = 3;
		blockSize = 128* blockSizeFactor *parallelizationFactor;
		desiredKeyLen = 32;
	}

	/**
	 * Gets the initial salt using PBKDF2
	 * <a href="https://blog.boot.dev/cryptography/very-basic-intro-to-the-scrypt-hash/#2---generate-initial-salt"><br/>
	 * Explanation</a>
	 * @return initial salt
	 */
	private byte[] getInitialSalt()
	{
		// Define blocksize
		
		PBKDF2 pbkdf2 = new PBKDF2(orgPass,salt,1,blockSize,PRF_ALGORITHM);
		return pbkdf2.createExpensiveSalt();
	}



	/**
	 * Rest of the algorithm.
	 *
	 */
	private void encryptPassword()
	{
		//Step 1 Get Initial Salt
		byte[] salt = getInitialSalt();
		byte[] pass = orgPass.getBytes();

		//Step 2 Mix Salt and Key
		byte[][] saltBlocks = PBKDF2.divideArray(salt,blockSize);
		byte[][] passBlocks = PBKDF2.divideArray(pass,blockSize);
		
		//Row Mix
		for(int i =0; i<saltBlocks.length;i++) {
			rowMix(passBlocks[i],saltBlocks[i]);
		}
		//Step 3 Get Final Key
		PBKDF2 pbkdf2 = new PBKDF2(orgPass,salt,1,blockSize,PRF_ALGORITHM);
		//Expensive Salt
		setPassHash(pbkdf2.createExpensiveSalt());
	}

	/**
	 * Set's the encrypted password in it' final form
	 * @param hash byte[]
	 */
	private void setPassHash(byte[] hash)
	{
		if(hash != null && hash.length > 0){
			this.passHash = hash;
		}
	}

	/**
	 * Mixes the rows
	 * @param passBlocks password block byte[]
	 * @param saltBlocks salt block byte[]
	 * @return byte[] resulting mix
	 */
	private byte[] rowMix(byte[] passBlocks,byte[] saltBlocks) {
		
		byte[] mixBlock = new byte[blockSize];
        byte[] xorBlock = new byte[blockSize];
        
        System.arraycopy(passBlocks, 0, mixBlock, 0, passBlocks.length);
		
		for (int i = 0; i < costFactor; i++) {
			//block mix
			blockMix(mixBlock, saltBlocks);

            // XOR the results together
            for (int j = 0; j < blockSize; j++) {
                xorBlock[j] ^= mixBlock[j];
            }
        }
		return xorBlock;
	}

	/**
	 * Mixes the blocks
	 * @param block1 byte[]
	 * @param block2 byte[]
	 */
	private void blockMix(byte[] block1, byte[] block2) {
        for (int i = 0; i < parallelizationFactor; i++) {
        	block1[i] = (byte) (block1[i] ^ block2[i]);
        }
    }

	/**
	 * Performs XOR operation on two blocks
	 * @param block1 byte[]
	 * @param block2 byte[]
	 * @return XOR're byte[]
	 */
	private static byte[] xorBlocks(byte[] block1, byte[] block2) {
        byte[] result = new byte[block1.length];
        for (int i = 0; i < block1.length; i++) {
            result[i] = (byte) (block1[i] ^ block2[i]);
        }
        return result;
    }

	/**
	 * Converts byte[] to hex string
	 * @param derivedKey byte[]
	 * @return Hex String
	 */
	private static String convertToHexString(byte[] derivedKey)
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (byte i : derivedKey) {
			stringBuilder.append(String.format("%02x", i));
		}
		return stringBuilder.toString();
	}

	/**
	 * Converts Hex string to byte[]
	 * @param hexString String
	 * @return byte[]
	 */
	private static byte[] convertHexToBytes(String hexString)
	{
		int length = hexString.length();
		byte[] result = new byte[length / 2];

		for (int i = 0; i < length; i += 2) {
			result[i/2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		}

		return result;
	}


	/**
	 * Creates a Secure Random salt to be used to create the expensive salt
	 * @return randomSalt
	 */
	private byte[] createRandomSalt() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] randomSalt = new byte[16]; //128 bits
		secureRandom.nextBytes(randomSalt);
		return randomSalt;
	}

	/**
	 * Returns the final encrypted password
	 * @return Encrypted Password
	 */
	public String getEncryptedPassphrase()
	{
		return convertToHexString(passHash);
	}

	private String getUsername()
	{
		return this.username;
	}

	private String getPlaintextPassword()
	{
		return new String(passphrase);
	}

	private byte[] getPassphrase()
	{
		return this.passphrase;
	}

}
