package util;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import entities.Conta;

public class ExternalSorting {

	//Função que faz a distribuição do arquivo total em 2 arquivos (usando 4 registros)
	public static void distribution(RandomAccessFile arq, RandomAccessFile tmpArq01, RandomAccessFile tmpArq02) {

		try {
			byte tmpLapid;
			int lastId, tmpTam, count = 0, changeArq = 0;
			long pos = arq.getFilePointer();
			ArrayList<Conta> tmpArrayAcc1 = new ArrayList<>();//lista na memoria para o arquivo 1(representão os 4 registros)
			ArrayList<Conta> tmpArrayAcc2 = new ArrayList<>();//lista na memoria para o arquivo 2(representão os 4 registros)

			tmpArq01.setLength(0);// reset tmp arq1
			tmpArq01.seek(0);
			tmpArq02.setLength(0);// reset tmp arq2
			tmpArq02.seek(0);

			//set up para leitura do arquivo sequencialmente
			arq.seek(0);
			lastId = arq.readInt();
			pos = arq.getFilePointer();
			
			//para manter o padrão de leitura todos os arquivos temporarios tem uma copia de last id no seu cabeçalho
			tmpArq01.writeInt(lastId);
			tmpArq02.writeInt(lastId);
			
			//tenta ler o numero de registros da memoria(nesse caso 4) validos ou até que o arquivo acabe ( caso de menos que o numero total de registos
			do {

				try {

					tmpLapid = arq.readByte();
					tmpTam = arq.readInt();
					byte[] tmpByteArray;
					tmpByteArray = new byte[tmpTam];
					arq.read(tmpByteArray);
					if (tmpLapid == 0) {
						count++;

						Conta tmpAcc = new Conta();
						tmpAcc.fromByteArray(tmpByteArray);
						
						// numero de cases representa o numero de registros maximos, quando atingido o estado muda para representar a troca de arquivo , isso se repete até a leitura sequencial completa de registros validos do arquivo
						switch (changeArq) {
						case 0:
						case 1:
						case 2:
						case 3:
							tmpArrayAcc1.add(tmpAcc);
							changeArq++;
							break;
						case 4:
						case 5:
						case 6:
						case 7:
							tmpArrayAcc2.add(tmpAcc);
							changeArq++;
							if (changeArq == 8)
								changeArq = 0;
							break;
						default:
							throw new IllegalArgumentException("Unexpected value: " + changeArq);
						}

					}
					pos = arq.getFilePointer();

					//esvazia a lista da memoria no arquivo 1
					if (tmpArrayAcc1.size() == 4 || pos >= arq.length() - 1) {
						tmpArrayAcc1
								.sort((o1, o2) -> o1.getId() == o2.getId() ? 0 : (o1.getId() > o2.getId() ? 1 : -1));
						while (tmpArrayAcc1.size() != 0) {
							tmpArq01.writeByte(0);
							tmpByteArray = tmpArrayAcc1.remove(0).toByteArray();
							tmpArq01.writeInt(tmpByteArray.length);
							tmpArq01.write(tmpByteArray);

						}
					}
					
					//esvazia a lista da memoria no arquivo 2
					if (tmpArrayAcc2.size() == 4 || pos >= arq.length() - 1) {
						tmpArrayAcc2
								.sort((o1, o2) -> o1.getId() == o2.getId() ? 0 : (o1.getId() > o2.getId() ? 1 : -1));
						while (tmpArrayAcc2.size() != 0) {
							tmpArq02.writeByte(0);
							tmpByteArray = tmpArrayAcc2.remove(0).toByteArray();
							tmpArq02.writeInt(tmpByteArray.length);
							tmpArq02.write(tmpByteArray);

						}
					}

				} catch (EOFException err) {
					break;
				}
			} while (pos < arq.length());
			System.out.println("Number of valid registers = " + count);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//Função que faz a intercalação dos arquivos de maneira estatica (4-4,8-8,....)
	public static void intercalation(RandomAccessFile arq, RandomAccessFile tmpArq01, RandomAccessFile tmpArq02,
			RandomAccessFile tmpArq03, RandomAccessFile tmpArq04, int count) {
		int state = 0, numRegisterInit = 4,lastId;
		boolean exit = false;
		byte[] tmpByteArray;
		
		//setup dos arquivos retorna o ponteiro dos arquivos a serem intercalados e limpa os arquivos que seram escritos
		try {
			tmpArq01.seek(0);
			tmpArq02.seek(0);
			tmpArq03.setLength(0);
			tmpArq03.seek(0);
			tmpArq04.setLength(0);
			tmpArq04.seek(0);
			lastId=tmpArq01.readInt();
			tmpArq03.writeInt(lastId);//padrão de manter o cabeçalho mesmo em arquivos temporarios
			lastId=tmpArq02.readInt();
			tmpArq04.writeInt(lastId);//padrão de manter o cabeçalho mesmo em arquivos temporarios
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//repete a intercalação até os dois arquivos serem totalmente intercalados
		while (!exit) {
			try {
				
				//state checa qual arquivo esta sendo escrito 
				switch (state) {
				case 0: {

					//contas temporarias para checagem (serão os "ponteiros" da comparação)
					Conta acc1 = readNextValidAcc(tmpArq01);
					Conta acc2 = readNextValidAcc(tmpArq02);
					
					//contadores do tamanho do registro valido de cada arquivo estaticamente (exmp:4,8,16)
					int num01 = 0;
					int num02 = 0;
					
					//faz todas as comparações do pior caso: registro arquivo1,registro arquivo2,...1,...2,...
					for (int i = 0; (i < numRegisterInit * 2 * count); i++) {
						if ((acc1 != null) && (acc2 != null)) {
							if ((num01 < numRegisterInit * count) && (num02 < numRegisterInit * count)) {

								if (acc1.getId() < acc2.getId()) {
									tmpArq03.writeByte(0);
									tmpByteArray = acc1.toByteArray();
									tmpArq03.writeInt(tmpByteArray.length);
									tmpArq03.write(tmpByteArray);
									num01++;
									if (num01 < numRegisterInit * count)
										acc1 = readNextValidAcc(tmpArq01);

								} else {
									tmpArq03.writeByte(0);
									tmpByteArray = acc2.toByteArray();
									tmpArq03.writeInt(tmpByteArray.length);
									tmpArq03.write(tmpByteArray);
									num02++;
									if (num02 < numRegisterInit * count)
										acc2 = readNextValidAcc(tmpArq02);

								}
							}
						}
					}
					
					//em caso dos registros do arquivo 2 acabarem antes do arquivo 1 descarrega o restante dos arquivos validos no arquivo
					while (acc1!=null&&(num01 < numRegisterInit * count)) {
						tmpArq03.writeByte(0);
						tmpByteArray = acc1.toByteArray();
						tmpArq03.writeInt(tmpByteArray.length);
						tmpArq03.write(tmpByteArray);
						num01++;
						if (num01 < numRegisterInit * count)
							acc1 = readNextValidAcc(tmpArq01);
					}
					
					//em caso dos registros do arquivo 1 acabarem antes do arquivo 2 descarrega o restante dos arquivos validos no arquivo
					while (acc2!=null&&(num02 < numRegisterInit * count)) {
						tmpArq03.writeByte(0);
						tmpByteArray = acc2.toByteArray();
						tmpArq03.writeInt(tmpByteArray.length);
						tmpArq03.write(tmpByteArray);
						num02++;
						if (num02 < numRegisterInit * count)
							acc2 = readNextValidAcc(tmpArq02);
					}

						state++;
				}
				
					break;
				//mesmo funcionamento do caso 0 trocando o arquivo de escrita
				case 1: {


					Conta acc1 = readNextValidAcc(tmpArq01);
					Conta acc2 = readNextValidAcc(tmpArq02);

					int num01 = 0;
					int num02 = 0;
					for (int i = 0; (i < numRegisterInit * 2 * count); i++) {
						if ((acc1 != null) && (acc2 != null)) {
							if ((num01 < numRegisterInit * count) && (num02 < numRegisterInit * count)) {

								if (acc1.getId() < acc2.getId()) {
									tmpArq04.writeByte(0);
									tmpByteArray = acc1.toByteArray();
									tmpArq04.writeInt(tmpByteArray.length);
									tmpArq04.write(tmpByteArray);
									num01++;
									if (num01 < numRegisterInit * count)
										acc1 = readNextValidAcc(tmpArq01);

								} else {
									tmpArq04.writeByte(0);
									tmpByteArray = acc2.toByteArray();
									tmpArq04.writeInt(tmpByteArray.length);
									tmpArq04.write(tmpByteArray);
									num02++;
									if (num02 < numRegisterInit * count)
										acc2 = readNextValidAcc(tmpArq02);

								}
							}
						}
					}
					while (acc1!=null&&(num01 < numRegisterInit * count)) {
						tmpArq04.writeByte(0);
						tmpByteArray = acc1.toByteArray();
						tmpArq04.writeInt(tmpByteArray.length);
						tmpArq04.write(tmpByteArray);
						num01++;
						if (num01 < numRegisterInit * count)
							acc1 = readNextValidAcc(tmpArq01);
					}
					while (acc2!=null&&(num02 < numRegisterInit * count)) {
						tmpArq04.writeByte(0);
						tmpByteArray = acc2.toByteArray();
						tmpArq04.writeInt(tmpByteArray.length);
						tmpArq04.write(tmpByteArray);
						num02++;
						if (num02 < numRegisterInit * count)
							acc2 = readNextValidAcc(tmpArq02);
					}
						state=0;
				}
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + state);
				}


				if ((tmpArq01.getFilePointer()>=tmpArq01.length())
						&& (tmpArq02.getFilePointer()>=tmpArq02.length())) 
					exit = true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	
	//função que le a proxima conta valida do arquivo
	public static Conta readNextValidAcc(RandomAccessFile arq) {
		Conta acc = null;
		byte tmpLapid;
		int tmpTam;
		try {
			long tmpPos = arq.getFilePointer();
			long tmpLen = arq.length();
			//procura por todo o arquivo registros validos (com lapides aceitas)
			while (tmpPos < tmpLen) {
				tmpLapid = arq.readByte();
				tmpTam = arq.readInt();
				byte[] tmpByteArray;
				tmpByteArray = new byte[tmpTam];
				arq.read(tmpByteArray);
				//ao encontrar um registro valido interrompe a operação
				if (tmpLapid == 0) {
					acc = new Conta();
					acc.fromByteArray(tmpByteArray);
					break;
				}
				tmpPos = arq.getFilePointer();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return acc;
	}
}
