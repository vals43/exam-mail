CREATE TABLE image_submission (
    id UUID PRIMARY KEY,
    nom_fichier VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_image_submission_email ON image_submission(email);
