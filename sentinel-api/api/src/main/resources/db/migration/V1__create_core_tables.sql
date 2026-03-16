CREATE TABLE machine (
                         id BIGSERIAL PRIMARY KEY,
                         hostname VARCHAR(255),
                         os VARCHAR(255),
                         status VARCHAR(50),
                         last_seen TIMESTAMP
);

CREATE TABLE task (
                      id BIGSERIAL PRIMARY KEY,
                      command VARCHAR(255) NOT NULL,
                      status VARCHAR(50) NOT NULL,
                      output_log TEXT,
                      created_at TIMESTAMP,
                      machine_id BIGINT NOT NULL,
                      CONSTRAINT fk_machine FOREIGN KEY (machine_id) REFERENCES machine(id)
);