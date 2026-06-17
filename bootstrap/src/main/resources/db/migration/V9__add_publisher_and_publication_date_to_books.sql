ALTER TABLE books
    ADD COLUMN publisher TEXT NOT NULL DEFAULT 'Unknown';

ALTER TABLE books
    ADD COLUMN publication_date TEXT NOT NULL DEFAULT 'Unknown';
