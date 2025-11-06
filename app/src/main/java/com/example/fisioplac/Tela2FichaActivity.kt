package com.example.fisioplac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView // Import necessário para a seta
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// Modelo de dados (sem alterações)
data class Medicamento(
    val nome: String,
    val tempoDeUso: String,
    val comoUsar: String,
    var isExpanded: Boolean = false
)

// A classe principal. A classe duplicada foi removida.
class Tela2FichaActivity : AppCompatActivity() {

    private val listaDeMedicamentos = mutableListOf<Medicamento>()
    private lateinit var medicamentoAdapter: MedicamentoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela2_ficha)

        // --- CÓDIGO PARA A SETA DE VOLTAR ---
        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            // finish() fecha a tela atual e volta para a anterior na pilha (Tela1)
            finish()
        }
        // --- FIM DO CÓDIGO DA SETA ---

        val recyclerView = findViewById<RecyclerView>(R.id.rv_medicamentos)
        val headerLista = findViewById<LinearLayout>(R.id.ll_header_lista)
        val btnAdicionar = findViewById<MaterialButton>(R.id.btn_adicionar)
        val etNome = findViewById<TextInputEditText>(R.id.et_nome_medicamento)
        val etComoUsar = findViewById<TextInputEditText>(R.id.et_como_usar)
        val etTempoUso = findViewById<TextInputEditText>(R.id.et_tempo_uso)
        val tilNome = findViewById<TextInputLayout>(R.id.til_nome_medicamento)
        val tilComoUsar = findViewById<TextInputLayout>(R.id.til_como_usar)
        val tilTempoUso = findViewById<TextInputLayout>(R.id.til_tempo_uso)

        headerLista.visibility = View.GONE
        recyclerView.visibility = View.GONE

        medicamentoAdapter = MedicamentoAdapter(listaDeMedicamentos)
        recyclerView.adapter = medicamentoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAdicionar.setOnClickListener {
            tilNome.error = null
            tilComoUsar.error = null
            tilTempoUso.error = null

            val nome = etNome.text.toString().trim()
            val comoUsar = etComoUsar.text.toString().trim()
            val tempoUso = etTempoUso.text.toString().trim()
            var isFormValid = true

            if (nome.isEmpty()) {
                tilNome.error = "Campo obrigatório"
                isFormValid = false
            }
            if (comoUsar.isEmpty()) {
                tilComoUsar.error = "Campo obrigatório"
                isFormValid = false
            }
            if (tempoUso.isEmpty()) {
                tilTempoUso.error = "Campo obrigatório"
                isFormValid = false
            }

            if (isFormValid) {
                val novoMedicamento = Medicamento(nome, tempoUso, comoUsar)
                listaDeMedicamentos.add(novoMedicamento)
                medicamentoAdapter.notifyItemInserted(listaDeMedicamentos.size - 1)

                etNome.text?.clear()
                etComoUsar.text?.clear()
                etTempoUso.text?.clear()

                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                etNome.clearFocus()

                if (listaDeMedicamentos.size == 1) {
                    headerLista.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        val btnAvancar = findViewById<MaterialButton>(R.id.btn_avancar)
        btnAvancar.setOnClickListener {

            val intent = Intent(this, Tela5FichaActivity::class.java)
            // Inicia a próxima tela
            startActivity(intent)
        }

        setupValidationListeners()
    }

    private fun setupValidationListeners() {
        val etNome = findViewById<TextInputEditText>(R.id.et_nome_medicamento)
        val tilNome = findViewById<TextInputLayout>(R.id.til_nome_medicamento)
        val etComoUsar = findViewById<TextInputEditText>(R.id.et_como_usar)
        val tilComoUsar = findViewById<TextInputLayout>(R.id.til_como_usar)
        val etTempoUso = findViewById<TextInputEditText>(R.id.et_tempo_uso)
        val tilTempoUso = findViewById<TextInputLayout>(R.id.til_tempo_uso)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (tilNome.error != null && !etNome.text.isNullOrBlank()) {
                    tilNome.error = null
                }
                if (tilComoUsar.error != null && !etComoUsar.text.isNullOrBlank()) {
                    tilComoUsar.error = null
                }
                if (tilTempoUso.error != null && !etTempoUso.text.isNullOrBlank()) {
                    tilTempoUso.error = null
                }
            }
        }
        etNome.addTextChangedListener(textWatcher)
        etComoUsar.addTextChangedListener(textWatcher)
        etTempoUso.addTextChangedListener(textWatcher)
    }
}

// Seu MedicamentoAdapter permanece fora da classe da Activity
class MedicamentoAdapter(private val medicamentos: MutableList<Medicamento>) :
    RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeMedicamento: TextView = itemView.findViewById(R.id.tv_nome_medicamento_item)
        val tempoDeUso: TextView = itemView.findViewById(R.id.tv_tempo_uso_item)
        val comoUsarTexto: TextView = itemView.findViewById(R.id.tv_como_usar_item)
        val areaClicavel: View = itemView.findViewById(R.id.ll_clickable_area)
        val detailsArea: LinearLayout = itemView.findViewById(R.id.ll_details_area)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_medicamento, parent, false)
        return MedicamentoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return medicamentos.size
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        val medicamentoAtual = medicamentos[position]
        val context = holder.itemView.context
        holder.nomeMedicamento.text = medicamentoAtual.nome
        holder.tempoDeUso.text = medicamentoAtual.tempoDeUso
        val textoHtml = "<b>Como usa:</b> ${medicamentoAtual.comoUsar}"
        holder.comoUsarTexto.text =
            HtmlCompat.fromHtml(textoHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)

        if (medicamentoAtual.isExpanded) {
            holder.detailsArea.visibility = View.VISIBLE
            holder.nomeMedicamento.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.seta_para_cima, 0)
        } else {
            holder.detailsArea.visibility = View.GONE
            holder.nomeMedicamento.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.setadoselect, 0)
        }
        holder.nomeMedicamento.compoundDrawables[2]?.setTint(context.getColor(R.color.cinza_escuro))

        holder.areaClicavel.setOnClickListener {
            medicamentoAtual.isExpanded = !medicamentoAtual.isExpanded
            notifyItemChanged(position)
        }
    }
}