package gui.dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import database.FBManager;
import utils.StrParser;
import dialogs.ErrorDialog;
import gui.models.TableCellRenderer;

/**
 * Filtro padrão para a seleção de um ou mais livros cadastrados.
 * @author Leandro Aparecido de Almeida
 */
public final class SelectBooksDialog extends javax.swing.JDialog {

    /**Opção para exibição de todos os livros, descartados ou não.*/
    public static final int SHOW_ALL = 1;
    /**Opção para exibição somente dos livros que não foram descartados.*/
    public static final int SHOW_ONLY_NOT_DISCARDED = 2;
    /**Opção para exibição somente dos livros que foram descartados.*/
    public static final int SHOW_ONLY_DISCARDED = 3;
    /**Opção para exibição somente dos livros que não estão emprestados.*/
    public static final int SHOW_ONLY_NOT_BORROWED = 4;
    /**Opção para exibição somente dos livros que estão emprestados.*/
    public static final int SHOW_ONLY_BORROWED = 5;
    /**Destacar os livros que estão emprestados com cor diferenciada.*/
    private final boolean highligthBorrowed;
    /**Destacar os livros que estão descartados com cor diferenciada.*/
    private final boolean highligthDiscarded;
    /**Permitir a seleção de múltiplos livros na JTable.*/
    private final boolean selectMultipleInterval;
    /**Lista dos livros que não serão exibidos na JTable.*/
    private final List<Integer> hiddenBooksIds;
    /**Lista dos livros selecionados pelo usuário na JTable.*/
    private final List<Integer> booksIdsList;
    /**Opção de exibição definida no constructor da classe.*/
    private final int showOption;
    /**Indica se o usuário clicou no botão cancelar.*/
    private boolean canceled;
    
