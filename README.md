# Tennis Scoreboard
REST API + веб-интерфейс для ведения счёта теннисных матчей в реальном времени:
очки → геймы → сеты → матч. 

Завершённые матчи сохраняются в PostgreSQL,
активные хранятся в памяти.


## Tech stack
- Java 21
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation)
- PostgreSQL 16
- Lombok
- JUnit 5
- Docker / docker-compose
- HTML/CSS/JS + jQuery на фронте (готовый из курса)


## Быстрый запуск (bash)
Через Docker
```bash
git clone https://github.com/KirillBadin/tennis-scoreboard.git
cd tennisscoreboard
cp .env.example .env
docker compose up -d --build
```
Приложение: http://localhost:8080/index.html
