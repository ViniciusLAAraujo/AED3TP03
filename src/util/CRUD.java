package util;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

import entities.Conta;

public class CRUD {
	
	//Função que escreve no arquivo o registro sendo informado os parametros para sua criação
	public static boolean create(String name, int nEmail, String[] emails, String username, String password, String cpf,
			String city, int tranfers, float balance, RandomAccessFile arq) throws IOException {
		int lastId;
		byte tmpByteArray[];
		
		//checagem do tamanho do arquivo , caso ele esteja vazio o cabeçalho é iniciado com um id=0(o que possibilita ids >0 para todos os registros criados em seguida)
		arq.seek(0);
		if (arq.length() > 0)
			lastId = arq.readInt();

		else
			lastId = 0;
		
		//instancia o registro
		Conta c = new Conta(name, nEmail, emails, username, password, cpf, city, tranfers, balance, lastId + 1);
		
		//atualiza o ultimo id para o novo id do registro criado
		arq.seek(0);
		arq.writeInt(lastId + 1);
		//converte o registro instanciado para um vetor de bytes
		tmpByteArray = c.toByteArray();
		//vai ao final do arquivo
		arq.seek(arq.length());
		//escreve a lapide , o tamaho do vetor de bytes e o vetor de bytes (que representa o registro convertido) no arquivo
		arq.writeByte(0);
		arq.writeInt(tmpByteArray.length);
		arq.write(tmpByteArray);
		return true;
	}
	
