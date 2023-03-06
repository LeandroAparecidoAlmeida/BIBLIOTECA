package gui.dialogs;

import java.awt.Frame;
import environment.Config;
import dialogs.ErrorDialog;

/**
 * Tela de informações sobre a versão atual do sistema.
 * @author Leandro Aparecido de Almeida
 */
public final class AboutDialog extends javax.swing.JDialog {

    public AboutDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        try {
            String versionNumber = Config.getString("version_number");
            String developer = Config.getString("developer");
            String versionDate = Config.getString("version_date");
            jlVersionNumber.setText(versionNumber);
            jlDeveloper.setText(developer);
            jlVersionDate.setText(versionDate);
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) this.getParent(),
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
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOBRE O PROGRAMA");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("BIBLIOTECA");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, -1));

        jlVersionNumber.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jlVersionNumber.setText("[versao]");
        jPanel1.add(jlVersionNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("DESENVOLVEDOR:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, -1, -1));

        jlDeveloper.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jlDeveloper.setText("[desenvolvedor]");
        jPanel1.add(jlDeveloper, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("DATA DA VERSÃO:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, -1, -1));

        jlVersionDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jlVersionDate.setText("[data da versão]");
        jPanel1.add(jlVersionDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, -1, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, 360, 10));

        jScrollPane1.setBorder(null);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Sistema para o gerenciamento do acervo \nparticular do autor, baseado na plataforma\nJava Standard Edition (para ambientes \nDesktop).\n");
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 370, 100));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Background.jpeg"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 690, 460));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel jlDeveloper;
    private javax.swing.JLabel jlVersionDate;
    private javax.swing.JLabel jlVersionNumber;
    // End of variables declaration//GEN-END:variables
}
