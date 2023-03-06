package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import database.FBManager;

/**
 * Classe para manutenção do acervo.
 * @author Leandro Aparecido de Almeida
 */
public final class Collection {

    private Collection() {
    }
    
    /**
     * Cadastrar um novo livro no acervo.
     * @param book novo livro.
     * @return identificador (id) do novo livro cadastrado.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static int addNewBook(Book book) throws SQLException {
        int bookId = -1;
        try (Connection connection = FBManager.getConnection()) {
            //Cria o novo registro de livro.
            String sql1 =
            "INSERT INTO BOOK ( " +
                "TITLE, " +
                "SUBTITLE, " +
                "PURCHASE_DATE, " +
                "AUTHOR, " +
                "PUBLISHER, "+
                "ISBN, " +
                "EDITION, " +
                "VOLUME, " +
                "ISSUE_YEAR, " +
                "NUM_PAGES, " +
                "SUMMARY, " +
                "COVER " +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement pstm1 = connection.prepareStatement(sql1)) {
                pstm1.setString(1, book.getTitle());
                pstm1.setString(2, book.getSubtitle());
                pstm1.setDate(3, (java.sql.Date)book.getPurchaseDate());
                pstm1.setString(4, book.getAuthor());
                pstm1.setString(5, book.getPublisher());
                pstm1.setString(6, book.getIsbn());
                pstm1.setInt(7, book.getEdition());
                pstm1.setInt(8, book.getVolume());
                pstm1.setInt(9, book.getIssueYear());
                pstm1.setInt(10, book.getNumPages());
                pstm1.setString(11, book.getSummary());
                if (book.getCover() == null) {
                    pstm1.setNull(12, Types.BLOB);
                } else {
                    pstm1.setBlob(12, book.getCover());
                }
                pstm1.executeUpdate();
            }
            //Recupera o identificador (id) do novo livro.
            String sql2 = "SELECT MAX(ID) FROM BOOK;";
            try (PreparedStatement pstm2 = connection.prepareStatement(sql2,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); 
            ResultSet rset = pstm2.executeQuery()) {
                if (rset.first()) {
                    bookId = rset.getInt(1);
                }
            }            
        }
        return bookId;
    }
    
    /**
     * Alterar os dados de cadastro de um livro.
     * @param book livro a ser alterado, identificado pelo id.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void updateBook(Book book) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql =                     
            "UPDATE BOOK " +
            "SET " +
                "TITLE = ?, " +
                "SUBTITLE = ?, " +
                "PURCHASE_DATE = ?, " +
                "AUTHOR = ?, " +
                "PUBLISHER = ?, " +
                "ISBN = ?, " +
                "EDITION = ?, " +
                "VOLUME = ?, " +
                "ISSUE_YEAR = ?, " +
                "NUM_PAGES = ?, ";
            if (book.updateCover) {       
                sql += 
                "SUMMARY = ?, " +
                "COVER = ? ";
            } else {
                sql += "SUMMARY = ? ";
            }
            sql += "WHERE ID = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, book.getTitle());
                pstm.setString(2, book.getSubtitle());
                pstm.setDate(3, (java.sql.Date)book.getPurchaseDate());
                pstm.setString(4, book.getAuthor());
                pstm.setString(5, book.getPublisher());
                pstm.setString(6, book.getIsbn());
                pstm.setInt(7, book.getEdition());
                pstm.setInt(8, book.getVolume());
                pstm.setInt(9, book.getIssueYear());
                pstm.setInt(10, book.getNumPages());
                pstm.setString(11, book.getSummary());
                if (book.updateCover) {
                    if (book.getCover() == null) {
                        pstm.setNull(12, Types.BLOB);
                    } else {
                        pstm.setBlob(12, book.getCover());
                    }
                    pstm.setInt(13, book.getId());
                } else {
                    pstm.setInt(12, book.getId());
                }            
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Excluir (eliminar) um livro do acervo (somente em caso de erro no cadastro).
     * @param book livro ser excluído, identificado pelo id.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void deleteBook(Book book) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = "DELETE FROM BOOK WHERE ID = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, book.getId());
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Descartar um livro (manter o registro, mas definir como descartado).
     * @param book livro a ser descartado, identificado pelo id.
     * @param reason motivo do descarte do livro.
     * @param date data do descarte do livro.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void discardBook(Book book, String reason, Date date) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "INSERT INTO DISCARDED_BOOK (ID_BOOK, REASON, DISCARD_DATE) " + 
            "VALUES (?, ?, ?);";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, book.getId());
                pstm.setString(2, reason);
                pstm.setDate(3, (java.sql.Date) date);
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Reverter o descarte de um livro.
     * @param book livro a se reverter o descarte, identificado pelo id.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void revertBookDiscard(Book book) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = "DELETE FROM DISCARDED_BOOK WHERE ID_BOOK = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setInt(1, book.getId());
                pstm.executeUpdate();
            }
        }
    }
    
    /**
     * Obter o motivo do descarte de um livro.
     * @param book livro descartado.
     * @return texto com o motivo do descarte de um livro.
     * @throws SQLException erro no acesso ao banco de dados. 
     */
    public static String getDiscardReason(Book book) throws SQLException {
        String description = null;
        try (Connection connection = FBManager.getConnection()) {  
            String sql = "SELECT REASON FROM DISCARDED_BOOK WHERE ID_BOOK = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstm.setInt(1, book.getId());
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        description = rset.getString(1);
                    }
                }
            }
        }
        return description;
    }
    
    /**
     * Alterar o motivo de descarte de um livro.
     * @param book livro descartado.
     * @param newReason novo texto de motivo do descarte do livro.
     * @throws SQLException erro no acesso ao banco de dados.
     */
    public static void setDiscardReason(Book book, String newReason) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE DISCARDED_BOOK " +
            "SET " +
                "REASON = ? " +
            "WHERE ID_BOOK = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                pstm.setString(1, newReason);
                pstm.setInt(2, book.getId());
                pstm.executeUpdate();
            }            
        }
    }
    
    /**
     * Registrar o empréstimo de livros.
     * @param loanData dados do empréstimo.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void borrowBook(LoanData loanData) throws SQLException {
        //Pelo menos um livro deve ser emprestado.
        if (loanData.getBooks().isEmpty()) {
            throw new SQLException(
                "Pelo menos 01 livro deve estar associado ao empréstimo."
            );
        }
        try (Connection connection = FBManager.getConnection(false)) {
            //Criar o registro do empréstimo.
            String sql1 = 
            "INSERT INTO LOAN(LOAN_DATE, APPLICANT) VALUES (?, ?);";
            try (PreparedStatement pstm1 = connection.prepareStatement(sql1)) {
                pstm1.setDate(1, (java.sql.Date)loanData.getDate());
                pstm1.setString(2, loanData.getApplicant());
                pstm1.executeUpdate();
            }
            int loanId = 0;
            String sql2 = "SELECT MAX(ID) FROM LOAN;";
            try (PreparedStatement pstm2 = connection.prepareStatement(sql2,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); 
            ResultSet rset = pstm2.executeQuery()) {
                if (rset.first()) {                  
                    loanId = rset.getInt(1);
                }
            }
            String sql3 = 
            "INSERT INTO LOAN_ITEM(ID_LOAN, ID_BOOK) VALUES (?, ?);";
            //Inserir os livros emprestados associando àquele registro.
            for (Book book: loanData.getBooks()) {                
                try (PreparedStatement pstm3 = connection.prepareStatement(sql3)) {
                    pstm3.setInt(1, loanId);
                    pstm3.setInt(2, book.getId());
                    pstm3.executeUpdate();
                }
            }
            connection.commit();
        }
    }
    
    /**
     * Registrar a devolução de livros emprestados. No objeto LoanData do
     * parâmetro deve ter a lista com os livros que estão sendo devolvidos,
     * e a data deve ser a data da devolução dos livros.
     * @param loanData dados do empréstimo relacionado, identificado pelo id.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void returnBook(LoanData loanData) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE LOAN_ITEM " +
            "SET " +
                "RETURNED = TRUE, " +
                "RETURN_DATE = ? " +
            "WHERE ID_LOAN = ? AND ID_BOOK = ?;";
            for (Book book : loanData.getBooks()) {                
                try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                    pstm.setDate(1, (java.sql.Date) loanData.getDate());
                    pstm.setInt(2, loanData.getId());
                    pstm.setInt(3, book.getId());                    
                    pstm.executeUpdate();
                }
            }
        }
    }
    
    /**
     * Reverter (cancelar) a devolução de livros. No objeto empréstimo do 
     * parâmetro deve ter a lista dos livros que vai cancelar a devolução.
     * @param loanData empréstimo relacionado, identificado pelo id.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void revertBookReturn(LoanData loanData) throws SQLException {
        try (Connection connection = FBManager.getConnection()) {
            String sql = 
            "UPDATE LOAN_ITEM " +
            "SET " +
                "RETURNED = FALSE, " +
                "RETURN_DATE = NULL " +
            "WHERE ID_LOAN = ? AND ID_BOOK = ?;";
            for (Book book : loanData.getBooks()) {              
                try (PreparedStatement pstm = connection.prepareStatement(sql)) {
                    pstm.setInt(1, loanData.getId());
                    pstm.setInt(2, book.getId());
                    pstm.executeUpdate();
                }                
            }            
        }
    }
    
    /**
     * Excluir (eliminar) os livros de um empréstimo. No objeto empréstimo do
     * parâmetro deve ter a lista dos livros que vão ser excluídos.
     * @param loanData empréstimo relacionado, identificado pelo id.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static void deleteBookFromLoan(LoanData loanData) throws SQLException {
        try (Connection connection = FBManager.getConnection(false)) {
            //Exclui os livros do empréstimo.
            String sql1 = 
            "DELETE FROM LOAN_ITEM WHERE ID_LOAN = ? AND ID_BOOK = ?;";
            for (Book book : loanData.getBooks()) {
                try (PreparedStatement pstm1 = connection.prepareStatement(sql1)) {
                    pstm1.setInt(1, loanData.getId());
                    pstm1.setInt(2, book.getId());
                    pstm1.executeUpdate();
                }
            }            
            //Recupera quantos livros ficaram ainda do empréstimo após a
            //exclusão dos livros.
            int count = 0;
            String sql2 = 
            "SELECT " +
                "COUNT(*) " +
            "FROM LOAN_ITEM " +
            "WHERE ID_LOAN = ?;";
            try (PreparedStatement pstm2 = connection.prepareStatement(sql2,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstm2.setInt(1, loanData.getId());
                try (ResultSet rset = pstm2.executeQuery()) {
                    if (rset.first()) {
                        count = rset.getInt(1);
                    }
                }
            }
            //Se no empréstimo não restou nenhum livro mais, exclui o registro
            //do empréstimo.
            if (count == 0) {
                String sql3 = "DELETE FROM LOAN WHERE ID = ?;";
                try (PreparedStatement pstm3 = connection.prepareStatement(sql3)) {
                    pstm3.setInt(1, loanData.getId());
                    pstm3.executeUpdate();
                }
            }
            connection.commit();
        }
    }
    
    /**
     * Verificar se o livro está emprestado.
     * @param book livro a se verificar se está emprestado, identificado pelo id.
     * @return true, o livro está emprestado, false, o livro não está emprestado.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static boolean isBorrowedBook(Book book) throws SQLException {
        boolean borrowed = false;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "COUNT(*) " +
            "FROM LOAN_ITEM " +
            "WHERE ID_BOOK = ? AND RETURNED = FALSE;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {   
                pstm.setInt(1, book.getId());
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        borrowed = rset.getInt(1) > 0;
                    }
                }
            }
        }
        return borrowed;
    }
    
    /**
     * Verificar se o livro está descartado.
     * @param book livro a verificar se está descartado.
     * @return true, o livro está descartado, false, o livro não está
     * descartado.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static boolean isDiscardedBook(Book book) throws SQLException {
        boolean discarded = false;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "COUNT(*) " +
            "FROM DISCARDED_BOOK " +
            "WHERE ID_BOOK = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                pstm.setInt(1, book.getId());
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        discarded = rset.getInt(1) > 0;
                    }
                }
            }
        }
        return discarded;
    }
    
    /**
     * Obter o livro identificado pelo id (identificador).
     * @param bookId identificador (id) do livro.
     * @param loadCover se true, carrega os binários da capa, se false,
     * carrega como null (economia de memória, melhor tempo de processamento).
     * @return livro correspondente ao identificador (id) ou null, caso nenhum
     * livro esteja associado ao identificador.
     * @throws java.sql.SQLException erro no acesso ao banco de dados.
     */
    public static Book getBook(int bookId, boolean loadCover) throws SQLException {
        Book book = null;
        try (Connection connection = FBManager.getConnection()) {  
            String sql =
            "SELECT " +
                "ID, " +
                "TITLE, " +
                "SUBTITLE, " +
                "PURCHASE_DATE, " +
                "AUTHOR, " +
                "PUBLISHER, " +
                "ISBN, " +
                "EDITION, " +
                "VOLUME, " +
                "ISSUE_YEAR, " +
                "NUM_PAGES, ";
                if (loadCover) {
                    sql +=
                    "SUMMARY, " +
                    "COVER ";
                } else {
                    sql +=
                    "SUMMARY ";
                }
            sql +=
            "FROM BOOK " +
            "WHERE ID = ?;";
            try (PreparedStatement pstm = connection.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) { 
                pstm.setInt(1, bookId);
                try (ResultSet rset = pstm.executeQuery()) {
                    if (rset.first()) {
                        book = new Book(
                            rset.getInt(1),
                            rset.getString(2),
                            rset.getString(3),
                            rset.getDate(4),
                            rset.getString(5),
                            rset.getString(6),
                            rset.getString(7),
                            rset.getInt(8),
                            rset.getInt(9),
                            rset.getInt(10),
                            rset.getInt(11),
                            rset.getString(12),
                            loadCover ? rset.getBlob(13) : null
                        );
                    }
                }
            }
        }
        return book;
    }
    
}