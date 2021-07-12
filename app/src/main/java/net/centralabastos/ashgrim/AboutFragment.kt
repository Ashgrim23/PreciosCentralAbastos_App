package net.centralabastos.ashgrim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar


class AboutFragment : Fragment() {

        override fun onDestroy() {
        super.onDestroy()
        val toolbar= activity?.findViewById<MaterialToolbar>(R.id.my_toolbar)
        toolbar?.inflateMenu(R.menu.menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar= activity?.findViewById<MaterialToolbar>(R.id.my_toolbar)
        toolbar?.menu?.clear()

        val txtAbout=view.findViewById<TextView>(R.id.txt_version)
        txtAbout.text= String.format(getString(R.string.string_about_version) ,BuildConfig.VERSION_NAME )
    }
}