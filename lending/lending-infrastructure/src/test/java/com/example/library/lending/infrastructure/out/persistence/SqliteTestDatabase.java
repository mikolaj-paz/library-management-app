package com.example.library.lending.infrastructure.out.persistence;

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
            CREATE TABLE readers (
                id TEXT PRIMARY KEY,
                status TEXT NOT NULL DEFAULT 'ACTIVE'
            )
            """);
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
            CREATE TABLE book_waiting_queue (
                id TEXT PRIMARY KEY,
                book_id TEXT NOT NULL,
                reader_id TEXT NOT NULL,
                queue_position INTEGER NOT NULL
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
    jdbc.execute(
        """
            CREATE TABLE loans (
                id TEXT PRIMARY KEY,
                reader_id TEXT NOT NULL,
                book_copy_id TEXT NOT NULL,
                due_date TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """);
    jdbc.execute(
        """
            CREATE TABLE reservations (
                id TEXT PRIMARY KEY,
                reader_id TEXT NOT NULL,
                book_copy_id TEXT NOT NULL,
                expires_at TEXT NOT NULL
            )
            """);
  }
}
