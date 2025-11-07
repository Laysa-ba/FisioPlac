package com.example.fisioplac

import com.example.fisioplac.TOTAL_FICHA_STEPS

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.checkbox.MaterialCheckBox
import com.example.fisioplac.R
import com.google.android.material.button.MaterialButton
import android.widget.ProgressBar // <-- 1. IMPORT ADICIONADO

class Tela11FichaActivity : AppCompatActivity() {

    // Componentes da UI
    private lateinit var actvSentarApoios: AutoCompleteTextView
    private lateinit var cbSentarDesequilibrio: MaterialCheckBox
    private lateinit var tvSentarNota: TextView

    private lateinit var actvLevantarApoios: AutoCompleteTextView
    private lateinit var cbLevantarDesequilibrio: MaterialCheckBox
    private lateinit var tvLevantarNota: TextView

    private lateinit var btnAvancar: MaterialButton

    // --- 2. VARIÁVEIS ADICIONADAS ---
    private lateinit var progressBar: ProgressBar
    private val PASSO_ATUAL = 11

    // Mapas para armazenar as notas base
    private val notasBase = mapOf(
        0 to 5.0, // Sem apoios
        1 to 4.0, // Com 1 apoio
        2 to 3.0, // Com 2 apoios
        3 to 2.0, // Com 3 apoios
        4 to 1.0, // Com 4 apoios
        5 to 0.0  // 4+ apoios ou ajuda externa
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela11_ficha)

        // Inicializa os componentes
        setupViews()

        // --- 4. LÓGICA DA BARRA DE PROGRESSO ADICIONADA ---
        progressBar.max = TOTAL_FICHA_STEPS
        progressBar.progress = PASSO_ATUAL
        // --- FIM DA LÓGICA DA BARRA ---

        // Configura os dropdowns
        setupDropdowns()

        // Configura os listeners para calcular as notas dinamicamente
        setupListeners()
    }

    // --- 3. setupViews ATUALIZADO ---
    private fun setupViews() {
        actvSentarApoios = findViewById(R.id.actvSentarApoios)
        cbSentarDesequilibrio = findViewById(R.id.cbSentarDesequilibrio)
        tvSentarNota = findViewById(R.id.tvSentarNota)

        actvLevantarApoios = findViewById(R.id.actvLevantarApoios)
        cbLevantarDesequilibrio = findViewById(R.id.cbLevantarDesequilibrio)
        tvLevantarNota = findViewById(R.id.tvLevantarNota)

        // Encontra o botão de avançar
        btnAvancar = findViewById(R.id.btnAvancar)

        // Encontra a barra de progresso
        progressBar = findViewById(R.id.ficha_progress_bar)
    }

    private fun setupDropdowns() {
        // Pega o array de strings do strings.xml
        val apoiosArray = resources.getStringArray(R.array.opcoes_apoio)

        // Cria o adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, apoiosArray)

        // Define o adapter para ambos os dropdowns
        actvSentarApoios.setAdapter(adapter)
        actvLevantarApoios.setAdapter(adapter)

        actvSentarApoios.setText("", false)
        actvLevantarApoios.setText("", false)
    }

    // --- 4. setupListeners ATUALIZADO ---
    private fun setupListeners() {
        // Listener para o dropdown de SENTAR
        actvSentarApoios.setOnItemClickListener { parent, _, position, _ ->
            val adapter = parent.adapter as ArrayAdapter<String>
            val selection = adapter.getItem(position)
            actvSentarApoios.setText(selection, false) // Garante que o texto selecionado apareça

            // Calcula a nota
            calcularNotaSentar(position, cbSentarDesequilibrio.isChecked)

            // --- A SOLUÇÃO ESTÁ AQUI ---
            actvSentarApoios.clearFocus() // Remove o foco
            // --- FIM DA SOLUÇÃO ---
        }

        // Listener para o checkbox de SENTAR
        cbSentarDesequilibrio.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = getPositionFromDropdown(actvSentarApoios)
            if (currentPosition != -1) {
                calcularNotaSentar(currentPosition, isChecked)
            }
        }

        // Listener para o dropdown de LEVANTAR
        actvLevantarApoios.setOnItemClickListener { parent, _, position, _ ->
            val adapter = parent.adapter as ArrayAdapter<String>
            val selection = adapter.getItem(position)
            actvLevantarApoios.setText(selection, false) // Garante que o texto selecionado apareça

            // Calcula a nota
            calcularNotaLevantar(position, cbLevantarDesequilibrio.isChecked)

            // --- A SOLUÇÃO ESTÁ AQUI ---
            actvLevantarApoios.clearFocus() // Remove o foco
            // --- FIM DA SOLUÇÃO ---
        }

        // Listener para o checkbox de LEVANTAR
        cbLevantarDesequilibrio.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = getPositionFromDropdown(actvLevantarApoios)
            if (currentPosition != -1) {
                calcularNotaLevantar(currentPosition, isChecked)
            }
        }

        // Listener para o botão AVANÇAR
        btnAvancar.setOnClickListener {
            // TODO: Adicionar lógica para salvar os dados no Firebase, se necessário

            // Navega para a Tela12FichaActivity
            val intent = Intent(this, Tela12FichaActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Calcula e atualiza a nota da seção SENTAR.
     */
    private fun calcularNotaSentar(position: Int, desequilibrio: Boolean) {
        val notaBase = notasBase[position] ?: 0.0
        val notaFinal = if (desequilibrio) notaBase - 0.5 else notaBase
        tvSentarNota.text = notaFinal.toString()
    }

    /**
     * Calcula e atualiza a nota da seção LEVANTAR.
     */
    private fun calcularNotaLevantar(position: Int, desequilibrio: Boolean) {
        val notaBase = notasBase[position] ?: 0.0
        val notaFinal = if (desequilibrio) notaBase - 0.5 else notaBase
        tvLevantarNota.text = notaFinal.toString()
    }

    /**
     * Helper para descobrir a posição selecionada no dropdown.
     */
    private fun getPositionFromDropdown(dropdown: AutoCompleteTextView): Int {
        val adapter = dropdown.adapter as? ArrayAdapter<String>
        val currentText = dropdown.text.toString()
        if (currentText.isEmpty() || currentText == "APOIOS") {
            return -1
        }
        return adapter?.getPosition(currentText) ?: -1
    }
}