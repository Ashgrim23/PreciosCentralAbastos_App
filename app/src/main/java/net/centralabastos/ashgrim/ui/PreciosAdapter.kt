package net.centralabastos.ashgrim.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.centralabastos.ashgrim.R
import net.centralabastos.ashgrim.datos.Precio

class PreciosAdapter(private val listener:OnItemClickListener) : ListAdapter<Precio, PreciosAdapter.PrecioViewHolder>(PreciosComparador()) {

    interface OnItemClickListener {
       fun onItemClick(current:Precio)
    }

    inner class PrecioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener {

        private val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        private val txtPrecio:TextView=itemView.findViewById(R.id.txtPrecio)
        private val txtPrecioAnt: TextView =itemView.findViewById(R.id.txtPrecioAnt)
        private val txtPrecioAntVar: TextView=itemView.findViewById(R.id.txtPrecioAntVar)
        private val txtUnidad : TextView=itemView.findViewById(R.id.txtUnidad)
        private val imgView:ImageView=itemView.findViewById(R.id.imgView)
        private val imgTrend:ImageView=itemView.findViewById(R.id.imgTrendArrow)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val current =getItem(adapterPosition)
            listener.onItemClick(current)
        }
        fun bind(precio:Precio?) {

            val context=itemView.context

            txtDescripcion.text=precio?.descripcion

            txtPrecio.text= String.format(context.getString(R.string.string_precio_card) ,
                precio?.precio?.toInt() )

            txtUnidad.text=String.format(context.getString(R.string.string_precio_unidad), precio?.unidad)


            val imagen=context.resources.getIdentifier("ic_${precio?.idProducto}","drawable",context.packageName)
            imgView.setImageResource( imagen)

            val varPrecio =(precio?.precio!! - precio.precioAnt)
            val varPrecioPorc =(((precio.precio.div(precio.precioAnt)-1) )*100)

            var resPorc=R.string.string_var_porc
            var resVar=R.string.string_var_monto
            txtPrecioAntVar.visibility=View.VISIBLE
            txtPrecioAnt.visibility=View.VISIBLE
            imgTrend.visibility=View.VISIBLE
            when {
                varPrecio==0.0 -> {
                    txtPrecioAntVar.visibility=View.GONE
                    txtPrecioAnt.visibility=View.GONE
                    imgTrend.visibility=View.GONE

                }
                varPrecio>0 -> {
                    resVar=R.string.string_var_montoPositivo
                    resPorc=R.string.string_var_porcPositivo
                    setTxtViewStyle(txtPrecioAnt,R.style.varPositivo)
                    setTxtViewStyle(txtPrecioAntVar,R.style.varPositivo)
                    imgTrend.setImageResource(R.drawable.ic_up_trend )


                }
                varPrecio<0 -> {
                    setTxtViewStyle(txtPrecioAnt,R.style.varNegativo)
                    setTxtViewStyle(txtPrecioAntVar,R.style.varNegativo)
                    imgTrend.setImageResource(R.drawable.ic_down_trend)
                }
            }
            txtPrecioAnt.text=String.format(context.getString(resVar),varPrecio.toInt() )
            txtPrecioAntVar.text=String.format(context.getString(resPorc),varPrecioPorc)
        }

        private fun setTxtViewStyle(view:TextView,resID:Int ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                view.setTextAppearance(itemView.context,resID )
            else
                view.setTextAppearance(resID)
        }




    }

    class PreciosComparador: DiffUtil.ItemCallback<Precio>(){
        override fun areItemsTheSame(oldItem: Precio, newItem: Precio): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Precio, newItem: Precio): Boolean {
            return oldItem.idProducto==newItem.idProducto
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrecioViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_precio,parent,false)
        return PrecioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrecioViewHolder, position: Int) {

        val current=getItem( holder.adapterPosition)
        holder.bind(current)



    }




}