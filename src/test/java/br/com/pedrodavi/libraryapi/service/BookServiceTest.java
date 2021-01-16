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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("Deve obter um livro pelo ID")
    void getById() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));
        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao fazer getById em livro inexistente no banco")
    void bookNotFoundById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Book> book = service.getById(id);
        assertThat(book).isNotPresent();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    void deleteBook() {
        Book book = Book.builder().id(1L).build();
        assertDoesNotThrow(() -> service.delete(book));
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve lançar erro ao deletar livro inexistente")
    void deleteInvalidBook() {
        Book book = Book.builder().build();
        assertThrows(IllegalArgumentException.class, () -> service.delete(book));
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    void updateBook() {
        Long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book book = service.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar livro inexistente")
    void updateInvalidBook() {
        Book book = Book.builder().build();
        assertThrows(IllegalArgumentException.class, () -> service.update(book));
        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelos params do get")
    void findBook(){
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> list = Arrays.asList(book);

        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);

        when(repository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

}