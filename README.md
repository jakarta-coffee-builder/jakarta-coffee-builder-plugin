# Jakarta Coffee Builder Plugin

Este plugin de Maven automatiza la configuración y el andamiaje (scaffolding) de proyectos Jakarta EE, facilitando la adopción de mejores prácticas y acelerando el desarrollo inicial.

## 🚀 Uso desde la Línea de Comandos

Todos los "goals" del plugin pueden ejecutarse directamente sin necesidad de estar configurados en el `pom.xml`, utilizando la siguiente sintaxis:

```bash
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:<goal> -D<opcion>=<valor>
```

---

## 🗄️ Persistencia y Datos

### `add-persistence`
Configura la unidad de persistencia (JPA).
- **`-Dpersistence-unit-name=<nombre>`**: Nombre de la Persistence Unit. (Por defecto: `defaultPU`)
- **`-Ddatasource-name=<nombre>`**: Nombre JNDI del Data Source. (Por defecto: `defaultDatasource`)
- **`-Durl=<jdbc-url>`**: URL de conexión JDBC. (Por defecto: H2 in-memory)
- **`-Duser=<usuario>`**: Usuario de base de datos.
- **`-Dpassword=<clave>`**: Contraseña de base de datos.
- **`-Ddeclare=<web|...>`**: Lugar donde declarar el recurso (Por defecto: `web`).

### `add-datasource`
Agrega la configuración de un Data Source.
- *(Mismas opciones que `add-persistence`)*.

### `add-entities`
Integra definiciones de entidades JPA en el proyecto.
- **`-Dentities-file=<path>`**: Ruta al archivo de definición de entidades. (**Requerido**)

---

## 🏗️ Arquitectura y Scaffolding

### `add-domain-models`
Genera capas de arquitectura (DTOs, Mappers, Repositorios y Servicios) basándose en una definición de entidades.
- **`-Dentities-file=<path>`**: Ruta al archivo JSON con la definición de entidades. (**Requerido**)

### `create-openapi`
Genera el código del lado del servidor (server-side) a partir de una especificación OpenAPI.
- **`-Dopenapi-server=<path>`**: Ruta al archivo OpenAPI (yml/json). (Por defecto: `${project.basedir}/openapi.yml`)

---

## 🎨 Jakarta Faces (JSF)

### `add-faces`
Configura Jakarta Faces en el proyecto (dependencias y declaración en `web.xml`).
- **`-Dwelcome-file=<nombre>`**: Nombre del archivo de bienvenida. (Por defecto: `index.xhtml`)

### `add-face-page`
Agrega una nueva página JSF al proyecto.
- **`-Dname=<nombre>`**: Nombre de la página (sin extensión). (**Requerido**)
- **`-Dmanaged-bean=<true|false>`**: Indica si se debe crear un backing bean asociado. (Por defecto: `true`)
- **`-Dtemplate=<nombre>`**: (Opcional) Nombre del template Facelet a utilizar.

### `add-face-template`
Crea un nuevo template Facelet.
- **`-Dname=<nombre>`**: Nombre del template. (**Requerido**)
- **`-Dinserts=<lista>`**: Lista separada por comas de nombres para los `ui:insert`.

### `add-forms-from-entities`
Genera formularios CRUD de JSF/PrimeFaces a partir de entidades.
- **`-Dforms-file=<path>`**: Ruta al JSON de definición de formularios. (**Requerido**)
- **`-Dentities-file=<path>`**: Ruta al JSON de definición de entidades. (**Requerido**)


---

## 🚀 Servidores de Aplicación y Microservicios

### `add-glassfish-embedded`
Configura un perfil de Maven para ejecutar con GlassFish Embedded.
- **`-Dprofile=<nombre>`**: ID del perfil de Maven a crear. (Por defecto: `glassfish`)
- **`-Dport=<puerto>`**: Puerto HTTP. (Por defecto: `8080`)
- **`-DcontextRoot=<ruta>`**: Context root de la aplicación. (Por defecto: `${project.build.finalName}`)

### `add-payaramicro`
Configura el plugin y perfil para Payara Micro.
- **`-Dprofile=<nombre>`**: ID del perfil de Maven a crear. (Por defecto: `payaramicro`)

---

## 🛠️ Utilidades

### `add-validation-api`
Agrega las dependencias de Jakarta Validation API para habilitar validaciones mediante anotaciones (`@NotNull`, `@Size`, etc.).

---

## 📝 Ejemplo Completo
Para agregar una página de login utilizando un template existente y sin crear un backing bean:
```bash
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:add-face-page -Dname=login -Dtemplate=mainTemplate -Dmanaged-bean=false
```