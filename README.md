# QA Automation Assignment

[![CI](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml/badge.svg)](https://github.com/edyachenko/qa-automation-assignment/actions/workflows/ci.yml)

Автотести трьох рівнів в одному Maven-проєкті, з Allure-звітом і CI на GitHub Actions.

| Рівень | Що тестуємо | Інструмент |
|---|---|---|
| REST | [Restful Booker](https://restful-booker.herokuapp.com): auth і CRUD бронювань | REST Assured |
| GraphQL | [Hygraph](https://hygraph.com/graphql-playground), схема Video: `movies`, `movie` | REST Assured + graphql-java-codegen |
| UI | [DemoQA Practice Form](https://demoqa.com/automation-practice-form): реєстрація студента | Playwright |

**Стек:** Java 17 · Maven · JUnit 5 · AssertJ · Jackson · Lombok · Allure 2 · SLF4J/Logback

## Подивитись результат

**Звіт останнього прогону:** https://edyachenko.github.io/qa-automation-assignment/

**Запустити заново на CI:**
1. **Actions** → workflow **CI** → **Run workflow**.
2. Вибери середовище (`prod` за замовчуванням) → **Run workflow**.
3. Коли прогін завершиться, звіт за посиланням вище оновиться. Звіт кожного job-а також лежить в artifacts прогону.

> Job **API tests** червоний навмисно: 6 тестів ловлять реальні дефекти Restful Booker, див. [Відомі дефекти](#відомі-дефекти-сервісів).

## Локальний запуск

**Потрібно:** JDK 17 і Maven. На новіших JDK Lombok падає на компіляції. Chromium для UI-тестів Playwright скачає сам при першому запуску (~150 MB).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS, якщо mvn бере інший JDK
```

REST-тестам потрібні креди Restful Booker. Це публічні демо-креди з [документації сервісу](https://restful-booker.herokuapp.com/apidoc/index.html#api-Auth-CreateToken). Вони передаються через `-D`, у репо їх немає.

```bash
export AUTH_USERNAME=<логін>
export AUTH_PASSWORD=<пароль>

mvn clean test -Dauth.username="$AUTH_USERNAME" -Dauth.password="$AUTH_PASSWORD"   # усе
mvn allure:serve                                                                 # відкрити звіт
```

Окремі групи:

```bash
mvn clean test -Dgroups=api -Dauth.username="$AUTH_USERNAME" -Dauth.password="$AUTH_PASSWORD"
mvn clean test -Dgroups=graphql
mvn clean test -Dgroups=ui
mvn clean test -Dgroups=ui -Dui.headless=false   # з видимим браузером
mvn clean test -Pstage                          # інше середовище: prod (за замовчуванням), stage, dev
```

## Відомі дефекти сервісів

6 REST-тестів падають, бо Restful Booker:
- приймає дубль бронювання;
- приймає в `PUT` невалідні дати;
- не знаходить бронювання пошуком за щойно оновленими датами.

Тести не вимкнені: вони позначені тегом `known-defect`, і у звіті їх видно по цьому тегу. Щоб прогнати REST без них, додай `-DexcludedGroups=known-defect`.

---

## Як влаштовано

### Структура

```
src/main/java/com/flamingo/qa/
├── common/    ApiClient (базовий HTTP-клієнт), ResponseAssert, HttpLoggingFilter
├── config/    Config, TestTag
├── api/       REST: client, assertions, dto, data (генерація тестових даних)
├── graphql/   GraphQL: client, assertions, dto, report (вкладення для Allure)
└── ui/        UI: pages (page objects), components (віджети), dto, data, browser
src/main/graphql/schema.json      схема Hygraph для codegen
src/test/java/com/flamingo/qa/
├── tests/     api, graphql, ui: тести і базові класи
├── api/extension, ui/extension   JUnit-extensions
└── report/    Allure: @ParentSuite, дашборд, лог тесту
```

### Спільне для всіх рівнів

- **Тест читається як сценарій.** Клієнт або сторінка повертає об'єкт із fluent-перевірками:
  ```java
  bookingClient.createBooking(booking).shouldHaveStatus(SC_OK).shouldHaveBooking(booking);
  ```
- **DTO — Java records**, тестові дані генерують фабрики (`BookingData`, `Students`).
- **Базовий клас на кожен рівень** (`BaseApiTest`, `BaseGraphQlTest`, `BaseUiTest`) ставить тег і `@ParentSuite("...")`. Остання підключає Allure й задає групу у звіті.

### REST

- `AuthClient` і `BookingClient` успадковують `ApiClient`. Base URL, логування й Allure-фільтр налаштовані в ньому один раз.
- Адмін-токен береться один раз за прогін (`AdminToken`). Створені тестами бронювання видаляються після кожного тесту.
- Дані приходять у параметри тесту через JUnit `ParameterResolver`. Data-driven кейси зроблені через `@ValueSource`, `@EnumSource` і `@MethodSource`.
- `shouldHaveEveryField` перевіряє всі поля через AssertJ soft assertions: при падінні видно всі розбіжності одразу.

### GraphQL

- **Codegen.** Зі `schema.json` генеруються DTO (`Movie`), обгортки відповіді й проєкції полів. Поля в запиті й DTO мають одне джерело, тож помилка в назві поля падає на компіляції.
- **Variables.** Значення йдуть окремим полем `variables`, у текст запиту не вшиваються.
- **Асерти.** `GraphQlResponseAssert` перевіряє `data`/`errors`, підкласи — конкретну форму відповіді. Для разових перевірок є `satisfies(...)`.
- Якщо Hygraph змінить схему, її треба оновити:
  ```bash
  python3 scripts/fetch_graphql_schema.py <graphql.url з config.properties> src/main/graphql/schema.json
  ```

### UI

- **Page Object.** Селектори й кроки (`@Step`) живуть у сторінці, логіка віджетів — у компонентах (`ReactSelect`, `DatePicker`). У тесті достатньо оголосити поле, і `PageObjectsExtension` сам створить сторінку:
  ```java
  PracticeFormPage practiceForm;

  practiceForm.open().fill(student).submit().shouldShow(student);
  ```
- **Браузер.** Один Chromium на потік і свій ізольований `BrowserContext` на кожен тест (`BrowserExtension`).
- **Очікування без `sleep`.** Дії Playwright чекають на елементи самі, перевірки йдуть через `PlaywrightAssertions`. Асинхронні стани чекаємо явно: наприклад, закриття календаря чи фокус у полі автокомпліту.
- **Таймаути:** навігація 45 с (demoqa вантажиться 4–8 с навіть в одному браузері), дії 15 с, перевірки 10 с.
- **Реклама demoqa** блокується на рівні мережі (`AdBlocker`), щоб не перекривала кнопки.
- **При падінні** в Allure додаються скріншот і Playwright trace. Trace відкривається так:
  ```bash
  mvn exec:java -Dexec.args="show-trace <скачаний trace.zip>"
  ```

### Звіт (Allure)

- **Групи:** `REST: Restful Booker`, `GraphQL: Hygraph`, `UI: DemoQA`, а всередині — ендпоінти або сценарії.
- **Кроки** з даними, які відправили й перевірили.
- **Вкладення:**
  - request/response кожного HTTP-виклику;
  - відформатований GraphQL-запит і variables;
  - скріншот і trace для UI;
  - лог тесту.
- **Дашборд Environment:** середовище, URL сервісів, юзер.

### Середовища

- Maven-профілі `prod` (за замовчуванням), `stage` і `dev` задають URL-и сервісів.
- Пріоритет значень: `-D` → профіль → `src/main/resources/config.properties` (там значення prod, тому запуск з IDE теж працює).
- У цих публічних сервісів немає окремих dev і stage, тож профілі зараз ведуть на ті самі URL. У реальному проєкті там будуть адреси відповідних середовищ.

### CI

`.github/workflows/ci.yml` запускається на push/PR у `main` (середовище `prod`) і вручну (середовище на вибір).

| Job | Що запускає |
|---|---|
| **API tests (REST + GraphQL)** | `mvn test -Dgroups=api,graphql` з кредами із секретів |
| **UI tests (Playwright)** | `mvn test -Dgroups=ui` після встановлення Chromium |
| **publish-report** | тільки для `main`: зливає результати обох job-ів в один звіт і публікує його на GitHub Pages |

**Щоб CI працював у форку:**
- додай секрети `AUTH_USERNAME` і `AUTH_PASSWORD` (Settings → Secrets and variables → Actions);
- увімкни Settings → Pages → Source: **GitHub Actions**.
