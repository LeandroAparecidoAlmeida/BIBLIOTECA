package gui.dialogs;

import java.awt.Cursor;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import dialogs.ErrorDialog;
import java.awt.Color;
import gui.panels.CollectionPanel;
import gui.panels.DiscardBookPanel;
import gui.panels.BookDetailsPanel;
import gui.panels.BorrowBookPanel;
import gui.panels.AboutPanel;
import gui.panels.ConfigurationsPanel;
import gui.panels.ReportsPanel;
import java.awt.Font;
import javax.swing.JButton;

/**
 * Tela principal do programa. Interface gráfica para interação com o usuário.
 * @author Leandro Aparecido de Almeida
 */
public final class MainWindow extends javax.swing.JFrame {

    /**JPanel acoplado no JLayeredPane à direita da tela.*/
    private JPanel panel;
    
    /**
     * Constructor da classe.
     */
    public MainWindow() {
        initComponents();
        URL url = getClass().getResource("/icons32/icon01.png");
        Image image = new ImageIcon(url).getImage();
        setIconImage(image);
        showBookDetailsPanel(-1);
    }

    /**
     * Acoplar o JPanel ao JLayeredPane. 
     * @param panel JPanel a ser acoplado.
     */
    private void createPanel(JPanel panel, int buttonIndex) {
        boolean change = true;
        if (this.panel != null) {
            if (this.panel.getClass().equals(panel.getClass())) {
                change = false;
            }
        }
        if (change) {
            if (this.panel != null && this.panel instanceof BookDetailsPanel) {
                ((BookDetailsPanel)this.panel).dispose();
            }
            this.panel = null;
            System.gc();
            this.panel = panel;
            jLayeredPane.removeAll();
            panel.setLocation(0, 0);
            panel.setSize(660, 595);
            jLayeredPane.add(this.panel);           
            pack();
            Font font1 = new java.awt.Font("Tahoma", 1, 11);
            Font font2 = new java.awt.Font("Tahoma", 3, 11); 
            Color color1 = new java.awt.Color(100,100,100);         
            Color color2 = Color.BLACK;
            button1.setFont(font1);
            button1.setForeground(color1);
            button1.setSelected(false);
            button2.setFont(font1);
            button2.setForeground(color1);
            button2.setSelected(false);
            button3.setFont(font1);
            button3.setForeground(color1);
            button3.setSelected(false);
            button4.setFont(font1);
            button4.setForeground(color1);
            button4.setSelected(false);
            button5.setFont(font1);
            button5.setForeground(color1);
            button5.setSelected(false);
            button6.setFont(font1);
            button6.setForeground(color1);
            button6.setSelected(false);
            button7.setFont(font1);
            button7.setForeground(color1);
            button7.setSelected(false);
            JButton button = null;
            switch (buttonIndex) {
                case 1: button = button1; break;
                case 2: button = button2; break;
                case 3: button = button3; break;
                case 4: button = button4; break;
                case 5: button = button5; break;
                case 6: button = button6; break;
                case 7: button = button7; break;
            }
            button.setFont(font2);
            button.setForeground(color2);  
            button.setSelected(true);
        }
    }
    
