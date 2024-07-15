docker run -p 5432:5432  -e POSTGRES_PASSWORD=leedan -v /home/wolper/postgres:/var/run/postgresql -d --name postgres postgres
docker exec -it postgres psql -U postgres -c "CREATE DATABASE videos;"
docker exec -i postgres psql -U postgres video <<EOSQL
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE videos (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    filename VARCHAR NOT NULL,
    tag VARCHAR NOT NULL
);

INSERT INTO users (username, password) VALUES ('admin', 'misha');
EOSQL

echo "Setup completed successfully"