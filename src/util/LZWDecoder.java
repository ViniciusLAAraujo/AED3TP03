package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public class LZWDecoder {

	private ArrayList<Integer> codes= new ArrayList<>();
	private ArrayList<String> decodedString= new ArrayList<>();
	
	//função que recebe o arquivo codificado e o arquivo a receber a versão decodificada
	public void lzwdecode(RandomAccessFile arq,RandomAccessFile dec) throws IOException {
		arq.seek(0);
		dec.setLength(0);
		readInputFile(arq); //le todos os codigos e os armazena numa lista
		decode(); //decodifica os codigos e produz o dicionario
		printOutput(dec);//escreve resultados no arquivo de saida
	}
	
	public void readInputFile(RandomAccessFile arq)
	{
		try {
			int read;
			do {
				read = arq.readChar();
				codes.add(read);
				
			} while (arq.getFilePointer()<arq.length());
		}catch (IOException e) {
			e.printStackTrace();
		} 

	}
	public void decode()
	{
		//construção do dicionario inicial
		HashMap<Integer,String> dictionary= new HashMap<Integer,String>();
		for(int i=0;i<256;i++)
		{
			dictionary.put(i,""+ (char) i);
		}
		int nextCode=256;
		String str=null;
		StringBuilder newstring= new StringBuilder();
		newstring.setLength(0);
		if(codes.size()>0)
			str=dictionary.get(codes.get(0));
		
		decodedString.add(str);
		for(int i=1;i<codes.size();i++) 
		{
			if(!dictionary.containsKey(codes.get(i)))
			{
				newstring.setLength(0);
				newstring.append(str+str.charAt(0));
			}
			else
			{
				newstring.setLength(0);
				newstring.append(dictionary.get(codes.get(i)));
			}
			decodedString.add(newstring.toString());

			dictionary.put(nextCode, str+newstring.toString().charAt(0));
			nextCode++;

			str=newstring.toString();
		}


	}
	public void printOutput(RandomAccessFile dec)
	{
		try {
			if(decodedString.size()>0)
			{
			//para cada string gerada é escrito char a char no arquivo de saida
			for(String x : decodedString)
			{
				for(int i=0;i<x.length();i++)
					dec.write(x.charAt(i));

			}
			dec.close();
			}

		}catch (IOException e) {
			e.printStackTrace();
		}
	}


}