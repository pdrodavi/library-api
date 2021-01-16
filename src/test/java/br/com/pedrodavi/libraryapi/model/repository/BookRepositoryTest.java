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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar true quando existir um livro com Isbn informado")
    void returnTrueWhenIsbnExists(){

        String isbn = "001";
        Book book = Book.builder().title("Livro API DB Test").author("Pedro").isbn(isbn).build();
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

}