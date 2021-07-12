package net.centralabastos.ashgrim.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.ads.AdRequest
import net.centralabastos.ashgrim.HomeFragmentDirections
import net.centralabastos.ashgrim.PreciosModelo
import net.centralabastos.ashgrim.R
import net.centralabastos.ashgrim.databinding.PaginaBinding
import net.centralabastos.ashgrim.datos.Precio


class FragmentAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment){


    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        val fragment=ObjectFragment()
        fragment.arguments=Bundle().apply {
            putInt(ARG_OBJECT,position+1)
        }

        return fragment
    }
}


private const val ARG_OBJECT = "OBJECT"


// Fragmento unico modelo:PreciosModelo
class ObjectFragment: Fragment(),PreciosAdapter.OnItemClickListener {
    private val preciosViewModel: PreciosModelo by activityViewModels()


    private var _binding: PaginaBinding?=null
    private val binding get() =_binding!!

    private val adapter:PreciosAdapter  by lazy {PreciosAdapter(this)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //return inflater.inflate(R.layout.pagina,container,false)
        _binding=DataBindingUtil.inflate(inflater,R.layout.pagina,container,false )
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            when (getInt(ARG_OBJECT)){
                1-> preciosViewModel.ldPrecios.observe(viewLifecycleOwner) { precios->
                    precios.let {
                        adapter.submitList(it) }
                }
                2->  preciosViewModel.ldAbarrotes.observe(viewLifecycleOwner) { precios->
                        precios.let{adapter.submitList(it) }
                    }

                3->  preciosViewModel.ldCarnicos.observe(viewLifecycleOwner) { precios->
                        precios.let{adapter.submitList(it) }
                    }

                4->
                    preciosViewModel.ldVerduras.observe(viewLifecycleOwner) { precios->
                        precios.let{adapter.submitList(it) }
                    }

                else->preciosViewModel.ldFrutas.observe(viewLifecycleOwner) { precios->
                    precios.let{adapter.submitList(it) }
                }


            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.adView.destroy()
        binding.recyclerView.adapter=null
        _binding=null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=GridLayoutManager(activity,resources.getInteger(R.integer.grid_column_count))


        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {


            when (getInt(ARG_OBJECT)){
                1-> binding.divider.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.primaryColor ))
                2-> binding.divider.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.abarrotes ) )
                3-> binding.divider.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.carnes ) )
                4-> binding.divider.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.verduras ) )
                else->binding.divider.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.frutas) )
            }

        }
    }

    override fun onItemClick(current: Precio) {
        preciosViewModel.loadHistorico(current.idProducto)

        val action= HomeFragmentDirections.actionNavHomeFragmentToGraficaFragment(current.idProducto,current.descripcion,
            current.precio.toFloat(),current.unidad,current.fecha)
        Navigation.findNavController(requireActivity(),R.id.fragment_container).navigate(action)


    }
}