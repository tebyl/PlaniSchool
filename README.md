# 📚 PlaniSchool

Aplicación Android para estudiantes (8–14 años) que ayuda a organizar evaluaciones y planificar el estudio de forma inteligente.

## ✨ Características

- 📅 Calendario de evaluaciones
- 🧠 Planificación automática de estudio
- 🎯 Prioridades por materia (configurables)
- 📊 Dashboard de progreso
- 🔥 Indicador de evaluaciones urgentes
- ✅ Gestión de tareas completadas

## 🚀 ¿Cómo funciona?

1. Agregas una evaluación.
2. Configuras cuántos días necesitas estudiar por materia.
3. PlaniSchool genera automáticamente tu plan de estudio.
4. Ves todo en tu calendario.

## 🖼️ Capturas

Agrega aquí tus imágenes:

- Onboarding
- Pantalla Calendario
- Pantalla Prioridades
- Pantalla Materias
- Modal Nueva Evaluación

## 🛠️ Tecnologías

- Kotlin
- Android XML UI
- ViewPager2
- Material Components
- Gson

## 📱 Onboarding

La app incluye onboarding visual de 4 pantallas con swipe:

1. 📚 Bienvenido a PlaniSchool  
2. 🧠 Planifica sin estrés  
3. 🎯 Define tus prioridades  
4. 🚀 ¡Todo listo!

Incluye:

- Indicadores de página (dots)
- Botón `Saltar`
- Persistencia de estado (se muestra solo la primera vez)

## 🧩 Arquitectura (resumen)

- `MainActivity`: contenedor principal con tabs y navegación.
- `CalendarFragment`: calendario mensual + detalles por día.
- `PriorityFragment`: configuración de días de estudio por materia.
- `SubjectsFragment`: dashboard y listado de evaluaciones.
- `DataManager`: persistencia local con `SharedPreferences` + `Gson`.

## ⚙️ Requisitos

- Android Studio (reciente)
- JDK 11+
- SDK Android 34
- minSdk 21

## 🔧 Ejecutar en local

```bash
./gradlew assembleDebug
```

APK debug:

`android/build/outputs/apk/debug/app-debug.apk`

## 🤝 Contribuir

Si quieres proponer mejoras:

1. Haz un fork.
2. Crea una rama (`feature/mi-mejora`).
3. Abre un Pull Request con descripción clara y capturas.

## 📄 Licencia

Define aquí la licencia del proyecto (por ejemplo, MIT).
