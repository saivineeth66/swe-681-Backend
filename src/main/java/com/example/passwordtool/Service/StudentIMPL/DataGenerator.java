package com.example.passwordtool.Service.StudentIMPL;

/**
 * Use this to fill up the database initially
 */
public class DataGenerator
{

	// Testing
	public static void main(String[] args)
	{
		//Test
		createUsers();
	}

	public static void createUsers()
	{
		String[] users = {"root", "admin" ,"test" ,"guest" ,"info" ,"adm" ,"mysql" ,"user" ,"administrator" ,"oracle"};
		String[] passwords = {"123456","password" ,"12345678","qwerty" ,"123456789" ,"12345" ,"1234" ,"111111" ,"1234567","dragon"};
		String[] encPasswords = new String[10];
		String [] salts = new String[10];
		for(int i = 0; i< 10; i++){
			ScryptHash scryptHash = new ScryptHash(users[i],passwords[i]);
			salts[i] = scryptHash.getStringRandomSalt();
			encPasswords[i] = scryptHash.getEncryptedPassphrase();
		}

		// Store these values in the database for each of the 10 users
		for(int i = 0; i< 10; i++){
			System.out.println(users[i]);
			System.out.println(encPasswords[i].substring(0,64)); // save only first 64 bytes
			System.out.println(salts[i]);
		}
	}

}
