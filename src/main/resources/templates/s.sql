CREATE TABLE swot_factor (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(255),
                             type VARCHAR(50),
                             extreme_min INT,
                             extreme_max INT,
                             expected_min INT,
                             expected_max INT
);
