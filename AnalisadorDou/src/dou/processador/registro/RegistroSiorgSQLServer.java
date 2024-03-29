package dou.processador.registro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import dou.processador.Ligacao;
import dou.processador1modo.Entidade;
import dou.processador1modo.Portaria;

public class RegistroSiorgSQLServer implements RegistroLigacaoStrategy
{

	Connection conn;
	protected final Logger log = Logger.getLogger(RegistroSiorgSQLServer.class);

	public RegistroSiorgSQLServer()
	{
		// Cria conexao com o banco para guardar os dados apenas uma vez
		try
		{
			conn = DriverManager
					.getConnection("jdbc:jtds:sqlserver://RAFAEL-PC:1433/dou;instance=SQLEXPRESS;user=douuser;password=1234");
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void registrar(Ligacao l)
	{
		// if (l.tipoEntidade == "Orgao")
		inserirNoBanco(l.entidade, String.valueOf(l.idEntidade), l.particao, l.identificacaoPortaria, l.textoPortaria,
				l.inicioPortaria, l.fimPortaria, l.inicioEntidade, l.fimEntidade, l.tipoEntidade, l.data);
	}

	private void inserirNoBanco(String entidade, String codigoSiorg, int particao, String identificacaoPortaria,
			String textoPortaria, Long offsetPortIni, Long offsetPortEnd, Long offsetEntIni, Long offsetEntEnd, String tipo,
			Date data)
	{

		try
		{

			long insertStart = System.currentTimeMillis();

			String SPsql = "EXEC [processaOrgaoSiorg] ?,?,?,?,?,?,?,?,?,?,?";

			PreparedStatement ps = conn.prepareStatement(SPsql);
			ps.setEscapeProcessing(true);
			ps.setQueryTimeout(1000);

			ps.setString(1, entidade);
			ps.setString(2, codigoSiorg);
			ps.setInt(3, particao);
			ps.setString(4, identificacaoPortaria);
			ps.setString(5, textoPortaria);
			ps.setLong(6, offsetPortIni);
			ps.setLong(7, offsetPortEnd);
			ps.setLong(8, offsetEntIni);
			ps.setLong(9, offsetEntEnd);
			ps.setString(10, tipo);
			ps.setDate(11, new java.sql.Date(data.getTime()));

			ps.executeQuery();

			long insertEnd = System.currentTimeMillis();

			if ((insertEnd - insertStart) > 200)
				log.warn("Insert: " + (insertEnd - insertStart) + " ms");

		} catch (Exception e)
		{
			log.warn("Parametros: " + entidade + ", " + identificacaoPortaria + ", " + "textoPortaria" + ", " + offsetPortIni
					+ ", " + offsetPortEnd + ", " + offsetEntIni + ", " + offsetEntEnd + ", " + tipo + ", " + data);
			log.warn("Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void registrar1Modo(Entidade entidadeA, Entidade entidadeB, Portaria portaria)
	{
		throw new NotImplementedException();

	}

}
