# 2025-EasyMatch
**EasyMatch** es una web para **organizar partidos** de pádel, tenis, volley playa, fútbol 7... con amigos (**privados**) o con desconocidos (**públicos**) de un nivel similar en los clubes disponibles. Incluye un chat para hablar con otros jugadores o para recibir ayuda por parte del club o del administrador. <br/><br/>

<img width="780" height="552" alt="image" src="https://github.com/user-attachments/assets/d66eca59-6eaf-4471-bbae-96de99a4dc5f" />  <br/><br/>

En este documento <ins>**solamente**</ins> se han definido los objetivos funcionales y los objetivos técnicos de la aplicación EasyMatch. En ningún caso se ha iniciado todavía la fase de implementación del sistema.

## Objetivos

■ <ins>**Objetivos funcionales**</ins>:
La web tiene como finalidad **facilitar la organización de partidos deportivos**, ofreciendo una plataforma centralizada donde los jugadores puedan buscar, crear y unirse a los partidos en los clubes asociados. Asimismo, pretende mejorar la comunicación jugador-club mediante un sistema de chat integrado.
 1. El sistema debe permitir a los usuarios la **creación de partidos públicos** (abiertos a desconocidos) o **privados** (entre amigos) indicando deporte, fecha y hora.
 2. El sistema debe permitir a los usuarios **buscar y unirse a partidos abiertos** en los clubes asociados filtrando por deporte, nivel, fecha, ciudad...
 3. El sistema debe permitir a los usuarios **salirse de esos mismos partidos** antes de que se hayan cerrado, es decir, antes de que el número de plazas se complete.
 4. El sistema debe permitir a los usuarios **consultar y/o modificar su información básica** (foto de perfil, nombre de usuario, historial de partidos, gráfico del nivel...). De igual forma, deberá permitir borrar su cuenta
 5. El sistema deberá permitir a los usuarios **añadir un resultado en un partido** ya jugado el cual tendrá que ser validado por las dos parejas.
 6. El sistema debe incorporar un **canal de comunicación en tiempo real**, entre jugadores así como con el club o el administrador de la web para resolver dudas o incidencias.

■ <ins>**Objetivos técnicos**</ins>:
La web se implementará con tecnologías contrastadas a nivel académico y profesional como son **Spring Boot** para el backend, **Angular** para el frontend y **MySQL** como sistema gestor de bases de datos. Además, se asegurará la calidad del software mediante la integración y despliegue continuo empleando **GitHub Actions**, junto a un conjunto de pruebas automáticas que garanticen la fiabilidad de las funcionalidades básicas.

1. El sistema deberá seguir una **arquitectura SPA** (Single Page Application) empleando el framework **Angular** para la construcción del frontend.
 2. El sistema deberá ofrecer un backend basado en **Java** con **Spring Boot**, implementando una **API REST** que gestione la lógica de negocio.
 3. El sistema deberá disponer de un pipeline de **integración y despliegue continuo** (CI/CD), configurado con **GitHub Actions** que automatice los procesos de compilación, ejecución de pruebas y despliegue del mismo.

 4. El sistema deberá emplear una base de datos relacional **MySQL** para el almacenamiento estructurado de la información (entidades).

 5. El sistema deberá contar con un repositorio **GitHub** para el control de versiones y la gestión del ciclo de vida del software.

 6. El sistema deberá incluir **pruebas unitarias** desarrolladas con **JUnit** para garantizar la validez de la lógica del backend.

 7. El sistema deberá incorporar **pruebas de integración** mediante **Rest Assured** para validar la comunicación con la API REST.

 8. El sistema deberá disponer de **pruebas end-to-end** con **Selenium** para verificar el correcto funcionamiento del frontend desde la perspectiva del usuario.

 9. El sistema deberá empaquetarse en contenedores **Docker** para asegurar la portabilidad, facilitar el despliegue y garantizar la homogeneidad entre entornos de desarrollo, pruebas y producción.
 10. El sistema deberá implementar comunicación en tiempo real mediante **WebSockets** para habilitar el **chat**.

## Metodología
El desarrollo de la página web se llevará a cabo siguiendo un enfoque **incremental** e **iterativo**, lo cual permite ir creando el sistema en **fases sucesivas**, entregando versiones funcionales al finalizar cada una de ellas.
- Fases:
  - <ins>**Fase 1 (1 al 15 de septiembre)**</ins> : **Definición de funcionalidades**. </br> En esta fase se han recogido y documentado los objetivos funcionales y técnicos de la web. La descripción de la funcionalidad general está en la sección _**Objetivos funcionales**_, en cambio, el detalle de cada una y a quién va dirigida se encuentra en la sección _**Funcionalidades detalladas**_.
  - <ins>**Fase 2 (15 de septiembre al 15 de octubre)**</ins> : **Configuración de las tecnologías y herramientas de desarrollo con controles de calidad que se realizan de forma periódica.**
  - <ins>**Fases 3, 4, 5 (15 de octubre al 15 de abril)**</ins>: **Desarrollo iterativo e incremental de la aplicación. Al final de cada fase se publicará una versión (release).**
  - <ins>**Fase 6 (15 de abril al 15 de mayo)**</ins>: **Escritura de la memoria**.
  - <ins>**Fase 7 (15 mayo al 15 de junio)**</ins>: **Preparación de la presentación**.

○	Se elaborará un diagrama de Gantt en el que se muestren gráficamente estas fases.

## Funcionalidades detalladas

■ <ins>**Funcionalidades básicas**</ins>: son aquellas que permiten que la web cumpla su propósito mínimo que, en este caso, es organizar partidos.<br/> <br/>
&emsp; ○	_**Usuario anónimo**_
1. **Consulta de la información básica de los clubes asociados** a la plataforma (nombre, dirección, instalaciones disponibles, contacto).
2. **Búsqueda de partidos abiertos** (pero no unirse).

