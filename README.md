# QA Automation Assignment — Restful Booker REST + Hygraph GraphQL Tests

[![CI](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml/badge.svg)](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml)

API-автотести для REST-сервісу [Restful Booker](https://restful-booker.herokuapp.com) і публічного GraphQL API [Hygraph](https://hygraph.com/graphql-playground) (схема Video) на Java 17 / Maven / JUnit 5 / REST Assured, з Allure-звітністю та паралельним запуском.

## Стек

Java 17 · Maven · JUnit 5 · REST Assured · AssertJ · Jackson · Lombok · graphql-java-codegen · graphql-java (форматування запитів для Allure) · Allure 2 (+ AspectJ weaving) · SLF4J/Logback

## Запуск

```bash
mvn clean test -Dauth.username=admin -Dauth.password=password123
mvn test -Dgroups=graphql   # тільки GraphQL, креденшели не потрібні
mvn test -Dgroups=api       # тільки REST
mvn allure:report   # або allure:serve
```

Паралельно (4 потоки за замовчуванням, `src/test/resources/junit-platform.properties`).

Потрібен JDK 17: на новіших JDK Lombok 1.18.38 падає на компіляції. Якщо `mvn -version` показує інший JDK:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

`@Step`-кроки в Allure пишуться тільки при запуску через Maven (AspectJ-агент підключається в surefire). При запуску з IDE агент треба додати в Run Configuration: `-javaagent:<шлях до aspectjweaver.jar>`.

## Що покрито

### REST (Restful Booker)

- `POST /auth` — валідні/невалідні креденшели
- `POST /booking` — створення, обов'язкові поля (`@EnumSource`), дублікати
- `GET /booking`, `GET /booking/{id}` — пошук за іменем/датами, 404
- `PUT /booking/{id}` — заміна, невалідні дати (`@MethodSource`), невалідний/відсутній токен (`@ValueSource`)
- `PATCH /booking/{id}` — часткове оновлення + перевірка через повторний GET
- `DELETE /booking/{id}` — з токеном і без

Кілька негативних тестів навмисно фіксують реальні дефекти сервіса (500 замість 400, приймання невалідних дат) — вони описані в Allure-звіті категорією **Product defects**.

### GraphQL (Hygraph, схема Video)

- список `movies` з лімітом (`first`) і пагінацією (`skip`), через GraphQL variables
- один `movie` по id: існуючий і неіснуючий (HTTP 200, `movie: null`, без `errors`)
- вкладені поля через інший тип: `movie → publishedBy → name`
- битий синтаксис (HTTP 400, `ParseError`, `data: null`)
- неіснуюче поле (HTTP 400, помилка валідації з назвою поля, `data: null`)
- валідація variables: відсутня обов'язкова змінна і змінна не того типу (HTTP 400, помилка з назвою змінної)
- аліаси: одне поле двічі з різними аргументами під власними іменами в одному запиті
- introspection увімкнена на публічному endpoint (на ній тримається codegen: якщо її вимкнуть, тест це покаже)

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
| **Codegen зі схеми** | `graphql-codegen-maven-plugin` генерує з `src/main/graphql/schema.json` DTO (`Movie`, `User`), обгортки відповіді (`MoviesQueryResponse`) і проєкції полів (`MovieResponseProjection`) — поля в запиті й DTO мають одне джерело, помилка в назві поля ловиться на компіляції |
| **GraphQL variables** | операції — шаблони з `$first`/`$skip`/`$id` у `GraphQLClient`, значення йдуть окремим полем `variables`, у текст запиту не вшиваються |
| **Fluent assertions для GraphQL** | `GraphQlResponseAssert<SELF, R>` — спільні перевірки `data`/`errors`, підкласи під форму відповіді; `satisfies(...)` для разових перевірок без роздування асерт-класів |

### GraphQL: схема і codegen

Згенеровані класи лежать у `target/generated-sources/graphql` і створюються на кожній збірці, в git їх немає. В IntelliJ після клону або змін у `pom.xml`: Maven → Reload All Maven Projects → Generate Sources and Update Folders, інакше IDE не бачить згенерованих класів. У git лежить тільки схема. Оновити її після змін на стороні Hygraph:

```bash
python3 scripts/fetch_graphql_schema.py https://us-east-1-shared-usea1-02.cdn.hygraph.com/content/clpvcopq3aavs01usft1idkgj/master src/main/graphql/schema.json
```

Скрипт робить introspection і лишає тільки типи, досяжні з `Query` (мутації на публічному read-only endpoint не потрібні). Якщо Hygraph перейменує чи видалить поле, яке використовують тести, збірка впаде на компіляції.

Нова GraphQL-операція = шаблон з variables у `GraphQLClient` + record для variables + асерт-клас під форму відповіді (або існуючий, якщо форма та сама).

## CI

GitHub Actions (`.github/workflows/ci.yml`), дві job:

- **test** — push/PR у `main` або запуск вручну → `mvn test` на JDK 17 → Allure-звіт → `allure-results` і готовий report заливаються як build artifacts, навіть якщо тести впали
- **publish-report** — тільки на push у `main`: бере `allure-results` з job `test`, генерує звіт і публікує на **GitHub Pages** (живе посилання, не архів для качання)

Креденшели беруться **тільки** з GitHub Secrets (`AUTH_USERNAME`, `AUTH_PASSWORD`) — у самому workflow-файлі їх немає. Додати: **Settings → Secrets and variables → Actions → New repository secret**. Без них REST-тести одразу впадуть з чіткою помилкою (`Missing config value: auth.username`), а не мовчки пройдуть з чимось невідомим. GraphQL-тестам креденшели не потрібні.

Бейдж може бути червоним — CI навмисно не приховує 6 тестів, що ловлять реальні дефекти сервіса (див. вище); `publish-report` при цьому все одно публікує звіт (`if: always()`), щоб дефекти було видно, а не приховано.

**Один раз перед першим запуском:** Settings → Pages → Build and deployment → Source: **GitHub Actions** (без цього job `publish-report` впаде на кроці деплою). Після цього звіт живе за адресою `https://edyachenko.github.io/qa-automation-assignment/`.

## Звітність

- Allure: кроки, request/response як attachments, лог тесту, дашборд з env-інфою (REST URL, GraphQL URL, сервіси, юзер)
- Сьюти в Allure дворівневі: `REST: Restful Booker` → `POST /booking`, `PUT /booking/{id}`…; `GraphQL: Hygraph` → `query movies`, `invalid queries`… (`parentSuite` ставиться в `BaseApiTest` / `BaseGraphql`, `suite` — з `@DisplayName` класу чи `@Nested`-групи)
- GraphQL: у кожному запиті окремі вкладення **GraphQL query** (відформатований багаторядковий запит) і **GraphQL variables** (JSON), кроки з параметрами (`Query movies (first=2, skip=2)`), групи тестів `query movies`, `invalid queries` тощо
- Кожен HTTP-виклик логується (SLF4J), тіла — на рівні DEBUG

## Плюси / межі

**Плюси:** тести читаються як сценарій, легко розширювати (новий ендпоінт = клієнт + DTO + асерт), автоочищення тестових даних, паралельний запуск без flaky, GraphQL-DTO не розходяться зі схемою завдяки codegen.

**Межі:** без JSON-schema-валідації REST-відповідей (поза скоупом), токен не оновлюється всередині прогону, GraphQL покриває тільки читання (публічний endpoint read-only), шаблони операцій з variables пишуться руками.
