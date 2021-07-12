package net.centralabastos.ashgrim

import android.app.Application
import android.util.Log

import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.centralabastos.ashgrim.api.PreciosApi
import net.centralabastos.ashgrim.datos.Precio
import net.centralabastos.ashgrim.datos.SerieBody
import org.json.JSONArray
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*


class PreciosModelo(app: PreciosApplication) : AndroidViewModel (app) {

    private val _status=MutableLiveData(Utils.PreciosApiStatus.DONE)
    val status:LiveData<Utils.PreciosApiStatus> = _status

    private val _filtroGrafica= MutableLiveData<Utils.FiltrosGrafica>(null)
    val ldFiltroGrafica:LiveData<Utils.FiltrosGrafica> = _filtroGrafica

    val ldMaxFecha:LiveData<String> = app.repositorio.maxFecha.asLiveData()

    val ldPrecios: LiveData<List<Precio>> =app.repositorio.allPrecios.asLiveData()
    val ldAbarrotes: LiveData<List<Precio>> = app.repositorio.preciosAbarrotes.asLiveData()
    val ldCarnicos: LiveData<List<Precio>> = app.repositorio.preciosCarnicos.asLiveData()
    val ldFrutas: LiveData<List<Precio>> = app.repositorio.preciosFrutas.asLiveData()
    val ldVerduras: LiveData<List<Precio>> = app.repositorio.preciosVerduras.asLiveData()

    private val serieHistorica = arrayListOf<Entry>()

    private val _serieFiltrada=MutableLiveData<LineData>()
    val ldSerieFiltrada:LiveData<LineData> =_serieFiltrada


    init {
        viewModelScope.launch{
            _status.value= Utils.PreciosApiStatus.LOADING
            val fecha=app.repositorio.maxFecha.first()

            if (fecha.isNullOrEmpty()) {
                loadPrecios()
            }
            else
                _status.value= Utils.PreciosApiStatus.DONE

        }
    }

    fun loadHistorico(idProducto: Int) = viewModelScope.launch {
        _status.value= Utils.PreciosApiStatus.LOADING
        serieHistorica.clear()

        try {
            val fecha= getApplication<PreciosApplication>().repositorio.maxFecha.first()

            val serie=PreciosApi.retrofitServicio.getSerie(SerieBody(fecha,idProducto))


            val arrDatos=JSONArray(serie[0].SERIE)

            for (i in  0 until arrDatos.length()){
                val item=arrDatos.getJSONObject(i)

                serieHistorica.add(Entry(Utils.formatoGrafica.parse(item.getString("x")).time.toFloat(),item.getString("y").toFloat() ))

            }

            filtraSerieGrafica(Utils.FiltrosGrafica.DIAS_10)


        } catch (e:Exception) {
            _status.value= Utils.PreciosApiStatus.ERROR
            Log.i("daff",e.stackTraceToString())
        } finally {
            _status.value=Utils.PreciosApiStatus.DONE
        }
    }

    fun filtraSerieGrafica(filtro: Utils.FiltrosGrafica) = viewModelScope.launch {

        if (_status.value!=Utils.PreciosApiStatus.LOADING)
            _status.value= Utils.PreciosApiStatus.LOADING


        val  dataset:LineDataSet
        if (filtro== Utils.FiltrosGrafica.TODO)
            dataset=LineDataSet(serieHistorica,"Precios" )
        else {
                val fecha= getApplication<PreciosApplication>().repositorio.maxFecha.first()
                val arrLen=serieHistorica.size
                val calendar= Calendar.getInstance()
                val formateador =  Utils.formatoApi
                formateador.timeZone= TimeZone.getTimeZone("UTC")

                calendar.time=formateador.parse(fecha)
                when (filtro) {
                    Utils.FiltrosGrafica.DIAS_10->calendar.add(Calendar.DAY_OF_YEAR,-10)
                    Utils.FiltrosGrafica.MES_1->calendar.add(Calendar.MONTH,-1)
                    Utils.FiltrosGrafica.MES_6->calendar.add(Calendar.MONTH,-6)
                    else -> Log.i("filtro",filtro.toString())
                }

                 val serieFiltrada=arrayListOf<Entry>()
                for (i in arrLen-1 downTo 0 ) {
                    val item= serieHistorica[i]
                    if (item.x >= calendar.timeInMillis )
                        serieFiltrada.add(0,item)
                    else
                        break
                }
                dataset= LineDataSet(serieFiltrada,"Precios" )

        }

        dataset.mode=LineDataSet.Mode.HORIZONTAL_BEZIER
        dataset.setDrawFilled(true)
        dataset.setDrawCircles(false)
        dataset.lineWidth=5F
        dataset.fillColor= getApplication<Application>().resources.getColor(R.color.secondaryColor)
        dataset.color=getApplication<Application>().resources.getColor(R.color.secondaryColor)
        dataset.setDrawValues(false)
        _serieFiltrada.value=LineData(dataset)
        _filtroGrafica.value=filtro
        _status.value= Utils.PreciosApiStatus.DONE
    }

    fun loadPrecios() = viewModelScope.launch{
        _status.value= Utils.PreciosApiStatus.LOADING
        try {
             getApplication<PreciosApplication>().repositorio.loadPrecios()
        } catch (e:Exception) {
            Log.e("daff",e.stackTraceToString())
            _status.value= Utils.PreciosApiStatus.ERROR
        } finally {
            val fecha= getApplication<PreciosApplication>().repositorio.maxFecha.first()

            if (fecha!=null)
                _status.value=Utils.PreciosApiStatus.DONE
            else
                _status.value= Utils.PreciosApiStatus.EMPTY
        }

    }
}

class PreciosModeloFactory( private val app: PreciosApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(PreciosModelo::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreciosModelo( app) as T
        }
        throw IllegalArgumentException("Clase de ViewModel invalida")
    }

}