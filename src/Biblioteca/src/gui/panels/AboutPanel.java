package gui.panels;

import dialogs.ErrorDialog;
import environment.Config;
import gui.dialogs.MainWindow;

public class AboutPanel extends javax.swing.JPanel {

    private final MainWindow mainWindow;

    public AboutPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
        try {
            String versionNumber = Config.getString("version_number");
            String developer = Config.getString("developer");
            String versionDate = Config.getString("version_date");
            jlVersionNumber.setText(versionNumber);
            jlDeveloper.setText(developer);
            jlVersionDate.setText(versionDate);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this.mainWindow,
                "Erro",
                ex
            );
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jlVersionNumber = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlDeveloper = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlVersionDate = new javax.swing.JLabel();

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("BIBLIOTECA");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jlVersionNumber.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jlVersionNumber.setText("[versao]");
        jPanel1.add(jlVersionNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("DESENVOLVEDOR:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, -1, -1));

        jlDeveloper.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jlDeveloper.setText("[desenvolvedor]");
        jPanel1.add(jlDeveloper, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("DATA DA VERSÃO:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, -1));

        jlVersionDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jlVersionDate.setText("[data da versão]");
        jPanel1.add(jlVersionDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 596, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jlDeveloper;
    private javax.swing.JLabel jlVersionDate;
    private javax.swing.JLabel jlVersionNumber;
    // End of variables declaration//GEN-END:variables
}
