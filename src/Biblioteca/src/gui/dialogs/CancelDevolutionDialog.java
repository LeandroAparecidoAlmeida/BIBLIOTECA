package gui.dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import library.LoanData;
import database.FBManager;
import utils.StrParser;
import library.Book;
import library.Collection;
import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;

/**
 * Tela para o cancelamento da devolução de livros tomados em empréstimo.
 * @author Leandro Aparecido de Almeida
 */
public final class CancelDevolutionDialog extends javax.swing.JDialog {
    
    /**
     * Constructor da classe.
     * @param parent frame proprietário.
     */
    public CancelDevolutionDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        jftInitialDate.setText("01/01/1900");
        jftFinalDate.setText("31/12/9999");
        listBooks();
    }
    
    /**
     * Listar os livros devolvidos no período.
     */
    private void listBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {  
            try (Connection connection = FBManager.getConnection()) {
                String sql =
                "SELECT + "  +
                    "LI.ID_LOAN, " +
                    "LI.ID_BOOK, " + 
                    "B.TITLE, " + 
                    "B.ISBN, " + 
                    "L.LOAN_DATE, " + 
                    "LI.RETURN_DATE, " +
                    "L.APPLICANT " +
                "FROM LOAN_ITEM LI " +
                "JOIN LOAN L ON (L.ID = LI.ID_LOAN) " + 
                "JOIN BOOK B ON (B.ID = LI.ID_BOOK) " + 
                "WHERE (LI.RETURNED = TRUE) AND (LI.RETURN_DATE BETWEEN ? AND ?) " +
                "AND B.ID NOT IN ( " + 
                    "SELECT ID_BOOK FROM DISCARDED_BOOK " + 
                ") ORDER BY LI.RETURN_DATE, B.TITLE, B.ID;";
                try (PreparedStatement pstm = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {  
                    Date date1 = StrParser.asDate(jftInitialDate.getText());
                    Date date2 = StrParser.asDate(jftFinalDate.getText());
                    pstm.setDate(1, (java.sql.Date) date1);
                    pstm.setDate(2, (java.sql.Date) date2);
                    try (ResultSet rset = pstm.executeQuery()) {
                        rset.last();
                        String[] columnsTitles = new String[]{"", "", "TÍTULO DO LIVRO",
                        "ISBN", "DATA RET.", "DATA DEV.", "SOLICITANTE"};
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
                            model.setValueAt(rset.getInt(2), row, 1);
                            model.setValueAt(rset.getString(3), row, 2);
                            model.setValueAt(rset.getString(4), row, 3);
                            model.setValueAt(StrParser.asString(rset.getDate(5)), row, 4);
                            model.setValueAt(StrParser.asString(rset.getDate(6)), row, 5);
                            model.setValueAt(rset.getString(7), row, 6);
                            row++;
                        }
                        jtBooks.setModel(model);
                    }
                    TableColumnModel tcm = jtBooks.getColumnModel();
                    tcm.getColumn(0).setMinWidth(0);
                    tcm.getColumn(0).setMaxWidth(0);
                    tcm.getColumn(1).setMinWidth(0);
                    tcm.getColumn(1).setMaxWidth(0);
                    tcm.getColumn(2).setMinWidth(250);
                    tcm.getColumn(2).setMaxWidth(250); 
                    tcm.getColumn(3).setMinWidth(100);
                    tcm.getColumn(3).setMaxWidth(100);
                    tcm.getColumn(4).setMinWidth(80);
                    tcm.getColumn(4).setMaxWidth(80);
                    tcm.getColumn(5).setMinWidth(80);
                    tcm.getColumn(5).setMaxWidth(80);
                    if (jtBooks.getRowCount() > 0) {
                        jtBooks.setRowSelectionInterval(0, 0); 
                        jbConfirm.setEnabled(true);
                    } else {
                        jbConfirm.setEnabled(false);
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
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Confirmar o cancelamento da devolução dos livros selecionados na JTable.
     */
    private void confirm() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            int option = JOptionPaneEx.showConfirmDialog(
                this,
                "Cancelar a devolução do(s) livro(s) selecionado(s)?", 
                "Atenção!",
                JOptionPaneEx.YES_NO_OPTION,
                JOptionPaneEx.QUESTION_MESSAGE
            );
            if (option == JOptionPaneEx.YES_OPTION) {
                int[] selectedRows = jtBooks.getSelectedRows();
                List<LoanData> loanDataList = new ArrayList<>();
                for (int selectedRow : selectedRows) {
                    int bookId = (int) jtBooks.getValueAt(selectedRow, 1);
                    Book book = Collection.getBook(bookId, false);
                    int loanId = (int) jtBooks.getValueAt(selectedRow, 0);
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
                    Collection.revertBookReturn(data);
                }
                setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorDialog.showException(
                (Frame) this.getParent(),
                "Erro ao cancelar a devolução dos livros selecionados.",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jftInitialDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jftFinalDate = new javax.swing.JFormattedTextField();
        jbList = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtBooks = new javax.swing.JTable();
        jbCancel = new javax.swing.JButton();
        jbConfirm = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CANCELAR DEVOLUÇÕES DE EMPRÉSTIMO");

        jLabel1.setText("PERÍODO DA DEVOLUÇÃO:");

        jLabel2.setText("De:");

        try {
            jftInitialDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel3.setText("Até:");

        try {
            jftFinalDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jbList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon9.png"))); // NOI18N
        jbList.setText("Listar");
        jbList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbListActionPerformed(evt);
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

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jbConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon19.png"))); // NOI18N
        jbConfirm.setText("Confirmar");
        jbConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbConfirmActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftFinalDate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbList, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jftInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jftFinalDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jbList))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jbConfirm))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbListActionPerformed
        listBooks();
    }//GEN-LAST:event_jbListActionPerformed

    private void jbConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConfirmActionPerformed
        confirm();
    }//GEN-LAST:event_jbConfirmActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jtBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooksMouseClicked
        boolean enabled = jtBooks.getSelectedRows().length > 0;
        jbConfirm.setEnabled(enabled);
    }//GEN-LAST:event_jtBooksMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbConfirm;
    private javax.swing.JButton jbList;
    private javax.swing.JFormattedTextField jftFinalDate;
    private javax.swing.JFormattedTextField jftInitialDate;
    private javax.swing.JTable jtBooks;
    // End of variables declaration//GEN-END:variables
}
