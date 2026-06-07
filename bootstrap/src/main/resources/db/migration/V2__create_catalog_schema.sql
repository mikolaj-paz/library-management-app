CREATE TABLE IF NOT EXISTS books (
    id      TEXT PRIMARY KEY,
    title   TEXT NOT NULL,
    author  TEXT NOT NULL,
    isbn    TEXT NOT NULL UNIQUE
);

ALTER TABLE book_copies ADD COLUMN book_id TEXT REFERENCES books(id);