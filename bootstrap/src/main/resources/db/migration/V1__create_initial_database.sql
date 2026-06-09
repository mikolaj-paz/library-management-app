CREATE TABLE IF NOT EXISTS readers (
    id         TEXT PRIMARY KEY,
    status     TEXT NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS book_copies (
    id          TEXT PRIMARY KEY,
    status      TEXT NOT NULL DEFAULT 'AVAILABLE',
    reserved_by TEXT NULL REFERENCES readers(id)
);

CREATE TABLE IF NOT EXISTS loans (
    id           TEXT PRIMARY KEY,
    reader_id    TEXT NOT NULL REFERENCES readers(id),
    book_copy_id TEXT NOT NULL REFERENCES book_copies(id),
    due_date     TEXT NOT NULL
);