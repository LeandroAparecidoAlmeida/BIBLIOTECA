package gui.panels;

import dialogs.ErrorDialog;
import environment.Config;
import gui.dialogs.MainWindow;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.JColorChooser;

/**
 * JPanel para configurações do sistema.
 * @author Leandro Aparecido de Almeida
 */
public class ConfigurationsPanel extends javax.swing.JPanel {

    /**Instância de {@link MainWindow}.*/
    private final MainWindow mainWindow;
    /**Cor da fonte para livros emprestados.*/
    private Color foreground1;
    /**Cor da fonte para livros descartados*/
    private Color foreground2;
    /**Estilo da fonte para livros emprestados.*/
    private int fontStyle1;
    /**Estilo da fonte para livros descartados.*/
    private int fontStyle2;
    /**Tipo de relatório.*/
    private int reportType;    

    public ConfigurationsPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
        try {
            foreground1 = new Color(Config.getInteger("foreground_1"));
            fontStyle1 = Config.getInteger("font_style_1");
            foreground2 = new Color(Config.getInteger("foreground_2"));
            fontStyle2 = Config.getInteger("font_style_2");
            reportType = Config.getInteger("report_type");
            update();
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) mainWindow,
                "Erro",
                ex
            );
        }
    }
        
    /**
     * Trocar a cor da fonte para livros emprestados.
     */
    private void changeFontColorBorrowedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        Color color = JColorChooser.showDialog(
            mainWindow,
            "Escolher a cor da fonte para Livros Emprestados",
            jtfFontColorBorrowedBooks.getForeground()
        );
        if (color != null) {
            foreground1 = color;
            update();
            saveConfigurations();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Trocar a cor da fonte para livros descartados.
     */
    private void changeFontColorDiscardedBooks() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        Color color = JColorChooser.showDialog(
            mainWindow,
            "Escolher a cor da fonte para Livros Descartados",
            jtfFontColorDiscadedBooks.getForeground()
        );
        if (color != null) {
            foreground2 = color;
            update();
            saveConfigurations();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
    }
    
    /**
     * Trocar o estilo da fonte para livros emprestados.
     */
    private void changeFontStyleBorrowedBooks() {
        fontStyle1 = Font.PLAIN;
        if (jcbItalicBorrowedBooks.isSelected()) fontStyle1 += Font.ITALIC;
        if (jcbBoldBorrowedBooks.isSelected()) fontStyle1 += Font.BOLD;
        update();
        saveConfigurations();
    }
    
    /**
     * Trocar o estilo da fonte para livros descartados.
     */
    private void changeFontStyleDiscardedBooks() {
        fontStyle2 = Font.PLAIN;
        if (jcbItalicDiscadedBooks.isSelected()) fontStyle2 += Font.ITALIC;
        if (jcbBoldDiscadedBooks.isSelected()) fontStyle2 += Font.BOLD;
        update();
        saveConfigurations();
    }
    
    /**
     * Trocar o tipo de relatório.
     */
    private void changeReportType() {
        if (jrbDefaultFormat.isSelected()) {
            reportType = 1;
        } else if (jrbPdfFormat.isSelected()) {
            reportType = 2;
        } else if (jrbHtmlFormat.isSelected()) {
            reportType = 3;
        }
        update();
        saveConfigurations();
    }
    
    /**
     * Atualizar a interface da tela de acordo com as configurações definidas.
     */
    private void update() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {            
            jtfFontColorBorrowedBooks.setForeground(foreground1);            
            jcbBoldBorrowedBooks.setSelected(fontStyle1 == 1 || fontStyle1 == 3);
            jcbItalicBorrowedBooks.setSelected(fontStyle1 == 2 || fontStyle1 == 3);
            jtfFontColorBorrowedBooks.setFont(new Font("tahoma", fontStyle1, 11));            
            jtfFontColorDiscadedBooks.setForeground(foreground2);            
            jcbBoldDiscadedBooks.setSelected(fontStyle2 == 1 || fontStyle2 == 3);
            jcbItalicDiscadedBooks.setSelected(fontStyle2 == 2 || fontStyle2 == 3);
            jtfFontColorDiscadedBooks.setFont(new Font("tahoma", fontStyle2, 11));            
            switch (reportType) {
                case 1: jrbDefaultFormat.setSelected(true); break;
                case 2: jrbPdfFormat.setSelected(true); break;
                case 3: jrbHtmlFormat.setSelected(true); break;
            }
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Gravar as configurações no banco de dados.
     */
    private void saveConfigurations() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            Config.setInteger("foreground_1", foreground1.getRGB());
            Config.setInteger("font_style_1", fontStyle1);
            Config.setInteger("foreground_2", foreground2.getRGB());
            Config.setInteger("font_style_2", fontStyle2);
            Config.setInteger("report_type", reportType);
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) mainWindow,
                "Erro",
                ex
            );
        }        
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Restaurar as configurações da 1ª guia para valores padrão.
     */
    private void restoreTextConfigurations() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            foreground1 = new Color(0,153,51);
            fontStyle1 = Font.PLAIN;
            foreground2 = Color.RED;  
            fontStyle2 = Font.PLAIN;           
            update();
            saveConfigurations();
        } catch (Exception ex) {
            ErrorDialog.showException(
                (Frame) mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jtfFontColorDiscadedBooks = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jcbBoldDiscadedBooks = new javax.swing.JCheckBox();
        jcbItalicDiscadedBooks = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jcbBoldBorrowedBooks = new javax.swing.JCheckBox();
        jcbItalicBorrowedBooks = new javax.swing.JCheckBox();
        jtfFontColorBorrowedBooks = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jrbDefaultFormat = new javax.swing.JRadioButton();
        jrbPdfFormat = new javax.swing.JRadioButton();
        jrbHtmlFormat = new javax.swing.JRadioButton();

        jMenuItem1.setText("Restaurar Padrões");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jPanel1.setComponentPopupMenu(jPopupMenu1);

        jtfFontColorDiscadedBooks.setEditable(false);
        jtfFontColorDiscadedBooks.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jtfFontColorDiscadedBooks.setText("  Cor de destaque para livros descartados (clique para alterar)");
        jtfFontColorDiscadedBooks.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtfFontColorDiscadedBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfFontColorDiscadedBooksMouseClicked(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jcbBoldDiscadedBooks.setText("Negrito");
        jcbBoldDiscadedBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbBoldDiscadedBooksActionPerformed(evt);
            }
        });

        jcbItalicDiscadedBooks.setText("Itálico");
        jcbItalicDiscadedBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbItalicDiscadedBooksActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbBoldDiscadedBooks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbItalicDiscadedBooks)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jcbBoldDiscadedBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jcbItalicDiscadedBooks))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jcbBoldBorrowedBooks.setText("Negrito");
        jcbBoldBorrowedBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbBoldBorrowedBooksActionPerformed(evt);
            }
        });

        jcbItalicBorrowedBooks.setText("Itálico");
        jcbItalicBorrowedBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbItalicBorrowedBooksActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbBoldBorrowedBooks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbItalicBorrowedBooks)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jcbBoldBorrowedBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jcbItalicBorrowedBooks))
        );

        jtfFontColorBorrowedBooks.setEditable(false);
        jtfFontColorBorrowedBooks.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jtfFontColorBorrowedBooks.setText("  Cor de destaque para livros emprestados (clique para alterar)");
        jtfFontColorBorrowedBooks.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtfFontColorBorrowedBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtfFontColorBorrowedBooksMouseClicked(evt);
            }
        });

        jLabel3.setText("Cor de Fonte:");

        jLabel4.setText("Estilo de fonte:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(jtfFontColorBorrowedBooks)
                    .addComponent(jtfFontColorDiscadedBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfFontColorDiscadedBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtfFontColorBorrowedBooks, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(448, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Destaques de Texto", jPanel1);

        buttonGroup1.add(jrbDefaultFormat);
        jrbDefaultFormat.setSelected(true);
        jrbDefaultFormat.setText("Formato padrão do sistema");
        jrbDefaultFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbDefaultFormatActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbPdfFormat);
        jrbPdfFormat.setText("Formato PDF (Portable Document Format):");
        jrbPdfFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbPdfFormatActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbHtmlFormat);
        jrbHtmlFormat.setText("Formato HTML (HyperText Markup Language)");
        jrbHtmlFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbHtmlFormatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jrbPdfFormat)
                    .addComponent(jrbDefaultFormat)
                    .addComponent(jrbHtmlFormat))
                .addContainerGap(396, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jrbDefaultFormat)
                .addGap(14, 14, 14)
                .addComponent(jrbPdfFormat)
                .addGap(18, 18, 18)
                .addComponent(jrbHtmlFormat)
                .addContainerGap(453, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Relatórios", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1)
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtfFontColorDiscadedBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfFontColorDiscadedBooksMouseClicked
        changeFontColorDiscardedBooks();
    }//GEN-LAST:event_jtfFontColorDiscadedBooksMouseClicked

    private void jcbBoldDiscadedBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbBoldDiscadedBooksActionPerformed
        changeFontStyleDiscardedBooks();
    }//GEN-LAST:event_jcbBoldDiscadedBooksActionPerformed

    private void jcbItalicDiscadedBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbItalicDiscadedBooksActionPerformed
        changeFontStyleDiscardedBooks();
    }//GEN-LAST:event_jcbItalicDiscadedBooksActionPerformed

    private void jcbBoldBorrowedBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbBoldBorrowedBooksActionPerformed
        changeFontStyleBorrowedBooks();
    }//GEN-LAST:event_jcbBoldBorrowedBooksActionPerformed

    private void jcbItalicBorrowedBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbItalicBorrowedBooksActionPerformed
        changeFontStyleBorrowedBooks();
    }//GEN-LAST:event_jcbItalicBorrowedBooksActionPerformed

    private void jtfFontColorBorrowedBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfFontColorBorrowedBooksMouseClicked
        changeFontColorBorrowedBooks();
    }//GEN-LAST:event_jtfFontColorBorrowedBooksMouseClicked

    private void jrbDefaultFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbDefaultFormatActionPerformed
        changeReportType();
    }//GEN-LAST:event_jrbDefaultFormatActionPerformed

    private void jrbPdfFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbPdfFormatActionPerformed
        changeReportType();
    }//GEN-LAST:event_jrbPdfFormatActionPerformed

    private void jrbHtmlFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbHtmlFormatActionPerformed
        changeReportType();
    }//GEN-LAST:event_jrbHtmlFormatActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        restoreTextConfigurations();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox jcbBoldBorrowedBooks;
    private javax.swing.JCheckBox jcbBoldDiscadedBooks;
    private javax.swing.JCheckBox jcbItalicBorrowedBooks;
    private javax.swing.JCheckBox jcbItalicDiscadedBooks;
    private javax.swing.JRadioButton jrbDefaultFormat;
    private javax.swing.JRadioButton jrbHtmlFormat;
    private javax.swing.JRadioButton jrbPdfFormat;
    private javax.swing.JTextField jtfFontColorBorrowedBooks;
    private javax.swing.JTextField jtfFontColorDiscadedBooks;
    // End of variables declaration//GEN-END:variables

}