# Sistema de Gestión Académica (Enfoque en Seguridad)

> **Una aplicación Java enfocada en implementar seguridad real y buenas prácticas de arquitectura.**

## 📌 ¿Por qué hice este proyecto?
Vengo de desarrollar una arquitectura de microservicios (puedes verla en mi repositorio `admin_tareas`). Aunque funcionaba bien, me di cuenta de que necesitaba **profundizar mucho más en la seguridad**.

A veces, al dividir todo en microservicios, perdemos de vista lo básico. Por eso decidí "dar un paso atrás" hacia una arquitectura monolítica con este proyecto. Mi objetivo fue claro: **entender y aplicar Spring Security desde adentro**, creando un sistema donde la seguridad sea base.

## ⚙️ Stack Tecnológico
Lo construí con las herramientas que busca el mercado actual:
* **Java 17** y **Spring Boot 3.2**
* **Seguridad:** Spring Security + JWT (Tokens)
* **Base de Datos:** MySQL + JPA (Hibernate)
* **Manejo de Datos:** DTOs (usando Records) y Mappers manuales.

## 🏗️ Cómo está organizado (Arquitectura)
Organicé el código en capas claras para que sea fácil de mantener:

1.  **Capa de Seguridad:** Maneja usuarios, roles y permisos. Está separada de la lógica del colegio.
2.  **Capa de Dominio (Student/Teacher):** Aquí vive la información académica.
3.  **Capa de Aplicación (El "Coordinador"):**
    * *El problema:* Crear un estudiante implica guardar sus datos personales Y crearle un usuario para loguearse.
    * *Mi solución:* Creé servicios específicos (como `CreateStudentAccount`) que coordinan estos dos pasos. Si uno falla, todo se cancela (Transaccionalidad).

## 🔒 La Seguridad (Lo más importante)
En lugar de usar la configuración por defecto, implementé controles más finos:

* **Roles vs. Permisos:** No me limité a decir "Si eres Admin, pasas". Implementé permisos específicos (como `READ_COURSE`). Esto permite que el sistema sea más flexible: hoy un profesor puede editar, mañana quizás solo leer, y solo cambio el permiso en la base de datos sin tocar el código.
* **Todo cerrado por defecto:** Usé `denyAll()` en los controladores. Esto significa que si se me olvida configurar un endpoint, nadie puede entrar. Es una medida de seguridad preventiva.
* **Errores Claros:** Si el token falla o no tienes permiso, el sistema no te devuelve una página HTML de error genérica. Te devuelve un JSON claro explicando qué pasó, gracias a mis excepciones personalizadas.

## 🚀 Instalación y Despliegue

Este proyecto utiliza variables de entorno para una configuración segura y flexible.

### 📋 Pre-requisitos
* **Opción Recomendada:** Docker y Docker Compose (Incluidos en el proyecto).
* **Opción Manual:** Java 17, Maven y un servidor MySQL corriendo localmente.

### ⚙️ Configuración (Paso Obligatorio)
⚠️ **Importante:** El proyecto **no arrancará** si no realizas este paso, ya que no incluye credenciales por defecto.

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/Jmrj24/educational-platform]
    ```

2.  **Crear archivo de entorno:**
    Copia el archivo plantilla `.env.example` y renómbralo a `.env`

3.  **Definir Valores:**
    Abre el archivo `.env` y completa las variables vacías según tu entorno:

    | Variable | Descripción | Valor para DOCKER 🐳 | Valor para LOCAL 💻 |
    | :--- | :--- | :--- | :--- |
    | `MYSQL_ROOT_PASSWORD` | Contraseña root para inicializar MySQL | Define una contraseña (ej: `secret`) | (No aplica, usa tu MySQL local) |
    | `BD_URL` | URL de conexión JDBC | `jdbc:mysql://mysql-container:3306/nombre_db`* | `jdbc:mysql://localhost:3306/nombre_db` |
    | `BD_USER` | Usuario de la Base de Datos | `root` | Tu usuario local (ej: root) |
    | `BD_PASSWORD` | Contraseña de la Base de Datos | La misma que `MYSQL_ROOT_PASSWORD` | Tu contraseña local |
    | `APP_USER` | **Username del primer Administrador** | Define un email/user (ej: `admin@mail.com`) | Igual |
    | `APP_PASSWORD` | **Password del primer Administrador** | Define una contraseña segura | Igual |
    | `PRIVATE_KEY` | Clave secreta para firmar JWT | Genera un string aleatorio largo | Igual |
    | `USER_GENERATOR` | Emisor del Token | Ej: `SAS_API` | Igual |

    > 🐳 **Nota para Docker:** En `BD_URL`, asegúrate de que el host (ej: `mysql-container`) coincida con el nombre del servicio de base de datos definido en tu archivo `docker-compose.yml`.

---

### 🐳 Opción A: Ejecutar con Docker
Docker Compose leerá el archivo `.env` automáticamente para levantar la BD y la App conectadas entre sí.

```bash
docker-compose up --build
```
---

### 🛠️ Opción B: Ejecución Manual
Asegúrate de tener MySQL corriendo y que las credenciales en el .env coincidan con tu configuración local.

## 🧪 Cómo probarlo
He incluido una colección de Postman.

---
**Autor:** [Jeferson Rosales]
*Proyecto realizado con fines de práctica profesional y profundización técnica.*