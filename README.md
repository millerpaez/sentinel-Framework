# Sentinel

**Monitor sintético BDD en Java:** ejecuta los flujos críticos de [automationexercise.com](https://automationexercise.com) (UI y API) cada 6 horas, publica los reportes en AWS S3 y deja evidencia de cada fallo.

> 🚧 En construcción por fases. Cada sección se completa cuando termina la fase que la implementa.

---

## Stack y versiones

| Herramienta | Versión | Rol en el proyecto |
|---|---|---|
| Java | 22 | Lenguaje. Se compila con *toolchain* de Gradle a Java 22 |
| Gradle (wrapper) | 9.8.1 | Build y tareas (`test` para unit tests, `acceptanceTest` para BDD) |
| Selenium | 4.51.0 | Automatización UI. Selenium Manager descarga el driver de Chrome |
| Cucumber (`java`, `testng`, `picocontainer`) | 7.34.9 | BDD con Gherkin |
| TestNG | 7.12.0 | **Runner** de Cucumber, con escenarios en paralelo |
| JUnit | 5.14.4 | **Unit tests del propio framework** |
| PicoContainer | (vía `cucumber-picocontainer`) | Inyección de dependencias por escenario |
| REST Assured | 5.5.7 | Capa API: preparación y limpieza de datos, pruebas de API |
| Datafaker | 2.7.0 | Datos dinámicos (usuarios únicos por ejecución) |
| AssertJ | 3.27.7 | Aserciones con mensajes claros |

Se usan las versiones estables más recientes de cada librería **dentro de la versión mayor definida para el proyecto** (Cucumber 7, JUnit 5, REST Assured 5, AssertJ 3). Ya existen versiones mayores más nuevas de algunas, pero cambiar de versión mayor trae cambios incompatibles que no aportan nada a este alcance.

---

## Gradle en 2 minutos

### ¿Qué es?
Gradle es la herramienta de **build**: descarga dependencias, compila el código y ejecuta las pruebas. Todo se define en dos archivos:

| Archivo | Para qué |
|---|---|
| `settings.gradle` | Nombre del proyecto (`sentinel`) |
| `build.gradle` | Plugins, versión de Java, dependencias y tareas |

### ¿Qué es una tarea?
Una **tarea** es una unidad de trabajo con nombre: `compileTestJava`, `test`, `build`… Las tareas dependen unas de otras: `build` ejecuta la compilación y `check`, y `check` ejecuta `test`. Este proyecto define dos tareas de pruebas que nunca se mezclan:

| Tarea | Motor | Qué ejecuta |
|---|---|---|
| `test` | JUnit 5 | Unit tests del framework (`**/unit/**`) |
| `acceptanceTest` | TestNG + Cucumber | Escenarios BDD desde `testng.xml` |

`acceptanceTest` **no** forma parte de `build`: necesita red y navegador, así que se ejecuta de forma explícita.

### Wrapper vs. instalación global

| | Wrapper (`./gradlew`) | Global (`gradle`) |
|---|---|---|
| ¿Qué es? | Un script dentro del repo que descarga y usa **una versión fija** de Gradle | Gradle instalado en la máquina (por ejemplo con `brew install gradle`) |
| Versión | La que dice `gradle/wrapper/gradle-wrapper.properties` (9.8.1) | La que tenga instalada cada persona |
| ¿Hay que instalar algo? | No, solo Java | Sí |
| Resultado | Igual en todas las máquinas y en CI | Puede variar entre máquinas |

**Regla del proyecto:** siempre usar `./gradlew`. Así tu Mac, la de cualquier otra persona y GitHub Actions ejecutan exactamente la misma versión de Gradle. El wrapper además verifica el SHA-256 del Gradle que descarga.

Archivos del wrapper (todos se suben al repo):
- `gradlew` / `gradlew.bat`: scripts para macOS/Linux y Windows.
- `gradle/wrapper/gradle-wrapper.jar`: el pequeño programa que descarga Gradle.
- `gradle/wrapper/gradle-wrapper.properties`: versión y checksum.

La primera ejecución descarga Gradle en `~/.gradle/wrapper/dists`; las siguientes lo reutilizan.

### Comandos básicos

```bash
./gradlew build                  # compila y ejecuta los unit tests
./gradlew test                   # solo unit tests del framework (JUnit 5)
./gradlew acceptanceTest         # escenarios BDD (TestNG + Cucumber)
./gradlew tasks                  # lista las tareas disponibles
./gradlew dependencies --configuration testRuntimeClasspath   # árbol de dependencias
./gradlew clean                  # borra la carpeta build/
```

---

## Arquitectura

```
src/test/
├── java/com/miller/sentinel/
│   ├── core/                      # Reutilizable: NO conoce automationexercise
│   │   ├── config/Config.java
│   │   ├── driver/DriverFactory.java    # Crea ChromeDriver (headless o visible)
│   │   ├── driver/DriverManager.java    # ThreadLocal<WebDriver>: un navegador por hilo
│   │   └── ui/BasePage.java             # Esperas explícitas y acciones comunes
│   ├── app/                       # Específico del sitio bajo prueba
│   │   ├── pages/LoginPage.java, HomePage.java
│   │   ├── pages/components/HeaderComponent.java
│   │   ├── hooks/Hooks.java
│   │   └── steps/LoginSteps.java
│   ├── runners/RunCucumberTest.java     # Cucumber sobre TestNG, escenarios en paralelo
│   └── unit/core/ConfigTest.java        # Unit tests del framework (JUnit 5)
└── resources/
    ├── config.properties, cucumber.properties, testng.xml
    └── features/ui/login.feature
```

**`core` vs `app`:** `core` es infraestructura genérica (configuración, navegador, esperas) y no menciona el sitio bajo prueba. Para probar otro sitio se reemplaza `app` y `core` queda igual. `app` contiene todo lo que conoce automationexercise: páginas, locators, steps y hooks.

Todo vive en `src/test` porque el proyecto es un framework de pruebas, no una aplicación.

---

## Page Object Model (POM)

### Reglas
1. **Una clase por página o componente.** Una **página** es una URL (`/login` → `LoginPage`). Un **componente** es una parte de la interfaz que se repite en varias páginas (el header) y tiene su propia clase, `HeaderComponent`, que las páginas incluyen como atributo. Así el header se escribe una sola vez.
2. **Locators privados** (`private static final By`). Ningún `WebElement` sale del Page Object.
3. **Métodos de negocio, no técnicos:** `loginAs(email, password)`, no `typeEmail()` + `clickButton()`.
4. **Métodos fluidos:** si la acción lleva a otra página, devuelve el Page Object de esa página. `loginAs(...)` devuelve `HomePage` (login correcto) y `loginExpectingError(...)` devuelve `LoginPage` (el sitio no sale de `/login`).
5. **Sin aserciones en los pages:** exponen estado (`getErrorMessage()`, `isUserLoggedIn()`) y los steps deciden qué es correcto.
6. **`BasePage`** obtiene el driver de `DriverManager.getDriver()` y hace todas las acciones (`click`, `type`, `textOf`, `isVisible`) con `WebDriverWait`, usando el timeout de `Config`.
7. **Navegación directa por URL** (`/login`) en lugar de clics en el menú, porque los anuncios del sitio pueden interceptar esos clics.

### Ejemplo: página y componente
```java
public class LoginPage extends BasePage {
    private static final By EMAIL_INPUT = By.cssSelector("[data-qa='login-email']");
    private final HeaderComponent header = new HeaderComponent();   // composición

    public HomePage loginAs(String email, String password) {        // acción de negocio
        submitCredentials(email, password);
        return new HomePage();                                       // método fluido
    }
}

public class HeaderComponent extends BasePage {
    private static final By LOGOUT_LINK = By.cssSelector("header a[href='/logout']");
    public boolean isUserLoggedIn() { ... }                          // expone estado, no asevera
}
```

### ¿Por qué no hay aserciones en los pages?
- **Responsabilidad única:** el page sabe *cómo* interactuar con la pantalla y el step sabe *qué* se espera.
- **Reutilización:** el mismo `getErrorMessage()` sirve para una prueba que espera un error y para otra que espera que no haya ninguno. Si el page hiciera la aserción, se necesitaría un método distinto para cada expectativa.
- **Fallos claros:** la aserción vive en el step, que es justo donde Cucumber muestra el error en el reporte.

### Locators verificados contra el sitio real
Antes de escribir cada locator se revisó el HTML real con `curl`:

| Elemento | Locator | Fuente |
|---|---|---|
| Email / password / botón | `[data-qa='login-email' \| 'login-password' \| 'login-button']` | Atributos `data-qa` propios para automatización: no cambian con el diseño |
| Error de login | `.login-form form p` → `Your email or password is incorrect!` | Respuesta real de un login inválido |
| Usuario logueado | `//header//a[contains(., 'Logged in as')]/b` y `header a[href='/logout']` | Header real después de iniciar sesión |

---

## Modelo de hilos

### Dos mecanismos, dos responsabilidades

| Mecanismo | Qué guarda | Alcance | Por qué |
|---|---|---|---|
| `DriverManager` con `ThreadLocal<WebDriver>` | El navegador | **Por hilo** | El driver es un recurso pesado, ligado al hilo que lo usa. Cada hilo paralelo necesita su propio navegador |
| PicoContainer (`cucumber-picocontainer`) | Instancias de steps y hooks (y su estado) | **Por escenario** | Cucumber crea un contenedor nuevo por escenario, así que el estado nunca se filtra a otro escenario |

**¿Por qué ambos?** Porque resuelven problemas distintos. Un `ThreadLocal` solo no basta: el hilo se reutiliza entre escenarios y cualquier estado guardado ahí pasaría al siguiente escenario. Pico solo tampoco basta: el `WebDriver` no lo maneja Pico, y Selenium necesita que cada hilo use su propio navegador. Por eso: **hilo → navegador, escenario → estado**.

### `DriverManager`
```java
private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

public static WebDriver getDriver() {           // creación lazy
    WebDriver driver = DRIVER.get();
    if (driver == null) { driver = DriverFactory.createDriver(); DRIVER.set(driver); }
    return driver;
}

public static void quit() {
    try { if (DRIVER.get() != null) DRIVER.get().quit(); }
    finally { DRIVER.remove(); }                // SIEMPRE
}
```
- **Lazy:** el navegador se crea la primera vez que un page lo pide. Un escenario que solo usa la API nunca abre Chrome.
- **¿Por qué `remove()` es obligatorio?** TestNG usa un **pool de hilos que se reutilizan**. Sin `remove()`, el siguiente escenario que corre en ese hilo encuentra en el `ThreadLocal` el driver ya cerrado y falla con `NoSuchSessionException`. Además, el objeto nunca se libera y la memoria se acumula. Está dentro de un `finally` para que se ejecute aunque `quit()` lance una excepción.

### Paralelismo: cómo se configura
- `RunCucumberTest` sobrescribe `scenarios()` con `@DataProvider(parallel = true)`: cada escenario es una fila del DataProvider y TestNG reparte las filas entre los hilos de su pool.
- El número de hilos está en `src/test/resources/testng.xml`: `data-provider-thread-count="3"`.
- **Evidencia:** el `@Before` escribe en el reporte `running on thread: TestNG-PoolService-N`.

### Hooks
| Hook | Qué hace |
|---|---|
| `@Before` | Registra en el reporte el nombre del escenario y del hilo |
| `@After(order = 0)` | Si el hilo tiene navegador: adjunta un screenshot si el escenario falló y luego `DriverManager.quit()` (con `remove()`) |

En Cucumber, los `@After` con **orden mayor se ejecutan primero**. La limpieza por API (Fase 3) tendrá `order = 100` para ejecutarse antes de cerrar el navegador.

### `DriverFactory`
- Usa solo `ChromeDriver`. **Selenium Manager** descarga el chromedriver que corresponde a la versión de Chrome instalada, así que no hay drivers manuales.
- `headless=true` agrega `--headless=new`, `--window-size=1920,1080`, `--no-sandbox` y `--disable-dev-shm-usage` (estos dos últimos son necesarios en contenedores Linux de CI).
- **`PageLoadStrategy.EAGER`:** Selenium devuelve el control cuando el DOM está listo, sin esperar a que terminen de cargar los anuncios y trackers del sitio. Las esperas explícitas de `BasePage` se encargan de los elementos que sí se usan.

---

## Flujo de ejecución completo

```mermaid
flowchart TD
    A["./gradlew acceptanceTest"] --> B["Gradle levanta una JVM de pruebas<br/>y ejecuta testng.xml"]
    B --> C["TestNG instancia RunCucumberTest"]
    C --> D["Cucumber lee @CucumberOptions<br/>y descubre los .feature"]
    D --> E["scenarios() — DataProvider paralelo<br/>una fila por escenario"]
    E --> F["Pool de TestNG (3 hilos)"]
    F --> G["Por cada escenario, en su hilo"]
    G --> G1["1. Cucumber crea un PicoContainer nuevo"]
    G1 --> G2["2. Pico instancia Hooks y Steps"]
    G2 --> G3["3. @Before → log del hilo"]
    G3 --> G4["4. Steps → API (REST Assured) y/o UI<br/>DriverManager.getDriver() crea el driver del hilo"]
    G4 --> G5["5. @After order=100 → borra el usuario por API"]
    G5 --> G6["6. @After order=0 → screenshot si falló<br/>+ quit() + remove()"]
    G6 --> G7["7. Pico se descarta con el estado del escenario"]
    G7 --> R["Reportes:<br/>build/reports/cucumber/index.html<br/>build/reports/cucumber/cucumber.json"]
```

### Detalle de Gradle 9 + TestNG
`RunCucumberTest` no declara ningún `@Test` propio: lo hereda de `AbstractTestNGCucumberTests`, que está dentro del jar de Cucumber. El escáner de clases de Gradle no lo detecta y Gradle 9 falla con *"did not discover any tests"*. Por eso `acceptanceTest` usa `scanForTestClasses = false` e `include '**/runners/**'`. La clase que se ejecuta ya la define `testng.xml`.

---

## Configuración (`Config`)

### Qué
`core/config/Config.java` es el único punto de acceso a la configuración. Los valores por defecto viven en `src/test/resources/config.properties`:

| Clave | Valor por defecto | Variable de entorno |
|---|---|---|
| `base.url` | `https://automationexercise.com` | `BASE_URL` |
| `api.base.url` | `https://automationexercise.com/api` | `API_BASE_URL` |
| `headless` | `false` | `HEADLESS` |
| `timeout.seconds` | `10` | `TIMEOUT_SECONDS` |

### Orden de precedencia (gana el primero que exista)

1. **System property:** `./gradlew ... -Dheadless=true`
2. **Variable de entorno:** la clave en mayúsculas y con `.` cambiado por `_` (`headless` → `HEADLESS`)
3. **`config.properties`**

Si la clave no existe en ninguno de los tres, se lanza `IllegalStateException` con el nombre de la clave. Es mejor fallar de inmediato que seguir con un `null`.

### Por qué así
- **El mismo código en local y en CI:** en tu Mac se usan los valores del archivo y en GitHub Actions se cambian con `HEADLESS=true`, sin tocar código.
- **El archivo se lee una sola vez** (inicialización estática) y nadie puede modificarlo después. La clase es `final` y tiene constructor privado, así que no se crean instancias: todo se hace con `Config.get(...)`.
- **Conversión estricta de tipos:** `getBool` solo acepta `true` o `false` (sin importar mayúsculas). `Boolean.parseBoolean("yes")` devolvería `false` sin avisar, y un error de escritura como `HEADLESS=ture` dejaría el navegador visible en CI sin que nadie lo notara. `getInt` muestra un error que incluye el nombre de la clave.

### Cómo se prueba (`ConfigTest`, JUnit 5)
Son 9 unit tests: lectura del archivo, system property por encima del archivo, uso de la variable de entorno, system property por encima de la variable de entorno, conversión de `int` y `boolean`, valores inválidos y clave faltante.

- Las variables de entorno no se pueden modificar desde Java. Por eso la prueba usa la clave `home`, que corresponde a la variable `HOME` y existe tanto en macOS como en el runner de CI.
- `@AfterEach` borra las system properties que cada prueba define, para que ninguna prueba afecte a otra.
- Las pruebas no dependen del entorno: dan el mismo resultado con `HEADLESS=true` (como en CI).

---

## Cómo ejecutar localmente (macOS)

**Requisitos:** Java 22, Google Chrome y Git. Gradle **no** hace falta: lo trae el wrapper.

```bash
java -version                              # debe mostrar 22
./gradlew build                            # compila y ejecuta los unit tests
./gradlew test                             # unit tests del framework (JUnit 5)
./gradlew acceptanceTest                   # escenarios BDD con Chrome visible
HEADLESS=true ./gradlew acceptanceTest     # sin ventana, como en CI
./gradlew acceptanceTest -Dtags="@smoke"   # filtrar por tag
```

Reporte: abrir `build/reports/cucumber/index.html` en el navegador.
