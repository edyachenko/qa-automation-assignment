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
export AUTH_USERNAME=<логін Restful Booker>
export AUTH_PASSWORD=<пароль Restful Booker>

mvn clean test -Dauth.username="$AUTH_USERNAME" -Dauth.password="$AUTH_PASSWORD"   # усе

mvn test -Dgroups=api -Dauth.username="$AUTH_USERNAME" -Dauth.password="$AUTH_PASSWORD"
mvn test -Dgroups=api -DexcludedGroups=known-defect -Dauth.username="$AUTH_USERNAME" -Dauth.password="$AUTH_PASSWORD"   # без відомих дефектів
mvn test -Dgroups=graphql
mvn test -Dgroups=ui
mvn test -Dgroups=ui -Dui.headless=false   # з видимим браузером

mvn allure:serve   # відкрити звіт локально
```

- Креденшели потрібні тільки REST-тестам. Їх передають через `-D`: у репо їх немає, і комітити їх не можна.
- Під час першого UI-прогону Playwright сам скачує Chromium, це близько 150 MB.
- Тести йдуть паралельно в 4 потоки (`src/test/resources/junit-platform.properties`).
- Конфіг: `src/main/resources/config.properties`. Будь-яке значення з нього можна перебити через `-Dключ=значення`.

## Структура

```
src/main/java/com/flamingo/qa/
├── common/    ApiClient (базовий HTTP-клієнт), ResponseAssert, HttpLoggingFilter
├── config/    Config, TestTag
├── api/       REST: client, assertions, dto, data (генерація тестових даних)
├── graphql/   GraphQL: client, assertions, dto, report (вкладення для Allure)
└── ui/        UI: pages (page objects), components (віджети), dto, data, browser (блокування реклами)
src/main/graphql/schema.json      схема Hygraph для codegen
src/test/java/com/flamingo/qa/
├── tests/     api, graphql, ui — самі тести і базові класи
├── api/extension, ui/extension   JUnit-extensions
└── report/    Allure: @ParentSuite, дашборд, лог тесту у звіті
```

## Як влаштовано

### Спільне для всіх рівнів

- **Тест читається як сценарій.** Клієнт або сторінка повертає типізований об'єкт з fluent-перевірками:
  ```java
  bookingClient.createBooking(booking).shouldHaveStatus(SC_OK).shouldHaveBooking(booking);
  ```
- **DTO — Java records** з Lombok `@Builder`/`@With`. Тестові дані генерують фабрики (`BookingData`, `Students`), а не збирає руками кожен тест.
- **Базовий клас на кожен рівень** (`BaseApiTest`, `BaseGraphQlTest`, `BaseUiTest`) ставить тег і `@ParentSuite("...")`. Ця анотація підключає Allure-extension і задає верхню групу у звіті.

### REST

- `AuthClient` і `BookingClient` успадковують `ApiClient`: base URL, логування і Allure-фільтр налаштовані один раз.
- Адмін-токен береться один раз за прогін (`AdminToken`, lazy singleton). Створені тестами бронювання видаляються після кожного тесту.
- Вхідні дані приходять у параметри тесту через JUnit `ParameterResolver` (`BookingDataExtension`, `AuthDataExtension`). Data-driven кейси зроблені через `@ValueSource`, `@EnumSource` і `@MethodSource`.

### GraphQL

- **Codegen.** Maven-плагін генерує зі `schema.json` DTO (`Movie`), обгортки відповіді (`MoviesQueryResponse`) і проєкції полів (`MovieResponseProjection`). Поля в запиті й DTO мають одне джерело, тож помилка в назві поля падає на компіляції, а не в рантаймі.
- **Variables.** Операції — це шаблони з `$first`/`$skip`/`$id` у `GraphQlClient`. Значення йдуть окремим полем `variables` і не вшиваються в текст запиту.
- **Асерти.** `GraphQlResponseAssert` містить спільні перевірки `data`/`errors`, підкласи — перевірки під форму конкретної відповіді. Для разової перевірки є `satisfies(...)`, щоб асерт-класи не роздувались.
- **Оновити схему**, якщо Hygraph її змінить:
  ```bash
  python3 scripts/fetch_graphql_schema.py <graphql.url з config.properties> src/main/graphql/schema.json
  ```

### UI

- **Page Object.** Селектори й кроки (`@Step`) живуть у сторінці (`PracticeFormPage`, `SubmissionModal`). Логіка складних віджетів винесена в компоненти (`ReactSelect`, `DatePicker`), а сторінка тільки передає їм свій селектор. У тестах немає ні селекторів, ні `new`: достатньо оголосити поле, і `PageObjectsExtension` сам створить сторінку на браузері цього тесту.
  ```java
  PracticeFormPage practiceForm;

  practiceForm.open().fill(student).submit().shouldShow(student);
  ```
- **Браузер** (`BrowserExtension`). Один Chromium на потік, бо Playwright не потокобезпечний. Кожен тест отримує свій ізольований `BrowserContext` і не бачить cookies та стану інших тестів.
- **Очікування без `sleep`.** Дії Playwright самі чекають, доки елемент стане видимим і доступним. Перевірки йдуть через `PlaywrightAssertions`, які повторюються до таймауту (10 с). Там, де стан змінюється асинхронно, чекаємо його явно: наприклад, що календар закрився після вибору дати.
- **Реклама demoqa** блокується на рівні мережі (`AdBlocker`), щоб банери не перекривали кнопки.
- **Скріншот і Playwright trace при падінні** знімаються до закриття контексту й додаються в Allure. Trace відкривається так:
  ```bash
  mvn exec:java -Dexec.args="show-trace <скачаний trace.zip>"
  ```

## Звіт (Allure)

- **Групи:** `REST: Restful Booker` → `POST /booking`…, `GraphQL: Hygraph` → `query movies`…, `UI: DemoQA` → `Student registration form`.
- **Кроки з даними:** видно, що саме відправили й перевірили, а не лише "Create booking".
- **Вкладення:** request/response кожного HTTP-виклику. Для GraphQL ще відформатований запит і variables, для UI — скріншот і Playwright trace при падінні. У кожному тесті є його лог.
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

Ці тести не вимкнені й не приховані. Вони позначені тегом `known-defect`: у звіті їх видно по тегу, а локально їх можна виключити через `-DexcludedGroups=known-defect`. Червоні тести Allure відносить до стандартної категорії **Product defects**.
