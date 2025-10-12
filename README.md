# Explorador de Videojuegos - App Android

Una aplicación Android multi-módulo para explorar videojuegos usando la API de RAWG.IO.

## 🎮 **Características**

- **Arquitectura Multi-Módulo** con separación clara de responsabilidades
- **Lista paginada** de videojuegos con Paging 3
- **Sistema de filtros** por plataformas, géneros, publishers y tiendas
- **Detalles completos** de cada videojuego
- **MVVM + Repository Pattern** en todos los módulos
- **Inyección de dependencias** con Hilt
- **Navigation Component** para navegación fluida

## 🏗️ **Arquitectura**

### Módulos Core:
- `:core:ui` - Recursos compartidos (colores, strings, temas)
- `:core:data` - Capa de datos (API, repositorios, mappers)
- `:core:navigation` - Contratos de navegación

### Módulos Feature:
- `:feature:gamelist` - Lista principal con paginación
- `:feature:filters` - Pantalla de filtros múltiples
- `:feature:gamedetail` - Detalles específicos de juegos

## 🛠️ **Stack Tecnológico**

- **Kotlin** con programación orientada a objetos
- **Hilt** para inyección de dependencias
- **Retrofit + Gson** para comunicación con API
- **Paging 3** para listas infinitas
- **Navigation Component** con Safe Args
- **Coil** para carga de imágenes
- **ViewBinding** para UI type-safe
- **Coroutines + StateFlow** para asincronía

## 🚀 **Configuración**

1. Clona el repositorio
2. Configura tu API Key de RAWG.IO en `local.properties`:
   ```
   RAWG_API_KEY=tu_api_key_aqui
   ```
3. Sincroniza el proyecto con Gradle
4. Ejecuta en emulador Pixel 4 con Android 13.0 (API 33)

## 📱 **Requisitos**

- **minSdk:** 26 (Android 8.0)
- **compileSdk:** 36
- **targetSdk:** 36

## 🎯 **API**

Integrado con [RAWG.IO API](https://rawg.io/apidocs) para datos de videojuegos.

## 📄 **Licencia**

Proyecto académico - TUDAI UNICEN
