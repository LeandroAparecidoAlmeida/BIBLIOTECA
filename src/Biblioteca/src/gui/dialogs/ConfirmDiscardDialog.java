package gui.dialogs;

import java.util.Date;
import utils.StrParser;
import dialogs.JOptionPaneEx;

/**
 * Tela para o usuário informar os dados sobre o descarte de um ou mais livros.
 * @author Leandro Aparecido de Almeida
 */
public final class ConfirmDiscardDialog extends javax.swing.JDialog {
    
    /**Indica se o usuário clicou no botão cancelar.*/
    private boolean canceled;
    /**Motivo para o descarte do(s) livro(s) digitado pelo usuário.*/
    private String reason;
    /**Data do descarte do(s) livro(s) digitada pelo usuário.*/
    private Date date;

    /**
     * Constructor da classe.
     * @param parent frame proprietário.
     * @param bookTitle título(s) do(s) livro(s) descartado(s).
     */
    public ConfirmDiscardDialog(java.awt.Frame parent, String bookTitle) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        canceled = true;        
        jtfBookTitle.setText(bookTitle);
        jftDiscardDate.setText(StrParser.asString(new java.util.Date()));        
    }

    /**
     * Confirmar o descarte do(s) livro(s).
     */
    private void confirm() {
        if (!jtaReason.getText().equals("") && 
        !jftDiscardDate.getText().equals("  /  /    ")) {
            reason = jtaReason.getText();
            date = StrParser.asDate(jftDiscardDate.getText());
            canceled = false;
            setVisible(false);
        } else {
            JOptionPaneEx.showMessageDialog(this.getParent(),
                "Informe o motivo para o descarte do(s) livro(s)\n" +
                "e a data para prosseguir.",
                "Erro ao descartar o(s) livro(s).",
                JOptionPaneEx.ERROR_MESSAGE
            );
        }
    }

    /**Obter o status de clique no botão Cancelar.*/
    public boolean isCanceled() {
        return canceled;
    }
    
    /**Obter o motivo para o descarte do(s) livro(s) digitado pelo usuário.*/
    public String getReason() {
        return reason;
    }

    /**Obter a data do descarte do(s) livro(s) digitada pelo usuário.*/
    public Date getDate() {
        return date;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbCancel = new javax.swing.JButton();
        jbConfirm = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jtfBookTitle = new javax.swing.JTextField();
        jftDiscardDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaReason = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DESCARTAR LIVRO(S)");
        setResizable(false);

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

        jLabel1.setText("Digite o motivo para o descarte do(s) livro(s):");

        jLabel2.setText("Título(s) do(s) Livro(s):");

        jtfBookTitle.setEditable(false);
        jtfBookTitle.setBackground(new java.awt.Color(255, 255, 255));
        jtfBookTitle.setForeground(new java.awt.Color(102, 102, 102));

        try {
            jftDiscardDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel3.setText("Data Descarte:");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jtaReason.setColumns(20);
        jtaReason.setLineWrap(true);
        jtaReason.setRows(5);
        jtaReason.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jtaReason);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jtfBookTitle))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jftDiscardDate, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfBookTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jftDiscardDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbConfirm;
    private javax.swing.JFormattedTextField jftDiscardDate;
    private javax.swing.JTextArea jtaReason;
    private javax.swing.JTextField jtfBookTitle;
    // End of variables declaration//GEN-END:variables
}
