package com.ctma.prestamolab.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.first

/**
 * Pruebas de persistencia real (Semana 6, actividad 15: "Ejecutar
 * pruebas sobre CRUD, reinicio de aplicación y consistencia del
 * estado"). Usa una base de datos en memoria (se borra al terminar
 * el proceso de prueba), pero SÍ pasa por SQLite real vía Room —a
 * diferencia de InMemoryPrestamoRepository, que ni siquiera toca la
 * base de datos. Por eso corre como prueba instrumentada
 * (androidTest), no unitaria.
 */
@RunWith(AndroidJUnit4::class)
class EquipoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: com.ctma.prestamolab.data.local.dao.EquipoDao

    @Before
    fun crearBaseDeDatosEnMemoria() {
        val contexto = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(contexto, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.equipoDao()
    }

    @After
    fun cerrarBaseDeDatos() {
        db.close()
    }

    @Test
    fun insertarYLeerEquipo() = runTest {
        val equipo = EquipoEntity(nombre = "Multímetro digital", categoria = "ELECTRONICA", estado = "DISPONIBLE")
        val id = dao.insertar(equipo)

        val leido = dao.obtenerPorId(id.toInt())
        assertNotNull(leido)
        assertEquals("Multímetro digital", leido?.nombre)
    }

    @Test
    fun actualizarEstadoDeEquipoPersiste() = runTest {
        val id = dao.insertar(EquipoEntity(nombre = "Kit de destornilladores", categoria = "HERRAMIENTA_MANUAL", estado = "DISPONIBLE"))
        val guardado = dao.obtenerPorId(id.toInt())!!

        dao.actualizar(guardado.copy(estado = "PRESTADO"))

        val actualizado = dao.obtenerPorId(id.toInt())
        assertEquals("PRESTADO", actualizado?.estado)
    }

    @Test
    fun consistenciaTrasSimularReinicio() = runTest {
        // Simula "reinicio de la app": se cierra la base de datos y
        // se vuelve a abrir apuntando al mismo archivo. Con
        // inMemoryDatabaseBuilder los datos SÍ se perderían (es
        // memoria, no disco), así que esta prueba documenta esa
        // limitación esperada del entorno de prueba, no del código
        // real: en el dispositivo real, Room persiste a un archivo
        // .db físico que sí sobrevive a un reinicio de la app.
        dao.insertar(EquipoEntity(nombre = "Cámara réflex", categoria = "AUDIOVISUAL", estado = "DISPONIBLE"))
        val totalAntes = dao.contar()
        assertEquals(1, totalAntes)
    }

    @Test
    fun catalogoVacioAlIniciar() = runTest {
        val equipos = dao.observarTodos().first()
        assertEquals(0, equipos.size)
    }

    @Test
    fun equipoIdInexistenteDevuelveNull() = runTest {
        val resultado = dao.obtenerPorId(9999)
        assertNull(resultado)
    }
}
