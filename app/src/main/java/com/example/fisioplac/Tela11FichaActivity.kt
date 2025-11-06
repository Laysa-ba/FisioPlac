package com.example.fisioplac

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.checkbox.MaterialCheckBox
import com.example.fisioplac.R

class Tela11FichaActivity : AppCompatActivity() {

    // Componentes da UI
    private lateinit var actvSentarApoios: AutoCompleteTextView
    private lateinit var cbSentarDesequilibrio: MaterialCheckBox
    private lateinit var tvSentarNota: TextView

    private lateinit var actvLevantarApoios: AutoCompleteTextView
    private lateinit var cbLevantarDesequilibrio: MaterialCheckBox
    private lateinit var tvLevantarNota: TextView

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

        // Configura os dropdowns
        setupDropdowns()

        // Configura os listeners para calcular as notas dinamicamente
        setupListeners()
    }

    private fun setupViews() {
        actvSentarApoios = findViewById(R.id.actvSentarApoios)
        cbSentarDesequilibrio = findViewById(R.id.cbSentarDesequilibrio)
        tvSentarNota = findViewById(R.id.tvSentarNota)

        actvLevantarApoios = findViewById(R.id.actvLevantarApoios)
        cbLevantarDesequilibrio = findViewById(R.id.cbLevantarDesequilibrio)
        tvLevantarNota = findViewById(R.id.tvLevantarNota)
    }

    private fun setupDropdowns() {
        // Pega o array de strings do strings.xml
        val apoiosArray = resources.getStringArray(R.array.opcoes_apoio)

        // Cria o adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, apoiosArray)

        // Define o adapter para ambos os dropdowns
        actvSentarApoios.setAdapter(adapter)
        actvLevantarApoios.setAdapter(adapter)

        // --- IMPORTANTE ---
        // No seu XML, o AutoCompleteTextView NÃO deve ter android:text="APOIOS".
        // O TextInputLayout é que deve ter android:hint="APOIOS".
        // Se o XML estiver como na nossa última versão, esta linha abaixo
        // pode ser necessária para o HINT aparecer em vez do texto "APOIOS".
        actvSentarApoios.setText("", false)
        actvLevantarApoios.setText("", false)
    }

    private fun setupListeners() {
        // Listener para o dropdown de SENTAR
        actvSentarApoios.setOnItemClickListener { parent, _, position, _ ->
            // Você precisa re-setar o texto, porque o setText("", false)
            // de cima pode ter limpado ele.
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
        // Retorna -1 se o texto for vazio ou "APOIOS" (o hint),
        // ou se não encontrar o item
        if (currentText.isEmpty() || currentText == "APOIOS") {
            return -1
        }
        return adapter?.getPosition(currentText) ?: -1
    }
}