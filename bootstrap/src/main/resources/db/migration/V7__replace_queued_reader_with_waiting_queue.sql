CREATE TABLE IF NOT EXISTS book_waiting_queue (
    id             TEXT PRIMARY KEY,
    book_id        TEXT NOT NULL REFERENCES books(id),
    reader_id      TEXT NOT NULL REFERENCES readers(id),
    queue_position INTEGER NOT NULL,
    UNIQUE (book_id, reader_id),
    UNIQUE (book_id, queue_position)
);

DROP INDEX IF EXISTS idx_book_waiting_queue_book_position;
CREATE INDEX idx_book_waiting_queue_book_position
    ON book_waiting_queue(book_id, queue_position);

ALTER TABLE books DROP COLUMN queued_reader_id;
