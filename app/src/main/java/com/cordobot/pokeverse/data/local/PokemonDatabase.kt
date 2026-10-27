package com.cordobot.pokeverse.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    // Type API names in slot order, comma separated: "grass,poison"
    val types: String
)

/** Detail already downloaded, stored as JSON so it can be shown offline. */
@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val json: String,
    val cachedAt: Long
)

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon ORDER BY id")
    fun observeAll(): Flow<List<PokemonEntity>>

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun count(): Int

    @Query("DELETE FROM pokemon")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonEntity>)

    @Transaction
    suspend fun replaceAll(pokemon: List<PokemonEntity>) {
        clear()
        insertAll(pokemon)
    }

    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    suspend fun getDetail(id: Int): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: PokemonDetailEntity)
}

@Database(entities = [PokemonEntity::class, PokemonDetailEntity::class], version = 1, exportSchema = true)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
