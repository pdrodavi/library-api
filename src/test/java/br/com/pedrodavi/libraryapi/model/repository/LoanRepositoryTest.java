package br.com.pedrodavi.libraryapi.model.repository;

import br.com.pedrodavi.libraryapi.model.entity.Book;
import br.com.pedrodavi.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static br.com.pedrodavi.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Deve checar se existe livro não devolvido")
    void existsByBookAndNotReturned(){
        Book book = createNewBook("001");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Pedro").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo por Isbn ou Customer")
    void findByBookIsbnOrCustomer(){
        Book book = createNewBook("001");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Pedro").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        Page<Loan> result = repository.findByBookIsbnOrCustomer("001", "Pedro", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).contains(loan);
    }



}