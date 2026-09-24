# QA Automation Assignment

[![CI](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml/badge.svg)](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml)

Автотести трьох рівнів в одному Maven-проєкті, з Allure-звітом і CI на GitHub Actions.

| Рівень | Що тестуємо | Інструмент |
|---|---|---|
| REST | [Restful Booker](https://restful-booker.herokuapp.com) — auth і CRUD бронювань | REST Assured |
| GraphQL | [Hygraph](https://hygraph.com/graphql-playground), схема Video — `movies`, `movie` | REST Assured + graphql-java-codegen |
| UI | [DemoQA Practice Form](https://demoqa.com/automation-practice-form) — реєстрація студента | Playwright |

**Звіт останнього прогону:** https://edyachenko.github.io/qa-automation-assignment/

**Стек:** Java 17 · Maven · JUnit 5 · AssertJ · Jackson · Lombok · Allure 2 · SLF4J/Logback

## Запуск

Потрібен **JDK 17**: на новіших версіях Lombok падає на компіляції.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS, якщо mvn бере інший JDK
```

```bash
mvn clean test -Dauth.username=admin -Dauth.password=password123   # усе

mvn test -Dgroups=api -Dauth.username=admin -Dauth.password=password123
mvn test -Dgroups=graphql
mvn test -Dgroups=ui
mvn test -Dgroups=ui -Dui.headless=false   # з видимим браузером

mvn allure:serve   # відкрити звіт локально
```

- Креденшели потрібні тільки REST-тестам. Їх передають через `-D`, у репо їх немає.
- Під час першого UI-прогону Playwright сам скачує Chromium, це близько 150 MB.
- Тести йдуть паралельно в 4 потоки (`src/test/resources/junit-platform.properties`).
- Конфіг: `src/main/resources/config.properties`. Будь-яке значення з нього можна перебити через `-Dключ=значення`.

**IntelliJ:**
- Якщо IDE не бачить класів із `...graphql.generated`: Maven → Reload All Maven Projects → Generate Sources and Update Folders.
- Allure-кроки (`@Step`) пишуться тільки при запуску через Maven, бо AspectJ-агент підключається в surefire.

## Структура

```
src/main/java/com/flamingo/qa/
├── common/    ApiClient (базовий HTTP-клієнт), ResponseAssert, HttpLoggingFilter
├── config/    Config, TestTag
├── api/       REST: client, assertions, dto, data (генерація тестових даних)
├── graphql/   GraphQL: client, assertions, dto, report (вкладення для Allure)
└── ui/        UI: pages (page objects), dto, data, browser (блокування реклами)
src/main/graphql/schema.json      схема Hygraph для codegen
src/test/java/com/flamingo/qa/
├── tests/     api, graphql, ui — самі тести і базові класи
├── api/extension, ui/extension   JUnit-extensions
└── report/    Allure: дашборд і лог тесту у звіті
```

## Як влаштовано

### Спільне для всіх рівнів

- **Тест читається як сценарій.** Клієнт або сторінка повертає типізований об'єкт з fluent-перевірками:
  ```java
  bookingClient.createBooking(booking).shouldHaveStatus(SC_OK).shouldHaveBooking(booking);
  ```
- **DTO — Java records** з Lombok `@Builder`/`@With`. Тестові дані генерують фабрики (`BookingData`, `Students`), а не збирає руками кожен тест.
- **Базовий клас на кожен рівень** (`BaseApiTest`, `BaseGraphql`, `BaseUiTest`) підключає тег, Allure-extension і верхню групу у звіті.

### REST

- `AuthClient` і `BookingClient` успадковують `ApiClient`: base URL, логування і Allure-фільтр налаштовані один раз.
- Адмін-токен береться один раз за прогін (`AdminToken`, lazy singleton). Створені тестами бронювання видаляються після кожного тесту.
- Вхідні дані приходять у параметри тесту через JUnit `ParameterResolver` (`BookingDataExtension`, `AuthDataExtension`). Data-driven кейси зроблені через `@ValueSource`, `@EnumSource` і `@MethodSource`.

### GraphQL

- **Codegen.** Maven-плагін генерує зі `schema.json` DTO (`Movie`), обгортки відповіді (`MoviesQueryResponse`) і проєкції полів (`MovieResponseProjection`). Поля в запиті й DTO мають одне джерело, тож помилка в назві поля падає на компіляції, а не в рантаймі.
- **Variables.** Операції — це шаблони з `$first`/`$skip`/`$id` у `GraphQLClient`. Значення йдуть окремим полем `variables` і не вшиваються в текст запиту.
- **Асерти.** `GraphQlResponseAssert` містить спільні перевірки `data`/`errors`, підкласи — перевірки під форму конкретної відповіді. Для разової перевірки є `satisfies(...)`, щоб асерт-класи не роздувались.
- **Оновити схему**, якщо Hygraph її змінить:
  ```bash
  python3 scripts/fetch_graphql_schema.py <graphql.url з config.properties> src/main/graphql/schema.json
  ```

### UI

- **Page Object.** Селектори й кроки (`@Step`) живуть у сторінці (`PracticeFormPage`, `SubmissionModal`). У тестах немає ні селекторів, ні `new`: достатньо оголосити поле, і `PageObjectsExtension` сам створить сторінку на браузері цього тесту.
  ```java
  PracticeFormPage practiceForm;

  practiceForm.open().fill(student).submit().shouldShow(student);
  ```
- **Браузер** (`BrowserExtension`). Один Chromium на потік, бо Playwright не потокобезпечний. Кожен тест отримує свій ізольований `BrowserContext` і не бачить cookies та стану інших тестів.
- **Очікування без `sleep`.** Дії Playwright самі чекають, доки елемент стане видимим і доступним. Перевірки йдуть через `PlaywrightAssertions`, які повторюються до таймауту (10 с). Там, де стан змінюється асинхронно, чекаємо його явно: наприклад, що календар закрився після вибору дати.
- **Реклама demoqa** блокується на рівні мережі (`AdBlocker`), щоб банери не перекривали кнопки.
- **Скріншот при падінні** знімається до закриття контексту й додається в Allure як "Screenshot on failure".

## Звіт (Allure)

- **Групи:** `REST: Restful Booker` → `POST /booking`…, `GraphQL: Hygraph` → `query movies`…, `UI: DemoQA` → `Student registration form`.
- **Кроки з даними:** видно, що саме відправили й перевірили, а не лише "Create booking".
- **Вкладення:** request/response кожного HTTP-виклику. Для GraphQL ще відформатований запит і variables, для UI — скріншот при падінні. У кожному тесті є його лог.
- **Дашборд Environment:** URL сервісів і юзер.

## CI

`.github/workflows/ci.yml` запускається на push/PR у `main` і вручну. Два job-и йдуть паралельно, у кожного свій статус, і перезапустити можна кожен окремо:

| Job | Що запускає | Особливості |
|---|---|---|
| **API tests (REST + GraphQL)** | `mvn test -Dgroups=api,graphql` | креди з секретів `AUTH_USERNAME`/`AUTH_PASSWORD` |
| **UI tests (Playwright)** | `mvn test -Dgroups=ui` | спершу ставить Chromium із системними залежностями, креди не потрібні |

Кожен job зберігає свої `allure-results` і готовий звіт як artifacts, навіть якщо тести впали. **publish-report** (тільки для `main`) зливає результати обох job-ів в один звіт і публікує його на GitHub Pages.

Потрібні секрети репозиторію `AUTH_USERNAME` і `AUTH_PASSWORD` (Settings → Secrets and variables → Actions). Для публікації звіту один раз увімкни Settings → Pages → Source: **GitHub Actions**.

## Відомі дефекти сервісів

Job **API tests** червоний навмисно: 6 REST-тестів ловлять реальні дефекти Restful Booker.
- Сервіс приймає дубль бронювання.
- `PUT` приймає невалідні дати.
- Пошук за новими датами не знаходить щойно оновлене бронювання.

Ці тести не вимкнені й не приховані. У звіті вони потрапляють у стандартну категорію Allure **Product defects**.
