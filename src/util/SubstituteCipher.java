package util;
public class SubstituteCipher {
	private static int move = 3;//tamanho do passo da crifragem de cesar
	//recebe mensagem e cifra cada caracter valido do espaço 32 ate z 122 de forma circular
	public static String subCipher(String x) {
		StringBuilder ciphered = new StringBuilder();
		int tmp;
		for(int i = 0;i<x.length();i++) {
			tmp =x.charAt(i);
			tmp = ((tmp+move)%123);
			if (tmp<32)
				tmp+=32;
			ciphered.append((char) tmp);
		}
		return ciphered.toString();
	}
	//recebe a mensagem criptografada e circula voltando para os caracteres antigos
	public static String subDecipher(String x) {
		StringBuilder ciphered = new StringBuilder();
		int tmp;
		for(int i = 0;i<x.length();i++) {
			tmp =x.charAt(i);
			tmp =tmp-move;
			if (tmp<32)
				tmp+=91;
			ciphered.append((char) tmp);
		}
		return ciphered.toString();
	}
}
