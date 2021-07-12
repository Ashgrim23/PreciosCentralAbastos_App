package net.centralabastos.ashgrim

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.centralabastos.ashgrim.databinding.FragmentHomeBinding

import net.centralabastos.ashgrim.datos.Precio
import net.centralabastos.ashgrim.ui.FragmentAdapter


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding?=null
    private val binding get() =_binding!!


    private val preciosViewModel: PreciosModelo by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FragmentAdapter( requireActivity())
        binding.lifecycleOwner = this

        preciosViewModel.ldMaxFecha.observe(viewLifecycleOwner, {
            it?.let {
                val date = Utils.formato.parse(it)
                val dia = DateFormat.format("dd", date)
                val mes =
                    DateFormat.format("MMMM", date).toString().replaceFirstChar { it.uppercase() }
                val anio = DateFormat.format("yyyy", date)

                binding.txtFecha.text =
                    String.format(getString(R.string.string_preciosFecha), dia, mes, anio)
            }
        })
        binding.viewmodel = preciosViewModel

        binding.fab.setOnClickListener {
            shareClick(it)
        }
        binding.fab.visibility=View.VISIBLE
        binding.pager.adapter = adapter


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    clearTabColor(tab, R.color.normal)

                    when (tab.position) {
                        0 -> setTabColor(tab, R.color.primaryColor)
                        1 -> setTabColor(tab, R.color.abarrotes)
                        2 -> setTabColor(tab, R.color.carnes)
                        3 -> setTabColor(tab, R.color.verduras)
                        else -> setTabColor(tab, R.color.frutas)

                    }
                }
            }

            private fun clearTabColor(tab: TabLayout.Tab?, color: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val filter = BlendModeColorFilter(getColor(requireContext(),color) , BlendMode.SRC_ATOP)
                    tab?.icon?.colorFilter = filter
                } else {
                    tab?.icon?.setColorFilter(
                        getColor(requireContext(), color),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            }

            private fun setTabColor(tab: TabLayout.Tab?, color: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val filter = BlendModeColorFilter(getColor(requireContext(),color), BlendMode.SRC_ATOP)
                    tab?.icon?.colorFilter = filter
                } else {
                    tab?.icon?.setColorFilter(
                        getColor(requireContext(), color),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let { clearTabColor(tab, R.color.normal) }

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                val toolbar = activity?.findViewById<MaterialToolbar>(R.id.my_toolbar)
                when (position) {
                    0-> toolbar?.title=getString(R.string.string_fragment_todosProds)
                    1->toolbar?.title=getString(R.string.string_fragment_abarrotes)
                    2->toolbar?.title=getString(R.string.string_fragment_carnicos)
                    3->toolbar?.title=getString(R.string.string_fragment_verduras)
                    else->toolbar?.title=getString(R.string.string_fragment_frutas)
                }
            }
        })



        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_list)
                1 -> tab.setIcon(R.drawable.ic_abarrotes)
                2 -> tab.setIcon(R.drawable.ic_carnicos)
                3 -> tab.setIcon(R.drawable.ic_verduras)
                else -> tab.setIcon(R.drawable.ic_frutas)
            }
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
       // binding.pager.adapter=null
        _binding=null
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false )


        return binding.root
    }


    private fun shareClick(view: View) {

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
        }
        val chooser: Intent = Intent.createChooser(intent, "Compartir precios con")
        binding.tabLayout.selectedTabPosition.let {
            when (it) {
                0 -> preciosViewModel.ldPrecios.observe(viewLifecycleOwner) { precio ->
                    intent.putExtra(Intent.EXTRA_TEXT, getListString(precio, it))
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(chooser)
                    }
                }
                1 -> preciosViewModel.ldAbarrotes.observe(viewLifecycleOwner) { precio ->
                    intent.putExtra(Intent.EXTRA_TEXT, getListString(precio, it))
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(chooser)
                    }
                }
                2 -> preciosViewModel.ldCarnicos.observe(viewLifecycleOwner) { precio ->
                    intent.putExtra(Intent.EXTRA_TEXT, getListString(precio, it))
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(chooser)
                    }
                }
                3 -> preciosViewModel.ldVerduras.observe(viewLifecycleOwner) { precio ->
                    intent.putExtra(Intent.EXTRA_TEXT, getListString(precio, it))
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(chooser)
                    }
                }
                else -> preciosViewModel.ldFrutas.observe(viewLifecycleOwner) { precio ->
                    intent.putExtra(Intent.EXTRA_TEXT, getListString(precio, it))
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(chooser)
                    }
                }
            }
        }

    }

    private fun getListString(lista: List<Precio>, tab: Int): String {
        val strBuilder = StringBuilder()

        when (tab) {
            1 -> strBuilder.appendLine(getString(R.string.string_share_abarrotes_titulo))
            2 -> strBuilder.appendLine(getString(R.string.string_share_carnicos_titulo))
            3 -> strBuilder.appendLine(getString(R.string.string_share_verduras_titulo))
            4 -> strBuilder.appendLine(getString(R.string.string_share_frutas_titulo))
            else -> strBuilder.appendLine(getString(R.string.string_share_todo_titulo))

        }
        strBuilder.appendLine(binding.txtFecha.text.toString())
        strBuilder.appendLine("")
        lista.forEach {
            strBuilder.append(it.descripcion).append("\t")
                .append(String.format(getString(R.string.string_precio_card), it.precio.toInt()))
                .append(" ")
                .append(String.format(getString(R.string.string_precio_unidad), it.unidad))
                .append("\n")
        }
        return strBuilder.toString()
    }

}