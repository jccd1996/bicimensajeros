package com.example.bicimensajeros.Controladores.Adaptadores

import android.graphics.Color
import com.example.bicimensajeros.Controladores.Modelos.Bicimensajero
import com.example.bicimensajeros.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.template_lista_mensajeros.view.*

class AdaptadorMensajeros(val mensajero:Bicimensajero): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.template_lista_mensajeros
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tvNombre.text=mensajero.nombres
        if(mensajero.disponible == "1"){
            viewHolder.itemView.tvDisponible.text="Disponible"
            viewHolder.itemView.tvDisponible.setTextColor(Color.parseColor("#25FF24"))
            viewHolder.itemView.ivDisponible.setImageResource(R.drawable.ic_disponible)

        }else{
            viewHolder.itemView.tvDisponible.text="No disponible"
            viewHolder.itemView.tvDisponible.setTextColor(Color.parseColor("#FF0000"))
            viewHolder.itemView.ivDisponible.setImageResource(R.drawable.ic_no_disponible_rojo)
        }
        if(mensajero.tipoVehiculo == "Bicicleta"){
            viewHolder.itemView.tvVehiculo.text="Bicicleta"
            viewHolder.itemView.ivVehiculo.setImageResource(R.drawable.ic_bicimensajero_navmenu)
        }else{
            viewHolder.itemView.tvVehiculo.text="Moto"
            viewHolder.itemView.ivVehiculo.setImageResource(R.drawable.ic_motocicleta_negra)
        }

        viewHolder.itemView.tvVehiculo.text=mensajero.tipoVehiculo
        Picasso.get()
            .load(mensajero.fotoPerfil)
            .error(R.drawable.error_icon1)
            .placeholder(R.drawable.animacion_carga)
            .into(viewHolder.itemView.ivFotoMensajero)
    }
}