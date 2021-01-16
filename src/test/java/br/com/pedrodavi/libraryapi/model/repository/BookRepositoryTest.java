package br.com.pedrodavi.libraryapi.model.repository;

import br.com.pedrodavi.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    private Book createNewBook(String isbn) {
        return Book.builder().title("Livro API DB Test").author("Pedro").isbn(isbn).build();
    }

    @Test
    @DisplayName("Deve retornar true quando existir um livro com Isbn informado")
    void returnTrueWhenIsbnExists(){
        String isbn = "001";
        Book book = createNewBook(isbn);
        entityManager.persist(book);
        boolean exists = repository.existsByIsbn(isbn);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando existir um livro com Isbn informado")
    void returnFalseWhenIsbnDoesntExist(){
        String isbn = "001";
        boolean exists = repository.existsByIsbn(isbn);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro pelo ID")
    void findBookById(){
        Book book = createNewBook("001");
        entityManager.persist(book);
        Optional<Book> foundBook = repository.findById(book.getId());
        assertThat(foundBook).isPresent();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void saveBook(){
        Book book = createNewBook("003");
        Book savedBook = repository.save(book);
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    void deleteBook(){
        Book book = createNewBook("003");
        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class, book.getId());
        repository.delete(foundBook);
        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }

}