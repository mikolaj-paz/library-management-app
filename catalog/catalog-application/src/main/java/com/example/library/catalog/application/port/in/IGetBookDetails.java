package com.example.library.catalog.application.port.in;

import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.application.query.GetBookDetails;

public interface IGetBookDetails {
  BookDetails bookDetails(GetBookDetails query);
}
