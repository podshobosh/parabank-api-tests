-- 001_extend_schema.sql
-- Bring bankdb's customers/transactions up to ParaBank's field coverage.
--
-- Safe to re-run: every statement uses IF NOT EXISTS.
-- Additive only -- no column is dropped, no row is deleted.
-- Existing rows get NULL in the new columns until the backfill below runs.

BEGIN;

-- ---------------------------------------------------------------
-- 1. customers -- the fields ParaBank has that bankdb didn't
-- ---------------------------------------------------------------
ALTER TABLE customers ADD COLUMN IF NOT EXISTS address       varchar(100);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS state         char(2);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS zip_code      varchar(10);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS phone_number  varchar(20);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS ssn           varchar(11);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS username      varchar(50);

-- NOTE: ParaBank stores a plaintext `password` column -- it is a deliberately
-- insecure demo app. Modelling it as a hash instead, because a plaintext
-- password column is itself a defect worth being able to name in an interview.
-- If you want exact ParaBank parity, rename this to `password`.
ALTER TABLE customers ADD COLUMN IF NOT EXISTS password_hash varchar(72);

-- ---------------------------------------------------------------
-- 2. transactions
-- ---------------------------------------------------------------
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS description varchar(200);

-- ---------------------------------------------------------------
-- 3. Backfill -- deterministic synthetic data, derived from the id.
--    Row-count agnostic: works whether you have 5 customers or 5000.
--    Only fills NULLs, so re-running never overwrites your edits.
--
--    All values are obviously fake. Never put real SSNs or real
--    personal data in a practice database.
-- ---------------------------------------------------------------
UPDATE customers
SET
  address = COALESCE(address,
              (100 + customer_id * 7)::text || ' ' ||
              (ARRAY['Main St','Oak Ave','Elm Rd','Park Blvd','Cedar Ln'])[1 + (customer_id % 5)]),

  state = COALESCE(state,
              (ARRAY['VA','MD','DC','NY','CA','TX','FL'])[1 + (customer_id % 7)]),

  zip_code = COALESCE(zip_code,
              lpad(((20000 + customer_id * 13) % 99999)::text, 5, '0')),

  phone_number = COALESCE(phone_number,
              '(703) 555-' || lpad((customer_id % 10000)::text, 4, '0')),

  -- 555-xx-xxxx is a never-issued SSN prefix, so this can't collide with a real one
  ssn = COALESCE(ssn,
              '555-' || lpad((customer_id % 100)::text, 2, '0')
                     || '-' || lpad((customer_id % 10000)::text, 4, '0')),

  username = COALESCE(username,
              lower(left(first_name, 1) || last_name) || customer_id::text),

  -- Obvious placeholder, not a real hash of anything
  password_hash = COALESCE(password_hash, '$2a$10$notarealhash' || lpad(customer_id::text, 8, '0'));

-- Transaction descriptions keyed off txn_type, falling back for unknown types.
UPDATE transactions
SET description = COALESCE(description,
      CASE lower(txn_type)
        WHEN 'deposit'    THEN 'Deposit to account #'    || account_id
        WHEN 'withdrawal' THEN 'Withdrawal from account #' || account_id
        WHEN 'transfer'   THEN 'Transfer involving account #' || account_id
        WHEN 'payment'    THEN 'Bill payment from account #'  || account_id
        ELSE initcap(COALESCE(txn_type, 'Adjustment')) || ' on account #' || account_id
      END);

COMMIT;

-- ---------------------------------------------------------------
-- Deliberately NOT added: foreign keys on accounts.customer_id and
-- transactions.account_id.
--
-- Leaving referential integrity unenforced keeps orphan rows possible,
-- which is what makes an anti-join check a real test instead of a
-- textbook exercise. Add them later, on purpose, when you want to see
-- a constraint violation fire:
--
--   ALTER TABLE accounts     ADD CONSTRAINT fk_accounts_customer
--     FOREIGN KEY (customer_id) REFERENCES customers(customer_id);
--   ALTER TABLE transactions ADD CONSTRAINT fk_txn_account
--     FOREIGN KEY (account_id)  REFERENCES accounts(account_id);
-- ---------------------------------------------------------------
