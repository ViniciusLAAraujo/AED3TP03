package application;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.Scanner;

import entities.Conta;
import util.BDStringfier;
import util.CRUD;
import util.ColuneTransposition;
import util.ExternalSorting;
import util.IndexMaker;
import util.LZWDecoder;
import util.LZWEncoder;
import util.Menu;
import util.SubstituteCipher;

public class Main {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in),scLine = new Scanner(System.in);
		//variaveis temporarias usadas
		String tmpName, tmpUsername, tmpPassword, tmpCPF, tmpCity;
		int tmpNumEmail, tmpTransactions, tmpAccSearchedId,currentVersion=1;
		float tmpBalance;
		boolean tmpCheck;
		//recebe nome do arquivo desejado pelo usuario
		String filePath;
		System.out.print("File Name: ");
		filePath = scLine.nextLine();
		RandomAccessFile arq;
		RandomAccessFile tmpArq01, tmpArq02, tmpArq03, tmpArq04;
		int control = -1; //variavel de controle do switch , repete os comandos até receber 0
		try {
			//acesso de todos os arquivos utilizados criam os arquivos caso eles não existão, ATENÇÃO: a pasta "data" tem que existir no diretorio
			arq = new RandomAccessFile("data/" + filePath + ".db", "rw");
			tmpArq01 = new RandomAccessFile("data/arq1" + ".db", "rw");
			tmpArq02 = new RandomAccessFile("data/arq2" + ".db", "rw");
			tmpArq03 = new RandomAccessFile("data/arq3" + ".db", "rw");
			tmpArq04 = new RandomAccessFile("data/arq4" + ".db", "rw");
			String pathIndex="data/" + filePath+ "Index"+ ".db";
			
			//cria o index quando o programa é inicializado
			try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")){
				System.out.println("Making first index instance");
				
				if (arq.length()>0)
					IndexMaker.makeIndexFile(arq, indxArq);
			} catch (Exception e) {
				e.printStackTrace();
			}


			do {
				System.out.println("|-----------CurrentVersion" + currentVersion +"---------|");
				Menu.call();//Função que imprime o menu
				control = sc.nextInt();
				switch (control) {
				case 0:
					System.out.println("Exiting....");
					break;
				case 1: {
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						System.out.println("Creating New Account:");
						//recolhe todos os dados para criação do registro
						System.out.print("Name: ");
						tmpName =scLine.nextLine();
						do {
							System.out.print("Number of emails: ");
							tmpNumEmail = sc.nextInt();
							if (tmpNumEmail < 1) {
								System.out.println("Inform at least 1 email");
							}
						} while (tmpNumEmail < 1);
						String tmpEmails[] = new String[tmpNumEmail];
						for (int i = 0; i < tmpNumEmail; i++) {
							System.out.printf("emails %d: ", i + 1);
							tmpEmails[i] = scLine.nextLine();
							if (i != tmpNumEmail - 1) {
								System.out.println();
							}
						}
						System.out.print("Username: ");
						tmpUsername = scLine.nextLine();
						System.out.print("Password: ");
						tmpPassword = scLine.nextLine();
						tmpPassword=SubstituteCipher.subCipher(tmpPassword);//crifra a senha lida para cesar 
						tmpPassword=ColuneTransposition.coluneCipher(tmpPassword);// cifra a senha cifrada em cesar para transposição de colunas
						do {
							System.out.print("cpf: ");
							tmpCPF = scLine.nextLine();
							if (tmpCPF.length() != 11) {
								System.out.println("CPF must have 11 caracters");
							}
						} while (tmpCPF.length() != 11);
						System.out.print("City: ");
						tmpCity = scLine.nextLine();
						System.out.print("Initial Number of Transactions: ");
						tmpTransactions = sc.nextInt();
						System.out.print("Account Balance: ");
						tmpBalance = sc.nextFloat();
						//Função que tenta criar o registro com os dados recebidos (mais detalhes da função no arquivo CRUD)
						tmpCheck = CRUD.create(tmpName, tmpNumEmail, tmpEmails, tmpUsername, tmpPassword, tmpCPF, tmpCity,
								tmpTransactions, tmpBalance, arq);
						if (tmpCheck) {
							System.out.println("Acc Created");
							IndexMaker.makeIndexFile(arq, indxArq);//atualiza index
						}
						else
							System.out.println("Failed to Create Acc");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case 2: {
					
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						System.out.println("Reading Account:");
						if (arq.length() <= 0) {
							System.out.println("Empty file...");
							break;
						}
						
						Conta tmpAcc = new Conta();
						
						System.out.print("User you looking for: ");
						tmpAccSearchedId = sc.nextInt();
						tmpAcc = CRUD.readIndex(tmpAccSearchedId, arq,indxArq); 
						if (tmpAcc == null) {
							System.out.println("Not found...");
							break;
						}
						System.out.println(tmpAcc);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
					break;
				case 3: {
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						Conta tmpAcc=null;
						System.out.println("Updating Account:");
						
						//checa se o arquivo esta vazio
						if (arq.length() <= 0) {
							System.out.println("Empty file...");
							break;
						}
						
						//recebe o parametro de pesquisa
						System.out.print("Id you looking for: ");
						tmpAccSearchedId = sc.nextInt();
						
						tmpAcc = CRUD.readIndex(tmpAccSearchedId, arq,indxArq); 
						if (tmpAcc == null) {
							System.out.println("Not found...");
							break;
						}
						System.out.print("Name: ");
						tmpName = scLine.nextLine();
						do {
							System.out.print("Number of emails: ");
							tmpNumEmail = sc.nextInt();
							if (tmpNumEmail < 1) {
								System.out.println("Inform at least 1 email");
							}
						} while (tmpNumEmail < 1);
						String tmpEmails[] = new String[tmpNumEmail];
						for (int i = 0; i < tmpNumEmail; i++) {
							System.out.printf("emails %d: ", i + 1);
							tmpEmails[i] = scLine.nextLine();
							if (i != tmpNumEmail - 1) {
								System.out.println();
							}
						}
						System.out.print("Account Username: ");
						tmpUsername = scLine.nextLine();
						System.out.print("Password: ");
						tmpPassword = scLine.nextLine();
						tmpPassword=SubstituteCipher.subCipher(tmpPassword);//crifra a senha lida para cesar 
						tmpPassword=ColuneTransposition.coluneCipher(tmpPassword);// cifra a senha cifrada em cesar para transposição de colunas
						do {
							System.out.print("cpf: ");
							tmpCPF = scLine.nextLine();
							if (tmpCPF.length() != 11) {
								System.out.println("CPF must have 11 caracters");
							}
						} while (tmpCPF.length() != 11);
						System.out.print("City: ");
						tmpCity = scLine.nextLine();
						System.out.print("Initial Number of Transactions: ");
						tmpTransactions = sc.nextInt();
						System.out.print("Account Balance: ");
						tmpBalance = sc.nextFloat();
						
						
						//cria o registro com os dados coletados
						Conta tmpAccNew = new Conta(tmpName, tmpNumEmail, tmpEmails, tmpUsername, tmpPassword, tmpCPF, tmpCity,
								tmpTransactions, tmpBalance, tmpAccSearchedId);
						//Função que tenta atualizar o registro com a nova conta criada(mais detalhes da função no arquivo CRUD)
						tmpCheck = CRUD.updateIndex(tmpAccNew, arq,indxArq);
						if (tmpCheck)
							System.out.println(tmpAccSearchedId + " Updated successfully");
						else
							System.out.println("Fail in updating not found or already deleted");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case 4: {
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						System.out.println("Deliting Account:");
						
						//checa se o arquivo esta vazio
						if (arq.length() <= 0) {
							System.out.println("Empty file...");
							break;
						}
						
						//recebe o parametro de pesquisa
						System.out.print("Id you looking for: ");
						tmpAccSearchedId = sc.nextInt();
						
						//Função que tenta fazer a deleção do registro (mais detalhes da função no arquivo CRUD)
						tmpCheck = CRUD.deleteIndex(tmpAccSearchedId, arq,indxArq);
						if (tmpCheck)
							System.out.println(tmpAccSearchedId + " Deleted successfully");
						else
							System.out.println("Fail in delition not found or already deleted");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case 5: {
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						float transactionAmount;
						Conta tmpAcc = null;

						System.out.println("Transaction(withdraw/deposit) beware if you withdraw more than you have "
								+ "the Account'll be negative, and won't be able to do any more tranferences "
								+ "for another accounts until it's balance is positive again");
						System.out.print("Id that will recive the deposit: ");
						tmpAccSearchedId = sc.nextInt();

						tmpAcc = CRUD.readIndex(tmpAccSearchedId, arq,indxArq);
						if (tmpAcc == null) {
							System.out.println("Acc not found, try again");
							break;
						}
						

						System.out.println("Amount that will be tranfer:");
						transactionAmount = sc.nextFloat();

						tmpAcc.transacao(transactionAmount);

						System.out.println("Transfering....");
						CRUD.updateIndex(tmpAcc, arq, indxArq);
						System.out.println("Transference was successful!");
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
					break;
				case 6: {
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						int idAccDeb, idAccCred;
						Conta accDeb =null;
						Conta accCred =null;
						float transactionAmount;

						System.out.println("Transaction");
						//recebe os parametros para fazer a transferencia
						System.out.print("User that will transfer: ");
						idAccDeb = sc.nextInt();
						//Procura o enviador da transferencia
						accDeb = CRUD.readIndex(idAccDeb, arq,indxArq);
						if (accDeb == null) {
							System.out.println("Debit Acc not found, try again");
							break;
						}
						
						System.out.print("User that will recieve: ");
						idAccCred = sc.nextInt();
						
						//Procura o recebedor da transferencia
						accCred = CRUD.readIndex(idAccCred, arq,indxArq);
						if (accCred == null) {
							System.out.println("Credit Acc not found, try again");
							break;
						}
						//recebe o valor da transferencia a ser feita
						do {
							System.out.println("Amount that will be tranfer:");
							transactionAmount = sc.nextFloat();
							if (transactionAmount <= 0) {
								System.out.println("Inform valid amount (x > 0)");
							}
						} while (transactionAmount <= 0);
						
						//verifica se o saldo infromado permite a transação
						if (accDeb.getSaldoConta() < transactionAmount) {
							System.out.println("Account's ballance is not enough");
							break;
						}
						
						
						//modifica os registros , enviador - transação e recebedor + transação (o numero de transações é calculado dentro dos registros automaticamente)
						accDeb.transacao(-transactionAmount);
						accCred.transacao(transactionAmount);
						
						//atualiza o arquivo em ambos os registros
						System.out.println("Transfering....");
						CRUD.updateIndex(accDeb, arq, indxArq);
						CRUD.updateIndex(accCred, arq, indxArq);
						System.out.println("Transference was successful!");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case 7: {
					char tmpConfirm;
					System.out.println("Distribution to arq1 and arq2 (Warnning!it resets arq1 and arq2!)");
					do {
						System.out.println("DO YOU WANNA PROCEED? Y/n");
						tmpConfirm = scLine.next().charAt(0);
						if (tmpConfirm == 'n')
							break;
					} while (tmpConfirm != 'Y' );
					if (tmpConfirm == 'n')
						break;
					System.out.println("Proceeding..");
					//Função que recebe o arquivo principal e distribui os registros (4 registros maximos na memoria. Mais detalhes no arquivo ExternalSorting)
					ExternalSorting.distribution(arq, tmpArq01, tmpArq02);
					System.out.println("Distribution was successful!");
				}
					break;
				case 8: {
					System.out.println("Intercalate");
					int state = 0, count = 1;//state controla qual dos pares esta sendo intercalado e count controla o numero de registros (4, 8 , 16, ....)
					boolean exit = false; //controle do numero de intercalações necessaria até que os arquivos resultem em um só
					while (!exit) {
						switch (state) {
						case 0: {
							System.out.println("Doing intercalation " + count);
							//Função que recebe o arquivo principal, os dois arquivos distribuidos,e os dois arquivos para serem escritas as intercalações (Mais detalhes no arquivo ExternalSorting)
							ExternalSorting.intercalation(arq, tmpArq01, tmpArq02, tmpArq03, tmpArq04, count);
							System.out.println("Intercalation " + count + " Done!");
							//avança o estado e muda o numero de registros novos a serem verificados 
							count++;
							state++;
						}
							break;
						case 1: {
							System.out.println("Doing intercalation " + count);
							//Função que recebe o arquivo principal, os dois arquivos distribuidos,e os dois arquivos para serem escritas as intercalações (Mais detalhes no arquivo ExternalSorting)
							ExternalSorting.intercalation(arq, tmpArq03, tmpArq04, tmpArq01, tmpArq02, count);
							System.out.println("Intercalation " + count + " Done!");
							//avança o estado e muda o numero de registros novos a serem verificados 
							count++;
							state = 0;
						}
							break;
						default:
							throw new IllegalArgumentException("Unexpected value: " + state);
						}
						//verifica se a intercalação resultou em um arquivo apenas no primeiro estado(fim da intercalação)
						if (tmpArq02.length()==4 && state==0) {
							exit =true;
							System.out.println("seu arquivo totalmente ordenado é o Arq1");
						}
						//verifica se a intercalação resultou em um arquivo apenas no segundoestado(fim da intercalação)
						if (tmpArq04.length()==4 && state==1) {
							System.out.println("seu arquivo totalmente ordenado é o Arq3");
							exit =true;
						}

					}
					System.out.println("Done!!!");
				}
					break;
				case 9: {
					//Recria o index manualmente, caso queira 
					System.out.println("Creating Index file");
					
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")){
					boolean worked = IndexMaker.makeIndexFile(arq, indxArq);
					if (worked) 
						System.out.println("Index created");
					else 
						System.out.println("Index could not be created");
					} 
					catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
					break;
				case 10: {
					System.out.println("LZWcompressing: ");
					//inicializa e reseta arquivos necessarios segundo o caminho e versão 
					String pathCompress = "data/"+filePath+"Compressao"+currentVersion+".db";
					String pathTXT="data/"+filePath+".txt";
					RandomAccessFile compFile = new RandomAccessFile(pathCompress,"rw");
					RandomAccessFile txtFile = new RandomAccessFile(pathTXT,"rw");
					compFile.setLength(0);
					txtFile.setLength(0);
				
					try (RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")){
						long startTime = System.currentTimeMillis();//começa a contar o tempo das operações
						System.out.println("Making txt file:"+pathTXT);
						BDStringfier.makeTXT(arq, indxArq, pathTXT); // faz um txt com as contas do arquivo
						System.out.println("Making compression....");
						LZWEncoder lzwEnc = new LZWEncoder();
						lzwEnc.lzwencode(txtFile, compFile); // chama as operações de LZW enconding passando o TXTresultado e arquivo para receber compressão
						System.out.println("Done!");
						long timeTaken = System.currentTimeMillis() - startTime;//calcula o tempo gasto
						StringBuilder sb = new StringBuilder();
						//System.out.println(compFile.length() +" "+ txtFile.length());
						
						//verifica se houve perda ou ganho no tamanho do arquivo e faz os calculos 
						if (txtFile.length()>compFile.length()) {
							sb.append("Gain = %");
							double gain =100.0 - ((double)compFile.length()/txtFile.length())*100.0;
							sb.append(String.format("%.2f", gain));
						}else {
							sb.append("Loss = %");
							double loss =100.0-((double)txtFile.length()/compFile.length())*100.0;
							sb.append(String.format("%.2f", loss));
						}
						sb.append(" Time taken= ");
						sb.append(timeTaken);
						sb.append(" millis");
						System.out.println(sb.toString());	
						currentVersion++; //avança para proxima instancia de conversão
						System.out.println("Next compression will be: "+currentVersion);
				
					} catch (Exception e) {
						e.printStackTrace();
					}
					compFile.close();
					txtFile.close();
				}
					break;
				case 11: {
					//inicializa e reseta arquivos necessarios segundo o caminho e versão escolhida
					System.out.println("LZWDecompressing: ");
					System.out.print("What version do you wish to decompress? ");
					currentVersion= sc.nextInt();
					String pathComprimed = "data/"+filePath+"Compressao"+currentVersion+".db";
					String pathDecompress ="data/"+filePath+"Desc"+currentVersion+".txt";
					RandomAccessFile decompFile = new RandomAccessFile(pathDecompress,"rw");
					RandomAccessFile compFile = new RandomAccessFile(pathComprimed,"rw");
					decompFile.setLength(0);
					compFile.seek(0);
					try(RandomAccessFile indxArq = new RandomAccessFile(pathIndex,"rw")) {
						System.out.println("Decoding file: "+pathComprimed);
						LZWDecoder lzwDec= new LZWDecoder();
						lzwDec.lzwdecode(compFile,decompFile); //decodifica em um TXT o arquivo codificado anteriormente 
						System.out.println("Decompressed");
						System.out.println("Loading this file");
						BDStringfier.readTXT(arq, pathDecompress); // transforma o txt descodificado em contas carregando-o no arquivo
						System.out.println("Loaded");
						IndexMaker.makeIndexFile(arq, indxArq); //atualiza index
					} catch (Exception e) {
						e.printStackTrace();
					}
					decompFile.close();
					compFile.close();
				}
					break;
				case 12: {
					System.out.println("What version do you want to set manually: ");
					currentVersion= sc.nextInt();
					System.out.println("Version now is : "+ currentVersion);
				}
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + control);
				}
			} while (control != 0);
			
			//fecha os arquivos abertos
			arq.close();
			tmpArq01.close();
			tmpArq02.close();
			tmpArq03.close();
			tmpArq04.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sc.close();
		scLine.close();
	}

}
