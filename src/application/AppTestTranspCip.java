package application;

import util.ColuneTransposition;
import util.SubstituteCipher;

public class AppTestTranspCip {

	public static void main(String[] args) {
		String test = "senha123";
		String result = SubstituteCipher.subCipher(test);
		System.out.println("Cesar ciphered "+test+ " = "+ result);
		String decode =SubstituteCipher.subDecipher(result);
		System.out.println("ciphered "+result+ " decoded to = "+ decode);
		String testcolune = ColuneTransposition.coluneCipher("senha123");
		System.out.println("Colune transposition ciphered "+test+ " = "+ testcolune);
		String resultDec=ColuneTransposition.coluneDecipher(testcolune);
		System.out.println("ciphered "+testcolune+ " decoded to = "+ resultDec);
		String CesarTransp = ColuneTransposition.coluneCipher(result);
		System.out.println("Cesar cipher + Colune transposition cipher of "+test+" = "+CesarTransp);
		System.out.println(SubstituteCipher.subDecipher(ColuneTransposition.coluneDecipher(CesarTransp)));
	}

}
