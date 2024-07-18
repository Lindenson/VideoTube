docker run -p 5432:5432  -e POSTGRES_PASSWORD=leedan -v /home/wolper/postgres:/var/run/postgresql -d --name postgres postgres
docker exec -it postgres psql -U postgres -c "CREATE DATABASE videos;"
docker exec -i postgres psql -U postgres video <<EOSQL
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);
CREATE TABLE videos (
    id integer NOT NULL,
    name character varying NOT NULL,
    filename character varying NOT NULL,
    tag character varying NOT NULL,
    tsv tsvector,
    "timestamp" timestamp with time zone DEFAULT now()
);
INSERT INTO users (username, password) VALUES ('admin', 'misha');
CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE ON videos FOR EACH ROW EXECUTE FUNCTION tsvector_update_trigger('tsv', 'pg_catalog.english', 'name');
EOSQL

echo "Setup completed successfully"


WITH nv AS (
  SELECT id,
         ROW_NUMBER() OVER (ORDER BY id) - 1 AS rn
  FROM videos
)
UPDATE videos v
SET "timestamp" = (SELECT CURRENT_DATE - INTERVAL '1 day' * nv.rn
                   FROM numbered_videos nv
                   WHERE v.id = nv.id);