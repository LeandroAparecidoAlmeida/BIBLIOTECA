package database;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * Classe para o gerenciamento de conexão com o banco de dados Firebird.
 * @author Leandro Aparecido de Almeida
 */
public final class FBManager {

    /**Constante codificação de caracteres do banco de dados.*/
    private final static String ENCODING = "ISO8859_1";
    /**URL para a conexão com o banco de dados.*/
    private static String url;
    /**Usuário do Firebird.*/
    private static String user;
    /**Senha do usuário do Firebird.*/
    private static String password;
    
    static {
        //Lê o arquivo XML "firebird.connection.xml" que está na raiz do sistema
        //para obter os parâmetros da conexão com o banco de dados Firebird (arquivo
        //do banco de dados, host, porta, etc).
        try {
            //Carrega o JDBC Driver do Firebird.
            Driver driver = (Driver) Class.forName("org.firebirdsql.jdbc.FBDriver")
            .newInstance();
            DriverManager.registerDriver(driver);
            //Lê o arquivo XML com as configurações.
            File file = new File("firebird.connection.xml");
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            Element root = (Element) doc.getRootElement();
            //Embedded: conectar com o Firebird embarcado.
            boolean embedded = root.getAttribute("embedded").getBooleanValue();
            //Arquivo do banco de dados Firebird.
            File fbFile = new File(root.getChild("file").getText());        
            //Usuário do Firebird.
            FBManager.user = root.getChild("user").getText();
            //Senha do usuário do Firebird.
            FBManager.password = root.getChild("password").getText();        
            if (!embedded) { 
                /*Conexão em modo TCP/IP com Firebird Server.*/
                //Host aonde está hospedado o Firebird Server.
                String host = root.getChild("host").getText();
                //Porta do Firebird Server.
                String port = root.getChild("port").getText();            
                FBManager.url = "jdbc:firebirdsql:" + host + "/" + port +
                ":" + fbFile.getAbsolutePath() + "?encoding=" + ENCODING;
            } else {
                /*Conexão local com Firebird Embedded (embarcado).*/
                url = "jdbc:firebirdsql:embedded:" + fbFile.getAbsolutePath() +
                "?encoding=" + ENCODING;
            }
        } catch (Exception ex) {
            url = null;
            user = null;
            password = null;
            ex.printStackTrace();
        }
    }

    private FBManager() {
    }

    /**
     * Obter uma instancia de {@link Connection} para a conexão com o banco
     * de dados Firebird.
     * @param autoCommit se <b>true</b>, faz o commit no banco de dados
     * automaticamente, se <b>false</b>, é necessário chamar o commit no
     * código (usado para operações com transactions).
     * @return instancia de {@link Connection}.
     * @throws SQLException erro ao conectar com o Banco de dados.
     */
    public static Connection getConnection(boolean autoCommit) throws SQLException {  
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(autoCommit);
        return connection;
    }
    
    /**
     * O mesmo que {@link #getConnection(boolean)}, porém configurado para 
     * commit automático.
     * @return instancia de {@link Connection}.
     * @throws SQLException erro ao conectar com o Banco de dados. 
     */
    public static Connection getConnection() throws SQLException { 
        return getConnection(true);
    }
    
}