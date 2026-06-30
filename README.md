# OpenAI-with-Java

A comprehensive learning project demonstrating how to integrate OpenAI's GPT models with Java microservices using Spring Boot and Spring AI. This project showcases a complete end-to-end workflow: from natural language queries to API execution with backend data services.

## 📋 Project Overview

**OpenAI-with-Java** is a multi-module project that demonstrates:
- Natural language processing using OpenAI's GPT models
- Microservice architecture with Spring Boot
- API orchestration and integration patterns
- Real-world use case: converting plain English questions into structured API calls

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                 End User / Client                           │
└────────────────────────┬────────────────────────────────────┘
│
"Show me my last 5 transactions"
│
▼
┌─────────────────────────────────────────────────────────────┐
│  user-prompt-to-api-svc (Port 8080)                         │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ NLController (/nl/ask)                                  ││
│  │  - Accepts natural language queries                     ││
│  └──────────────────────┬─────────────────────────────────┘│
│  ┌──────────────────────▼─────────────────────────────────┐│
│  │ NLService                                               ││
│  │  - Orchestrates query → API → Response flow            ││
│  └──────────────────────┬─────────────────────────────────┘│
│  ┌──────────────────────▼─────────────────────────────────┐│
│  │ LLMService                                              ││
│  │  - Calls OpenAI GPT-4o-mini                            ││
│  │  - Converts query to JSON API request                  ││
│  │  Output: {"endpoint": "/transactions/last5", ...}      ││
│  └──────────────────────┬─────────────────────────────────┘│
│  ┌──────────────────────▼─────────────────────────────────┐│
│  │ ApiService                                              ││
│  │  - Executes the generated API call                     ││
│  │  - Fetches data from downstream service               ││
│  └──────────────────────┬─────────────────────────────────┘│
└──────────────────────────────┬──────────────────────────────┘
│
HTTP GET /transactions/last5?userId=123
│
▼
┌─────────────────────────────────────────────────────────────┐
│ transaction-svc-master (Port 8081)                          │
│ ┌─────────────────────────────────────────────────────────┐│
│ │ TransactionController (/transactions)                  ││
│ │  - /last5 - Get 5 most recent transactions             ││
│ │  - /highest - Get transaction with highest amount      ││
│ └──────────────────────┬─────────────────────────────────┘│
│ ┌──────────────────────▼─────────────────────────────────┐│
│ │ TransactionService                                      ││
│ │  - Business logic layer                                ││
│ └──────────────────────┬─────────────────────────────────┘│
│ ┌──────────────────────▼─────────────────────────────────┐│
│ │ TransactionRepository (JPA)                            ││
│ │  - Data access layer                                   ││
│ └──────────────────────┬─────────────────────────────────┘│
│ ┌──────────────────────▼─────────────────────────────────┐│
│ │ H2 In-Memory Database (jdbc:h2:mem:txdb)               ││
│ │  - Transaction data storage                            ││
│ └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

## 📦 Modules

### 1. **transaction-svc-master** (Data Service)
A Spring Boot microservice providing REST APIs for transaction data queries.

**Key Features:**
- RESTful API endpoints for transaction queries
- H2 in-memory database with JPA ORM
- Built-in H2 web console for database inspection
- Auto-schema creation from JPA entities

**Endpoints:**
- `GET /transactions/last5?userId={userId}` - Retrieve 5 most recent transactions
- `GET /transactions/highest?userId={userId}` - Retrieve highest amount transaction

**Technologies:** Java 17, Spring Boot 4.0.5, Spring Data JPA, H2, Lombok

**Port:** 8081

---

### 2. **user-prompt-to-api-svc-main** (LLM Gateway Service)
A Spring Boot microservice that bridges natural language with API calls using OpenAI's GPT models.

**Key Features:**
- Converts natural language queries into structured API requests
- Integrates with OpenAI's GPT-4o-mini model
- Orchestrates multi-step workflows: NL Query → LLM Translation → API Execution
- Validates and constrains API calls to authorized endpoints

