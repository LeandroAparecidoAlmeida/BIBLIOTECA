package library;

import java.sql.Blob;
import java.util.Date;

/**
 * Classe que representa um livro.
 * @author Leandro Aparecido de Almeida.
 */
public final class Book {
    
    /**Identificador (id) do livro.*/
    private final int id;
    /**Título.*/
    private String title;
    /**Subtítulo.*/
    private String subtitle;
    /**Data da aquisição.*/
    private Date purchaseDate;
    /**Nome(s) do(s) Autor(es).*/
    private String author;
    /**Nome da editora.*/
    private String publisher;
    /**Número do ISBN.*/
    private String isbn;
    /**Número da edição.*/
    private int edition;
    /**Número do volume.*/
    private int volume;
    /**Ano da publicação.*/
    private int issueYear;
    /**Número de páginas.*/
    private int numPages;
    /**Resumo do livro.*/
    private String summary;
    /**Imagem da capa do livro*/
    private Blob cover;
    /**Atualizar a imagem de capa (vinculado ao método {@link #setCover(java.sql.Blob)}).*/
    boolean updateCover;

    /**
     * Constructor da classe.
     * @param id identificador (id) do livro.
     * @param title título.
     * @param subtitle subtítulo.
     * @param purchaseDate data da aquisição.
     * @param author nome(s) do(s) autor(es).
     * @param publisher nome da editora.
     * @param isbn número do ISBN.
     * @param edition número da edição.
     * @param volume número do volume.
     * @param issueYear ano da publicação.
     * @param numPages número de páginas.
     * @param summary resumo do livro.
     * @param cover imagem da capa do livro.
     */
    public Book(int id, String title, String subtitle, Date purchaseDate,
    String author, String publisher, String isbn, int edition, int volume, 
    int issueYear, int numPages, String summary, Blob cover) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.purchaseDate = purchaseDate;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.edition = edition;
        this.volume = volume;
        this.issueYear = issueYear;
        this.numPages = numPages;
        this.summary = summary;
        this.cover = cover;
        this.updateCover = false;
    }

    /**
     * Definir o título do livro.
     * @param title título do livro.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Definir o subtítulo do livro.
     * @param subtitle subtítulo do livro.
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Definir a data da aquisição do livro.
     * @param purchaseDate data da aquisição do livro.
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Definir o(s) nome(s) do(s) autor(es) do livro.
     * @param author nome(s) do(s) autor(es) do livro.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Definir a editora do livro.
     * @param publisher editora do livro.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Definir o número do ISBN do livro.
     * @param isbn número do ISBN do livro.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Definir o número da edição do livro.
     * @param edition número da edição do livro.
     */
    public void setEdition(int edition) {
        this.edition = edition;
    }

    /**
     * Definir o número do volume do livro.
     * @param volume número do volume do livro.
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * Definir o ano da publicação do livro.
     * @param issueYear ano da publicação do livro.
     */
    public void setIssueYear(int issueYear) {
        this.issueYear = issueYear;
    }

    /**
     * Definir o número de páginas do livro.
     * @param numPages número de páginas do livro.
     */
    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    /**
     * Definir o resumo do livro.
     * @param summary resumo do livro.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Definir a imagem da capa do livro.
     * @param cover capa do livro.
     */
    public void setCover(Blob cover) {
        this.cover = cover;
        //Sinaliza que a imagem de capa foi alterada e deverá ser regravada.
        this.updateCover = true;
    }

    /**Obter o identificador (id) do livro.*/
    public int getId() {
        return id;
    }

    /**Obter o título do livro.*/
    public String getTitle() {
        return title;
    }

    /**Obter o subtítulo do livro.*/
    public String getSubtitle() {
        return subtitle;
    }
    
    /**Obter a data da aquisição do livro.*/
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /**Obter o(s) nome(s) do(s) autor(es) do livro.*/
    public String getAuthor() {
        return author;
    }

    /**Obter a editora do livro.*/
    public String getPublisher() {
        return publisher;
    }

    /**Obter o número do ISBN do livro.*/
    public String getIsbn() {
        return isbn;
    }

    /**Obter o número da edição do livro.*/
    public int getEdition() {
        return edition;
    }

    /**Obter o número do volume do livro.*/
    public int getVolume() {
        return volume;
    }

    /**Obter o ano da publicação do livro.*/
    public int getIssueYear() {
        return issueYear;
    }

    /**Obter o número de páginas do livro.*/
    public int getNumPages() {
        return numPages;
    }

    /**Obter o resumo do livro.*/
    public String getSummary() {
        return summary;
    }  

    /**Obter a imagem da capa do livro.*/
    public Blob getCover() {
        return cover;
    }
    
    /**
     * Sobrescrito para ser igual somente quando ambos os livros tem o mesmo
     * identificador (id) no banco de dados.
     * @param obj outro livro a ser comparado.
     * @return true, são iguais, false, são diferentes.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Book) {
            return ((Book)obj).id == this.id;
        } else {
            return false;
        }    
    }

}