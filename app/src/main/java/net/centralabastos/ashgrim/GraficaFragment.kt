package net.centralabastos.ashgrim

import android.app.Activity
import android.content.Context

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.first
import net.centralabastos.ashgrim.Utils.isConnected
import net.centralabastos.ashgrim.databinding.FragmentGraficaBinding
import java.lang.Exception
import java.util.*


class GraficaFragment : Fragment() {
    private lateinit var binding: FragmentGraficaBinding

    private val args: GraficaFragmentArgs by navArgs()
    private val preciosViewModel: PreciosModelo by activityViewModels()


    override fun onDestroy() {
        super.onDestroy()
        val toolbar= activity?.findViewById<MaterialToolbar>(R.id.my_toolbar)
        toolbar?.inflateMenu(R.menu.menu)
        if (this::binding.isInitialized)
            binding.adView.destroy()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar= activity?.findViewById<MaterialToolbar>(R.id.my_toolbar)
        toolbar?.menu?.clear()
        if (!isConnected(requireContext() )) return

        val adRequest = AdRequest.Builder().build()

        binding.adView.loadAd(adRequest)

        binding.txtGraficaDescripcion.text=args.descripcion
        binding.txtGraficaPrecio.text=String.format(getString(R.string.string_precio_card) ,args.precio.toInt() )
        binding.txtGraficaUnidad.text=String.format(getString(R.string.string_precio_unidad),args.unidad)

        val calendar = Calendar.getInstance()
        calendar.time=Utils.formato.parse(args.fecha)

        binding.txtGraficaPeriodo.text=String.format(getString(R.string.string_grafica_periodo,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault() ),
            calendar.get(Calendar.YEAR)
        ))


        binding.btnGrafica10dias.setOnClickListener {
            preciosViewModel.filtraSerieGrafica(Utils.FiltrosGrafica.DIAS_10)
        }
        binding.btnGrafica1mes.setOnClickListener {
            preciosViewModel.filtraSerieGrafica(Utils.FiltrosGrafica.MES_1)
        }
        binding.btnGrafica6meses.setOnClickListener {
            preciosViewModel.filtraSerieGrafica(Utils.FiltrosGrafica.MES_6)
        }
        binding.btnGraficaTodo.setOnClickListener {
            preciosViewModel.filtraSerieGrafica(Utils.FiltrosGrafica.TODO)
        }





    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!isConnected(requireContext())){
            return inflater.inflate(R.layout.sinred, container, false)
        }

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_grafica,container,false )

        preciosViewModel.ldFiltroGrafica.observe(viewLifecycleOwner) {
            it?.let {
                binding.chart.xAxis.valueFormatter =FechasFormateador(it)
                when (it) {
                    Utils.FiltrosGrafica.DIAS_10->binding.chart.xAxis.labelCount = 4
                    Utils.FiltrosGrafica.MES_1-> binding.chart.xAxis.labelCount = 5
                    else->
                        binding.chart.xAxis.labelCount = 6
                }
            }
        }

        preciosViewModel.ldSerieFiltrada.observe(viewLifecycleOwner) {
            it?.let {
                binding.chart.apply {
                    data=it
                    invalidate()
                }
            }
        }

        binding.chart.apply {
            setScaleEnabled(false)
            legend.isEnabled=false
            description.isEnabled=false



            xAxis.setDrawGridLines(false)
            xAxis.textSize=11f
            setDrawGridBackground(false)
            axisRight.isEnabled=false
            setDrawMarkers(true)
            setTouchEnabled(true)
            markerView = CustomMarkerView(activity,R.layout.chart_marker)
        }



        return binding.root
    }


    inner class CustomMarkerView(context: Context?, layoutResource: Int) :
        MarkerView(context, layoutResource) {


        override fun getOffset(): MPPointF {
            return MPPointF(-(width.toFloat())/2,-height.toFloat()/4)
        }

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val txtPrecio= findViewById<TextView>(R.id.txtMarkerPrecio)
            txtPrecio.text=String.format(getString(R.string.string_grafica_marker_precio,e?.y?.toInt().toString()))

            val txtFecha= findViewById<TextView>(R.id.txtMarkerFecha)
            txtFecha.text=String.format(getString(R.string.string_grafica_marker_fecha,Utils.formatoGraficaLabelDiario.format(e?.x)))

            super.refreshContent(e, highlight)
        }
    }

    inner class FechasFormateador(private val filtro:Utils.FiltrosGrafica) : ValueFormatter() {


        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {

            when (filtro) {
                Utils.FiltrosGrafica.TODO-> {
                    val formateador=Utils.formatoGraficaLabelAnual
                    formateador.timeZone= TimeZone.getTimeZone("UTC")
                    return formateador.format(value)
                }
                Utils.FiltrosGrafica.MES_6->{
                    val formateador=Utils.formatoGraficaLabelMensual
                    formateador.timeZone= TimeZone.getTimeZone("UTC")
                    return formateador.format(value)
                }
                else-> {
                    val formateador=Utils.formatoGraficaLabelDiario
                    formateador.timeZone= TimeZone.getTimeZone("UTC")
                    return formateador.format(value)
                }


            }

        }


    }
}



