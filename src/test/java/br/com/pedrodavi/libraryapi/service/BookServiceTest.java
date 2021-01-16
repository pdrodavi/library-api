package br.com.pedrodavi.libraryapi.service;

import br.com.pedrodavi.libraryapi.exception.BusinessException;
import br.com.pedrodavi.libraryapi.model.entity.Book;
import br.com.pedrodavi.libraryapi.model.repository.BookRepository;
import br.com.pedrodavi.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    private Book createValidBook() {
        return Book.builder().author("Pedro").title("Livro API").isbn("001").build();
    }

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void saveBook(){

        Book book = createValidBook();

        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(Book.builder().id(1L).author("Pedro").title("Livro API").isbn("001").build());

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("Pedro");
        assertThat(savedBook.getTitle()).isEqualTo("Livro API");
        assertThat(savedBook.getIsbn()).isEqualTo("001");
    }

    @Test
    @DisplayName("Deve lançar erro de negócio quando ISBN já existir")
    void shouldNotSaveABookWithDuplicatedISBN(){

        Book book = createValidBook();
        String msgError = "ISBN já cadastrado.";

        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage(msgError);

        // verifica o repository e diz que nunca vai executar o método save com esse objeto
        // para não salvar um objeto qapós exceção ser lançada
        verify(repository, never()).save(book);
    }

}