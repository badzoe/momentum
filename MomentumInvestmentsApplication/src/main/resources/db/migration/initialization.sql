INSERT
INTO
  investor
  (name, surname, date_of_birth, address, phone_number, email, password, role)
VALUES
  ('John', 'Doe', now(), ' test address', '+267172123456', 'test@test.com', '$2a$10$FudtZNKSbrQgcXgTmrTRLO3DGw7NeAHMzWkPMwlTk4Hd2DfYcCllK', 'Investor');

SET @investor_id = LAST_INSERT_ID();

INSERT
INTO
  product
  (type, name, balance, investor_id)
VALUES
  (0, 'TEST RETIREMENT', 50000, @investor_id);
INSERT
INTO
  product
  (type, name, balance, investor_id)
VALUES
  (1, 'TEST SAVINGS', 36000, @investor_id);