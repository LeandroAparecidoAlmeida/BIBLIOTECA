package gui.panels;

import gui.dialogs.MainWindow;
import gui.dialogs.PrintReportDialog;

public class ReportsPanel extends javax.swing.JPanel {

    private final MainWindow mainWindow;

    public ReportsPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
    }
    
    private void printReport1() {
        PrintReportDialog dialog = new PrintReportDialog(
            mainWindow,
            "RELATÓRIO LIVROS ADQUIRIDOS NO PERÍODO",
            "/report/layout/collection_books.jasper"
        );
        dialog.setVisible(true);
        jcbReport1.setSelected(false);
    }
    
    private void printReport2() {
        PrintReportDialog dialog = new PrintReportDialog(
            mainWindow,
            "RELATÓRIO LIVROS DESCARTADOS NO PERÍODO",
            "/report/layout/discarded_books.jasper"
        );
        dialog.setVisible(true);
        jcbReport2.setSelected(false);
    }
    
    private void printReport3() {
        PrintReportDialog dialog = new PrintReportDialog(
            mainWindow,
            "RELATÓRIO LIVROS EMPRESTADOS NO PERÍODO",
            "/report/layout/borrowed_books.jasper"
        );
        dialog.setVisible(true);
        jcbReport3.setSelected(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jcbReport1 = new javax.swing.JCheckBox();
        jcbReport2 = new javax.swing.JCheckBox();
        jcbReport3 = new javax.swing.JCheckBox();

        jcbReport1.setText("Livros adquiridos no período");
        jcbReport1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbReport1ActionPerformed(evt);
            }
        });

        jcbReport2.setText("Livros descartados no período");
        jcbReport2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbReport2ActionPerformed(evt);
            }
        });

        jcbReport3.setText("Livros emprestados no período");
        jcbReport3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbReport3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbReport1)
                    .addComponent(jcbReport2)
                    .addComponent(jcbReport3))
                .addContainerGap(481, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbReport1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbReport2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbReport3)
                .addContainerGap(514, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbReport1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbReport1ActionPerformed
        printReport1();
    }//GEN-LAST:event_jcbReport1ActionPerformed

    private void jcbReport2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbReport2ActionPerformed
        printReport2();
    }//GEN-LAST:event_jcbReport2ActionPerformed

    private void jcbReport3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbReport3ActionPerformed
        printReport3();
    }//GEN-LAST:event_jcbReport3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jcbReport1;
    private javax.swing.JCheckBox jcbReport2;
    private javax.swing.JCheckBox jcbReport3;
    // End of variables declaration//GEN-END:variables
}
