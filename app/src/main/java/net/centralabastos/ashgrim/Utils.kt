package net.centralabastos.ashgrim


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    enum class PreciosApiStatus {LOADING,ERROR,DONE,EMPTY}
    enum class FiltrosGrafica {DIAS_10,MES_1,MES_6,TODO}
    val formatoGrafica: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    val formatoGraficaLabelDiario: SimpleDateFormat = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)
    val formatoGraficaLabelMensual: SimpleDateFormat = SimpleDateFormat("MMM yy", Locale.ENGLISH)
    val formatoGraficaLabelAnual: SimpleDateFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)
    val formato: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val formatoApi: SimpleDateFormat =SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun dateFormat(fecha:String):String {
       return  formatoApi.format(  formato.parse(fecha))
    }

    fun isConnected(context: Context) : Boolean {
        var result=false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {

                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result=true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result=true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        result=true
                    }
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result=when (type) {
                            ConnectivityManager.TYPE_WIFI->true
                            ConnectivityManager.TYPE_MOBILE->true
                            ConnectivityManager.TYPE_ETHERNET->true
                            else ->false
                        }
                    }
                }
            }


        }
        return result
    }


}
