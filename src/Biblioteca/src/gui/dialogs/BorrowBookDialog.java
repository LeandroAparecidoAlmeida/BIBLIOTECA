package gui.dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import library.LoanData;
import database.FBManager;
import library.Book;
import library.Collection;
import utils.StrParser;
import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;

/**
 * Tela para o empréstimo de um ou mais livros.
 * @author Leandro Aparecido de Almeida
 */
public final class BorrowBookDialog extends javax.swing.JDialog {
    
    /**Identificadores (ids) dos livros selecionados para o empréstimo.*/
    private final List<Integer> booksIdsList;
     
    /**
     * Constructor da classe.
     * @param parent frame proprietário.
     */
    public BorrowBookDialog(java.awt.Frame parent) {
        super(parent, true);  
        initComponents();
        setLocationRelativeTo(parent);
        booksIdsList = new ArrayList<>();
        jftDate.setText(StrParser.asString(new Date()));        
        listBooks();
    }
    
    /**
     * Redimensionar as colunas de uma JTable. 
     * @param jtable JTable a ter as colunas redimensionadas.
     */
    private void resizeTableColumns(JTable jtable) {
        TableColumnModel tcm = jtable.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);                    
        tcm.getColumn(1).setMinWidth(270);
        tcm.getColumn(1).setMaxWidth(270);                    
        tcm.getColumn(3).setMinWidth(80);
        tcm.getColumn(3).setMaxWidth(80);
        tcm.getColumn(4).setMinWidth(100);
        tcm.getColumn(4).setMaxWidth(100);
    }
    
    /**
     * Listar os livros tanto na JTable superior (todos os livros, exceto os que
     * já estão emprestados) quanto na inferior (livros que serão emprestados).
     */
    private void listBooks() {              
        try {  
            StringBuilder sb = new StringBuilder();
            sb.append("-1");
            for (Integer bookId : booksIdsList) {
                sb.append(", ");
                sb.append(String.valueOf(bookId));
            }
            String booksIdsStr = sb.toString();
            String[] columnsTitles = new String[]{"", "TÍTULO", "AUTOR",
            "DATA AQUIS.", "ISBN"};
            try (Connection connection = FBManager.getConnection()) {
                String sql1 =
                "SELECT " +
                    "B.ID, " +
                    "B.TITLE, " +                
                    "B.AUTHOR, " +
                    "B.PURCHASE_DATE, " +  
                    "B.ISBN " + 
                "FROM BOOK B " +
                "WHERE B.ID NOT IN (SELECT ID_BOOK FROM DISCARDED_BOOK) AND B.ID NOT IN ( " + 
                    "SELECT " +
                        "LI.ID_BOOK " +
                    "FROM LOAN_ITEM LI " +
                    "WHERE LI.ID_BOOK = B.id AND LI.RETURNED = FALSE " +
                ") AND B.ID NOT IN (" + booksIdsStr + ") " +  
                "ORDER BY 2,1;";
                try (PreparedStatement pstm1 = connection.prepareStatement(sql1,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    try (ResultSet rset = pstm1.executeQuery()) {
                        rset.last();                    
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
                        jtBooks1.setModel(model);
                    }
                    resizeTableColumns(jtBooks1);
                    if (jtBooks1.getRowCount() > 0) {
                        jtBooks1.setRowSelectionInterval(0, 0);                        
                        jbInsert.setEnabled(true);
                        jbFilter.setEnabled(true);
                    } else {
                        jbInsert.setEnabled(false);
                        jbFilter.setEnabled(false);
                    }
                }
                String sql2 =
                "SELECT " +
                    "ID, " + 
                    "TITLE, " +               
                    "AUTHOR, " +
                    "PURCHASE_DATE, " + 
                    "ISBN " + 
                "FROM BOOK " +
                "WHERE ID IN (" + booksIdsStr + ") " +  
                "ORDER BY 2,1;";
                try (PreparedStatement pstm2 = connection.prepareStatement(sql2,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                    try (ResultSet rset = pstm2.executeQuery()) {
                        rset.last();                    
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
                        jtBooks2.setModel(model);
                    }
                    resizeTableColumns(jtBooks2);
                    if (jtBooks2.getRowCount() > 0) {
                        jtBooks2.setRowSelectionInterval(0, 0);                        
                        jbRemove.setEnabled(true);
                        jbClear.setEnabled(true);
                        jbSalve.setEnabled(true);
                    } else {
                        jbRemove.setEnabled(false);
                        jbClear.setEnabled(false);
                        jbSalve.setEnabled(false);
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
    
    /**
     * Transferir o(s) livro(s) selecionado(s) na JTable superior para a JTable
     * inferior.
     */
    private void insertSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));  
        int[] selectedRows = jtBooks1.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int bookId = (int) jtBooks1.getValueAt(selectedRow, 0);
            booksIdsList.add(bookId);            
        }
        listBooks();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
    }
    
    /**
     * Transferir o(s) livro(s) selecionado(s) na JTable inferior para a JTable
     * superior.
     */
    private void deleteSelectedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int[] selectedRows = jtBooks2.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int bookId = (int) jtBooks2.getValueAt(selectedRow, 0);
            for (int i = 0; i < booksIdsList.size(); i++) {
                if (booksIdsList.get(i).equals(bookId)) {
                    booksIdsList.remove(i);
                    break;
                }
            }            
        }
        listBooks();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Transferir todos os livros da JTable inferior para a JTable superior.
     */
    private void clearBooksList() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        booksIdsList.clear();
        listBooks();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir o filtro para selecionar o(s) livro(s) a ser(em) emprestado(s).
     */
    private void find() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SelectBooksDialog filtro = new SelectBooksDialog(
            (Frame) this.getParent(), 
            SelectBooksDialog.SHOW_ONLY_NOT_BORROWED,
            false, 
            false,
            true,
            booksIdsList
        );
        filtro.setVisible(true);        
        if (!filtro.isCanceled()) {
            int[] booksIds = filtro.getBooksIds();
            for (int bookId : booksIds) {
                booksIdsList.add(bookId);
                listBooks();
            }            
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Confirmar os dados do empréstimo.
     */
    private void save() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (!jtfApplicant.getText().equals("") && 
        !jftDate.getText().equals("  /  /    ")) {
            int[] booksIds = new int[booksIdsList.size()];
            for (int i = 0; i < booksIdsList.size(); i++) {
                booksIds[i] = booksIdsList.get(i);
            }
            String applicant = jtfApplicant.getText();
            Date date = StrParser.asDate(jftDate.getText());
            try {
                Book[] books = new Book[booksIds.length];
                for (int i = 0; i < booksIds.length; i++) {
                    books[i] = Collection.getBook(
                        booksIds[i],
                        false
                    );
                }
                Collection.borrowBook(new LoanData(0, applicant, date, books));
                setVisible(false);
            } catch (Exception ex) {
                ErrorDialog.showException(
                    (Frame) this.getParent(),
                    "Erro ao salvar os dados do empréstimo.",
                    ex
                );
            }
        } else {
            jtfApplicant.requestFocus();
            JOptionPaneEx.showMessageDialog(this,
                "Preencher todos os campos e inserir pelo menos " +
                "01 livro para prosseguir.",
                "Erro ao salvar os dados do empréstimo.",
                JOptionPaneEx.ERROR_MESSAGE
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbCancel = new javax.swing.JButton();
        jbSalve = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtBooks1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtBooks2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jftDate = new javax.swing.JFormattedTextField();
        jtfApplicant = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jbInsert = new javax.swing.JButton();
        jbRemove = new javax.swing.JButton();
        jbClear = new javax.swing.JButton();
        jbFilter = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EMPRÉSTIMO DE LIVROS");
        setResizable(false);

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jbSalve.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon2.png"))); // NOI18N
        jbSalve.setText("Salvar");
        jbSalve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSalveActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jtBooks1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jtBooks1.setFillsViewportHeight(true);
        jtBooks1.setRowHeight(18);
        jtBooks1.setShowHorizontalLines(false);
        jtBooks1.setShowVerticalLines(false);
        jtBooks1.getTableHeader().setResizingAllowed(false);
        jtBooks1.getTableHeader().setReorderingAllowed(false);
        jtBooks1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtBooks1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtBooks1);

        jtBooks2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jtBooks2.setFillsViewportHeight(true);
        jtBooks2.setRowHeight(18);
        jtBooks2.setShowHorizontalLines(false);
        jtBooks2.setShowVerticalLines(false);
        jtBooks2.getTableHeader().setResizingAllowed(false);
        jtBooks2.getTableHeader().setReorderingAllowed(false);
        jtBooks2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtBooks2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtBooks2);

        jLabel3.setText("Solicitante:");

        jLabel1.setText("Data:");

        try {
            jftDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel4.setText("Livros Emprestados (Lista inferior):");

        jbInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon46.png"))); // NOI18N
        jbInsert.setText("Inserir");
        jbInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbInsertActionPerformed(evt);
            }
        });

        jbRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon45.png"))); // NOI18N
        jbRemove.setText("Remover");
        jbRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveActionPerformed(evt);
            }
        });

        jbClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon3.png"))); // NOI18N
        jbClear.setText("Esvaziar");
        jbClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbClearActionPerformed(evt);
            }
        });

        jbFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon9.png"))); // NOI18N
        jbFilter.setText("Filtrar");
        jbFilter.setMaximumSize(new java.awt.Dimension(79, 25));
        jbFilter.setMinimumSize(new java.awt.Dimension(79, 25));
        jbFilter.setPreferredSize(new java.awt.Dimension(79, 25));
        jbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jtfApplicant))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jftDate, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbClear, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jftDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfApplicant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jbInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbClear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbSalve, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbSalve)
                    .addComponent(jbCancel))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbSalveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSalveActionPerformed
        save();
    }//GEN-LAST:event_jbSalveActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jtBooks1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooks1MouseClicked
        if (evt.getClickCount() == 2) {
            if (jtBooks1.getRowCount() > 0) {
                insertSelectedBooks();
            }
        }
        boolean enabled = jtBooks1.getSelectedRows().length > 0;
        jbInsert.setEnabled(enabled);
        
    }//GEN-LAST:event_jtBooks1MouseClicked

    private void jbInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbInsertActionPerformed
        insertSelectedBooks();
    }//GEN-LAST:event_jbInsertActionPerformed

    private void jbRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveActionPerformed
        deleteSelectedBooks();
    }//GEN-LAST:event_jbRemoveActionPerformed

    private void jbClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbClearActionPerformed
        clearBooksList();
    }//GEN-LAST:event_jbClearActionPerformed

    private void jtBooks2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtBooks2MouseClicked
        if (evt.getClickCount() == 2) {
            if (jtBooks1.getRowCount() > 0) {
                deleteSelectedBooks();
            }
        }
        boolean enabled = jtBooks2.getSelectedRows().length > 0;
        jbRemove.setEnabled(enabled);
        jbClear.setEnabled(enabled);
    }//GEN-LAST:event_jtBooks2MouseClicked

    private void jbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFilterActionPerformed
        find();
    }//GEN-LAST:event_jbFilterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbClear;
    private javax.swing.JButton jbFilter;
    private javax.swing.JButton jbInsert;
    private javax.swing.JButton jbRemove;
    private javax.swing.JButton jbSalve;
    private javax.swing.JFormattedTextField jftDate;
    private javax.swing.JTable jtBooks1;
    private javax.swing.JTable jtBooks2;
    private javax.swing.JTextField jtfApplicant;
    // End of variables declaration//GEN-END:variables
}
