package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa os dados de um empréstimo de livros para terceiros. Toda
 * a operação baseada em empréstimo depende desta abstração.
 * <br>
 * <br>
 * As operações baseadas em empréstimo são:
 * <ol>
 * <li>Registrar o empréstimo de livros para terceiros;</li>
 * <li>Registrar a devolução de livros tomados em empréstimo;</li>
 * <li>Cancelar a devolução de livros tomados em empréstimo;</li>
 * <li>Remover livros inseridos num empréstimo por engano.</li>
 * </ol>
 * @author Leandro Aparecido de Almeida
 */
public final class LoanData {
    
    /**Livros tomados em empréstimo por terceiro.*/
    private final List<Book> books;
    /**Identificador (id) do empréstimo.*/
    private final int id;
    /**Nome do solicitante do empréstimo.*/
    private final String applicant;
    /**Data da retirada/devolução do empréstimo.*/
    private final Date date;

    /**
     * Constructor da classe.
     * @param id identificador (id) do empréstimo.
     * @param applicant nome do solicitante do empréstimo.
     * @param date data da retirada/devolução do empréstimo.
     * @param books livros tomados em empréstimo pelo solicitante. 
     */
    public LoanData(int id, String applicant, Date date, Book ... books) {
        this.books = new ArrayList<>();
        this.id = id;
        this.applicant = applicant;
        this.date = date;
        if (books.length > 0) {
            this.books.addAll(Arrays.asList(books));
        }
    }

    /**Obter o identificador (id) do empréstimo.*/
    public int getId() {
        return id;
    }
    
    /**Obter o nome do solicitante do empréstimo.*/
    public String getApplicant() {
        return applicant;
    }

    /**Obter a data da retirada/devolução do empréstimo.*/
    public Date getDate() {
        return date;
    }

    /**
     * Adicionar um livro ao empréstimo.
     * @param book livro retirado em empréstimo.
     */
    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
    }
    
    /**
     * Remover um livro do empréstimo.
     * @param book livro a ser removido.
     * @return true, houve a remoção, false, não foi possível remover.
     */
    public boolean deleteBook(Book book) {
        return books.remove(book);
    }

    /**Obter uma lista com os livros tomados em empréstimo.*/
    public List<Book> getBooks() {
        return books;
    }

    /**
     * Sobrescrito para retornar igual se ambos os empréstimos tem o mesmo
     * identificador (id).
     * @param obj outro objeto a ser comparado.
     * @return true, são iguais, false, são diferentes.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoanData) {
            return ((LoanData)obj).getId() == this.getId();
        } else {
            return false;
        }
    }

}