    /**
     * Acoplar o JPanel BookDetailsPanel.
     * @param bookId identificar do livro a ser exibido no JPanel.
     */
    public void showBookDetailsPanel(int bookId) {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new BookDetailsPanel(this, bookId), 1);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Acoplar o JPanel CollectionPanel.
     */
    public void showCollectionPanel() {  
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new CollectionPanel(this), 2);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Acoplar o JPanel DiscardBookPanel.
     */
    private void showDiscardBookPanel() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new DiscardBookPanel(this), 3);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Acoplar o JPanel BorrowBookPanel.
     */
    private void showBorrowBookPanel() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new BorrowBookPanel(this), 4);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Acoplar o JPanel ReportsPanel. 
     */
    private void showReportsPanel() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new ReportsPanel(this), 5);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Acoplar o JPanel ReportsPanel. 
     */
    private void showConfigurationsPanel() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new ConfigurationsPanel(this), 6);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**Abrir a tela Sobre o Programa.*/
    private void showAboutDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            createPanel(new AboutPanel(this), 7);
        } catch (Exception ex) {
            ErrorDialog.showException(
                this,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jpMenu = new javax.swing.JPanel();
        button2 = new javax.swing.JButton();
        button6 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button7 = new javax.swing.JButton();
        button5 = new javax.swing.JButton();
        button1 = new javax.swing.JButton();
        jLayeredPane = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BIBLIOTECA");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpMenu.setBackground(new java.awt.Color(153, 153, 153));
        jpMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        button2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button2.setForeground(new java.awt.Color(100, 100, 100));
        button2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon16.png"))); // NOI18N
        button2.setText("Livros no Acervo");
        button2.setFocusable(false);
        button2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        button6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button6.setForeground(new java.awt.Color(100, 100, 100));
        button6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon12.png"))); // NOI18N
        button6.setText("Configurações");
        button6.setFocusable(false);
        button6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });

        button4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button4.setForeground(new java.awt.Color(100, 100, 100));
        button4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon16.png"))); // NOI18N
        button4.setText("Livros Emprestados");
        button4.setFocusable(false);
        button4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });

        button3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button3.setForeground(new java.awt.Color(100, 100, 100));
        button3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon16.png"))); // NOI18N
        button3.setText("Livros Descartados");
        button3.setFocusable(false);
        button3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });

        button7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button7.setForeground(new java.awt.Color(100, 100, 100));
        button7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon14.png"))); // NOI18N
        button7.setText("Sobre o Programa");
        button7.setFocusable(false);
        button7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button7ActionPerformed(evt);
            }
        });

        button5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button5.setForeground(new java.awt.Color(100, 100, 100));
        button5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon4.png"))); // NOI18N
        button5.setText("Relatórios");
        button5.setFocusable(false);
        button5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });

        button1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        button1.setForeground(new java.awt.Color(100, 100, 100));
        button1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon20.png"))); // NOI18N
        button1.setText("Detalhes do Livro");
        button1.setFocusable(false);
        button1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jLayeredPaneLayout = new javax.swing.GroupLayout(jLayeredPane);
        jLayeredPane.setLayout(jLayeredPaneLayout);
        jLayeredPaneLayout.setHorizontalGroup(
            jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );
        jLayeredPaneLayout.setVerticalGroup(
            jLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpMenuLayout = new javax.swing.GroupLayout(jpMenu);
        jpMenu.setLayout(jpMenuLayout);
        jpMenuLayout.setHorizontalGroup(
            jpMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(button7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(button3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(button1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLayeredPane)
                .addContainerGap())
        );
        jpMenuLayout.setVerticalGroup(
            jpMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMenuLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jpMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpMenuLayout.createSequentialGroup()
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLayeredPane))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jpMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //((BookDetailsPanel)jTabbedPane.getComponentAt(0)).dispose();
    }//GEN-LAST:event_formWindowClosing

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        showBookDetailsPanel(-1);
    }//GEN-LAST:event_button1ActionPerformed

    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        showReportsPanel();
    }//GEN-LAST:event_button5ActionPerformed

    private void button7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button7ActionPerformed
        showAboutDialog();
    }//GEN-LAST:event_button7ActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        showDiscardBookPanel();
    }//GEN-LAST:event_button3ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        showBorrowBookPanel();
    }//GEN-LAST:event_button4ActionPerformed

    private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
        showConfigurationsPanel();
    }//GEN-LAST:event_button6ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        showCollectionPanel();
    }//GEN-LAST:event_button2ActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button1;
    private javax.swing.JButton button2;
    private javax.swing.JButton button3;
    private javax.swing.JButton button4;
    private javax.swing.JButton button5;
    private javax.swing.JButton button6;
    private javax.swing.JButton button7;
    private javax.swing.JLayeredPane jLayeredPane;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jpMenu;
    // End of variables declaration//GEN-END:variables
}
