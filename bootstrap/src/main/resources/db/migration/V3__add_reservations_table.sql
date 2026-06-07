CREATE TABLE IF NOT EXISTS reservations (
    id TEXT PRIMARY KEY,
    reader_id TEXT NOT NULL,
    book_copy_id TEXT NOT NULL
);