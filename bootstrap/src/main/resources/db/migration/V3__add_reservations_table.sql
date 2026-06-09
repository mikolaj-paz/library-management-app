CREATE TABLE IF NOT EXISTS reservations (
    id TEXT PRIMARY KEY,
    reader_id TEXT NOT NULL REFERENCES readers(id),
    book_copy_id TEXT NOT NULL REFERENCES book_copies(id)
);