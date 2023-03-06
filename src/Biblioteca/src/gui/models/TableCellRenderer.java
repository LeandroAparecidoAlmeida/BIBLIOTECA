package gui.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import database.FBManager;
import environment.Config;

/**
 * Renderizador personalizado para JTable que tem a finalidade de destacar
 * com cor diferenciada os livros que foram descartados ou que estão emprestados,
 * de acordo com o configurado pelo usuário.
 * @author Leandro Aparecido de Almeida
 */
public final class TableCellRenderer implements javax.swing.table.TableCellRenderer {
    
    /**Lista dos livros que estão emprestados (identificadores dos livros).*/
    private final List<Integer> listBorrowed;
    /**Lista dos livros que foram descartados (identificadores dos livros).*/
    private final List<Integer> listDiscarded;
    /**Destacar livros que estão emprestados.*/
    private final boolean highligthBorrowed;
    /**Destacar livros que foram descartados.*/
    private final boolean highligthDiscarded;
    /**Cor de fonte para livros descartados.*/
    private Color foregroundDiscarded;
    /**Cor de fonte para livros emprestados.*/
    private Color foregroundBorrowed;
    /**Estilo de fonte para livros descartados.*/
    private int fontStyleDiscarded;
    /**Estilo de fonte para livros emprestados.*/
    private int fontStyleBorrowed;

    /**
     * Constructor da classe.
     * @param highligthBorrowed destacar livros que estão emprestados. Se
     * <b>true</b> os livros que estão emprestados aparecerão na JTable com
     * uma cor diferenciada, configurada pelo usuário.
     * @param highligthDiscarded destacar livros que foram descartados.  Se
     * <b>true</b> os livros que estão descartados aparecerão na JTable com
     * uma cor diferenciada, configurada pelo usuário.
     */
    public TableCellRenderer(boolean highligthBorrowed, boolean highligthDiscarded) throws SQLException {
        this.highligthBorrowed = highligthBorrowed;
        this.highligthDiscarded = highligthDiscarded;
        listBorrowed = new ArrayList<>();
        listDiscarded = new ArrayList<>();
        if (this.highligthBorrowed) {
            foregroundBorrowed = new Color(Config.getInteger("foreground_1"));
            fontStyleBorrowed = Config.getInteger("font_style_1");
            try (Connection connection = FBManager.getConnection()) {
                String sql = 
                "SELECT " +
                    "LI.ID_BOOK " +
                "FROM LOAN_ITEM LI " +  
                "JOIN LOAN L ON (LI.ID_LOAN = L.ID) " +  
                "WHERE LI.RETURNED = FALSE;";
                PreparedStatement pstm = connection.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
                );
                ResultSet rs = pstm.executeQuery();
                rs.beforeFirst();
                while (rs.next()) {
                    listBorrowed.add(rs.getInt(1));
                }
            }
        }
        if (this.highligthDiscarded) {
            foregroundDiscarded = new Color(Config.getInteger("foreground_2"));            
            fontStyleDiscarded = Config.getInteger("font_style_2");
            try (Connection connection = FBManager.getConnection()) {
                String sql = 
                "SELECT " +
                    "ID_BOOK " +
                "FROM DISCARDED_BOOK;";
                PreparedStatement pstm = connection.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
                );
                ResultSet rs = pstm.executeQuery();
                rs.beforeFirst();
                while (rs.next()) {
                    listDiscarded.add(rs.getInt(1));
                }
            }
        }
    }

    /**Retorna o componente de exibição de uma célula configurado (uma JLabel).*/
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel(value.toString());        
        label.setOpaque(true);
        label.setForeground(Color.BLACK);
        if (highligthDiscarded) {
            if (listDiscarded.contains((int)table.getValueAt(row, 0))) {
                Font font = new Font("tahoma", fontStyleDiscarded, 11);
                label.setFont(font);
                label.setForeground(foregroundDiscarded);
            } 
        }
        if (highligthBorrowed) {
            if (listBorrowed.contains((int)table.getValueAt(row, 0))) {
                Font font = new Font("tahoma", fontStyleBorrowed, 11);
                label.setFont(font);
                label.setForeground(foregroundBorrowed);
            }            
        }        
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
        } else {
            label.setBackground(table.getBackground());
        }
        return label;
    }
    
}
