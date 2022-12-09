package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import entities.Conta;

public class IndexMaker {
	
	//função que recebe o arquivo original e um arquivo para ser escrito os indices
	public static boolean makeIndexFile(RandomAccessFile arq,RandomAccessFile indxArq) {
		try {
			indxArq.setLength(0);
			if (arq.length()<=4)
				return false;
			arq.seek(0);
			arq.readInt();
			ArrayList<Long> positions = new ArrayList<>();
			ArrayList<Integer> indexes = new ArrayList<>();
			readValidPositionsAndIndex(arq, positions,indexes); // carrega index e posições validas
			if (positions.size()==0)
				return false;
			indxArq.writeInt(positions.size());//sempre o primeiro numero do arquivo de index é o numero de valores validos que o mesmo possui
			//escreve de maneira pareada os index e suas posições 
			for(int i =0;i<positions.size();i++) {
				indxArq.writeInt(indexes.get(i));
				indxArq.writeInt(positions.get(i).intValue());
			}
			return true;
			
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return false;
	} 
	
	//função que le todas as posições validas e index de um arquivo
	public static void readValidPositionsAndIndex(RandomAccessFile arq,ArrayList<Long> positions,ArrayList<Integer> indexes) {
		
		byte tmpLapid;
		int tmpTam;
		try {
			long tmpPos = arq.getFilePointer();
			long tmpLen = arq.length();
			//procura por todo o arquivo registros validos (com lapides aceitas)
			while (tmpPos < tmpLen) {
				Conta acc = null;
				tmpLapid = arq.readByte();
				tmpTam = arq.readInt();
				byte[] tmpByteArray;
				tmpByteArray = new byte[tmpTam];
				arq.read(tmpByteArray);
				//ao encontrar um registro valido interrompe a operação
				if (tmpLapid == 0) {
					positions.add(tmpPos);
					acc = new Conta();
					acc.fromByteArray(tmpByteArray);
					indexes.add(acc.getId());
//					System.out.println("Position found: "+acc.getId()+" at: "+tmpPos);
				}
				tmpPos = arq.getFilePointer();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
