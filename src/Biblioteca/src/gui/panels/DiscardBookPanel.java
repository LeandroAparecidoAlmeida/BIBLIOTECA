package gui.panels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import database.FBManager;
import library.Collection;
import library.Book;
import utils.StrParser;
import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;
import gui.dialogs.AlterDiscardDialog;
import gui.dialogs.ConfirmDiscardDialog;
import gui.dialogs.SelectBooksDialog;
import gui.dialogs.MainWindow;
import report.Report;

/**
 * JPanel para a manutenção de livros descartados. Implementa a interface
 * {@link ConfigurationsListener} pois deverá ser notificado sempre que houver alteração
 * nas configurações do sistema.
 * @author Leandro Aparecido de Almeida
 */
public final class DiscardBookPanel extends javax.swing.JPanel {
    
    /**Frase exibida no campo de pesquisa.*/
    private final String TEXT = "[Digite o título do livro para filtrar. " + 
    "Tecle ESC para limpar a pesquisa]";
    /**Instância de {@link MainWindow}.*/
    private final MainWindow mainWindow;

    /**
     * Constructor da classe.
     * @param mainWindow instância de {@link MainWindow}.
     */
    public DiscardBookPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
        jtfFilter.setText(TEXT);
        listBooks();       
    }
    
    /**
     * Listar na JTable os livros que foram descartados, de acordo com o critério
     * de filtragem.
     */
    public void listBooks() {                
        try {
            try (Connection connection = FBManager.getConnection()) {
                String sql =
                "SELECT " +
                    "B.ID, " +
                    "B.TITLE, " +
                    "B.AUTHOR, " +  
                    "DB.DISCARD_DATE, " + 
                    "B.ISBN, " +
                    "DB.REASON " +
                "FROM BOOK B " +
                "JOIN DISCARDED_BOOK DB ON (B.ID = DB.ID_BOOK) " +
                "WHERE (LOWER(B.TITLE) CONTAINING LOWER(?)) " +
                "ORDER BY 2, 1;";
                try (PreparedStatement pstm = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) { 
                    String filter = jtfFilter.getText();
                    pstm.setString(1, !filter.equals(TEXT)? filter : "");
                    try (ResultSet rset = pstm.executeQuery()) {
                        rset.last();
                        String[] columnsTitles = new String[]{"", "TÍTULO", "AUTOR",
                        "DATA DESCARTE", "ISBN", ""};
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
                            model.setValueAt(rset.getString(6), row, 5);
                            row++;
                        }
                        jtBooks.setModel(model);
                    }
                    TableColumnModel tcm = jtBooks.getColumnModel();                    
                    tcm.getColumn(0).setMinWidth(0);
                    tcm.getColumn(0).setMaxWidth(0);                    
                    tcm.getColumn(1).setMinWidth(270);
                    tcm.getColumn(1).setMaxWidth(270);                    
                    tcm.getColumn(3).setMinWidth(95);
                    tcm.getColumn(3).setMaxWidth(95);
                    tcm.getColumn(4).setMinWidth(100);
                    tcm.getColumn(4).setMaxWidth(100);
                    tcm.getColumn(5).setMinWidth(0);
                    tcm.getColumn(5).setMaxWidth(0);
                    if (jtBooks.getRowCount() > 0) {
                        jtBooks.setRowSelectionInterval(0, 0);                        
                        jbDelete.setEnabled(true);
                        jbEdit.setEnabled(true);
                    } else {
                        jbDelete.setEnabled(false);
                        jbEdit.setEnabled(false);
                    }
                    updateReason();
                }
            }           
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
    }
    
    /**
     * Atualizar o motivo do descarte do livro selecionado na JTable.
     */
    private void updateReason() {
        if (jtBooks.getRowCount() > 0) {
            if (jtBooks.getSelectedRows().length > 0) {
                String reason = (String) jtBooks.getValueAt(jtBooks
                .getSelectedRow(), 5);
                jtaReason.setText(reason);
            } else {
                jtaReason.setText("");
            }
        } else {
            jtaReason.setText("");
        }        
    }
    
    /**
     * Inserir livros à lista dos livros descartados.
     */
    private void insertBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SelectBooksDialog dialog1 = new SelectBooksDialog(
            mainWindow,
            SelectBooksDialog.SHOW_ONLY_NOT_BORROWED,
            false,
            false,
            true,
            null
        );
        dialog1.setVisible(true);        
        if (!dialog1.isCanceled()) {
            int[] booksIds = dialog1.getBooksIds();
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(Collection.getBook(booksIds[0], false).getTitle());
                for (int i = 1; i < booksIds.length; i++) {
                    sb.append(", ");
                    sb.append(Collection.getBook(booksIds[i], false).getTitle());
                }
                String booksTitles = sb.toString();
                //O mesmo motivo do descarte e data serão usados para todos os
                //livros selecionados.
                ConfirmDiscardDialog dialog2 = new ConfirmDiscardDialog(
                    mainWindow, 
                    booksTitles
                );
                dialog2.setVisible(true);
                if (!dialog2.isCanceled()) {
                    for (int bookId : booksIds) {
                        try {
                            String reason = dialog2.getReason();
                            Date date = dialog2.getDate();
                            Book book = Collection.getBook(bookId, false);
                            Collection.discardBook(book, reason, date);                            
                        } catch (Exception ex) {
                            ErrorDialog.showException(
                                mainWindow,
                                "Erro ao inserir o livro \"" +
                                Collection.getBook(bookId, false).getTitle() +
                                "\".",
                                ex
                            );
                        }
                    }
                    listBooks();
                }
            } catch (Exception ex) {
                ErrorDialog.showException(
                    mainWindow,
                    "Erro ao inserir o(s) livro(s).",
                    ex
                );
            }
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Remover os livros selecionados na JTable (reverter o descarte).
     */
    private void deleteSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int option = JOptionPaneEx.showConfirmDialog(
            mainWindow,
            "Remover o(s) livro(s) selecionado(s)?", 
            "Atenção!",
            JOptionPaneEx.YES_NO_OPTION,
            JOptionPaneEx.QUESTION_MESSAGE
        );
        if (option == JOptionPaneEx.YES_OPTION) {
            int[] selectedRows = jtBooks.getSelectedRows();
            for (int selectedRow : selectedRows) {
                try {
                    int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
                    Book book = Collection.getBook(bookId, false);
                    Collection.revertBookDiscard(book);                     
                } catch (Exception ex) {
                    ErrorDialog.showException(
                        mainWindow,
                        "Erro ao remover o livro \"" + 
                        jtBooks.getValueAt(selectedRow, 1) + "\".",
                        ex
                    );
                }
            }
            listBooks();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Alterar o texto de descrição do motivo de descarte do livro.
     */
    private void alterReason() {
        int[] selectedRows = jtBooks.getSelectedRows();
        for (int selectedRow : selectedRows) {
            try {
                int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
                Book book = Collection.getBook(bookId, false);
                AlterDiscardDialog dialog = new AlterDiscardDialog(mainWindow, book);
                dialog.setVisible(true);                
            } catch (Exception ex) {
                ErrorDialog.showException(
                    mainWindow,
                    "Erro ao alterar o cadastro.",
                    ex
                );
            }
        }
        listBooks();
    }
    
    /**
     * Exibir os dados do livro selecionado na JTable.
     */
    private void showSelectedBookDetails() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (jtBooks.getRowCount() > 0) {
            int selectedRow = jtBooks.getSelectedRow();
            int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
            mainWindow.showBookDetailsPanel(bookId);
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Imprimir o relatórios Livros Descartados.
     */
    public void printReport() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            Date date1 = StrParser.asDate("01/01/1900");
            Date date2 = StrParser.asDate("31/12/9999");
            Report report = new Report("/report/layout/discarded_books.jasper");
            report.putParameter("DATE1", date1);
            report.putParameter("DATE2", date2);
            report.print("RELATÓRIO LIVROS DESCARTADOS");
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro ao exibir o relatório.",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtBooks = new javax.swing.JTable();
        jbDelete = new javax.swing.JButton();
        jtfFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaReason = new javax.swing.JTextArea();
        jbInsert = new javax.swing.JButton();
        jbReport = new javax.swing.JButton();
        jbEdit = new javax.swing.JButton();

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
        jtBooks.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jtBooks.setShowHorizontalLines(false);
        jtBooks.setShowVerticalLines(false);
        jtBooks.getTableHeader().setResizingAllowed(false);
        jtBooks.getTableHeader().setReorderingAllowed(false);
        jtBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtBooksMouseClicked(evt);
            }
        });
        jtBooks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtBooksKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jtBooks);

        jbDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon44.png"))); // NOI18N
        jbDelete.setText("Remover");
        jbDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDeleteActionPerformed(evt);
            }
        });

        jtfFilter.setForeground(new java.awt.Color(102, 102, 102));
        jtfFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtfFilterFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfFilterFocusLost(evt);
            }
        });
        jtfFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfFilterKeyReleased(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jtaReason.setEditable(false);
        jtaReason.setColumns(20);
        jtaReason.setLineWrap(true);
        jtaReason.setRows(5);
        jtaReason.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jtaReason);

        jbInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon43.png"))); // NOI18N
        jbInsert.setText("Inserir");
        jbInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbInsertActionPerformed(evt);
            }
        });

        jbReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon4.png"))); // NOI18N
        jbReport.setText("Imprimir");
        jbReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReportActionPerformed(evt);
            }
        });

        jbEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon5.png"))); // NOI18N
        jbEdit.setText("Alterar");
        jbEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jbInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbReport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                            .addComponent(jtfFilter, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(3, 3, 3))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jtfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbDelete)
                    .addComponent(jbInsert)
                    .addComponent(jbReport)
                    .addComponent(jbEdit))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtfFilterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfFilterFocusGained
        if (jtfFilter.getText().equals(TEXT)) {
            jtfFilter.setText("");
        }
    }//GEN-LAST:event_jtfFilterFocusGained

    private void jtfFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfFilterFocusLost
        if (jtfFilter.getText().equals("")) {
            jtfFilter.setText(TEXT);
        }
    }//GEN-LAST:event_jtfFilterFocusLost

    private void jtfFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfFilterKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jtfFilter.setText("");
        }
        listBooks();
    }//GEN-LAST:event_jtfFilterKeyReleased

    private void jtBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooksMouseClicked
        if (evt.getClickCount() == 2) {
            showSelectedBookDetails();
        }
        boolean enabled = jtBooks.getSelectedRows().length > 0;
        jbDelete.setEnabled(enabled);
        updateReason();
    }//GEN-LAST:event_jtBooksMouseClicked

    private void jbDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDeleteActionPerformed
        deleteSelectedBooks();
    }//GEN-LAST:event_jbDeleteActionPerformed

    private void jtBooksKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtBooksKeyReleased
        updateReason();
    }//GEN-LAST:event_jtBooksKeyReleased

    private void jbInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbInsertActionPerformed
        insertBooks();
    }//GEN-LAST:event_jbInsertActionPerformed

    private void jbReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReportActionPerformed
        printReport();
    }//GEN-LAST:event_jbReportActionPerformed

    private void jbEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEditActionPerformed
        alterReason();
    }//GEN-LAST:event_jbEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbDelete;
    private javax.swing.JButton jbEdit;
    private javax.swing.JButton jbInsert;
    private javax.swing.JButton jbReport;
    private javax.swing.JTable jtBooks;
    private javax.swing.JTextArea jtaReason;
    private javax.swing.JTextField jtfFilter;
    // End of variables declaration//GEN-END:variables
}
