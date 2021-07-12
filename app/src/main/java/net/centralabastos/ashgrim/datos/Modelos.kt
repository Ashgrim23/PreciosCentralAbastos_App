package net.centralabastos.ashgrim.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json



@Entity(tableName="precios")
data class Precio(
    @PrimaryKey
    @Json(name="ID_PRODUCTO")
    val idProducto: Int,
    @Json(name="FECHA")
    val fecha: String,
    @Json(name="CATEGORIA")
    val categoria: String,
    @Json(name="DESCRIPCION")
    val descripcion: String,
    @Json(name="PRECIO")
    val precio :Double,
    @Json(name="UNIDAD")
    val unidad: String,
    @Json(name="PRECIOANT")
    val precioAnt: Double,
    @Json(name="FECHAANT")
    val fechaAnt: String,
)


data class SerieBody(
    @Json(name = "fecha") val fecha:String,
    @Json(name = "id_producto") val idProducto:Int
)