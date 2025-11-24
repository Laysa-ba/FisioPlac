package com.example.fisioplac.ui.form_geriatrica

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fisioplac.R
import com.example.fisioplac.data.model.Medicamento
import com.example.fisioplac.databinding.ListItemMedicamentoBinding // <-- Import do ViewBinding

class MedicamentoAdapter(
    private val medicamentos: List<Medicamento>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    /**
     * ViewHolder agora usa ViewBinding
     */
    class MedicamentoViewHolder(val binding: ListItemMedicamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medicamento: Medicamento, context: Context) {
            binding.tvNomeMedicamentoItem.text = medicamento.nome
            binding.tvTempoUsoItem.text = medicamento.tempoDeUso
            val textoHtml = "<b>Como usa:</b> ${medicamento.comoUsar}"
            binding.tvComoUsarItem.text =
                HtmlCompat.fromHtml(textoHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)

            if (medicamento.isExpanded) {
                binding.llDetailsArea.visibility = View.VISIBLE
                binding.tvNomeMedicamentoItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.seta_para_cima, 0)
            } else {
                binding.llDetailsArea.visibility = View.GONE
                binding.tvNomeMedicamentoItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.setadoselect, 0)
            }
            binding.tvNomeMedicamentoItem.compoundDrawables[2]?.setTint(context.getColor(R.color.cinza_escuro))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        // Infla o layout usando ViewBinding
        val binding = ListItemMedicamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicamentoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return medicamentos.size
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        val medicamentoAtual = medicamentos[position]
        holder.bind(medicamentoAtual, holder.itemView.context)

        holder.binding.llClickableArea.setOnClickListener {
            onItemClicked(position) // Delega o clique para o ViewModel
        }
    }
}