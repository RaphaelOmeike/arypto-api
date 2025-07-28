CREATE TABLE users (
   id         VARCHAR(36) NOT NULL PRIMARY KEY,
   first_name VARCHAR(50) NOT NULL,
   last_name  VARCHAR(50) NOT NULL,
   email      VARCHAR(100) NOT NULL UNIQUE,
   password   VARCHAR(255) NOT NULL,
   role       VARCHAR(20) NOT NULL
);

CREATE TABLE wallets (
 id              VARCHAR(36) NOT NULL PRIMARY KEY,
 deposit_address VARCHAR(50) NOT NULL,
 crypto_currency VARCHAR(25) NOT NULL,
 network         VARCHAR(20) NOT NULL,
 user_id         VARCHAR(36) NOT NULL,
 balance         DECIMAL(10, 2) NOT NULL,
 CONSTRAINT wallets_users_id_fk
     FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone_number       VARCHAR(15) NULL,
  service_id         VARCHAR(30) NULL,
  amount_naira       DECIMAL(10, 2) NULL,
  amount_crypto      DECIMAL(20, 8) NOT NULL,
  crypto_currency    VARCHAR(25) NOT NULL,
  wallet_id          VARCHAR(36) NOT NULL,
  transaction_type   VARCHAR(20) NOT NULL,
  transaction_status VARCHAR(20) NOT NULL,
  delivery_status    VARCHAR(20) NULL,
  request_id         VARCHAR(50) NULL,
  transaction_hash   VARCHAR(100) NULL,
  transaction_id     VARCHAR(50) NULL,
  created_at         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT transactions_wallets_id_fk
      FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);