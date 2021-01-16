package br.com.pedrodavi.libraryapi.api.resource;

import br.com.pedrodavi.libraryapi.api.dto.BookDTO;
import br.com.pedrodavi.libraryapi.api.dto.LoanDTO;
import br.com.pedrodavi.libraryapi.exception.BusinessException;
import br.com.pedrodavi.libraryapi.model.entity.Book;
import br.com.pedrodavi.libraryapi.model.entity.Loan;
import br.com.pedrodavi.libraryapi.service.BookService;
import br.com.pedrodavi.libraryapi.service.LoanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc
class LoanControllerTest {

    static String LOAN_API = "/api/loans";

    MockHttpServletRequestBuilder request;

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    public String objectToJson(LoanDTO loanDTO) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(loanDTO);
    }

    private LoanDTO createNewLoan(){
        return LoanDTO.builder().isbn("001").customer("Pedro").build();
    }

    @Test
    @DisplayName("Deve realizar um empréstimo")
    void createLoan() throws Exception {

        String json = objectToJson(createNewLoan());

        Book book = Book.builder().id(1L).isbn("001").build();
        given(bookService.getBookByIsbn("001"))
                .willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).customer("Pedro")
                .book(book).loanDate(LocalDate.now()).build();
        given(loanService.save(any(Loan.class))).willReturn(loan);

        request = post(LOAN_API)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

    }

    @Test
    @DisplayName("Deve retornar erro ao fazer empréstimo de livro inexistente")
    void invalidIsbnCreateLoan() throws Exception {

        String json = objectToJson(createNewLoan());

        given(bookService.getBookByIsbn("001"))
                .willReturn(Optional.empty());

        request = post(LOAN_API)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed Isbn"));
    }

    @Test
    @DisplayName("Deve retornar erro ao fazer empréstimo de livro já emprestado")
    void loanedBookErrorOnCreateLoan() throws Exception {

        String json = objectToJson(createNewLoan());

        Book book = Book.builder().id(1L).isbn("001").build();
        given(bookService.getBookByIsbn("001"))
                .willReturn(Optional.of(book));

        given(loanService.save(any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        request = post(LOAN_API)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

}
