package entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import util.ColuneTransposition;
import util.SubstituteCipher;


public class Conta {
	private int id;
	private String nomePessoa;
	private int numEmail;
	private ArrayList<String> emails= new ArrayList<>();
	private String nomeUsuario;
	private String senha;
	private String cpf;
	private String cidade;
	private int transferenciasRealizadas = 0;
	private float saldoConta=0;

	public Conta(String nomePessoa, int numEmail, String[] emails, String nomeUsuario, String senha, String cpf,
			String cidade,int transferenciasRealizadas ,float saldoConta, int countId) {
		this.id=countId;
		this.nomePessoa = nomePessoa;
		this.numEmail = numEmail;
		for (int i = 0; i < emails.length; i++) {
			this.emails.add(emails[i]);
		}
		this.nomeUsuario = nomeUsuario;
		this.senha = senha;
		this.cpf = cpf;
		this.cidade = cidade;
		this.transferenciasRealizadas=transferenciasRealizadas;
		this.saldoConta=saldoConta;
	}
	public Conta(String nomePessoa, int numEmail, String[] emails, String nomeUsuario, String senha, String cpf,
			String cidade,float initialDeposit ,int countId) {
		this.id=countId;
		this.nomePessoa = nomePessoa;
		this.numEmail = numEmail;
		for (int i = 0; i < emails.length; i++) {
			this.emails.add(emails[i]);
		}
		this.nomeUsuario = nomeUsuario;
		this.senha = senha;
		this.cpf = cpf;
		this.cidade = cidade;
		this.transacao(initialDeposit);
	}

	public Conta() {
		this.nomePessoa="---";
		this.numEmail=1;
		this.emails=new ArrayList<String>();
		this.nomeUsuario="---";
		this.senha="---";
		this.cpf="-----------";
		this.cidade="---";
		this.transferenciasRealizadas=0;
		this.saldoConta=0;
		this.id=-1;
	}

	public int getId() {
		return id;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCpf() {
		return cpf;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public int getTransferenciasRealizadas() {
		return transferenciasRealizadas;
	}

	public float getSaldoConta() {
		return saldoConta;
	}

	public int getNumEmail() {
		return numEmail;
	}

	public void setNumEmail(int numEmail) {
		this.numEmail = numEmail;
	}

	public String[] getEmail() {
		String emails[] = new String[this.emails.size()];
		return this.emails.toArray(emails);
	}

	public void setEmail(String[] emails) {
		for (int i = 0; i < emails.length; i++) {
			this.emails.add(emails[i]);
		}
	}

	public void transacao(float val) {
		this.saldoConta = this.saldoConta + val;
		this.transferenciasRealizadas++;
	}
	
	  public String toString() {
		  String realPassword = ColuneTransposition.coluneDecipher(this.senha);//decifra a senha em transposição de colunas 
		  realPassword = SubstituteCipher.subDecipher(realPassword);//decifra a transposição em cesar para gerar valor real
		    return "\nID: " + this.id + 
		           "\nName: " + this.nomePessoa + 
		           "\nUsername: " + this.nomeUsuario + 
		           "\nPassword: " + realPassword + 
		           "\nNumber of Emails: " + this.numEmail +
		           "\nEmails: " + this.emails +
		           "\nBalance: R$ " + this.saldoConta + 
		           "\nNumber of Transfers: " + this.transferenciasRealizadas + 
		           "\nCity: " + this.cidade + 
		           "\nCPF: " + this.cpf;
		  }

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(this.id);
		dos.writeUTF(this.nomePessoa);
		dos.writeInt(this.numEmail);
		for (int i = 0 ; i < this.numEmail; i++) {
			dos.writeUTF(this.emails.get(i));
		}
		dos.writeUTF(this.nomeUsuario);
		dos.writeUTF(this.senha);
		dos.writeUTF(this.cpf);
		dos.writeUTF(cidade);
		dos.writeInt(this.transferenciasRealizadas);
		dos.writeFloat(this.saldoConta);
		return baos.toByteArray();
	}

	public void fromByteArray(byte[] ba) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(ba);
		DataInputStream dis = new DataInputStream(bais);
		this.id = dis.readInt();
		this.nomePessoa=dis.readUTF();
		this.numEmail=dis.readInt();
		for (int i = 0; i < this.numEmail; i++) {
			this.emails.add(dis.readUTF());
		}
		this.nomeUsuario=dis.readUTF();
		this.senha=dis.readUTF();
		this.cpf=dis.readUTF();
		this.cidade=dis.readUTF();
		this.transferenciasRealizadas=dis.readInt();
		this.saldoConta=dis.readFloat();
	}
	//transforma conta binaria em TXT
	public String txtWriter() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id);
		sb.append("|");
		sb.append(this.nomePessoa);
		sb.append("|");
		sb.append(this.nomeUsuario);
		sb.append("|");
		sb.append(this.senha);
		sb.append("|");
		sb.append(this.cpf);
		sb.append("|");
		sb.append(this.transferenciasRealizadas);
		sb.append("|");
		sb.append(this.cidade);
		sb.append("|");
		sb.append(this.numEmail);
		sb.append("|");
		for (int i = 0; i < this.numEmail; i++) {
			sb.append(this.emails.get(i));
			sb.append("|");
		}
		sb.append(this.saldoConta);
	    return sb.toString();
	  }
	//le txt e transforma conta em registo
	public void txtReader(String str) {
		 String params[] = str.split("[|]",0);
		 this.id=Integer.parseInt(params[0]);
		 this.nomePessoa=params[1];
		 this.nomeUsuario=params[2];
		 this.senha=params[3];
		 this.cpf=params[4];
		 this.transferenciasRealizadas=Integer.parseInt(params[5]);
		 this.cidade=params[6];
		 this.numEmail=Integer.parseInt(params[7]);
		 for (int i = 8; i < 8+this.numEmail; i++) {
			this.emails.add(params[i]);
		}
		 this.saldoConta=Float.parseFloat(params[(params.length)-1]);
		 
	  }
}
