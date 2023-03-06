package environment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.FBManager;

/**
 * Classe para leitura/escrita das configurações do programa. As configurações
 * em questão estão gravadas no banco de dados do sistema.
 * @author Leandro Aparecido de Almeida
 */
public final class Config {

    /**
     * Constructor private para não permitir a instanciação.
     */
    private Config() {
    }
    
    /**
     * Definir uma configuração no formato String.
     * @param key nome da chave.
     * @param value valor string a ser gravado.
     */
    public static void setString(String key, String value) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE CONFIGURATION " +
            "SET F_1 = ? " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, value);
                pstm.setString(2, key);
                pstm.executeUpdate();
            }
        }
    }

    /**
     * Definir uma configuração no formato Integer.
     * @param key nome da chave.
     * @param value valor integer a ser gravado.
     */
    public static void setInteger(String key, int value) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE CONFIGURATION " +
            "SET F_2 = ? " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, value);
                pstm.setString(2, key);
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Definir uma configuração no formato Float.
     * @param key nome da chave.
     * @param value valor float a ser gravado.
     */
    public static void setFloat(String key, float value) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE CONFIGURATION " +
            "SET F_3 = ? " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setFloat(1, value);
                pstm.setString(2, key);
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Definir uma configuração no formato Boolean.
     * @param key nome da chave.
     * @param value valor boolean a ser gravado.
     */
    public static void setBoolean(String key, boolean value) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE CONFIGURATION " +
            "SET F_4 = ? " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setBoolean(1, value);
                pstm.setString(2, key);
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Obter o valor da configuração no formato String.
     * @param key nome da chave.
     * @return valor string da configuração.
     */
    public static String getString(String key) throws SQLException {
        String value = null;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "F_1 " +
            "FROM CONFIGURATION " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) { 
                pstm.setString(1, key);
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        value = rset.getString(1);
                    }
                }
            }
        }
        return value;
    }

    /**
     * Obter o valor da configuração no formato Integer.
     * @param key nome da chave.
     * @return valor integer da configuração.
     */
    public static int getInteger(String key) throws SQLException {
        int value = -1;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "F_2 " +
            "FROM CONFIGURATION " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) { 
                pstm.setString(1, key);
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        value = rset.getInt(1);
                    }
                }
            }
        }
        return value;
    }
    
    /**
     * Obter o valor da configuração no formato Float.
     * @param key nome da chave.
     * @return valor float da configuração.
     */
    public static float getFloat(String key) throws SQLException {
        float value = 0;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "F_3 " +
            "FROM CONFIGURATION " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {   
                pstm.setString(1, key);
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        value = rset.getFloat(1);
                    }
                }
            }
        }
        return value;
    }
    
    /**
     * Obter o valor da configuração no formato Boolean.
     * @param key nome da chave.
     * @return valor boolean da configuração.
     */
    public static boolean getBoolean(String key) throws SQLException {
        boolean value = false;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "F_4 " +
            "FROM CONFIGURATION " +
            "WHERE KEY = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstm.setString(1, key);
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        value = rset.getBoolean(1);
                    }
                }
            }
        }
        return value;
    }
        
}