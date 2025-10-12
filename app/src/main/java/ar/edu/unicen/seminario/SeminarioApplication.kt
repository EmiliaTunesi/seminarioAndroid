package ar.edu.unicen.seminario

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal que inicializa Hilt para la inyección de dependencias
 */
@HiltAndroidApp
class SeminarioApplication : Application()
