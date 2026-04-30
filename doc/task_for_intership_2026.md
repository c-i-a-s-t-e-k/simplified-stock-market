Implement a service that simulates a **simplified** stock market. In this scenario stocks market is composed of:

- Wallets \- entities that can own various number of various stocks  
- Bank \- a single entity that controls how many stocks are available, sells and buys them   
- Audit log \- a log of all actions that happened on the user wallets, so bank operations are excluded

Assumptions:

This is a simplified stock exchange without order books. The following simplifications apply:

- Stock price is always 1 (fixed, no price fluctuation)  
- Wallet balance is not tracked — no account state or funds management  
- Buy/sell operations are always executed immediately at face value  
- The Bank acts as the sole liquidity provider  
- Initially there should be no wallets and bank account should be empty

The candidate can model this internally according to his beliefs and preferences. However following REST endpoints are required:

1. POST /wallets/{wallet\_id}/stocks/{stock\_name}   
   - body: {type: “sell|buy”}  
   - simulates sell or buy of a single stock, we don’t support bulk operations,   
   - if the stock doesn’t exist this should return 404  
   - If there is no stock in the bank buy should fail with 400  
   - if there is no stock in the wallet sell should fail with 400  
   - If the operation succeeds it should return 200  
   - if the wallet doesn’t exist this operation should create it  
   - each operation should affect number of stocks available in the Bank  
2. GET /wallets/{wallet\_id}  
   - response: {id: “12qdsdadsa”, stocks: \[{“name”:”stock1”, “quantity”:99}, {“name”:”stock2”, “quantity”:1}...\]}  
   - returns current state of the particular wallet  
3. GET /wallets/{wallet\_id}/stocks/{stock\_name}  
- returns a single number, like: 99  
- returns quantity of the specified stock in the specified wallet  
4. GET /stocks  
- response: {stocks: \[{“name”:”stock1”, “quantity”:99}, {“name”:”stock2”, “quantity”:1}...\]}  
  - returns current state of the bank  
5. POST /stocks  
- body: {stocks: \[{“name”:”stock1”, “quantity”:99}, {“name”:”stock2”, “quantity”:1}...\]}  
  - sets the state of the bank    
  - If the operation succeeds it should return 200  
6. GET /log  
- response: {log: \[{“type”:”buy”, “wallet\_id”:”23qdsadsa”, “stock\_name”:”cbdadsa”}, {“type”:”sell”, “wallet\_id”:”12qdsdadsa”, “stock\_name”:”cbdadsa”\]...}  
- returns entire audit log in order of occurrence  
- should log only successful operations  
- there will be no more 10000 operations  
7. POST /chaos  
- Kills an instance that serves this request.

Additional non functional requirements:

- Solution should work on all major operating systems: windows/linux/macos and both arm64 and x64 architecture  
- Solution can be started using one command (per architecture), and that command is provided, it can be script invocation.  
- Application must be available at localhost:XXXX, where XXXX is a parameter to startup command and all endpoints must be implemented exactly as stated above  
- Solution is highly available, so killing 1 instance doesn’t kill the product  
- Solution doesn’t make any assumption about the environment apart from:  
  - java/go/typescript/kotlin runtimes are available in most up to date versions  
  - Docker is available  
- Provide [README.md](http://README.md)  
- Solution should be provided via a link to publicly available repositories (github, gitlab etc.)  
- Best engineering practices should be used during the development, and they will be subject to assessment

