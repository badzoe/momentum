INSERT
INTO
  investor
  (id, name, surname, date_of_birth, address, phone_number, email, password, role)
VALUES
  (2, 'John', 'Doe', '2000-01-01', ' test address', '+267172123456', 'test@test.com', '$2a$10$FudtZNKSbrQgcXgTmrTRLO3DGw7NeAHMzWkPMwlTk4Hd2DfYcCllK', 'Investor');

INSERT
INTO
  product
  (type, name, id)
VALUES
  (0, 'RETIREMENT', 2345);
INSERT
INTO
  product
  (type, name, id)
VALUES
  (1, 'SAVINGS', 2347);
  INSERT
  INTO
  investor_products
  (investor_id, product_id, balance, id)
  VALUES (2, 2345, 50000.00, 3);
  INSERT
  INTO
  investor_products
  (investor_id, product_id, balance, id)
  VALUES (2, 2347, 36000.00, 4);
