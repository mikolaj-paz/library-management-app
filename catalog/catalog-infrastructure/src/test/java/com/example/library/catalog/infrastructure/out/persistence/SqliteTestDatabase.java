package com.example.library.catalog.infrastructure.out.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

final class SqliteTestDatabase {

  private SqliteTestDatabase() {}

  static JdbcTemplate createJdbcTemplate() {
    var dataSource = new SingleConnectionDataSource("jdbc:sqlite::memory:", true);
    var jdbc = new JdbcTemplate(dataSource);
    createSchema(jdbc);
    return jdbc;
  }

  private static void createSchema(JdbcTemplate jdbc) {
    jdbc.execute(
        """
            CREATE TABLE books (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                isbn TEXT NOT NULL UNIQUE
            )
            """);
    jdbc.execute(
        """
            CREATE TABLE book_copies (
                id TEXT PRIMARY KEY,
                status TEXT NOT NULL DEFAULT 'AVAILABLE',
                reserved_by TEXT NULL,
                book_id TEXT REFERENCES books(id)
            )
            """);
  }
}
