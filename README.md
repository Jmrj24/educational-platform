# Sistema de Gestión Académica (Enfoque en Seguridad)

> **Una aplicación Java enfocada en implementar seguridad real y buenas prácticas de arquitectura.**

## 📌 ¿Por qué hice este proyecto?
Vengo de desarrollar una arquitectura de microservicios (puedes verla en mi repositorio `admin_tareas`). Aunque funcionaba bien, me di cuenta de que necesitaba **profundizar mucho más en la seguridad**.

A veces, al dividir todo en microservicios, perdemos de vista lo básico. Por eso decidí "dar un paso atrás" hacia una arquitectura monolítica con este proyecto. Mi objetivo fue claro: **entender y aplicar Spring Security 6 desde adentro**, creando un sistema donde la seguridad no sea un parche, sino la base de todo.

## ⚙️ Stack Tecnológico
Lo construí con las herramientas que busca el mercado actual:
* **Java 17** y **Spring Boot 3.2**
* **Seguridad:** Spring Security 6 + JWT (Tokens)
* **Base de Datos:** MySQL + JPA (Hibernate)
* **Manejo de Datos:** DTOs (usando Records) y Mappers manuales.

## 🏗️ Cómo está organizado (Arquitectura)
No quería el típico "código espagueti" donde todo está mezclado. Organicé el código en capas claras para que sea fácil de mantener:

1.  **Capa de Seguridad (UserSec):** Maneja usuarios, roles y permisos. Está separada de la lógica del colegio.
2.  **Capa de Dominio (Student/Teacher):** Aquí vive la información académica.
3.  **Capa de Aplicación (El "Coordinador"):**
    * *El problema:* Crear un estudiante implica guardar sus datos personales Y crearle un usuario para loguearse.
    * *Mi solución:* Creé servicios específicos (como `CreateStudentAccount`) que coordinan estos dos pasos. Si uno falla, todo se cancela (Transaccionalidad).

## 🔒 La Seguridad (Lo más importante)
En lugar de usar la configuración por defecto, implementé controles más finos:

* **Roles vs. Permisos:** No me limité a decir "Si eres Admin, pasas". Implementé permisos específicos (como `READ_COURSE`). Esto permite que el sistema sea más flexible: hoy un profesor puede editar, mañana quizás solo leer, y solo cambio el permiso en la base de datos sin tocar el código.
* **Todo cerrado por defecto:** Usé `denyAll()` en los controladores. Esto significa que si se me olvida configurar un endpoint, nadie puede entrar. Es una medida de seguridad preventiva.
* **Errores Claros:** Si el token falla o no tienes permiso, el sistema no te devuelve una página HTML de error genérica. Te devuelve un JSON claro explicando qué pasó, gracias a mis excepciones personalizadas.

## 🚀 Instalación y Ejecución

**Requisitos:** Tener instalado Java 17, Maven y MySQL.

1.  **Clonar el proyecto:**
    ```bash
    git clone [https://github.com/Jmrj24/educational-platform.git](https://github.com/Jmrj24/educational-platform.git)
    ```
2.  **Configurar la Base de Datos:**
    Abre el archivo `src/main/resources/application.properties` y ajusta tus credenciales de MySQL:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/db_school
    spring.datasource.username=root
    spring.datasource.password=tu_password
    # Clave para firmar los Tokens (¡Cámbiala en producción!)
    security.jwt.private.key=tu_clave_secreta_aqui
    ```
3.  **Correr la aplicación:**
    ```bash
    mvn spring-boot:run
    ```

## 🧪 Cómo probarlo
He incluido una colección de Postman.

---
**Autor:** [Jeferson Rosales]
*Proyecto realizado con fines de práctica profesional y profundización técnica.*