&emsp; ○	_**Usuario registrado**_
1. **Creación de partidos públicos y privados** indicando deporte, fecha, hora y ubicación (club o pista privada). 
2. **Búsqueda y unión a partidos abiertos** filtrando por deporte, fecha, y ciudad.
3. **Abandonar un partido antes de que se complete el número de plazas**.
4. **Gestión de perfil básico del usuario** que incluye foto de perfil, nombre de usuario (información personal en general), historial de partidos, progresión de nivel y eliminación de la cuenta. <br/>

&emsp; ○	_**Usuario administrador**_

1. **Validar y mantener la información de los clubes** (altas, bajas, modificaciones).
2. **Supervisar los partidos creados por los usuarios** para garantizar el correcto uso de la web.

■ <ins>**Funcionalidades intermedias**</ins>: son aquellas mejoras que enriquecen la experiencia del usuario con el sistema (adicionales) pero no son esenciales . <br/><br/>
&emsp; ○	_**Usuario registrado**_
1. **Consulta del historial de partidos jugados**.
2. **Visualización de la progresión del nivel del jugador** en forma de gráfica.
3. **Posibilidad de modificar la información personal del usuario** como la foto de perfil, nombre de usuario, teléfono o email.

&emsp; ○	_**Usuario administrador**_
1. Acceder y consultar el historial global de actividad (partidos, usuarios, mensajes...)

■ <ins>**Funcionalidades avanzadas**</ins>: son aquellas funciones más sofisticadas que aportan mayor interacción y soporte y con las que se llega a la versión final. <br/><br/>
&emsp; ○	_**Usuario registrado**_
1. **Registro de resultados de partidos jugados**, con validación obligatoria por las dos parejas.
2. **Canal de comunicación en tiempo real mediante chat**, tanto entre jugadores como con el club o el administrador.
3. **Algoritmo avanzado de cálculo del nivel** que ajusta la puntuación en función del resultado y los niveles de los rivales.

&emsp; ○	_**Usuario administrador**_
1. **Intervenir en el chat** como soporte técnico o moderador
2. **Gestionar incidencias reportadas** por los usuarios mediante el chat (falta de deportividad, fraude en el resultado,...).

## Análisis
- **Pantallas y navegación**: Se mostrará el mockup de cada pantalla, una breve descripción de cada una y a las páginas a las que se accede desde ella.
- El modelo de datos de la web está compuesto por las  <ins>**entidades**</ins> Usuario, Partido, Club y Mensaje. A continuación, se muestran sus atributos y relaciones en el siguiente diagrama: <br/> <br/>
 ![Diagrama entidad-relación](https://github.com/user-attachments/assets/6fdbd5e4-183a-4ce8-95cd-9310bae06f3a)


-	<ins>**Permisos de usuarios**</ins> : en la tabla a continuación se detallan los permisos por tipo de usuario
  

| **Entidad**       | **Operación** | **Usuario Anónimo** | **Usuario Registrado** | **Administrador** |
|-------------------|---------------|----------------------|-------------------------|-------------------|
| **Club**          | Consultar     | ✅                   | ✅                      | ✅                |
|                   | Crear         | ❌                   | ❌                      | ✅                |
|                   | Modificar     | ❌                   | ❌                      | ✅                |
|                   | Eliminar      | ❌                   | ❌                      | ✅                |
| **Partido**       | Consultar     | ✅ *(solo abiertos)* | ✅                      | ✅                |
|                   | Crear         | ❌                   | ✅                      | ✅                |
|                   | Modificar     | ❌                   | ❌                      | ✅                |
|                   | Eliminar      | ❌                   | ✅ *(si abandona y estaba solo)* | ✅       |
| **Usuario**       | Consultar     | ❌                   | ✅ *(perfil propio)*    | ✅                |
|                   | Crear         | ❌                   | ✅ *(registro)*         | ✅                |
|                   | Modificar     | ❌                   | ✅ *(perfil propio)*    | ✅                |
|                   | Eliminar      | ❌                   | ✅ *(cuenta propia)*    | ✅                |
| **Mensaje (chat)**| Consultar     | ❌                   | ✅                      | ✅                |
|                   | Crear         | ❌                   | ✅                      | ✅ *(moderación)* |
|                   | Modificar     | ❌                   | ❌                      | ✅                |
|                   | Eliminar      | ❌                   | ❌                      | ✅ *(moderación)* |

- Las entidades que tendrán asociadas una o varias <ins>**imágenes**</ins> son Club y Usuario. Cada usuario podrá subir una foto de perfil y los clubes podrán subir imágenes de sus instalaciones. 
- La evolución del nivel de los jugadores en los distintos deportes se visualizará mediante **gráficos** lineales.
- Se emplearán WebSockets como  <ins>**tecnología complementaria**</ins> para implementar el chat en tiempo real entre jugadores, clubes y administradores
- Se implementará un  <ins>**algoritmo avanzado**</ins> para el cálculo dinámico del nivel de los jugadores, ajustando la puntuación obtenida en función de la diferencia de nivel con los rivales.

## Seguimiento

## Autor
El presente desarrollo web se realiza en el contexto del **Trabajo de Fin de Grado** correspondiente a la titulación de Ingeniería Informática, impartida en la Escuela Técnica Superior de Ingeniería Informática (ETSII) de la Universidad Rey Juan Carlos, en su campus de Móstoles. El proyecto está siendo llevado a cabo por el estudiante _**Daniel Muñoz Martínez**_, bajo la supervisión del profesor _**Michel Maes Bermejo**_.



