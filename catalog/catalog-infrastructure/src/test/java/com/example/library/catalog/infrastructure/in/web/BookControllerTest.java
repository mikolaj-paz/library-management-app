package com.example.library.catalog.infrastructure.in.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.catalog.application.port.in.IAddBook;
import com.example.library.catalog.domain.book.BookFactoryImpl;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

  @Mock private IAddBook addBookService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new BookController(addBookService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
  }

  @Test
  void should_return_book_id_when_book_is_added() throws Exception {
    var book =
        new BookFactoryImpl()
            .create(
                "Domain-Driven Design",
                "Eric Evans",
                new ISBN("978-0321125217"),
                "Addison-Wesley",
                LocalDate.of(2003, 8, 30));
    when(addBookService.addBook(any())).thenReturn(book);

    mockMvc
        .perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(requestJson()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId").value(book.id().value().toString()));
  }

  @Test
  void should_return_bad_request_when_book_cannot_be_added() throws Exception {
    when(addBookService.addBook(any())).thenThrow(new IllegalArgumentException("ISBN exists"));

    mockMvc
        .perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(requestJson()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  private String requestJson() {
    return """
        {
          "title": "Domain-Driven Design",
          "author": "Eric Evans",
          "isbn": "978-0321125217",
          "publisher": "Addison-Wesley",
          "publicationDate": "2003-08-30"
        }
        """;
  }
}
