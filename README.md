# QA Automation Assignment — Restful Booker API Tests

[![CI](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml/badge.svg)](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml)

API-автотести для [Restful Booker](https://restful-booker.herokuapp.com) на Java 17 / Maven / JUnit 5 / REST Assured, з Allure-звітністю та паралельним запуском.

## Стек

Java 17 · Maven · JUnit 5 · REST Assured · AssertJ · Jackson · Lombok · Allure 2 (+ AspectJ weaving) · SLF4J/Logback

## Запуск

```bash
mvn clean test -Dauth.username=admin -Dauth.password=password123
mvn allure:report   # або allure:serve
```

Паралельно (4 потоки за замовчуванням, `src/test/resources/junit-platform.properties`).

## Що покрито

- `POST /auth` — валідні/невалідні креденшели
- `POST /booking` — створення, обов'язкові поля (`@EnumSource`), дублікати
- `GET /booking`, `GET /booking/{id}` — пошук за іменем/датами, 404
- `PUT /booking/{id}` — заміна, невалідні дати (`@MethodSource`), невалідний/відсутній токен (`@ValueSource`)
- `PATCH /booking/{id}` — часткове оновлення + перевірка через повторний GET
- `DELETE /booking/{id}` — з токеном і без

Кілька негативних тестів навмисно фіксують реальні дефекти сервіса (500 замість 400, приймання невалідних дат) — вони описані в Allure-звіті категорією **Product defects**.

## Підходи й патерни

| Підхід | Де |
|---|---|
| **Fluent response assertions** | `ResponseAssert<SELF>` (self-typed generic) → `.shouldHaveStatus().shouldHaveBooking()` |
| **Client / Service layer** | `ApiClient` → `AuthClient`, `BookingClient` |
| **DTO для request/response** | окремі record'и з `@JsonProperty` |
| **Object Mother / Builder** | `BookingData`, Lombok `@Builder`/`@With` |
| **Singleton (enum) + lazy init** | `AdminToken` — токен один раз на прогін, потокобезпечно |
| **Chain of Responsibility** | REST Assured фільтри: логування + Allure |
| **JUnit `ParameterResolver`** | `BookingDataExtension`, `AuthDataExtension` — дані прокидуються в параметри тесту |
| **Data-driven тести** | `@ValueSource`, `@EnumSource`, `@MethodSource` |
| **`@Step`-анотації** | кроки в Allure-звіті на клієнтах і асертах, з контекстом запиту (не тільки id) |

## CI

GitHub Actions (`.github/workflows/ci.yml`): push/PR у `main` або запуск вручну → `mvn test` на JDK 17 → Allure-звіт → обидва (`allure-results` і готовий report) заливаються як build artifacts, навіть якщо тести впали.

Креденшели беруться **тільки** з GitHub Secrets (`AUTH_USERNAME`, `AUTH_PASSWORD`) — у самому workflow-файлі їх немає. Додати: **Settings → Secrets and variables → Actions → New repository secret**. Без них CI одразу впаде з чіткою помилкою (`Missing config value: auth.username`), а не мовчки пройде з чимось невідомим.

Бейдж може бути червоним — CI навмисно не приховує 6 тестів, що ловлять реальні дефекти сервіса (див. вище).

## Звітність

- Allure: кроки, request/response як attachments, лог тесту, дашборд з env-інфою (Base URL, сервіс, юзер)
- Кожен HTTP-виклик логується (SLF4J), тіла — на рівні DEBUG

## Плюси / межі

**Плюси:** тести читаються як сценарій, легко розширювати (новий ендпоінт = клієнт + DTO + асерт), автоочищення тестових даних, паралельний запуск без flaky.

**Межі:** один сервіс (без мультисервісності), без schema-валідації (поза скоупом), токен не оновлюється всередині прогону.
