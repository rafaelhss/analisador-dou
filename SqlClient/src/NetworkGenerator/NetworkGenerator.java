package NetworkGenerator;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NetworkGenerator {
/*
	public static void main(String[] args) throws Exception {
		
		String tiposEntidade = " ";
		String filtroEntidades = "(Instituto AND Federal) OR ((Universidade AND Minas) OR (Universidade AND Minas))";
		String filtroPortarias = "Defesa AND Seguran�a";
		
		
		geraRede(filtroPortarias , filtroEntidades, tiposEntidade);
		
	}
	
	*/
	private String ConnectionString = "jdbc:sqlite:@DBNAME@";

	public NetworkGenerator(String dbname)
	{ //database/dou2.db
		
		ConnectionString = ConnectionString.replace("@DBNAME@", dbname);
		
	}
	
	
	public void geraRede(String filtroPortarias, String filtroEntidades, String tiposEntidade) throws ClassNotFoundException, SQLException {
		StringBuilder sb = new StringBuilder();
		
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(ConnectionString);
		
		
		preparaBanco(conn);
		
		populaTabelasAuxiliares(conn, filtroEntidades, filtroPortarias, tiposEntidade);
		
		populaTabeladeVertices(conn);
		
		sb.append(geraCabecalho(conn));
		
		sb.append(geraListaVertices(conn));
		
		sb.append("\n*Arcs\n");
		
		sb.append(geraListaArcos(conn));
		
		System.out.println("Teste: " + sb.toString());
	}



	private  String buscaRegistros(Connection conn, String coluna, String sql) throws SQLException
	{
		StringBuilder sb = new StringBuilder();
		
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);
		
				
		while (rs.next()) {
			sb.append(rs.getString(coluna));
			sb.append("\n");
		}
		rs.close();
		if(sb.length() > 0)
			return sb.toString();
		else
			return "Erro ao buscar " + coluna; 
	}
		
	private  Object geraListaArcos(Connection conn) throws SQLException {

		String sql = " SELECT" +
				"		cast(v.ID as nvarchar) || ' ' || cast(v2.id as nvarchar) || ' 1 [' || cast(pe.Tempo as nvarchar) || ']' AS Arcos" +
				"	FROM" +
				"		PortariaEntidadeFiltrada pe" +
				"	LEFT JOIN" +
				"		Vertice v on pe.IDEntidade = v.IdOriginal" +
				"	LEFT JOIN" +
				"		Vertice v2 on pe.IdPortaria = v2.IdOriginal" +
				"	ORDER BY pe.Tempo";	

		return buscaRegistros(conn, "Arcos", sql);
	}
	
	private  Object geraListaVertices(Connection conn) throws SQLException {
		String sql = "SELECT" +
				"				CAST(ID as NVARCHAR) ||  '  ' || Entidade  AS Vertices" +
				"				FROM" +
				"				Vertice";

		return buscaRegistros(conn, "Vertices", sql);
	}

	private  String geraCabecalho(Connection conn) throws SQLException {
		
	String sql ="SELECT" +
				"		'*Vertices ' ||" +
				"		(" +
				"			CAST((SELECT count(*) from TbEntidade WHERE IdEntidade in (SELECT IdEntidade FROM PortariaEntidadeFiltrada)) AS INTEGER)" +
				"			+ CAST((SELECT count(*) from TbPortaria WHERE IdPortaria in (SELECT IdPortaria FROM PortariaEntidadeFiltrada)) AS INTEGER)" +
				"		) ||" +
				"		' ' ||" +
				"		(SELECT count(*) from TbEntidade WHERE IdEntidade in (SELECT IdEntidade FROM PortariaEntidadeFiltrada)) " +
				" AS Cabecalho";

			return buscaRegistros(conn, "Cabecalho", sql);
	}
	
	private  void populaTabeladeVertices(Connection conn) throws SQLException {
		Statement stat = conn.createStatement();
		stat.execute("INSERT INTO Vertice (IdOriginal, Entidade)" +
				"		SELECT" +
				"			IdEntidade as IdOriginal," +
				"			\'\"\' || Texto || \'\"                                      0.0000    0.0000    0.5000 \' AS Entidade" +
				"		FROM" +
				"			TbEntidade" +
				"		WHERE" +
				"			IdEntidade in (SELECT IdEntidade FROM PortariaEntidadeFiltrada)");			
			
		stat.execute("INSERT INTO Vertice (IdOriginal, Entidade)" +
				"		SELECT" +
				"			IdPortaria as IdOriginal," +
				"			 \'\"\' || cast(Identificacao as nvarchar) || \'\"                    0.0000    0.0000    0.5000 \' AS Entidade" +
				"		FROM" +
				"			TbPortaria" +
				"		WHERE" +
				"			IdPortaria in (SELECT IdPortaria FROM PortariaEntidadeFiltrada)");
	}

	
	private  void populaTabelasAuxiliares(Connection conn,
			String filtroEntidades, String filtroPortarias, String tiposEntidade) throws SQLException {
		
		
		Statement stat = conn.createStatement();
		
		String sqlEnt = " INSERT INTO EntidadeFiltrada" +
				" 	SELECT * FROM TbEntidade" +
				"	 @WHERECLAUSE@	";
		
		stat.execute(sqlEnt.replace("@WHERECLAUSE@", preparaFiltro(tiposEntidade, filtroEntidades)));
		
		
		
		String sqlPortEnt = "	INSERT INTO PortariaEntidadeFiltrada" +
				"			SELECT" +
				"				TbPortariaEntidade.*" +
				"			FROM" +
				"				TbPortaria" +
				"			JOIN" +
				"				TbPortariaEntidade  ON TbPortaria.IdPortaria = TbPortariaEntidade.IdPortaria" +
				"	 @WHERECLAUSE@	";
		stat.execute(sqlPortEnt.replace("@WHERECLAUSE@", preparaFiltro(tiposEntidade, filtroPortarias)));
	}

	
	private  String preparaFiltro(String tiposEntidade,
			String filtroTexto) {
		final String FILTRO_TIPO_ENTIDADE = " TipoEntidade in (@TIPO@)";
		
		String WClause = "";
		
		
		//prepara tipo
		if(tiposEntidade.trim().length() > 0)
		{
			String[] tokens = tiposEntidade.split(",");
			for (String token : tokens) {
				token = token.trim();
				if(token.length() > 0)
				{
					if(tiposEntidade.indexOf("\'" + token + "\'") < 0) //evita problema com palavras repetidas
						tiposEntidade = tiposEntidade.replace(token, "\'" + token + "\'");
				}
			}
			tiposEntidade = FILTRO_TIPO_ENTIDADE.replace("@TIPO@", tiposEntidade);
			
			WClause = "WHERE " + tiposEntidade;
			
		}
		
		
		//prepara filtro
		if(filtroTexto.trim().length() > 0)
		{
			String[] tokens = filtroTexto.split("AND|OR|\\(+|\\)+");
			
			for (String token : tokens) {
				token = token.trim();
				if(token.length() > 0)
				{
					if(filtroTexto.indexOf("Texto like \'%" + token + "%\'") < 0) //evita problema com palavras repetidas
						filtroTexto = filtroTexto.replace(token, "Texto like \'%" + token + "%\'");
				}
			}
			
			if (WClause.trim().length() > 0)
			{
				WClause += "AND (" + filtroTexto +")";
			}
			else
			{
				WClause = "WHERE " + filtroTexto;
			}
			
			
		}
		return WClause;
	}

	
	
	// Cria as tabelas temporarias
	private  void preparaBanco(Connection conn) throws SQLException {
		
		Statement stat = conn.createStatement();
		stat.execute("drop table if exists EntidadeFiltrada");
		
		stat.execute(" CREATE TABLE if not exists EntidadeFiltrada" +
				"		(" +
				"			IdEntidade INTEGER NOT NULL," +
				"			Texto NVARCHAR(100)," +
				"			TipoEntidade NVARCHAR(10)," +
				"		PRIMARY KEY (IdEntidade )," +
				"		UNIQUE (IdEntidade )) ");		
		
		
		stat.execute("drop table if exists PortariaEntidadeFiltrada");
		stat.execute(" CREATE TABLE if not exists PortariaEntidadeFiltrada" +
				"		(" +
				"			IdPortaria INTEGER NOT NULL," +
				"			IdEntidade INTEGER NOT NULL," +
				"			TipoLigacao INTEGER," +
				"			Tempo datetime" +
				"		)" );
		
		stat.execute(" drop table if exists Vertice");
		stat.execute(" CREATE TABLE if not exists Vertice" +
				"		(" +
				"			ID INTEGER PRIMARY KEY AUTOINCREMENT,			IdOriginal BIGINT," +
				"			Entidade NVARCHAR(300)" +
				"		)" );
		
	}
	
		
	

	private  void criaForm() {
		Frame frm = new Frame("Extrator de redes");
		frm.setSize(450, 200);
		frm.setVisible(true);
		frm.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Panel p = new Panel();
		Panel p1 = new Panel();
		Label jFirstName = new Label("Filtro Entidades [Separe por virgula]:");
		TextField lFirstName = new TextField(20);

		Label jLastName = new Label("Filtro Portarias [Separe por virgula]:");
		TextField lLastName = new TextField(20);

		p.setLayout(new GridLayout(3, 1));
		p.add(jFirstName);
		p.add(lFirstName);
		p.add(jLastName);
		p.add(lLastName);
		Button Submit = new Button("Submit");
		p.add(Submit);
		p1.add(p);
		frm.add(p1, BorderLayout.NORTH);
	}

	/*
	 * 
	 * Class.forName("org.sqlite.JDBC"); Connection conn = DriverManager
	 * .getConnection("jdbc:sqlite:database\\dou.db"); Statement stat =
	 * conn.createStatement(); ResultSet rs =
	 * stat.executeQuery("select * from tbentidade;"); while (rs.next()) {
	 * System.out.println("id = " + rs.getString("IdEntidade") + " Texto = " +
	 * rs.getString("Texto"));
	 * 
	 * } rs.close(); conn.close();
	 */

	public void testeStackOverflow() throws Exception {

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists people;");
		stat.executeUpdate("create table people (name, occupation);");
		PreparedStatement prep = conn
				.prepareStatement("insert into people values (?, ?);");

		prep.setString(1, "Gandhi");
		prep.setString(2, "politics");
		prep.addBatch();
		prep.setString(1, "Turing");
		prep.setString(2, "computers");
		prep.addBatch();
		prep.setString(1, "Wittgenstein");
		prep.setString(2, "smartypants");
		prep.addBatch();

		conn.setAutoCommit(false);
		prep.executeBatch();
		conn.setAutoCommit(true);

		ResultSet rs = stat.executeQuery("select * from people;");
		while (rs.next()) {
			System.out.println("name = " + rs.getString("name"));
			System.out.println("job = " + rs.getString("occupation"));
		}
		rs.close();
		conn.close();
	}

}