package net.centralabastos.ashgrim.api

import net.centralabastos.ashgrim.datos.Precio


data class PreciosResponse (
    val exito: Boolean,
    val rows: List<Precio>
)

data class MaxFechaResponse (
    val maxfecha: String
)

data class SerieResponse (
    val SERIE: String
)

