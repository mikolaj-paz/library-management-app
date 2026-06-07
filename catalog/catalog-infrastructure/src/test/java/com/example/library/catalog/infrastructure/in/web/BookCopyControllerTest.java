package com.example.library.catalog.infrastructure.in.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.catalog.application.port.in.IAddBookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import java.util.UUID;
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
class BookCopyControllerTest {

  @Mock private IAddBookCopy addBookCopyService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new BookCopyController(addBookCopyService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
  }

  @Test
  void should_return_book_copy_id_when_copy_is_added() throws Exception {
    var bookId = UUID.randomUUID().toString();
    var copyId = BookCopyId.create();
    when(addBookCopyService.add(any())).thenReturn(copyId);

    mockMvc
        .perform(
            post("/book-copies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(bookId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookCopyId").value(copyId.value().toString()));
  }

  @Test
  void should_return_bad_request_when_book_does_not_exist() throws Exception {
    var bookId = UUID.randomUUID().toString();
    when(addBookCopyService.add(any())).thenThrow(new IllegalArgumentException("Book not found"));

    mockMvc
        .perform(
            post("/book-copies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(bookId)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_bad_request_when_book_id_is_invalid() throws Exception {
    mockMvc
        .perform(
            post("/book-copies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson("not-a-uuid")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  private String requestJson(String bookId) {
    return """
        {
          "bookId": "%s"
        }
        """
        .formatted(bookId);
  }
}
