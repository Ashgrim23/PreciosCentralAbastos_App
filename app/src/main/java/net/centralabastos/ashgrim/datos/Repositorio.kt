package net.centralabastos.ashgrim.datos

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import net.centralabastos.ashgrim.Utils
import net.centralabastos.ashgrim.api.PreciosApi

class Repositorio(private val preciosDao:PrecioDao) {
    val maxFecha:Flow<String> =preciosDao.getMaxFecha()
    val allPrecios: Flow<List<Precio>> =preciosDao.getPrecios()
    val preciosAbarrotes: Flow<List<Precio>> =preciosDao.getAbarrotes()
    val preciosVerduras: Flow<List<Precio>> =preciosDao.getVerduras()
    val preciosFrutas: Flow<List<Precio>> = preciosDao.getFrutas()
    val preciosCarnicos: Flow<List<Precio>> = preciosDao.getCarnicos()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun loadPrecios():Boolean{

        val tmpDbFecha=maxFecha.first()
        val serverMaxFecha = Utils.dateFormat(PreciosApi.retrofitServicio.getMaxFecha().maxfecha)
        if (tmpDbFecha==null || Utils.dateFormat(tmpDbFecha) !=serverMaxFecha) {
            val res= PreciosApi.retrofitServicio.getPrecios(serverMaxFecha)
            if (res.exito){
                preciosDao.deleteAll()
                preciosDao.insertPrecios(res.rows)
                return true
            }
        }
        return false

    }
}