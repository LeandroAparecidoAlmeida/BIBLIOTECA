package gui.dialogs;

import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;
import java.awt.Frame;
import library.Book;
import library.Collection;

/**
 * Tela para alteração do motivo de descarte de um livro.
 * @author Leandro Aparecido de Almeida
 */
public final class AlterDiscardDialog extends javax.swing.JDialog {
    
    /**Livro selecionado.*/
    private Book book;

    /**
     * Instanciar a tela de alteração de motivo de descarte.
     * @param parent frame proprietário.
     * @param book livro selecionado.
     */
    public AlterDiscardDialog(java.awt.Frame parent, Book book) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        this.book = book;
        jtfTitle.setText(book.getTitle());
        try {
            String description = Collection.getDiscardReason(book);
            jtaReason.setText(description);
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) this.getParent(),
                "Erro",
                ex
            );
        }
    }
    
    /**
     * Alterar o texto do motivo do descarte do livro.
     */
    private void confirm() {
        String description = jtaReason.getText();
        if (!description.equals("")) {
            try {
                Collection.setDiscardReason(book, description);
                setVisible(false);
            } catch (Exception ex) {
                ErrorDialog.showException(
                    (Frame) this.getParent(),
                    "Erro",
                    ex
                );
            }
        } else {
            jtaReason.requestFocus();
            JOptionPaneEx.showMessageDialog(
                this,
                "Informe o motivo do descarte do livro.",
                "Erro ao alterar o cadastro",
                JOptionPaneEx.ERROR_MESSAGE
            );
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jtaReason = new javax.swing.JTextArea();
        jbConfirm = new javax.swing.JButton();
        jbCancel = new javax.swing.JButton();
        jtfTitle = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ALTERAR MOTIVO DE DESCARTE");
        setResizable(false);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jtaReason.setColumns(20);
        jtaReason.setLineWrap(true);
        jtaReason.setRows(5);
        jtaReason.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jtaReason);

        jbConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon19.png"))); // NOI18N
        jbConfirm.setText("Confirmar");
        jbConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbConfirmActionPerformed(evt);
            }
        });

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jtfTitle.setEditable(false);

        jLabel1.setText("Título:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 343, Short.MAX_VALUE)
                        .addComponent(jbConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addComponent(jtfTitle))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jtfTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jbConfirm))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConfirmActionPerformed
        confirm();
    }//GEN-LAST:event_jbConfirmActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbConfirm;
    private javax.swing.JTextArea jtaReason;
    private javax.swing.JTextField jtfTitle;
    // End of variables declaration//GEN-END:variables
}
