package gui.panels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import database.FBManager;
import library.LoanData;
import library.Book;
import library.Collection;
import utils.StrParser;
import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;
import gui.dialogs.BorrowBookDialog;
import gui.dialogs.CancelDevolutionDialog;
import gui.dialogs.ConfirmDevolutionDialog;
import gui.dialogs.MainWindow;
import java.awt.Frame;
import report.Report;

/**
 * JPanel para a manutenção de empréstimos de livros.
 * @author Leandro Aparecido de Almeida
 */
public final class BorrowBookPanel extends javax.swing.JPanel {

    /**Instância de {@link MainWindow}.*/
    private final MainWindow mainWindow;
    
    /**
     * Constructor da classe.
     * @param mainWindow instância de {@link MainWindow}.
     */
    public BorrowBookPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
        listBooks();
    }
    
    /**
     * Listar os livros que estão emprestados para terceiros na JTable.
     */
    private void listBooks() {
        try { 
            try (Connection connection = FBManager.getConnection()) {
                String sql =
                "SELECT " +
                    "B.ID, " + 
                    "B.TITLE, " +
                    "B.ISBN, " +
                    "L.ID, " +  
                    "L.LOAN_DATE, " +
                    "L.APPLICANT " +
                "FROM BOOK B " +
                "JOIN LOAN_ITEM LI ON (LI.ID_BOOK = B.ID) " +
                "JOIN LOAN L ON (L.ID = LI.ID_LOAN) " +
                "WHERE LI.RETURNED = FALSE " +
                "ORDER BY 2, 1;";
                try (PreparedStatement pstm = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    try (ResultSet rset = pstm.executeQuery()) {
                        rset.last();
                        String[] columnsTitles = new String[]{"", "", "DATA RET.",
                        "TÍTULO DO LIVRO", "ISBN", "SOLICITANTE"};
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
                            model.setValueAt(rset.getInt(4), row, 1); 
                            model.setValueAt(StrParser.asString(rset.getDate(5)), row, 2);
                            model.setValueAt(rset.getString(2), row, 3);
                            model.setValueAt(rset.getString(3), row, 4);
                            model.setValueAt(rset.getString(6), row, 5);
                            row++;
                        }
                        jtBooks.setModel(model);
                    }
                    TableColumnModel tcm = jtBooks.getColumnModel();
                    tcm.getColumn(0).setMinWidth(0);
                    tcm.getColumn(0).setMaxWidth(0); 
                    tcm.getColumn(1).setMinWidth(0);
                    tcm.getColumn(1).setMaxWidth(0);
                    tcm.getColumn(2).setMinWidth(80);
                    tcm.getColumn(2).setMaxWidth(80);
                    tcm.getColumn(3).setMinWidth(270);
                    tcm.getColumn(3).setMaxWidth(270);                    
                    tcm.getColumn(4).setMinWidth(100);
                    tcm.getColumn(4).setMaxWidth(100);  
                    if (jtBooks.getRowCount() > 0) {
                        jtBooks.setRowSelectionInterval(0, 0);
                        jbDevolution.setEnabled(true);
                        jbDeleteBooks.setEnabled(true);
                    } else {
                        jbDevolution.setEnabled(false);
                        jbDeleteBooks.setEnabled(false);
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
     * Criar um novo registro de empréstimo de livros para terceiro.
     */
    private void borrowBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR)); 
        new BorrowBookDialog(mainWindow).setVisible(true);
        listBooks();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
    }
    
    /**
     * Registrar a devolução dos livros selecionados na JTable.
     */
    private void returnSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        int[] selectedRows = jtBooks.getSelectedRows();
        sb1.append((String)jtBooks.getValueAt(selectedRows[0], 3));
        sb2.append((String)jtBooks.getValueAt(selectedRows[0], 5));
        for (int i = 1; i < selectedRows.length; i++) {
            int selectedRow = selectedRows[i];
            String title = (String) jtBooks.getValueAt(selectedRow, 3);
            sb1.append(", ");
            sb1.append(title);            
            String applicant = (String) jtBooks.getValueAt(selectedRow, 5);
            if (sb2.indexOf(applicant) == -1) {
                sb2.append(", ");
                sb2.append(applicant);
            }
        }
        String booksTitles = sb1.toString();
        String applicants = sb2.toString();
        ConfirmDevolutionDialog dialog = new ConfirmDevolutionDialog(
            mainWindow,
            booksTitles,
            applicants
        );
        dialog.setVisible(true);
        if (!dialog.isCanceled()) {
            try {
                Date date = dialog.getDate();
                List<LoanData> loanDataList = new ArrayList<>();
                for (int selectedRow : selectedRows) {
                    int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
                    Book book = Collection.getBook(bookId, false);
                    int loanId = (int) jtBooks.getValueAt(selectedRow, 1);
                    int idx = -999;
                    for (int i = 0; i < loanDataList.size(); i++) {
                        if (loanDataList.get(i).getId() == loanId) {
                            idx = i;
                            break;
                        }
                    }
                    if (idx < 0) {
                        loanDataList.add(new LoanData(loanId, null, date));
                        idx = loanDataList.size() - 1;
                    }                 
                    loanDataList.get(idx).addBook(book);               
                }
                for (LoanData data : loanDataList) {
                    Collection.returnBook(data);
                }
                listBooks();
            } catch (Exception ex) {
                ErrorDialog.showException(
                    mainWindow,
                    "Erro na devolução do(s) livro(s).",
                    ex
                );
            } 
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
    }
    
    /**
     * Excluir os livros selecionados na JTable, cancelando o empréstimo.
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
            try {
                int[] selectedRows = jtBooks.getSelectedRows();
                List<LoanData> loanDataList = new ArrayList<>();
                for (int selectedRow : selectedRows) {
                    int bookId = (int) jtBooks.getValueAt(selectedRow, 0);
                    Book book = Collection.getBook(bookId, false);
                    int loanId = (int) jtBooks.getValueAt(selectedRow, 1);
                    int idx = -999;
                    for (int i = 0; i < loanDataList.size(); i++) {
                        if (loanDataList.get(i).getId() == loanId) {
                            idx = i;
                            break;
                        }
                    }
                    if (idx < 0) {
                        loanDataList.add(new LoanData(loanId, null, null));
                        idx = loanDataList.size() - 1;
                    }                   
                    loanDataList.get(idx).addBook(book);               
                }
                for (LoanData data : loanDataList) {
                    Collection.deleteBookFromLoan(data);
                }
                listBooks();
            } catch (Exception ex) {
                ErrorDialog.showException(
                    mainWindow,
                    "Erro ao tentar excluir o(s) livro(s).",
                    ex
                );
            }           
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
    }
    
    /**
     * Cancelar a devolução de um ou mais livros emprestados.
     */
    private void cancelBooksDevolution() {
        new CancelDevolutionDialog(mainWindow).setVisible(true);
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
     * Imprimir o relatório Livros Emprestados.
     */
    public void printReport() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            Date date1 = StrParser.asDate("01/01/1900");
            Date date2 = StrParser.asDate("31/12/9999");
            Report report = new Report("/report/layout/borrowed_books.jasper");
            report.putParameter("DATE1", date1);
            report.putParameter("DATE2", date2);
            report.print("RELATÓRIO LIVROS EMPRESTADOS");
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
        jbDevolution = new javax.swing.JButton();
        jbBorrowBooks = new javax.swing.JButton();
        jbDeleteBooks = new javax.swing.JButton();
        jbReport = new javax.swing.JButton();
        jbCancelDevolution = new javax.swing.JButton();

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
        jScrollPane1.setViewportView(jtBooks);

        jbDevolution.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon44.png"))); // NOI18N
        jbDevolution.setText("Devolução");
        jbDevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDevolutionActionPerformed(evt);
            }
        });

        jbBorrowBooks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon43.png"))); // NOI18N
        jbBorrowBooks.setText("Empréstimo");
        jbBorrowBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBorrowBooksActionPerformed(evt);
            }
        });

        jbDeleteBooks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbDeleteBooks.setText("Excluir");
        jbDeleteBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDeleteBooksActionPerformed(evt);
            }
        });

        jbReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon4.png"))); // NOI18N
        jbReport.setText("Histórico");
        jbReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbReportActionPerformed(evt);
            }
        });

        jbCancelDevolution.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon12.png"))); // NOI18N
        jbCancelDevolution.setText("Canc. Devolução");
        jbCancelDevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelDevolutionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                .addGap(3, 3, 3))
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jbBorrowBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbDevolution, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbDeleteBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbCancelDevolution)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbReport, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbDevolution)
                    .addComponent(jbBorrowBooks)
                    .addComponent(jbDeleteBooks)
                    .addComponent(jbReport)
                    .addComponent(jbCancelDevolution))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbBorrowBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBorrowBooksActionPerformed
        borrowBooks();
    }//GEN-LAST:event_jbBorrowBooksActionPerformed

    private void jbDeleteBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDeleteBooksActionPerformed
        deleteSelectedBooks();
    }//GEN-LAST:event_jbDeleteBooksActionPerformed

    private void jbDevolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDevolutionActionPerformed
        returnSelectedBooks();
    }//GEN-LAST:event_jbDevolutionActionPerformed

    private void jtBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooksMouseClicked
        if (evt.getClickCount() == 2) {
            if (jtBooks.getRowCount() > 0) {
                showSelectedBookDetails();
            }
        }
        boolean enabled = jtBooks.getSelectedRows().length > 0;
        jbDevolution.setEnabled(enabled);
        jbDeleteBooks.setEnabled(enabled);
    }//GEN-LAST:event_jtBooksMouseClicked

    private void jbReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReportActionPerformed
        printReport();
    }//GEN-LAST:event_jbReportActionPerformed

    private void jbCancelDevolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelDevolutionActionPerformed
        cancelBooksDevolution();
    }//GEN-LAST:event_jbCancelDevolutionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbBorrowBooks;
    private javax.swing.JButton jbCancelDevolution;
    private javax.swing.JButton jbDeleteBooks;
    private javax.swing.JButton jbDevolution;
    private javax.swing.JButton jbReport;
    private javax.swing.JTable jtBooks;
    // End of variables declaration//GEN-END:variables
}