**Endpoints:**
- `POST /nl/ask` - Submit a natural language query

**Input Example:**
```json
"Show me my highest transaction"
```

**Output Flow:**
1. LLMService → OpenAI GPT-4o-mini converts to: `{"endpoint": "/transactions/highest", "method": "GET", "params": {"userId": "123"}}`
2. ApiService → Executes the API call on downstream service
3. Returns the result to client

**Technologies:** Java 17, Spring Boot 4.0.5, Spring AI 2.0.0-M3, OpenAI GPT-4o-mini, Jackson, Lombok

**Port:** 8080

---

## 🚀 Quick Start

### Prerequisites
- **Java 17** or higher
- **Maven 3.6** or higher
- **OpenAI API Key** (for user-prompt-to-api-svc-main)

### Step 1: Start Transaction Service (Backend)

```bash
cd transaction-svc-master
mvn clean install
mvn spring-boot:run
```

The service starts on **http://localhost:8081**

**Verify it's running:**
```bash
curl "http://localhost:8081/transactions/last5?userId=user123"
```

---

### Step 2: Configure OpenAI API Key

Edit `user-prompt-to-api-svc-main/src/main/resources/application.properties`:

```properties
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
```

---

### Step 3: Start LLM Gateway Service

```bash
cd user-prompt-to-api-svc-main
mvn clean install
mvn spring-boot:run
```

The service starts on **http://localhost:8080**

---

## 💬 Usage Examples

### Using cURL

**Request:**
```bash
curl -X POST http://localhost:8080/nl/ask \
  -H "Content-Type: text/plain" \
  -d "What are my last 5 transactions?"
```

**Flow:**
1. NLController receives the query
2. LLMService calls OpenAI with a system prompt
3. OpenAI responds with: `{"endpoint": "/transactions/last5", "method": "GET", "params": {"userId": "123"}}`
4. ApiService executes: `GET http://localhost:8081/transactions/last5?userId=123`
5. Response is returned to client

**Response:**
```json
[
  {
    "id": 1,
    "userId": "user123",
    "amount": 150.50,
    "type": "CREDIT",
    "timestamp": "2026-06-30T10:30:00"
  },
  {
    "id": 2,
    "userId": "user123",
    "amount": 75.25,
    "type": "DEBIT",
    "timestamp": "2026-06-29T14:15:00"
  }
]
```

### Natural Language Query Examples

Try these queries with the LLM Gateway:
- "Show me my last 5 transactions"
- "What is my highest transaction?"
- "Get my recent transactions"
- "Find my biggest transaction"
- "Give me my transaction history"

The LLM automatically maps these to the appropriate API endpoints.

---

## 🏗️ Project Structure

```
OpenAI-with-Java/
├── transaction-svc-master/
│   ├── src/main/java/com/example/demo/
│   │   ├── DemoApplication.java
│   │   ├── Transaction.java           # JPA Entity
│   │   ├── TransactionController.java # REST API
│   │   ├── TransactionService.java    # Business Logic
│   │   └── TransactionRepository.java # Data Access
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── README.md
│
├── user-prompt-to-api-svc-main/
│   ├── src/main/java/com/example/demo/
│   │   ├── DemoApplication.java
│   │   ├── NLController.java          # REST API (/nl/ask)
│   │   ├── NLService.java             # Orchestration
│   │   ├── LLMService.java            # OpenAI Integration
│   │   ├── ApiService.java            # API Execution
│   │   └── ApiRequest.java            # Data Model
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── README.md
│
└── README.md (this file)
```

---

## 🔌 Integration Flow (Detailed)

### Step-by-Step Flow: "Show me my highest transaction"

