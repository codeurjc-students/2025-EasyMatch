# 2025-EasyMatch
EasyMatch es una web para organizar partidos de pádel, tenis, volley playa, fútbol 7... con amigos (privados) o con desconocidos (públicos) de un nivel similar en los clubes disponibles. Incluye un chat para hablar con otros jugadores o para recibir ayuda por parte del club o del administrador. 

## Objetivos

■ Objetivos funcionales:
La web tiene como finalidad facilitar la organización de partidos deportivos, ofreciendo una plataforma centralizada donde los jugadores puedan buscar, crear y unirse a los partidos en los clubes asociados. Asimismo, pretende mejorar la comunicación jugador-club mediante un sistema de chat integrado.
 1. El sistema debe permitir a los usuarios la creación de partidos públicos (abiertos a desconocidos) o privados (entre amigos) indicando deporte, fecha y hora.
 2. El sistema debe permitir a los usuarios buscar y unirse a partidos abiertos en los clubes asociados filtrando por deporte, nivel, fecha, localidad...
 3. El sistema debe permitir a los usuarios salirse de esos mismos partidos antes de que se hayan cerrado, es decir, antes de que el número de plazas se complete.
 4. El sistema debe permitir a los usuarios consultar y/o modificar su información básica (foto de perfil, nombre de usuario, historial de partidos, gráfico del nivel...). De igual forma, deberá permitir borrar su cuenta
 5. El sistema deberá permitir a los usuarios añadir un resultado en un partido ya jugado el cual tendrá que ser validado por las dos parejas.
 6. El sistema debe incorporar un canal de comunicación en tiempo real, entre jugadores así como con el club o el administrador de la web para resolver dudas o incidencias.

■ Objetivos técnicos:
La web se implementará con tecnologías contrastadas a nivel académico y profesional como son Spring Boot para el backend, Angular para el frontend y MySQL como sistema gestor de bases de datos. Además, se asegurará la calidad del software mediante la integración y despliegue continuo empleando GitHub Actions, junto a un conjunto de pruebas automáticas que garanticen la fiabilidad de las funcionalidades básicas.

1. El sistema deberá seguir una arquitectura SPA (Single Page Application) empleando el framework Angular para la construcción del frontend.
 2. El sistema deberá ofrecer un backend basado en Java con Spring Boot, implementando una API REST que gestione la lógica de negocio
 3. El sistema deberá disponer de un pipeline de integración y despliegue continuo (CI/CD), configurado con GitHub Actions que automatice los procesos de compilación, ejecución de pruebas y despliegue del mismo.

 4. El sistema deberá emplear una base de datos relacional MySQL para el almacenamiento estructurado de la información (entidades)

 5. El sistema deberá contar con un repositorio GitHub para el control de versiones, el trabajo colaborativo y la gestión del ciclo de vida del software.

 6. El sistema deberá incluir pruebas unitarias desarrolladas con JUnit para garantizar la validez de la lógica del backend.

 7. El sistema deberá incorporar pruebas de integración mediante Rest Assured para validar la comunicación con la API REST.

 8. El sistema deberá disponer de pruebas end-to-end con Selenium para verificar el correcto funcionamiento del frontend desde la perspectiva del usuario.

 9. El sistema deberá empaquetarse en contenedores Docker para asegurar la portabilidad, facilitar el despliegue y garantizar la homogeneidad entre entornos de desarrollo, pruebas y producción.
 10. El sistema deberá implementar comunicación en tiempo real mediante WebSockets para habilitar el chat.