	//Função que procura um registro a partir de um dado informado e devolve a posição do mesmo no arquivo
	public static int read(int searchUser, RandomAccessFile arq) throws IOException {
		//armazenamento para o registro a ser lido
		Conta tmpAcc = new Conta();
		byte tmpLapid;
		int tmpTam, position = 0, lastId;
		byte tmpByteArray[];
		
		//le o cabeçalho e atualiza posição
		arq.seek(0);
		lastId = arq.readInt();
		
		if (lastId<searchUser) 
			return -1;
		
		position = position + 4;
		
		
		//le registros requencialmente até que o arquivo acabe
		do {
			try {
				
				//le lapide e tamanho dos registros
				tmpLapid = arq.readByte();
				tmpTam = arq.readInt();
				
				//se lapide == 0 o registro é valido
				if (tmpLapid == 0) {
					//recebe o registro em vetor de bytes
					tmpByteArray = new byte[tmpTam];
					arq.read(tmpByteArray);
					//converte o registro 
					tmpAcc.fromByteArray(tmpByteArray);
					//checa se o parametro foi encontrado
					if (searchUser == tmpAcc.getId()) {
						//retorna a posição (lapide+tamanho+registro) caso seja encontrado
						return position;
					}
				}
				//atualiza posição
				position = position + 5 + tmpTam; //1 lapid + 4 int tam + register size
				arq.seek(position);

			} catch (EOFException err) {
				break;
			}

		} while (position < arq.length());
		
		//caso não encontrado até fim de arquivo retorna posição -1
		return -1;

	}
	//read que utilizase do arquivo de index , le o id e sua posição no arquivo de index então pula para a mesma no arquivo original e le o registro
	public static Conta readIndex(int searchUser, RandomAccessFile arq,RandomAccessFile index) throws IOException {
		
		Conta tmpAcc = null;
		byte tmpLapid;
		int tmpTam,tmpId,tmpPos,tmpTamInd;
		byte tmpByteArray[];
		
		
		index.seek(0);
		tmpTamInd=index.readInt();
		
		if (tmpTamInd<=0)
			return tmpAcc;
		
		
		
		for (int i = 0; i < tmpTamInd; i++) {
			try {
				tmpId = index.readInt();
				tmpPos = index.readInt();
				if (tmpId==searchUser) {
					arq.seek(tmpPos);
					tmpLapid = arq.readByte();
					tmpTam = arq.readInt();
					tmpByteArray = new byte[tmpTam];
					arq.read(tmpByteArray);
					tmpAcc = new Conta();
					tmpAcc.fromByteArray(tmpByteArray);
					return tmpAcc;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		return tmpAcc;

	}
	
	//Função que faz a deleção do arquivo recebendo o parametro e o arquivo a ser vasculhado
	public static boolean delete(int searchUser, RandomAccessFile arq) throws IOException {
		int  position = 0, lastId;
		
		//le o cabeçalho
		arq.seek(0);
		lastId = arq.readInt();
		position = position + 4;
		
		if (lastId<searchUser) 
			return false;

		//encontra a posição do registro no arquivo
		position =CRUD.read(searchUser, arq);
		
		//caso não encontrado retorna falso
		if (position <= 0)
			return false;
		
		//vai a posição do arquivo e substitui lapide valida 0 por 1 (invalida)
		arq.seek(position);
		arq.writeByte(1);

		return true;
	}
	
	//função que recebe um registro e o atualiza 
	public static boolean update(Conta tmpAcc, RandomAccessFile arq) throws IOException {
		int  position = 0, lastId,tmpTamOrig;
		byte tmpByteArray[];
		
		//le cabeçalho
		arq.seek(0);
		lastId = arq.readInt();
		position = position + 4;
		
		if (lastId<tmpAcc.getId()) 
			return false;

		//procura posição do registro no arquivo
		position =CRUD.read(tmpAcc.getId(), arq);
		//se não encontrado retorna falso
		if (position <= 0)
			return false;
		
		//vai a posição do registro no arquivo , passa pela lapide e verifica o tamanho original
		arq.seek(position);
		arq.readByte();
		tmpTamOrig=arq.readInt();
		//converte o novo registro atualizado para um vetor de bytes
		tmpByteArray = tmpAcc.toByteArray();
		//checa se o novo registro é menor ou igual ao tamanho original
		if (tmpByteArray.length<=tmpTamOrig)
			//caso menor ou igual escreve o novo vetor
			arq.write(tmpByteArray);
		else{
			//caso maior marca a lapide do registro existente (deleta-o) e o escreve novamente no final do arquivo (atualizando assim seu tamanho)
			arq.seek(position); 
			arq.writeByte(1);   
			arq.seek(arq.length());
			arq.writeByte(0);
			arq.writeInt(tmpByteArray.length);
			arq.write(tmpByteArray);
		}
		
		return true;
	}
	//update que usa a leitura do arquivo de index
	public static boolean updateIndex(Conta tmpAcc, RandomAccessFile arq,RandomAccessFile index) throws IOException {
		int  position = 0, lastId,tmpTamOrig;
		byte tmpByteArray[];
		
		//le cabeçalho
		arq.seek(0);
		lastId = arq.readInt();
		position = position + 4;
		
		if (lastId<tmpAcc.getId()) 
			return false;

		//procura posição do registro no arquivo
		position =CRUD.read(tmpAcc.getId(), arq);
		//se não encontrado retorna falso
		if (position <= 0)
			return false;
		
		//vai a posição do registro no arquivo , passa pela lapide e verifica o tamanho original
		arq.seek(position);
		arq.readByte();
		tmpTamOrig=arq.readInt();
		//converte o novo registro atualizado para um vetor de bytes
		tmpByteArray = tmpAcc.toByteArray();
		//checa se o novo registro é menor ou igual ao tamanho original
		if (tmpByteArray.length<=tmpTamOrig)
			//caso menor ou igual escreve o novo vetor
			arq.write(tmpByteArray);
		else{
			//caso maior marca a lapide do registro existente (deleta-o) e o escreve novamente no final do arquivo (atualizando assim seu tamanho)
			arq.seek(position); 
			arq.writeByte(1);   
			arq.seek(arq.length());
			arq.writeByte(0);
			arq.writeInt(tmpByteArray.length);
			arq.write(tmpByteArray);
		}
		IndexMaker.makeIndexFile(arq, index);
		return true;
	}
	//delete que utiliza a leitura do arquivo de index
	public static boolean deleteIndex(int searchUser, RandomAccessFile arq,RandomAccessFile index) throws IOException {
		int  position = 0, lastId;
		
		//le o cabeçalho
		arq.seek(0);
		lastId = arq.readInt();
		position = position + 4;
		
		if (lastId<searchUser) 
			return false;

		//encontra a posição do registro no arquivo
		position =CRUD.read(searchUser, arq);
		
		//caso não encontrado retorna falso
		if (position <= 0)
			return false;
		
		//vai a posição do arquivo e substitui lapide valida 0 por 1 (invalida)
		arq.seek(position);
		arq.writeByte(1);
		IndexMaker.makeIndexFile(arq, index);
		return true;
	}
}
