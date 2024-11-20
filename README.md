# banking_solution

  To run the application, you need to download it from the git repository and go to the root folder
  Then:

    docker-compose up

  The application will run on port 8181

  Swagger is connected via the link http://localhost:8181/swagger-ui/index.html#

  banking_solution is required for registration, storage and
  administration of data about the accounts.
  Built on Rest architecture.

  Main tools: PostgreSQL, Hibernate search, Spring Security.

  PostgresSQL is used to store basic data about the user and his account.
  
  The protection of the entire system is built on jwt tokens. 
  There are two types of tokens: refresh token (valid for 2 days) and
  access token (valid for 1 hour). Upon successful authorization (end point */auth*),
  two tokens are issued at once. Access token is used to access all protected endpoints.
  When the access token expires, the end point */refresh* is issued, which issues
  a new access token and a refresh token, and the subsequent refresh token becomes invalid
  
### POST /accounts/create

  Creates a new account

  Request body:

    {
        "email": "emiail@gmail.com",
        "password": "password",
        "confirmPassword": "password"
    }

### GET  /accounts/{accountNumber}

  Get account by account number

  200 OK

  Response body
  
    {
      "id": "8ffb74aa-0e4f-4c4a-87d5-e1375d4ff450",
      "email": "emiail@gmail.com",
      "accountNumber": "616308725",
      "balance": "0.0",
      "role": "USER"
    }

### POST /accounts/all

  Get all accounts in database

### POST /accounts/deposit

  Deposit funds to an account by account number

  Request param:

  string accountNumber,
  numer depositAmount

  

### POST /accounts/withdraw

  Withdraw funds to an account by account number

  Request param:

  string accountNumber,
  number withdrawAmount

### POST /accounts/transfer

  Transfer funds between two accounts

  Request param:
  
  string senderNumber,
  string receiverNumber,
  number transferNumber