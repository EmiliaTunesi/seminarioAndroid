# Seminario: Introducción al Desarrollo Mobile Android

## Alumna
María Emilia Tunesi 
41.146.697
TUDAI -UNICEN - Sede Tandil

## Descripción

* Este es un proyecto de aplicación nativa para Android que funciona como un explorador de videojuegos. 
* Permite ver una lista de video juegos, filtrar y ver detalles sobre los diferentes juegos. 
* Toda la información es consumida en tiempo real desde la API pública indicada por la cátedra. 


## Características Principales

* **Lista de Juegos**: Navegación por catálogo de videojuegos con paginación automática.
* **Filtros y Ordenamiento**: Búsqueda por plataformas, géneros, distribuidores y tiendas. Orden de los resultados por nombre, fecha de lanzamiento, antiguedad, entre otros. 
* **Detalle Completo**: Haciendo tap en cualquier juego se puede ver su información detallada.
* **Interfaz**: La UI siempre muestra estados de **carga** y **error** (con opción de reintento).
* **Arquitectura**: Desarrollado con una arquitectura multi-módulo.

### Estructura de Módulos

El proyecto está organizado de la siguiente manera:

* `:app`: El módulo principal y punto de entrada. Integra todas las funcionalidades.
* `:core`: Módulos con lógica y recursos compartidos.
    * `:core:data`: Capa de datos.
    * `:core:ui`: Recursos de UI compartidos (temas, colores, estilos).
    * `:core:navigation`: Contratos de navegación entre módulos.
* `:feature`: Módulos que encapsulan cada funcionalidad de la aplicación.
    * `:feature:gamelist`: Pantalla principal con la lista de juegos.
    * `:feature:filters`: Pantalla de filtros.
    * `:feature:gamedetail`: Pantalla de detalle de un juego.

### Patrones de Diseño

Dentro de cada módulo de funcionalidad se aplica el patrón **MVVM** junto con el **Patrón Repository**.

## Ejecución del Proyecto

Para compilar y ejecutar el proyecto, seguí estos pasos:

### Prerrequisitos

* Versión "Iguana" o superior de Android Studio.

### Pasos para Ejecutar el Proyecto

1. Clonar el Repositorio desde Git

2. Abrir y Sincronizar el Proyecto

3. Configurar un Emulador (Pixel 4 - API 34)

4. Ejecutar la Aplicación


