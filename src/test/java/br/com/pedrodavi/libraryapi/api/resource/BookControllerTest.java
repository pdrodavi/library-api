package br.com.pedrodavi.libraryapi.api.resource;

import br.com.pedrodavi.libraryapi.api.dto.BookDTO;
import br.com.pedrodavi.libraryapi.exception.BusinessException;
import br.com.pedrodavi.libraryapi.model.entity.Book;
import br.com.pedrodavi.libraryapi.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";

    MockHttpServletRequestBuilder request;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    public String objectToJson(BookDTO bookDTO) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(bookDTO);
    }

    private BookDTO createNewBook(){
        return BookDTO.builder().title("API TDD BDD").author("Pedro Davi").isbn("12345").build();
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    void createBook() throws Exception {

        BookDTO book = createNewBook();

        Book savedBook = Book.builder().id(10L).title("API TDD BDD").author("Pedro Davi").isbn("12345").build();

        given(service.save(any(Book.class))).willReturn(savedBook);

        request = post(BOOK_API)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectToJson(book));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(savedBook.getId()))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro quando receber dados insuficientes")
    void createInvalidBook() throws Exception {

        String json = objectToJson(new BookDTO());

        request = post(BOOK_API)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(6)));
    }

    @Test
    @DisplayName("Deve lançar erro quando já existir um ISBN")
    void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO bookDTO = createNewBook();
        String json = objectToJson(bookDTO);
        String msgError = "ISBN já cadastrado.";

        given(service.save(any(Book.class))).willThrow(new BusinessException(msgError));

        request = post(BOOK_API)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(msgError));

    }

    @Test
    @DisplayName("Deve retornar informações de um livro")
    void getBookDetails() throws Exception {

        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        given(service.getById(id)).willReturn(Optional.of(book));

        request = get(BOOK_API.concat("/"+id))
        .accept(APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar not found quando livro não encontrado")
    void bookNotFound() throws Exception {

        given(service.getById(anyLong())).willReturn(Optional.empty());

        request = get(BOOK_API.concat("/"+1)).accept(APPLICATION_JSON);

        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    void deleteBook() throws Exception {

        given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        request = delete(BOOK_API.concat("/"+1));

        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar livro para deletar")
    void deleteBookNotFound() throws Exception {

        given(service.getById(anyLong())).willReturn(Optional.empty());

        request = delete(BOOK_API.concat("/"+1));

        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    void updateBook() throws Exception {

        Long id = 1L;
        String json = objectToJson(createNewBook());

        Book updatingBook = Book.builder()
                .id(1L).title("Livro Update")
                .author("Davi").isbn("002").build();

        given(service.getById(id)).willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder().id(id).title("API TDD BDD").author("Pedro Davi").isbn("002").build();

        given(service.update(updatingBook)).willReturn(updatedBook);

        request = put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("002"));

    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar livro inexistente")
    void updateBookNotFound() throws Exception {

        String json = objectToJson(createNewBook());

        given(service.getById(anyLong())).willReturn(Optional.empty());

        request = put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON);

        mvc.perform(request).andExpect(status().isNotFound());

    }


}