```
1. CLIENT
   POST /nl/ask
   Body: "Show me my highest transaction"
   ↓
2. NLController.handleQuery()
   Receives the raw query string
   ↓
3. NLService.processQuery()
   Delegates to LLMService
   ↓
4. LLMService.translateToApi()
   Constructs system prompt with allowed endpoints
   Calls OpenAI GPT-4o-mini API
   ↓
5. OpenAI Response:
   {
     "endpoint": "/transactions/highest",
     "method": "GET",
     "params": {"userId": "123"}
   }
   ↓
6. NLService receives ApiRequest object
   Delegates to ApiService
   ↓
7. ApiService.executeApi(apiRequest)
   Constructs URL: http://localhost:8081/transactions/highest?userId=123
   Makes HTTP GET request
   ↓
8. transaction-svc-master
   TransactionController receives GET /transactions/highest?userId=123
   TransactionService finds highest transaction
   Returns Transaction object
   ↓
9. ApiService returns response to NLService
   ↓
10. NLService returns to NLController
    ↓
11. NLController returns to CLIENT
    Response body: {"id": 5, "userId": "123", "amount": 500.00, ...}
```

---

## 🗄️ Data Model

### Transaction Entity

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Auto-generated primary key |
| `userId` | String | User identifier |
| `amount` | Double | Transaction amount |
| `type` | String | `CREDIT` or `DEBIT` |
| `timestamp` | LocalDateTime | Transaction date/time |

---

## 🔧 Configuration Reference

### transaction-svc-master (Port 8081)

```properties
# Server
server.port=8081

# H2 Database
spring.datasource.url=jdbc:h2:mem:txdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
```

**H2 Web Console:** http://localhost:8081/h2-console

---

### user-prompt-to-api-svc-main (Port 8080)

```properties
# Server
server.port=8080

# H2 Database (optional for this service)
spring.datasource.url=jdbc:h2:mem:testdb

# OpenAI Integration
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
spring.ai.openai.chat.options.model=gpt-4o-mini
```

**Important:** Set your OpenAI API key before running.

---

## 🧠 LLM Constraints

The LLMService enforces strict API constraints in the system prompt:

**Allowed Endpoints:**
- `GET /transactions/last5` (parameter: userId)
- `GET /transactions/highest` (parameter: userId)

**Safety Rules:**
- ❌ No endpoints other than above are allowed
- ❌ No `/users` endpoint access
- ✅ User ID defaults to "123"
- ✅ Response is always valid JSON

This prevents the LLM from generating unauthorized API calls.

---

## 🧪 Testing

### Test Transaction Service
```bash
curl "http://localhost:8081/transactions/last5?userId=user123"
curl "http://localhost:8081/transactions/highest?userId=user123"
```

### Test LLM Gateway
```bash
curl -X POST http://localhost:8080/nl/ask \
  -H "Content-Type: text/plain" \
  -d "What are my last 5 transactions?"
```

### Using Postman

**Collection Setup:**

1. **Transaction Service - Last 5**
    - Method: GET
    - URL: `http://localhost:8081/transactions/last5?userId=user123`

2. **Transaction Service - Highest**
    - Method: GET
    - URL: `http://localhost:8081/transactions/highest?userId=user123`

3. **LLM Gateway**
    - Method: POST
    - URL: `http://localhost:8080/nl/ask`
    - Headers: `Content-Type: text/plain`
    - Body (raw): `Show me my last 5 transactions`

---

## 📊 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 4.0.5 |
| AI Integration | Spring AI + OpenAI | 2.0.0-M3 / GPT-4o-mini |
| Database | H2 | In-memory |
| ORM | Spring Data JPA | Native |
| JSON Processing | Jackson | Native |
| Boilerplate Reduction | Lombok | Native |
| Build Tool | Maven | 3.6+ |

---

## 🔍 Debugging & Troubleshooting

### Port Already in Use

**Transaction Service (8081):**
```properties
# Edit transaction-svc-master/src/main/resources/application.properties
server.port=8082
```

**LLM Gateway (8080):**
```properties
# Edit user-prompt-to-api-svc-main/src/main/resources/application.properties
server.port=8081
```

