
package com.example.recordatorios_karinameza.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RecordatorioEntity::class],
    version = 4,
    exportSchema = false
)
abstract class RecordatoriosDatabase : RoomDatabase() {

    abstract fun recordatorioDao(): RecordatorioDao

    companion object {

        @Volatile
        private var INSTANCE: RecordatoriosDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE recordatorios ADD COLUMN fechaCreacion INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE recordatorios ADD COLUMN fechaActualizacion INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE recordatorios ADD COLUMN identificadorRemoto TEXT"
                )
                db.execSQL(
                    "ALTER TABLE recordatorios ADD COLUMN pendienteSincronizacion INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE recordatorios ADD COLUMN identificadorCliente TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE recordatorios ADD COLUMN eliminadoPendienteSincronizacion INTEGER NOT NULL DEFAULT 0"
                )

                db.execSQL(
                    """
                    UPDATE recordatorios
                    SET identificadorCliente = 'registro-' || id
                    WHERE identificadorCliente = ''
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX index_recordatorios_identificadorCliente
                    ON recordatorios(identificadorCliente)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE recordatorios
                    ADD COLUMN propietarioUid TEXT
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX index_recordatorios_propietarioUid_eliminadoPendienteSincronizacion
                    ON recordatorios(
                        propietarioUid,
                        eliminadoPendienteSincronizacion
                    )
                    """.trimIndent()
                )
            }
        }

        fun obtenerInstancia(
            context: Context
        ): RecordatoriosDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RecordatoriosDatabase::class.java,
                    "recordatorios_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