    /**
     * Constructor da classe.
     * @param parent frame proprietário.
     * @param showOption opção de exibição dos livros, conforme as
     * constantes desta classe:
     * <br>
     * <ul> 
     * <li>SHOW_ALL: exibir todos os livros, descartados ou não.</li>
     * <li>SHOW_ONLY_NOT_DISCARDED: exibir somente os livros que 
     * não foram descartados.</li>
     * <li>SHOW_ONLY_DISCARDED: exibir somente os livros que foram
     * descartados</li>
     * <li>SHOW_ONLY_NOT_BORROWED: exibir somente os livros não
     * descartados que não estão emprestados.</li>
     * <li>SHOW_ONLY_BORROWED: exibir somente os livros que estão
     * emprestados.</li>
     * </ul>
     * @param highligthBorrowed se <b>true</b>, vai destacar os livros
     * que estão emprestados com uma cor diferenciada dos outros que não
     * estão.
     * @param highligthDiscarded se <b>true</b>, vai destacar os livros
     * que estão descartados com uma cor diferenciada dos outros que não
     * estão.
     * @param selectMultipleInterval se <b>true</b> vai permitir a seleção de 
     * múltiplos livros na JTable, se <b>false</b>, vai permitir a seleção
     * de apenas um único livro.
     * @param hiddenBooksIds lista dos livros que não serão exibidos, mesmo
     * obedecendo aos critérios de exibição.
     */
    public SelectBooksDialog(java.awt.Frame parent, int showOption, 
    boolean highligthBorrowed, boolean highligthDiscarded, 
    boolean selectMultipleInterval, List<Integer> hiddenBooksIds) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        this.booksIdsList = new ArrayList<>();
        this.canceled = true;
        this.showOption = showOption;
        this.highligthBorrowed = highligthBorrowed;
        this.highligthDiscarded = highligthDiscarded;
        this.selectMultipleInterval = selectMultipleInterval;
        this.hiddenBooksIds = hiddenBooksIds;
        int selectionModel;
        if (this.selectMultipleInterval) {
            selectionModel = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
            setTitle("SELECIONAR OS LIVROS");
        } else {
            selectionModel = ListSelectionModel.SINGLE_SELECTION;
            setTitle("SELECIONAR O LIVRO");
        }
        jtBooks.setSelectionMode(selectionModel);
        listBooks();
    }
    
    /**
     * Atualizar a lista com os livros filtrados de acordo com o critério
     * definido pelo usuário.
     */
    private void listBooks() {      
        try {  
            try (Connection connection = FBManager.getConnection()) {
                StringBuilder sb = new StringBuilder();
                sb.append("-1");
                if (hiddenBooksIds != null) {
                    for (Integer id : hiddenBooksIds) {
                        sb.append(", ");
                        sb.append(String.valueOf(id));
                    }
                }
                String hiddenBooksIdsStr = sb.toString();
                String sql =
                "SELECT " +
                    "ID, " +                    
                    "TITLE, " +                
                    "AUTHOR, " +
                    "PURCHASE_DATE, " +
                    "ISBN " +
                "FROM BOOK ";
                if (jrbTitle.isSelected()) {
                    sql += "WHERE (LOWER(TITLE) CONTAINING LOWER(?)) ";
                } else if (jrbAuthor.isSelected()) {
                    sql += "WHERE (LOWER(AUTHOR) CONTAINING LOWER(?)) ";
                } else if (jrbIsbn.isSelected()) {
                    sql += "WHERE (LOWER(ISBN) CONTAINING LOWER(?)) ";
                }
                switch (showOption) {
                    case SHOW_ONLY_NOT_DISCARDED: {
                        sql +=
                        "AND ID NOT IN ( " +
                            "SELECT ID_BOOK FROM DISCARDED_BOOK " +
                        ") ";
                    } break;
                    case SHOW_ONLY_DISCARDED: {
                        sql +=
                        "AND ID IN ( " +
                            "SELECT ID_BOOK FROM DISCARDED_BOOK " +
                        ") ";
                    } break;
                    case SHOW_ONLY_NOT_BORROWED: {
                        sql +=
                        "AND ID NOT IN ( " +
                            "SELECT ID_BOOK FROM DISCARDED_BOOK " +
                        ") AND id NOT IN (" +
                            "SELECT " +
                                "B.id " + 
                            "FROM BOOK B " + 
                            "JOIN LOAN_ITEM LI ON (LI.ID_BOOK = B.ID) " + 
                            "JOIN LOAN L ON (L.ID = LI.ID_LOAN) " +
                            "WHERE LI.RETURNED = FALSE " +
                        ")";
                    } break;
                    case SHOW_ONLY_BORROWED: {
                        sql +=
                        "AND ID NOT IN ( " +
                            "SELECT ID_BOOK FROM DISCARDED_BOOK " +
                        ") AND ID IN (" +
                            "SELECT " +
                                "B.id " + 
                            "FROM BOOK B " + 
                            "JOIN LOAN_ITEM LI ON (LI.ID_BOOK = B.ID) " + 
                            "JOIN LOAN L ON (L.ID = LI.ID_LOAN) " +
                            "WHERE LI.RETURNED = FALSE " +
                        ")";
                    } break;
                }
                sql += 
                "AND ID NOT IN (" + hiddenBooksIdsStr + ") " +
                "ORDER BY 2, 1;";
                try (PreparedStatement pstm = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    String filter = jtfFilter.getText();
                    pstm.setString(1, filter);
                    try (ResultSet rset = pstm.executeQuery()) {
                        rset.last();
                        String[] columnsTitles = new String[]{"", "TÍTULO", "AUTOR",
                        "DATA AQUIS.", "ISBN"};
                        DefaultTableModel model = new DefaultTableModel(columnsTitles,
                        rset.getRow()){                  
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }                    
                        };
                        rset.beforeFirst(); 
                        int row = 0;
                        while (rset.next()) {
                            model.setValueAt(rset.getInt(1), row, 0);                        
                            model.setValueAt(rset.getString(2), row, 1);
                            model.setValueAt(rset.getString(3), row, 2);
                            model.setValueAt(StrParser.asString(rset.getDate(4)), row, 3);
                            model.setValueAt(rset.getString(5), row, 4);
                            row++;
                        }
                        jtBooks.setModel(model);
                    }                    
                    TableColumnModel tcm = jtBooks.getColumnModel();
                    TableCellRenderer tcr = new TableCellRenderer(
                        highligthBorrowed,
                        highligthDiscarded
                    ); 
                    //Atribui renderizador personalizado.
                    tcm.getColumn(1).setCellRenderer(tcr);
                    tcm.getColumn(2).setCellRenderer(tcr);
                    tcm.getColumn(3).setCellRenderer(tcr);
                    tcm.getColumn(4).setCellRenderer(tcr);
                    //Redimensiona as colunas da JTable.
                    tcm.getColumn(0).setMinWidth(0);
                    tcm.getColumn(0).setMaxWidth(0);                    
                    tcm.getColumn(1).setMinWidth(270);
                    tcm.getColumn(1).setMaxWidth(270);                    
                    tcm.getColumn(3).setMinWidth(80);
                    tcm.getColumn(3).setMaxWidth(80);
                    tcm.getColumn(4).setMinWidth(100);
                    tcm.getColumn(4).setMaxWidth(100);
                    if (jtBooks.getRowCount() > 0) {
                        jtBooks.setRowSelectionInterval(0, 0);
                        jbSelect.setEnabled(true);
                    } else {
                        jbSelect.setEnabled(false);
                    } 
                }
            }
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) this.getParent(),
                "Erro",
                ex
            );
        }
    }
    
    /**Selecionar o(s) livro(s) marcado(s) pelo usuário na JTable.*/
    private void select() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (jtBooks.getRowCount() > 0) {
            int[] selectedRows = jtBooks.getSelectedRows();
            for (int selectedRow : selectedRows) {
                int id = (int) jtBooks.getValueAt(selectedRow, 0);
                booksIdsList.add(id);
            }
            canceled = false;
            setVisible(false);
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Obter o identificador (id) do único livro selecionado pelo usuário. 
     */
    public int getBookId() {
        return booksIdsList.get(0);
    }
    
    /**
     * Obter o array de identificadores (ids) de livros que foram selecionados 
     * pelo usuário em caso de opção de múltipla seleção.
     */
    public int[] getBooksIds() {        
        int[] booksIds = new int[booksIdsList.size()];
        for (int i = 0; i < booksIdsList.size(); i++) {
            booksIds[i] = booksIdsList.get(i);
        }
        return booksIds;
    }

    /**Obter o status de clique no botão Cancelar.*/
    public boolean isCanceled() {
        return canceled;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jtfFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtBooks = new javax.swing.JTable();
        jbCancel = new javax.swing.JButton();
        jbSelect = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jrbTitle = new javax.swing.JRadioButton();
        jrbAuthor = new javax.swing.JRadioButton();
        jrbIsbn = new javax.swing.JRadioButton();
        jbClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SELECIONAR LIVRO");
        setMinimumSize(new java.awt.Dimension(640, 462));
        setResizable(false);

        jLabel1.setText("Filtro:");

        jtfFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfFilterKeyReleased(evt);
            }
        });

        jtBooks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jtBooks.setFillsViewportHeight(true);
        jtBooks.setRowHeight(18);
        jtBooks.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtBooks.setShowHorizontalLines(false);
        jtBooks.setShowVerticalLines(false);
        jtBooks.getTableHeader().setResizingAllowed(false);
        jtBooks.getTableHeader().setReorderingAllowed(false);
        jtBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtBooksMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtBooks);

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jbSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon19.png"))); // NOI18N
        jbSelect.setText("Selecionar");
        jbSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSelectActionPerformed(evt);
            }
        });

        jLabel2.setText("Filtrar por:");

        buttonGroup1.add(jrbTitle);
        jrbTitle.setSelected(true);
        jrbTitle.setText("Título");
        jrbTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbTitleActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbAuthor);
        jrbAuthor.setText("Autor");
        jrbAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbAuthorActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbIsbn);
        jrbIsbn.setText("ISBN");
        jrbIsbn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbIsbnActionPerformed(evt);
            }
        });

        jbClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon3.png"))); // NOI18N
        jbClear.setText("Limpar");
        jbClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jrbTitle)
                        .addGap(10, 10, 10)
                        .addComponent(jrbAuthor)
                        .addGap(10, 10, 10)
                        .addComponent(jrbIsbn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                        .addComponent(jbSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtfFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbClear)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtfFilter)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jbClear, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jbSelect)
                    .addComponent(jrbTitle)
                    .addComponent(jLabel2)
                    .addComponent(jrbAuthor)
                    .addComponent(jrbIsbn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSelectActionPerformed
        select();
    }//GEN-LAST:event_jbSelectActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jbClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbClearActionPerformed
        jtfFilter.setText("");
    }//GEN-LAST:event_jbClearActionPerformed

    private void jtBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooksMouseClicked
        if (evt.getClickCount() == 2) {
            select();
        }
        boolean enabled = jtBooks.getSelectedRows().length > 0;
        jbSelect.setEnabled(enabled);
    }//GEN-LAST:event_jtBooksMouseClicked

    private void jtfFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfFilterKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jtfFilter.setText("");
        }
        listBooks();
    }//GEN-LAST:event_jtfFilterKeyReleased

    private void jrbTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbTitleActionPerformed
        listBooks();
    }//GEN-LAST:event_jrbTitleActionPerformed

    private void jrbAuthorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbAuthorActionPerformed
        listBooks();
    }//GEN-LAST:event_jrbAuthorActionPerformed

    private void jrbIsbnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbIsbnActionPerformed
        listBooks();
    }//GEN-LAST:event_jrbIsbnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbClear;
    private javax.swing.JButton jbSelect;
    private javax.swing.JRadioButton jrbAuthor;
    private javax.swing.JRadioButton jrbIsbn;
    private javax.swing.JRadioButton jrbTitle;
    private javax.swing.JTable jtBooks;
    private javax.swing.JTextField jtfFilter;
    // End of variables declaration//GEN-END:variables
}
