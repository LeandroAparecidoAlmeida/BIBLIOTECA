package gui.dialogs;

import java.awt.Cursor;
import java.util.Date;
import utils.StrParser;
import dialogs.JOptionPaneEx;

/**
 * Tela para a devolução de um ou mais livros que foi(ram) emprestado(s).
 * @author Leandro Aparecido de Almeida
 */
public final class ConfirmDevolutionDialog extends javax.swing.JDialog {

    /**Indica se o usuário clicou no botão cancelar.*/
    private boolean canceled;
    /**Data da devolução do(s) livro(s).*/
    private Date date;
    
    /**
     * Constructor da classe.
     * @param parent frame proprietário.
     * @param bookTitle título(s) do(s) livro(s).
     * @param applicant nome(s) do(s) solicitante(s) que fez(fizeram) a 
     * devolução.
     */
    public ConfirmDevolutionDialog(java.awt.Frame parent, String bookTitle, 
    String applicant) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        canceled = true;
        date = null;        
        jtpNotes.setText(bookTitle); 
        jtfApplicant.setText(applicant);
        jftDate.setText(StrParser.asString(new Date()));        
    }
    
    /**
     * Confirmar a devolução do(s) livro(s) selecionado(s).
     */
    private void confirm() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));  
        if (!jftDate.getText().equals("  /  /    ")) {
            int option = JOptionPaneEx.showConfirmDialog(this,
                "Confima a devolução do(s) livro(s)?", 
                "Atenção!",
                JOptionPaneEx.YES_NO_OPTION,
                JOptionPaneEx.QUESTION_MESSAGE
            );
            if (option == JOptionPaneEx.YES_OPTION) {
                date = StrParser.asDate(jftDate.getText());
                canceled = false;
                setVisible(false);
            }
        } else {
            jftDate.requestFocus();
            JOptionPaneEx.showMessageDialog(this,
                "Informar a data da devolução para prosseguir.",
                "Erro ao confirmar a devolução do(s) livro(s).",
                JOptionPaneEx.ERROR_MESSAGE
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
    }
    
    /**Obter o status de clique no botão Cancelar.*/
    public boolean isCanceled() {
        return canceled;
    }

    /**Obter a data da devolução do(s) livro(s).*/
    public Date getDate() {        
        return date;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jftDate = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpNotes = new javax.swing.JTextPane();
        jbCancel = new javax.swing.JButton();
        jbConfirm = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jtfApplicant = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DEVOLUÇÃO DO(S) LIVRO(S)");
        setResizable(false);

        jLabel1.setText("Data Devolução:");

        try {
            jftDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jtpNotes.setEditable(false);
        jtpNotes.setEnabled(false);
        jScrollPane1.setViewportView(jtpNotes);

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

        jLabel3.setText("Título(s):");

        jLabel4.setText("Solicitante(s):");

        jtfApplicant.setEditable(false);
        jtfApplicant.setBackground(new java.awt.Color(255, 255, 255));
        jtfApplicant.setForeground(new java.awt.Color(102, 102, 102));
        jtfApplicant.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jtfApplicant))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jftDate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 264, Short.MAX_VALUE)
                                .addComponent(jbConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfApplicant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jbConfirm))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jbConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConfirmActionPerformed
        confirm();
    }//GEN-LAST:event_jbConfirmActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbConfirm;
    private javax.swing.JFormattedTextField jftDate;
    private javax.swing.JTextField jtfApplicant;
    private javax.swing.JTextPane jtpNotes;
    // End of variables declaration//GEN-END:variables
}
