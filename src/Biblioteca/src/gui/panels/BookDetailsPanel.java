package gui.panels;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import database.FBManager;
import library.Collection;
import library.Book;
import utils.StrParser;
import dialogs.ErrorDialog;
import gui.dialogs.SelectBooksDialog;
import report.Report;
import gui.dialogs.MainWindow;

/**
 * JPanel para exibição dos dados de um livro. Permite a navegação por
 * todos os livros cadastrados no banco de dados.
 * @author Leandro Aparecido de Almeida
 */
public final class BookDetailsPanel extends javax.swing.JPanel  {
    
    /**Instância de {@link MainWindow}.*/
    private final MainWindow mainWindow;
    /**Instância de {@link Connection} para conexão com o banco de dados.*/
    private final Connection connection;
    /**Instância de {@link PreparedStatement} para a execução de SQL no banco de dados.*/
    private PreparedStatement pstm;
    /**Instância de {@link ResultSet} com os registros da consulta SQL.*/
    private ResultSet rset;    
    /**Cursor para posicionar no registro específico.*/
    private int cursor;
    /**Posição do último registro da consulta.*/
    private int lastRow;
    /**Identificador (id) do livro em exibição.*/
    private int bookId;

    /**
     * Constructor da classe.
     * @param mainWindow instância de {@link MainWindow}.
     * @param bookId identificador (id) de um livro a ser posicionado.
     */
    public BookDetailsPanel(MainWindow mainWindow, int bookId) throws SQLException {
        this.mainWindow = mainWindow;
        initComponents();
        connection = FBManager.getConnection();
        cursor = lastRow = 1;
        this.bookId = bookId;         
        listBooks(); 
    }
    
    /**
     * Recuperar os identificadores (ids) de todos os livros cadastrados no
     * banco de dados do sistema, ordenados por título do livro.
     */
    