### OpenAI API Key Issues

1. Verify key is set in `application.properties`
2. Check key has API access enabled at https://platform.openai.com
3. Monitor usage to avoid rate limits
4. Check OpenAI pricing - ensure account has credits

### H2 Database Not Showing Data

- H2 is in-memory; data is recreated on restart
- To persist: Change URL to `jdbc:h2:~/transaction-svc-data`
- Or use PostgreSQL/MySQL for production

### LLM Gateway Returns Errors

1. Ensure transaction-svc-master is running on port 8081
2. Check OpenAI API key is valid
3. Review LLM response in console logs
4. Verify network connectivity to `api.openai.com`

---

## 📚 Additional Resources

### Documentation Files
- [`transaction-svc-master/README.md`](./transaction-svc-master/README.md) - Transaction Service Details
- [`user-prompt-to-api-svc-main/README.md`](./user-prompt-to-api-svc-main/README.md) - LLM Gateway Details

### External Links
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://spring.io/projects/spring-ai)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [OpenAI API Reference](https://platform.openai.com/docs/api-reference)
- [H2 Database](https://www.h2database.com)

---

## 🎓 Learning Objectives

This project demonstrates:

✅ **Microservices Architecture** - Multiple independent services communicating via REST  
✅ **Spring Boot Fundamentals** - Building modern Java applications  
✅ **Spring Data JPA** - Object-relational mapping and queries  
✅ **OpenAI Integration** - Calling LLM APIs from Java  
✅ **API Orchestration** - Chaining multiple API calls  
✅ **Natural Language Processing** - Converting NL to structured requests  
✅ **RESTful Design** - Building and consuming REST APIs  
✅ **In-Memory Databases** - Using H2 for development

---

## 🚧 Future Enhancements

### Phase 2: Improvements
- [ ] Add authentication & authorization (Spring Security)
- [ ] Implement transaction creation/update endpoints (POST, PUT, DELETE)
- [ ] Switch to persistent database (PostgreSQL, MySQL)
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Implement request/response logging
- [ ] Add comprehensive unit & integration tests
- [ ] Containerize with Docker (docker-compose setup)
- [ ] Add caching (Redis)
- [ ] Implement transaction pagination
- [ ] Add advanced filtering capabilities

### Phase 3: Advanced Features
- [ ] Support more LLM providers (Anthropic, Google PaLM)
- [ ] Implement conversation history & context
- [ ] Add transaction analytics endpoints
- [ ] Support financial metrics calculations
- [ ] Multi-user support with proper authorization
- [ ] Real-time transaction notifications
- [ ] Transaction categorization using ML

---

## 📄 License

This project is part of a learning initiative. Feel free to use it for educational purposes.

---

## 🤝 Contributing

Suggestions and improvements are welcome! Consider:
- Adding more allowed API endpoints
- Improving LLM prompt engineering
- Adding error handling & validation
- Creating comprehensive tests
- Enhancing documentation

---

## 📞 Support

For issues or questions:
1. Check the individual module READMEs
2. Review Spring Boot and OpenAI documentation
3. Examine console logs for error messages
4. Verify service ports and configurations

---

## 🎉 Quick Reference Cheatsheet

```bash
# Start Transaction Service (in terminal 1)
cd transaction-svc-master
mvn spring-boot:run
# Runs on http://localhost:8081

# Start LLM Gateway (in terminal 2)
cd user-prompt-to-api-svc-main
mvn spring-boot:run
# Runs on http://localhost:8080

# Test Transaction Service
curl "http://localhost:8081/transactions/last5?userId=user123"
curl "http://localhost:8081/transactions/highest?userId=user123"

# Test LLM Gateway
curl -X POST http://localhost:8080/nl/ask \
  -H "Content-Type: text/plain" \
  -d "Show me my last 5 transactions"

# Access H2 Console
# Transaction DB: http://localhost:8081/h2-console
# LLM Gateway DB: http://localhost:8080/h2-console
```

---





