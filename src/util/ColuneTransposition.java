package util;


public class ColuneTransposition {
	private static String KEY = "C0LUN3";//chave escolhida
	
	//recebe mensagem e cifra para a chave
	public static String coluneCipher(String msg) {
		StringBuilder sb = new StringBuilder(msg);
		int [] keyCharPos = makeAlphaVect();
		int extra = msg.length()%KEY.length();
		int trashChars = KEY.length() - extra;
		
		if(extra != 0)
			for(int i = 0; i < trashChars;i++)
				sb.append('$');//caracter escolhido como lixo das posições vazias de quando a mensagem não couber perfeitamente na chave
		
		int rows = sb.length()/KEY.length();
		
		char[][]transp1= new char [rows][KEY.length()];
		
		int msgPos =0;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < KEY.length(); j++) {
				transp1[i][j] = sb.charAt(msgPos);
				msgPos++;
			}
		String numPos="";
		for (int i = 1; i < KEY.length()+1; i++)
			for (int j = 0; j < KEY.length(); j++)
				if (keyCharPos[j]==i)
					numPos+=j;
		StringBuilder cipherText = new StringBuilder();
		
		for (int i = 0; i < KEY.length() ; i++) {
			int d;
			if (i==KEY.length()) {
				break;
			}else {
				d = Character.getNumericValue(numPos.charAt(i));
			}
			for (int j = 0; j < rows; j++) 
					cipherText.append(transp1[j][d]);
			
			
		}
		
		return cipherText.toString();
		
	}
	//preenche um vetor da ordem alfabetica nas posições validas
	public static int[] makeAlphaVect() {
		StringBuilder accAlp = new StringBuilder();
		for(int i =32;i<123;i++)
			accAlp.append((char) i);
		int count=0;
		int [] keyCharPos = new int [KEY.length()];
		for(int i = 0 ; i < accAlp.length();i++)
			for (int j = 0; j < KEY.length(); j++)
				if (accAlp.charAt(i)==KEY.charAt(j)) {
					count++;
					keyCharPos[j]=count;
				}
		return keyCharPos;
	}
	//recebe mensagem cifrada e faz processo inverso para retornar a mensagem ao seu estado original
	public static String coluneDecipher(String msg) {
		int rows = msg.length()/KEY.length();
		int [] keyCharPos = makeAlphaVect();
		char[][]transp1= new char [rows][KEY.length()];
		String numPos="";
		for (int i = 1; i < KEY.length()+1; i++)
			for (int j = 0; j < KEY.length(); j++)
				if (keyCharPos[j]==i)
					numPos+=j;
		
		
		for (int i = 0,x=0; i < msg.length(); i++,x++) {
			int d;
			if (x == KEY.length()) {
				break;
			}else {
				d = Character.getNumericValue(numPos.charAt(x));
			}
			for (int j = 0; j < rows; j++,i++) {
				transp1[j][d]=msg.charAt(i);
			}
			--i;
		}

		StringBuilder plainText=new StringBuilder();
		
		for (int i = 0; i < rows; i++) 
			for (int j = 0; j < KEY.length(); j++) 
				plainText.append(transp1[i][j]);
			
		String dec = plainText.toString().replace("$","");
		
		return dec;
	}
	
}
