package net.centralabastos.ashgrim.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PrecioDao {

    @Query("select * from precios order by descripcion asc")
    fun getPrecios(): Flow<List<Precio>>

    @Query("select max(fecha) from precios")
    fun getMaxFecha(): Flow<String>

    @Insert
    suspend fun insertPrecios(precios:List<Precio>)

    @Insert
    suspend fun insertPrecio(precio:Precio)

    @Query("delete from precios")
    suspend fun deleteAll()

    @Update
    suspend fun updatePrecios(precios:List<Precio>)

    @Query("select * from precios where upper(categoria)='ABARROTES' order by descripcion asc " )
    fun getAbarrotes(): Flow<List<Precio>>

    @Query("select * from precios where upper(categoria)='FRUTAS' order by descripcion asc " )
    fun getFrutas(): Flow<List<Precio>>

    @Query("select * from precios where upper(categoria)='VERDURAS Y LEGUMBRES'  order by descripcion asc" )
    fun getVerduras(): Flow<List<Precio>>

    @Query("select * from precios where upper(categoria)='CÁRNICOS' order by descripcion asc" )
    fun getCarnicos(): Flow<List<Precio>>
}