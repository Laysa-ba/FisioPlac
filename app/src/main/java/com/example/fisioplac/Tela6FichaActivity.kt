package com.example.fisioplac

import android.annotation.SuppressLint
import android.content.Intent // Importação ADICIONADA
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast // Importação ADICIONADA
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class Tela6FichaActivity : AppCompatActivity() {

    // ... (declarações de variáveis existentes)
    private lateinit var backButton: ImageButton
    private lateinit var btnAvancar: MaterialButton
    private lateinit var actvOmbroD: AutoCompleteTextView
    private lateinit var actvOmbroE: AutoCompleteTextView
    private lateinit var actvCotoveloD: AutoCompleteTextView
    private lateinit var actvCotoveloE: AutoCompleteTextView
    private lateinit var actvPunhoD: AutoCompleteTextView
    private lateinit var actvPunhoE: AutoCompleteTextView
    private lateinit var actvQuadrilD: AutoCompleteTextView
    private lateinit var actvQuadrilE: AutoCompleteTextView
    private lateinit var actvJoelhoD: AutoCompleteTextView
    private lateinit var actvJoelhoE: AutoCompleteTextView
    private lateinit var actvTornozeloD: AutoCompleteTextView
    private lateinit var actvTornozeloE: AutoCompleteTextView
    private lateinit var allDropdowns: List<AutoCompleteTextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela6_ficha)

        inicializarViews()
        setupClickListeners()
        setupDropdownMenus()
        setupTouchParaAbrirDropdown()
    }

    private fun inicializarViews() {
        backButton = findViewById(R.id.setaVoltar)
        btnAvancar = findViewById(R.id.btnAvancar)
        actvOmbroD = findViewById(R.id.actvOmbroD)
        actvOmbroE = findViewById(R.id.actvOmbroE)
        actvCotoveloD = findViewById(R.id.actvCotoveloD)
        actvCotoveloE = findViewById(R.id.actvCotoveloE)
        actvPunhoD = findViewById(R.id.actvPunhoD)
        actvPunhoE = findViewById(R.id.actvPunhoE)
        actvQuadrilD = findViewById(R.id.actvQuadrilD)
        actvQuadrilE = findViewById(R.id.actvQuadrilE)
        actvJoelhoD = findViewById(R.id.actvJoelhoD)
        actvJoelhoE = findViewById(R.id.actvJoelhoE)
        actvTornozeloD = findViewById(R.id.actvTornozeloD)
        actvTornozeloE = findViewById(R.id.actvTornozeloE)

        allDropdowns = listOf(
            actvOmbroD, actvOmbroE, actvCotoveloD, actvCotoveloE,
            actvPunhoD, actvPunhoE, actvQuadrilD, actvQuadrilE,
            actvJoelhoD, actvJoelhoE, actvTornozeloD, actvTornozeloE
        ) //
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        // --- LÓGICA DE VALIDAÇÃO ADICIONADA ---
        btnAvancar.setOnClickListener {
            if (validarCampos()) { // Verifica se todos os campos estão preenchidos
                val intent = Intent(this, Tela7FichaActivity::class.java)
                startActivity(intent)
            } else {
                // Exibe mensagem se algum campo estiver vazio
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
        // --- FIM DA LÓGICA DE VALIDAÇÃO ---
    }

    // --- FUNÇÃO DE VALIDAÇÃO ADICIONADA (igual à da Tela 5) ---
    /**
     * Verifica se todos os AutoCompleteTextViews da lista `allDropdowns` estão preenchidos.
     * @return `true` se todos estiverem preenchidos, `false` caso contrário.
     */
    private fun validarCampos(): Boolean {
        for (dropdown in allDropdowns) {
            if (dropdown.text.isNullOrEmpty()) {
                return false // Encontrou um campo vazio, retorna falso
            }
        }
        return true // Todos os campos estão preenchidos
    }
    // --- FIM DA FUNÇÃO DE VALIDAÇÃO ---


    private fun setupDropdownMenus() {
        val arrayResourceId = R.array.opcoes_nota_0_5
        allDropdowns.forEach { dropdown ->
            setupAutoCompleteTextView(dropdown, arrayResourceId)
        } //
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchParaAbrirDropdown() {
        allDropdowns.forEach { dropdown ->
            dropdown.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (!dropdown.isPopupShowing) {
                        dropdown.showDropDown()
                    }
                    return@setOnTouchListener true
                }
                return@setOnTouchListener false
            }
        }
    } //

    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    } //
}