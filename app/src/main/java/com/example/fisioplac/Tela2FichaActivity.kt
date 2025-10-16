package com.example.fisioplac

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

// Modelo de dados (Correto)
data class Medicamento(
    val nome: String,
    val tempoDeUso: String,
    val comoUsar: String,
    var isExpanded: Boolean = false
)

class Tela2FichaActivity : AppCompatActivity() {

    // 1. A lista de medicamentos agora é uma variável da classe e é MUTÁVEL
    private val listaDeMedicamentos = mutableListOf<Medicamento>()
    // 2. O adapter também se torna uma variável da classe para ser acessado depois
    private lateinit var medicamentoAdapter: MedicamentoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela2_ficha)

        // Referências para os componentes da UI
        val recyclerView = findViewById<RecyclerView>(R.id.rv_medicamentos)
        val headerLista = findViewById<LinearLayout>(R.id.ll_header_lista)
        val btnAdicionar = findViewById<MaterialButton>(R.id.btn_adicionar)
        val etNome = findViewById<TextInputEditText>(R.id.et_nome_medicamento)
        val etComoUsar = findViewById<TextInputEditText>(R.id.et_como_usar)
        val etTempoUso = findViewById<TextInputEditText>(R.id.et_tempo_uso)

        // 3. Oculta a lista e o cabeçalho no início
        headerLista.visibility = View.GONE
        recyclerView.visibility = View.GONE

        // Configuração do Adapter e RecyclerView
        medicamentoAdapter = MedicamentoAdapter(listaDeMedicamentos)
        recyclerView.adapter = medicamentoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 4. LÓGICA DO BOTÃO ADICIONAR
        btnAdicionar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val comoUsar = etComoUsar.text.toString().trim()
            val tempoUso = etTempoUso.text.toString().trim()

            // Validação simples para não adicionar item vazio
            if (nome.isNotEmpty()) {
                // Cria o novo objeto Medicamento
                val novoMedicamento = Medicamento(nome, tempoUso, comoUsar)

                // Adiciona na lista
                listaDeMedicamentos.add(novoMedicamento)

                // Notifica o adapter que um novo item foi inserido na última posição
                medicamentoAdapter.notifyItemInserted(listaDeMedicamentos.size - 1)

                // Limpa os campos de texto
                etNome.text?.clear()
                etComoUsar.text?.clear()
                etTempoUso.text?.clear()

                // Esconde o teclado
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                etNome.clearFocus()

                // 5. Se for o primeiro item, torna a lista e o cabeçalho visíveis
                if (listaDeMedicamentos.size == 1) {
                    headerLista.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                }

            } else {
                // Mensagem de erro se o nome estiver vazio
                Toast.makeText(this, "Por favor, insira o nome do medicamento.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class MedicamentoAdapter(private val medicamentos: List<Medicamento>) :
    RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeMedicamento: TextView = itemView.findViewById(R.id.tv_nome_medicamento_item)
        val tempoDeUso: TextView = itemView.findViewById(R.id.tv_tempo_uso_item)
        val comoUsar: TextView = itemView.findViewById(R.id.tv_como_usar_item)
        val areaClicavel: View = itemView.findViewById(R.id.ll_clickable_area)
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

        holder.nomeMedicamento.text = medicamentoAtual.nome
        holder.tempoDeUso.text = medicamentoAtual.tempoDeUso
        holder.comoUsar.text = medicamentoAtual.comoUsar

        holder.comoUsar.visibility = if (medicamentoAtual.isExpanded) View.VISIBLE else View.GONE

        holder.areaClicavel.setOnClickListener {
            medicamentoAtual.isExpanded = !medicamentoAtual.isExpanded
            notifyItemChanged(position)
        }
    }
}