    public void listBooks() {
        try {            
            String sql =
            "SELECT " +
               "ID " +
            "FROM BOOK " +
            "ORDER BY TITLE, ID;";
            if (pstm != null) {
                pstm.close();
                rset.close();
            }
            pstm = connection.prepareStatement(
                sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE
            );
            rset = pstm.executeQuery();
            rset.last();
            lastRow = rset.getRow();
            if (lastRow > 0) {
                moveToEspecificBook(bookId);
            } else {
                jlCover.setText("[Nenhuma imagem de capa]");
                jtfTitle.setText("");
                jtfSubtitle.setText("");
                jftPurchaseDate.setText("");
                jtfAuthor.setText("");
                jtfPublisher.setText("");
                jftIsbn.setText("");
                jftEdition.setText("");
                jftVolume.setText("");
                jftIssueYear.setText("");
                jftNumPages.setText("");
                jtaSummary.setText("");
                jbFirst.setEnabled(false);
                jbPrevious.setEnabled(false);
                jbLast.setEnabled(false);
                jbNext.setEnabled(false);
                jbFind.setEnabled(false);
                jbPrint.setEnabled(false);
            }
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
    }
    
    /**
     * Exibir os dados do livro apontado pelo cursor.
     * @param cursor cursor para um registro no resultset.
     */
    private void select(int cursor) {   
        try {
            this.cursor = cursor;
            rset.absolute(this.cursor);            
            bookId = rset.getInt(1);    
            Book book = Collection.getBook(bookId, true);            
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
            jlCover.setIcon(null);
            Blob cover = book.getCover();
            if (cover != null) {
                BufferedImage bfImage = ImageIO.read(cover.getBinaryStream());
                Image image = bfImage.getScaledInstance(
                    213,
                    282,
                    Image.SCALE_SMOOTH
                );
                ImageIcon imageIcon = new ImageIcon(image);
                jlCover.setText("");
                jlCover.setIcon(imageIcon); 
            } else {
                jlCover.setText("[Nenhuma imagem de capa]");
            }
            if (Collection.isBorrowedBook(book)) {
                jftStatus.setText("Emprestado");
            } else if (Collection.isDiscardedBook(book)) {
                jftStatus.setText("Descartado");
            } else {
                jftStatus.setText("Disponível");
            }
            jbFirst.setEnabled(true);
            jbPrevious.setEnabled(true);
            jbLast.setEnabled(true);
            jbNext.setEnabled(true);
            jbFind.setEnabled(true);
            if (rset.isFirst()) {
                jbFirst.setEnabled(false);
                jbPrevious.setEnabled(false);                
            }
            if (rset.isLast()) {
                jbLast.setEnabled(false);
                jbNext.setEnabled(false);
            }
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
    }
    
    /**
     * Posicionar o cursor no primeiro livro.
     */
    private void moveToFirstBook() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            select(1);
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**Posicionar o cursor no livro anterior ao atual.*/
    private void moveToPreviousBook() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            if (!rset.isFirst()) {
                select(cursor - 1);
            }
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**Posicionar o cursor no livro adiante ao atual.*/
    private void moveToNextBook() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            if (!rset.isLast()) {
                select(cursor + 1);
            }
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Posicionar o cursor no último livro.
     */
    private void moveToLastBook() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            select(lastRow);
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Posicionar o cursor num livro específico selecionado pelo usuário.
     * @param bookId identificador do livro selecionado.
     */
    public void moveToEspecificBook(int bookId) {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            int row = 1;
            rset.beforeFirst();
            while (rset.next()) {
                if (rset.getInt(1) == bookId) {
                    row = rset.getRow();
                    break;
                }
            }
            select(row);
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Imprimir os dados do livro selecionado.
     */
    private void printReport() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            String layout = "/report/layout/book_details.jasper";
            Report report = new Report(layout);
            report.putParameter("ID_BOOK", bookId);
            report.print("DETALHES DO LIVRO");            
        } catch (Exception ex) {
            ErrorDialog.showException(
                mainWindow,
                "Erro",
                ex
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir filtro para o usuário selecionar um livro específico.
     */
    private void find() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SelectBooksDialog dialog = new SelectBooksDialog(
            mainWindow,
            SelectBooksDialog.SHOW_ALL,
            true,
            true,
            false,
            null
        );
        dialog.setVisible(true);        
        if (!dialog.isCanceled()) {
            int id = dialog.getBookId();
            moveToEspecificBook(id);
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Fechar a conexão com o Banco de Dados estabelecida na inicialização
     * do sistema.
     */
    public void dispose() {
        try {
            rset.close();
            pstm.close();
            connection.close();
        } catch (Exception ex) {            
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jftNumPages = new javax.swing.JFormattedTextField();
        jftPurchaseDate = new javax.swing.JFormattedTextField();
        jftEdition = new javax.swing.JFormattedTextField();
        jftIssueYear = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jtfTitle = new javax.swing.JTextField();
        jbFirst = new javax.swing.JButton();
        jbPrevious = new javax.swing.JButton();
        jbNext = new javax.swing.JButton();
        jbLast = new javax.swing.JButton();
        jbFind = new javax.swing.JButton();
        jbPrint = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jpCover = new javax.swing.JPanel();
        jlCover = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaSummary = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jtfSubtitle = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jftStatus = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();

        jLabel4.setText("Autor(es):");

        jtfAuthor.setEditable(false);
        jtfAuthor.setFocusable(false);
        jtfAuthor.setSelectionColor(new java.awt.Color(255, 255, 255));

        jLabel2.setText("ISBN:");

        jftIsbn.setEditable(false);
        jftIsbn.setFocusable(false);
        jftIsbn.setSelectionColor(new java.awt.Color(255, 255, 255));

        jLabel3.setText("Edição:");

        jtfPublisher.setEditable(false);
        jtfPublisher.setFocusable(false);
        jtfPublisher.setSelectionColor(new java.awt.Color(255, 255, 255));

        jLabel5.setText("Ano Public:");

        jLabel7.setText("Volume:");

        jLabel6.setText("Editora:");

        jftVolume.setEditable(false);
        jftVolume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jftVolume.setFocusable(false);
        jftVolume.setSelectionColor(new java.awt.Color(255, 255, 255));

        jLabel8.setText("Núm. Págs:");

        jLabel10.setText("Data Aquis.:");

        jftNumPages.setEditable(false);
        jftNumPages.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jftNumPages.setFocusable(false);
        jftNumPages.setSelectionColor(new java.awt.Color(255, 255, 255));

        jftPurchaseDate.setEditable(false);
        try {
            jftPurchaseDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jftPurchaseDate.setFocusable(false);
        jftPurchaseDate.setSelectionColor(new java.awt.Color(255, 255, 255));

        jftEdition.setEditable(false);
        jftEdition.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jftEdition.setFocusable(false);
        jftEdition.setSelectionColor(new java.awt.Color(255, 255, 255));

        jftIssueYear.setEditable(false);
        jftIssueYear.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jftIssueYear.setFocusable(false);
        jftIssueYear.setSelectionColor(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Título:");

        jtfTitle.setEditable(false);
        jtfTitle.setFocusable(false);
        jtfTitle.setSelectionColor(new java.awt.Color(255, 255, 255));

        jbFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon36.png"))); // NOI18N
        jbFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFirstActionPerformed(evt);
            }
        });

        jbPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon35.png"))); // NOI18N
        jbPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbPreviousActionPerformed(evt);
            }
        });

        jbNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon34.png"))); // NOI18N
        jbNext.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jbNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbNextActionPerformed(evt);
            }
        });

        jbLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon37.png"))); // NOI18N
        jbLast.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jbLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbLastActionPerformed(evt);
            }
        });

        jbFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon9.png"))); // NOI18N
        jbFind.setText(" Filtrar");
        jbFind.setMaximumSize(new java.awt.Dimension(100, 25));
        jbFind.setMinimumSize(new java.awt.Dimension(100, 25));
        jbFind.setPreferredSize(new java.awt.Dimension(100, 25));
        jbFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFindActionPerformed(evt);
            }
        });

        jbPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon4.png"))); // NOI18N
        jbPrint.setText("Imprimir");
        jbPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbPrintActionPerformed(evt);
            }
        });

        jTabbedPane1.setFocusable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jpCover.setBackground(new java.awt.Color(255, 255, 255));
        jpCover.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jlCover.setForeground(new java.awt.Color(102, 102, 102));
        jlCover.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jpCoverLayout = new javax.swing.GroupLayout(jpCover);
        jpCover.setLayout(jpCoverLayout);
        jpCoverLayout.setHorizontalGroup(
            jpCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlCover, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
        );
        jpCoverLayout.setVerticalGroup(
            jpCoverLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlCover, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(jpCover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpCover, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Capa", jPanel1);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jtaSummary.setEditable(false);
        jtaSummary.setBackground(new java.awt.Color(240, 240, 240));
        jtaSummary.setColumns(20);
        jtaSummary.setLineWrap(true);
        jtaSummary.setRows(5);
        jtaSummary.setWrapStyleWord(true);
        jtaSummary.setFocusable(false);
        jtaSummary.setSelectionColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jtaSummary);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 643, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 290, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sinopse", jPanel2);

        jLabel9.setText("Subtítulo:");

        jtfSubtitle.setEditable(false);
        jtfSubtitle.setFocusable(false);
        jtfSubtitle.setSelectionColor(new java.awt.Color(255, 255, 255));
        jtfSubtitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfSubtitleActionPerformed(evt);
            }
        });

        jLabel11.setText("            ");

        jftStatus.setEditable(false);
        jftStatus.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jftStatus.setFocusable(false);
        jftStatus.setSelectionColor(new java.awt.Color(255, 255, 255));

        jLabel12.setText("Situação:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbFirst)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbPrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbLast)
                        .addGap(176, 176, 176)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtfAuthor)
                    .addComponent(jtfPublisher)
                    .addComponent(jtfSubtitle)
                    .addComponent(jtfTitle, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jftIsbn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jftEdition, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jftVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jftIssueYear, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jftNumPages, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jftPurchaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jftStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
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
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftIsbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addGap(26, 26, 26))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftPurchaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jftStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jTabbedPane1)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addContainerGap(23, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbNext, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbLast, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jbFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFirstActionPerformed
        moveToFirstBook();
    }//GEN-LAST:event_jbFirstActionPerformed

    private void jbPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPreviousActionPerformed
        moveToPreviousBook();
    }//GEN-LAST:event_jbPreviousActionPerformed

    private void jbNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNextActionPerformed
        moveToNextBook();
    }//GEN-LAST:event_jbNextActionPerformed

    private void jbLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbLastActionPerformed
        moveToLastBook();
    }//GEN-LAST:event_jbLastActionPerformed

    private void jbFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFindActionPerformed
        find();
    }//GEN-LAST:event_jbFindActionPerformed

    private void jbPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPrintActionPerformed
        printReport();
    }//GEN-LAST:event_jbPrintActionPerformed

    private void jtfSubtitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfSubtitleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfSubtitleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbFind;
    private javax.swing.JButton jbFirst;
    private javax.swing.JButton jbLast;
    private javax.swing.JButton jbNext;
    private javax.swing.JButton jbPrevious;
    private javax.swing.JButton jbPrint;
    private javax.swing.JFormattedTextField jftEdition;
    private javax.swing.JFormattedTextField jftIsbn;
    private javax.swing.JFormattedTextField jftIssueYear;
    private javax.swing.JFormattedTextField jftNumPages;
    private javax.swing.JFormattedTextField jftPurchaseDate;
    private javax.swing.JFormattedTextField jftStatus;
    private javax.swing.JFormattedTextField jftVolume;
    private javax.swing.JLabel jlCover;
    private javax.swing.JPanel jpCover;
    private javax.swing.JTextArea jtaSummary;
    private javax.swing.JTextField jtfAuthor;
    private javax.swing.JTextField jtfPublisher;
    private javax.swing.JTextField jtfSubtitle;
    private javax.swing.JTextField jtfTitle;
    // End of variables declaration//GEN-END:variables
}
