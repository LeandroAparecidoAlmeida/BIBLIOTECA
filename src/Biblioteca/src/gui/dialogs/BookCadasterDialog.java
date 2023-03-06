package gui.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import library.Collection;
import library.Book;
import utils.StrParser;
import dialogs.ErrorDialog;
import dialogs.JOptionPaneEx;

/**
 * Tela para o cadastro/alteração de cadastro de um livro.
 * @author Leandro Aparecido de Almeida
 */
public final class BookCadasterDialog extends javax.swing.JDialog {

    /**Caminho do arquivo de imagem de capa.*/
    private File coverFile;
    /**Indicador de que houve alteração na imagem de capa.*/
    private boolean updatedCover;
    /**Modo de operação (cadastro/alteração de cadastro de um livro)*/
    private final int operationMode;
    /**Livro em edição na tela.*/
    private Book book = null;  
    /**Identificador do livro cadastrado/alterado na tela.*/
    private int bookId;
    /**Indica se o usuário clicou no botão cancelar.*/
    private boolean canceled;

    /**
     * Instanciar a tela em modo de cadastro de um novo livro.
     * @param parent frame proprietário.
     */
    public BookCadasterDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        setTitle("CADASTRAR NOVO LIVRO");
        operationMode = 1;
        coverFile = null;
        bookId = -1;
        canceled = true;
        jftPurchaseDate.setText(StrParser.asString(new Date()));
        jftEdition.setText("1");
        jftVolume.setText("1");        
        jlCover.setIcon(null);
        jlCover.setText("[Nenhuma imagem de capa]");
    }

    /**
     * Instanciar a tela em modo de alteração de cadastro de um livro.
     * @param parent frame proprietário.
     * @param book livro a ter o cadastro alterado.
     */
    public BookCadasterDialog(java.awt.Frame parent, Book book) throws SQLException,
    IOException {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);
        setTitle("ALTERAR CADASTRO DO LIVRO");
        operationMode = 2;
        updatedCover = false;
        coverFile = null;
        this.book = book;
        bookId = this.book.getId();
        canceled = true;
        jtfTitle.setText(book.getTitle());
        jtfSubtitle.setText(book.getSubtitle());
        jftPurchaseDate.setText(StrParser.asString(book.getPurchaseDate()));
        jtfAuthor.setText(book.getAuthor());
        jtfPublisher.setText(book.getPublisher());
        jftIsbn.setText(book.getIsbn());
        jftEdition.setText(StrParser.asString(book.getEdition()));
        jftVolume.setText(StrParser.asString(book.getVolume()));
        jftIssueYear.setText(StrParser.asString(book.getIssueYear()));
        jftNumPages.setText(StrParser.asString(book.getNumPages()));
        jtaSummary.setText(book.getSummary()); 
        jtaSummary.setSelectionStart(0);
        jtaSummary.setSelectionEnd(0);
        Blob cover = book.getCover();
        if (cover != null) {
            BufferedImage bfImage = ImageIO.read(cover.getBinaryStream());
            Image image = bfImage.getScaledInstance(
                jlCover.getWidth(),
                jlCover.getHeight(),
                Image.SCALE_SMOOTH
            );
            ImageIcon imageIcon = new ImageIcon(image);
            jlCover.setIcon(imageIcon); 
            jlCover.setText("");
        } else {
            jlCover.setText("[Nenhuma imagem de capa]");
        }
    }
    
    /**
     * Abrir o arquivo com a imagem de capa.
     */
    private void openImageFile() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        JFileChooser fileChooser = new JFileChooser();
        String exts[] = new String[] {"jpg", "jpeg", "png", "gif"};
        FileFilter filter1 = new FileNameExtensionFilter(
            "Arquivo de imagem (.jpg, .jpeg, .png, .gif)",
            exts
        );
        fileChooser.addChoosableFileFilter(filter1);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Abrir Capa");
        int option = fileChooser.showOpenDialog(this);
        if (option != JFileChooser.CANCEL_OPTION) {
            updatedCover = true;
            coverFile = fileChooser.getSelectedFile();
            try {
                BufferedImage bfImage = ImageIO.read(coverFile);
                Image image = bfImage.getScaledInstance(
                    jlCover.getWidth(),
                    jlCover.getHeight(),
                    Image.SCALE_SMOOTH
                );
                ImageIcon imageIcon = new ImageIcon(image);
                jlCover.setIcon(imageIcon);
                jlCover.setText("");
            } catch (Exception ex) {
                ErrorDialog.showException(
                    (Frame) this.getParent(),
                    "Erro ao abrir a imagem.",
                    ex
                );
            }
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Remover a imagem de capa.
     */
    private void deleteCover() {
        jlCover.setIcon(null);
        jlCover.setText("[Nenhuma imagem de capa]");
        updatedCover = true;
    }

    /**
     * Salvar os dados de cadastro do livro em edição.
     */
    private void save() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        boolean emptyFields =
        jtfTitle.getText().equals("") ||        
        jftPurchaseDate.getText().equals("  /  /    ") ||
        jtfAuthor.getText().equals("") ||
        jtfPublisher.getText().equals("") ||
        jftIsbn.getText().equals("") ||
        jftEdition.getText().equals("") ||
        jftVolume.getText().equals("") ||
        jftIssueYear.getText().equals("") ||
        jftNumPages.getText().equals("");
        if (!emptyFields) {
            try {
                String title = jtfTitle.getText();
                String subtitle = !jtfSubtitle.getText().equals("") ? 
                jtfSubtitle.getText() : null;
                Date purchaseDate = StrParser.asDate(jftPurchaseDate.getText());
                String author = jtfAuthor.getText();
                String publisher = jtfPublisher.getText();
                String isbn = jftIsbn.getText();
                int edition = StrParser.asInt(jftEdition.getText());
                int volume = StrParser.asInt(jftVolume.getText());
                int issueYear = StrParser.asInt(jftIssueYear.getText());
                int numPages = StrParser.asInt(jftNumPages.getText());
                String summary = jtaSummary.getText();
                Blob cover = null;
                if (coverFile != null) {
                    InputStream istream = new FileInputStream(coverFile);
                    byte[] bytes = new byte[istream.available()];
                    istream.read(bytes); 
                    cover = new SerialBlob(bytes);
                }
                switch (operationMode) {
                    //Cadastro de um novo livro.
                    case 1: {
                        book = new Book(
                            0,
                            title,
                            subtitle,
                            purchaseDate,
                            author,
                            publisher,
                            isbn,
                            edition,
                            volume,
                            issueYear,
                            numPages,
                            summary,
                            cover
                        ); 
                        bookId = Collection.addNewBook(book);
                    } break;
                    //Alteração do cadastro de um livro.
                    case 2: {
                        book.setTitle(title);
                        book.setSubtitle(subtitle);
                        book.setPurchaseDate(purchaseDate);
                        book.setAuthor(author);
                        book.setPublisher(publisher);
                        book.setIsbn(isbn);
                        book.setEdition(edition);
                        book.setVolume(volume);
                        book.setIssueYear(issueYear);
                        book.setNumPages(numPages);
                        book.setSummary(summary);
                        if (updatedCover) {
                            book.setCover(cover);
                        }
                        Collection.updateBook(book);                        
                    } break;                    
                }
                canceled = false;
                setVisible(false);
            } catch (Exception ex) {
                ErrorDialog.showException(
                    (Frame) this.getParent(),
                    "Erro ao salvar os dados do cadastro.",
                    ex
                );
            }
        } else {
            //Destaca os campos de preenchimento obrigatório.
            Color color = new Color(255,255,204);
            jtfTitle.setBackground(color);
            jftPurchaseDate.setBackground(color);
            jtfAuthor.setBackground(color);
            jtfPublisher.setBackground(color);
            jftIsbn.setBackground(color);
            jftEdition.setBackground(color);
            jftVolume.setBackground(color);
            jftIssueYear.setBackground(color);
            jftNumPages.setBackground(color);
            jtfTitle.requestFocus();
            JOptionPaneEx.showMessageDialog(
                this,
                "Preencher todos os campos em destaque para prosseguir.",
                "Erro",
                JOptionPaneEx.ERROR_MESSAGE
            );          
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**Obter o identificador (id) do livro que foi cadastrado/alterado.*/
    public int getBookId() {
        return bookId;
    }

    /**Obter o status de clique no botão Cancelar.*/
    public boolean isCanceled() {
        return canceled;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jftIssueYear = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jtfAuthor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jftIsbn = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jtfPublisher = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jftVolume = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jtfTitle = new javax.swing.JTextField();
        jftNumPages = new javax.swing.JFormattedTextField();
        jftPurchaseDate = new javax.swing.JFormattedTextField();
        jbCancel = new javax.swing.JButton();
        jftEdition = new javax.swing.JFormattedTextField();
        jbSave = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jtfSubtitle = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jpCover = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jlCover = new javax.swing.JLabel();
        jpSummary = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaSummary = new javax.swing.JTextArea();
        jbOpenCover = new javax.swing.JButton();
        jbDeleteCover = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jftIssueYear.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        jLabel4.setText("Autor(es):");

        jLabel2.setText("ISBN:");

        jLabel3.setText("Edição:");

        jLabel5.setText("Ano Public:");

        jLabel7.setText("Volume:");

        jLabel6.setText("Editora:");

        jftVolume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        jLabel1.setText("Título:");

        jLabel8.setText("Núm. Págs:");

        jLabel10.setText("Data Aquisição");

        jftNumPages.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        try {
            jftPurchaseDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jftEdition.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        jbSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon2.png"))); // NOI18N
        jbSave.setText("Salvar");
        jbSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSaveActionPerformed(evt);
            }
        });

        jLabel11.setText("Subtítulo:");

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jpCover.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlCover.setForeground(new java.awt.Color(102, 102, 102));
        jlCover.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlCover, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlCover, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpCoverLayout = new javax.swing.GroupLayout(jpCover);
        jpCover.setLayout(jpCoverLayout);
        jpCoverLayout.setHorizontalGroup(
            jpCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpCoverLayout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(171, Short.MAX_VALUE))
        );
        jpCoverLayout.setVerticalGroup(
            jpCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Capa", jpCover);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jtaSummary.setColumns(20);
        jtaSummary.setLineWrap(true);
        jtaSummary.setRows(5);
        jtaSummary.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jtaSummary);

        javax.swing.GroupLayout jpSummaryLayout = new javax.swing.GroupLayout(jpSummary);
        jpSummary.setLayout(jpSummaryLayout);
        jpSummaryLayout.setHorizontalGroup(
            jpSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
        );
        jpSummaryLayout.setVerticalGroup(
            jpSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Sinopse", jpSummary);

        jbOpenCover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
        jbOpenCover.setText("Abrir Capa");
        jbOpenCover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpenCoverActionPerformed(evt);
            }
        });

        jbDeleteCover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon3.png"))); // NOI18N
        jbDeleteCover.setText("Remover Capa");
        jbDeleteCover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDeleteCoverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(jtfPublisher)
                    .addComponent(jtfAuthor)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbOpenCover, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbDeleteCover)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbSave, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtfTitle)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel11)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jftIsbn, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jftEdition, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jftVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jftIssueYear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jftNumPages, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jftPurchaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jtfSubtitle))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtfPublisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jftIsbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jftIssueYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel8))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jftNumPages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jftVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(26, 26, 26)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jftEdition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(26, 26, 26)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftPurchaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbSave)
                    .addComponent(jbCancel)
                    .addComponent(jbOpenCover)
                    .addComponent(jbDeleteCover))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jbSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSaveActionPerformed
        save();
    }//GEN-LAST:event_jbSaveActionPerformed

    private void jbOpenCoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpenCoverActionPerformed
        openImageFile();
    }//GEN-LAST:event_jbOpenCoverActionPerformed

    private void jbDeleteCoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDeleteCoverActionPerformed
        deleteCover();
    }//GEN-LAST:event_jbDeleteCoverActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        boolean visible = jTabbedPane1.getSelectedIndex() == 0;
        jbOpenCover.setVisible(visible);
        jbDeleteCover.setVisible(visible);
    }//GEN-LAST:event_jTabbedPane1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbDeleteCover;
    private javax.swing.JButton jbOpenCover;
    private javax.swing.JButton jbSave;
    private javax.swing.JFormattedTextField jftEdition;
    private javax.swing.JFormattedTextField jftIsbn;
    private javax.swing.JFormattedTextField jftIssueYear;
    private javax.swing.JFormattedTextField jftNumPages;
    private javax.swing.JFormattedTextField jftPurchaseDate;
    private javax.swing.JFormattedTextField jftVolume;
    private javax.swing.JLabel jlCover;
    private javax.swing.JPanel jpCover;
    private javax.swing.JPanel jpSummary;
    private javax.swing.JTextArea jtaSummary;
    private javax.swing.JTextField jtfAuthor;
    private javax.swing.JTextField jtfPublisher;
    private javax.swing.JTextField jtfSubtitle;
    private javax.swing.JTextField jtfTitle;
    // End of variables declaration//GEN-END:variables

}