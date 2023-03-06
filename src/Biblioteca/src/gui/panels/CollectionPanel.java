package gui.panels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import database.FBManager;
import library.Collection;
import library.Book;
import utils.StrParser;
import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;
import gui.dialogs.MainWindow;
import gui.dialogs.BookCadasterDialog;
import gui.dialogs.ConfirmDiscardDialog;
import report.Report;

/**
 * JPanel para manutenção dos livros no acervo pessoal. Implementa a interface
 * {@link ConfigurationsListener} pois deverá ser notificado sempre que houver alteração
 * nas configurações do sistema.
 * @author Leandro Aparecido de Almeida
 */
public final class CollectionPanel extends JPanel {
    
    /**Frase exibida no campo de pesquisa.*/
    private final String TEXT = "[Digite o título do livro para filtrar. " + 
    "Tecle ESC para limpar a pesquisa]";
    /**Instância de {@link MainWindow}.*/
    private final MainWindow mainWindow;
    /**Cursor para posicionar na linha selecionada da JTable ao atualizar.*/
    private int cursor;

    /**
     * Constructor da classe.
     * @param mainWindow instância de {@link MainWindow}.
     */
    public CollectionPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        cursor = 0;
        initComponents(); 
        jtfFind.setText(TEXT);
        listBooks();        
    }
    
    /**
     * Listar na JTable os livros no acervo de acordo com o critério de filtragem.
     */
    private void listBooks() {
        try {  
            try (Connection connection = FBManager.getConnection()) {
                String sql =
                "SELECT " +
                    "ID, " +                    
                    "TITLE, " +                
                    "AUTHOR, " +
                    "PURCHASE_DATE, " +
                    "ISBN " +
                "FROM BOOK " +
                "WHERE (LOWER(TITLE) CONTAINING LOWER(?)) AND  ID NOT IN ( " +
                    "SELECT " + 
                        "ID_BOOK " +
                    "FROM DISCARDED_BOOK " + 
                ") ORDER BY 2, 1;";
                try (PreparedStatement pstm = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) { 
                    String filter = jtfFind.getText();
                    pstm.setString(1, !filter.equals(TEXT)? filter : "");
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
                        jtBooks.setRowSelectionInterval(cursor, cursor);                        
                        jbDiscard.setEnabled(true);
                        jbEdit.setEnabled(true);
                        jmiDelete.setEnabled(true);
                    } else {
                        cursor = 0;
                        jbDiscard.setEnabled(false);
                        jbEdit.setEnabled(false);
                        jmiDelete.setEnabled(false);
                    }
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
     * Cadastrar um novo livro no acervo pessoal.
     */
    private void addNewBook() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        BookCadasterDialog dialog = new BookCadasterDialog(mainWindow);
        dialog.setVisible(true);
        if (!dialog.isCanceled()) {
            int bookId = dialog.getBookId();
            listBooks();
            moveCursorTo(bookId);            
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Alterar os cadastros dos livros selecionados na JTable.
     */
    private void editSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int updatedBookId = -1;        
        int[] selectedRows = jtBooks.getSelectedRows();
        for (int selectedRow : selectedRows) {
            try {
                int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
                Book book = Collection.getBook(bookId, true);
                BookCadasterDialog dialog = new BookCadasterDialog(
                    mainWindow,
                    book
                );
                dialog.setVisible(true);
                if (!dialog.isCanceled()) {
                    updatedBookId = dialog.getBookId();                    
                }
            } catch (Exception ex) {
                ErrorDialog.showException(
                    mainWindow,
                    "Erro ao alterar o cadastro do livro \"" + 
                    jtBooks.getValueAt(selectedRow, 1) + "\".",
                    ex
                );
            }
        }
        moveCursorTo(updatedBookId);
        listBooks();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Descartar os livros selecionados na JTable.
     */
    private void discardSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            //Se algum livro dos que foram selecionados na JTable está
            //emprestado, lança uma exceção e não continua o processo.
            int[] selectedRows = jtBooks.getSelectedRows();
            for (int selectedRow : selectedRows) {
                int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
                Book book = Collection.getBook(bookId, false);
                if (Collection.isBorrowedBook(book)) {
                    throw new Exception(
                        "Livro(s) selecionado(s) tomado(s) em empréstimo. " +
                        "Cancele o(s) empréstimo(s) em questão para descartar " +
                        "o(s) mesmo(s)."
                    );
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append((String) jtBooks.getValueAt(selectedRows[0], 1));
            for (int i = 1; i < selectedRows.length; i++) {
                sb.append(", ");
                sb.append((String) jtBooks.getValueAt(selectedRows[i], 1));
            }
            String booksTitles = sb.toString();
            //O mesmo motivo do descarte e data serão usados para todos os livros
            //selecionados.
            ConfirmDiscardDialog dialog = new ConfirmDiscardDialog(
                mainWindow, 
                booksTitles
            );
            dialog.setVisible(true);
            if (!dialog.isCanceled()) {
                String reason = dialog.getReason();
                Date date = dialog.getDate();
                for (int selectedRow : selectedRows) {
                    try {
                        int bookId = (int) jtBooks.getValueAt(selectedRow, 0);              
                        Book book = Collection.getBook(bookId, false);
                        Collection.discardBook(book, reason, date);                         
                    } catch (Exception ex) {
                        ErrorDialog.showException(
                            mainWindow,
                            "Erro ao descartar o livro \"" + 
                            jtBooks.getValueAt(selectedRow, 1) + "\".",
                            ex
                        );
                    }
                }                
                cursor = 0;
                listBooks();
            } 
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro ao descartar o(s) livro(s) selecionado(s).",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Excluir os livros selecionados na JTable.
     */
    private void deleteSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int option = JOptionPaneEx.showConfirmDialog(
            mainWindow,
            "Confirma a exclusão do(s) livro(s) selecionado(s)?", 
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
                    Collection.deleteBook(book);                     
                } catch (Exception ex) {
                    ErrorDialog.showException(
                        mainWindow,
                        "Erro ao excluir o livro \"" + 
                        jtBooks.getValueAt(selectedRow, 1) + "\".",
                        ex
                    );
                }
            }
            cursor = 0;
            listBooks();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Exibir os dados do livro selecionado na JTable.
     */
    private void showSelectedBookDetails() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (jtBooks.getRowCount() > 0) {
            int bookId = (int)jtBooks.getValueAt(cursor, 0);
            mainWindow.showBookDetailsPanel(bookId);
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Imprimir o relatório Livros Cadastrados.
     */
    public void printReport() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            Date date1 = StrParser.asDate("01/01/1900");
            Date date2 = StrParser.asDate("31/12/9999");
            Report report = new Report("/report/layout/collection_books.jasper");
            report.putParameter("DATE1", date1);
            report.putParameter("DATE2", date2);
            report.print("RELATÓRIO LIVROS NO ACERVO");
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro ao exibir o relatório.",
                ex
            );
        }        
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Mover o cursor para a posição aonde está um livro específico.
     * @param bookId identificador (id) do livro.
     */
    private void moveCursorTo(int bookId) {
        for (int i = 0; i < jtBooks.getRowCount(); i++) {
            if (((Integer)jtBooks.getValueAt(i, 0)).equals(bookId)) {
                cursor = i;
                break;
            }
        }
        jtBooks.setRowSelectionInterval(cursor, cursor);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jmiDelete = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtBooks = new javax.swing.JTable();
        jbRegister = new javax.swing.JButton();
        jbDiscard = new javax.swing.JButton();
        jbEdit = new javax.swing.JButton();
        jtfFind = new javax.swing.JTextField();
        jbReport = new javax.swing.JButton();

        jmiDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jmiDelete.setText("Excluir livro(s)");
        jmiDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiDeleteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiDelete);

        jtBooks.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jtBooks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {}
            },
            new String [] {

            }
        ));
        jtBooks.setComponentPopupMenu(jPopupMenu1);
        jtBooks.setFillsViewportHeight(true);
        jtBooks.setRowHeight(18);
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtBooksKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jtBooks);

        jbRegister.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon41.png"))); // NOI18N
        jbRegister.setText("  Novo");
        jbRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRegisterActionPerformed(evt);
            }
        });

        jbDiscard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon40.png"))); // NOI18N
        jbDiscard.setText("Descartar");
        jbDiscard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDiscardActionPerformed(evt);
            }
        });

        jbEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon5.png"))); // NOI18N
        jbEdit.setText("Alterar");
        jbEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbEditActionPerformed(evt);
            }
        });

        jtfFind.setForeground(new java.awt.Color(102, 102, 102));
        jtfFind.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtfFindFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtfFindFocusLost(evt);
            }
        });
        jtfFind.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfFindKeyReleased(evt);
            }
        });

        jbReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon4.png"))); // NOI18N
        jbReport.setText("Imprimir");
        jbReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jbRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbDiscard, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbReport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jtfFind)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jtfFind, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbRegister)
                    .addComponent(jbDiscard)
                    .addComponent(jbEdit)
                    .addComponent(jbReport))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbDiscardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDiscardActionPerformed
        discardSelectedBooks();
    }//GEN-LAST:event_jbDiscardActionPerformed

    private void jbEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEditActionPerformed
        editSelectedBooks();
    }//GEN-LAST:event_jbEditActionPerformed

    private void jtBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooksMouseClicked
        cursor = jtBooks.getRowCount() > 0 ? jtBooks.getSelectedRow() : -1;
        if (evt.getClickCount() == 2) {
            showSelectedBookDetails();
        }
        boolean enabled = jtBooks.getSelectedRows().length > 0;
        jbEdit.setEnabled(enabled);
        jbDiscard.setEnabled(enabled);
        jmiDelete.setEnabled(enabled);
    }//GEN-LAST:event_jtBooksMouseClicked

    private void jtfFindKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfFindKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jtfFind.setText("");
        }   
        cursor = 0;
        listBooks();
    }//GEN-LAST:event_jtfFindKeyReleased

    private void jtfFindFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfFindFocusGained
        if (jtfFind.getText().equals(TEXT)) {
            jtfFind.setText("");
        }
    }//GEN-LAST:event_jtfFindFocusGained

    private void jtfFindFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfFindFocusLost
        if (jtfFind.getText().equals("")) {
            jtfFind.setText(TEXT);
        }
    }//GEN-LAST:event_jtfFindFocusLost

    private void jbRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRegisterActionPerformed
        addNewBook();
    }//GEN-LAST:event_jbRegisterActionPerformed

    private void jtBooksKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtBooksKeyPressed
        cursor = jtBooks.getRowCount() > 0 ? jtBooks.getSelectedRow() : -1;
    }//GEN-LAST:event_jtBooksKeyPressed

    private void jbReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReportActionPerformed
        printReport();
    }//GEN-LAST:event_jbReportActionPerformed

    private void jmiDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDeleteActionPerformed
        deleteSelectedBooks();
    }//GEN-LAST:event_jmiDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbDiscard;
    private javax.swing.JButton jbEdit;
    private javax.swing.JButton jbRegister;
    private javax.swing.JButton jbReport;
    private javax.swing.JMenuItem jmiDelete;
    private javax.swing.JTable jtBooks;
    private javax.swing.JTextField jtfFind;
    // End of variables declaration//GEN-END:variables
}
