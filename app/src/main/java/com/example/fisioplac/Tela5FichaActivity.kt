package com.example.fisioplac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button // Modificado de com.google.android.material.button.MaterialButton
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast // Importação ADICIONADA
import androidx.appcompat.app.AppCompatActivity

class Tela5FichaActivity : AppCompatActivity() {

    // ... (declarações de variáveis existentes)
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var avancarButton: Button // Usando Button padrão aqui
    private lateinit var tvResultadoFinal: TextView
    private lateinit var actvMemorizacao: AutoCompleteTextView
    private lateinit var actvLinguagemObjetos: AutoCompleteTextView
    private lateinit var actvLinguagemFrase: AutoCompleteTextView
    private lateinit var actvLinguagemEstagios: AutoCompleteTextView
    private lateinit var actvLinguagemLerOrdem: AutoCompleteTextView
    private lateinit var actvLinguagemEscreverFrase: AutoCompleteTextView
    private lateinit var actvLinguagemCopiarDesenho: AutoCompleteTextView
    private lateinit var tvCliqueAquiLerOrdem: TextView
    private lateinit var tvCliqueAquiVerDesenho: TextView
    private lateinit var allDropdowns: List<AutoCompleteTextView>

    private val totalPassosDaFicha = 13 // Exemplo
    private val passoAtual = 5 // Exemplo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela5_ficha)

        inicializarViews()
        setupClickListeners()
        setupDropdownMenus()
        setupDropdownListeners()
        setupTouchParaAbrirDropdown()
        updateProgressBar(passoAtual, totalPassosDaFicha) // Atualiza barra de progresso
        calcularEExibirResultado()
    }

    private fun inicializarViews() {
        backButton = findViewById(R.id.btn_back)
        progressBar = findViewById(R.id.ficha_progress_bar)
        avancarButton = findViewById(R.id.btn_avancar)
        tvResultadoFinal = findViewById(R.id.tv_resultado_final)
        actvMemorizacao = findViewById(R.id.actv_memorizacao_nota)
        actvLinguagemObjetos = findViewById(R.id.actv_linguagem_objetos_nota)
        actvLinguagemFrase = findViewById(R.id.actv_linguagem_frase_nota)
        actvLinguagemEstagios = findViewById(R.id.actv_linguagem_estagios_nota)
        actvLinguagemLerOrdem = findViewById(R.id.actv_linguagem_ler_ordem_nota)
        actvLinguagemEscreverFrase = findViewById(R.id.actv_linguagem_escrever_frase_nota)
        actvLinguagemCopiarDesenho = findViewById(R.id.actv_linguagem_copiar_desenho_nota)
        tvCliqueAquiLerOrdem = findViewById(R.id.tv_clique_aqui_ler_ordem)
        tvCliqueAquiVerDesenho = findViewById(R.id.tv_clique_aqui_ver_desenho)

        allDropdowns = listOf(
            actvMemorizacao, actvLinguagemObjetos, actvLinguagemFrase,
            actvLinguagemEstagios, actvLinguagemLerOrdem, actvLinguagemEscreverFrase,
            actvLinguagemCopiarDesenho
        ) //
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        // --- LÓGICA DE VALIDAÇÃO ADICIONADA ---
        avancarButton.setOnClickListener {
            if (validarCampos()) { // Verifica se todos os campos estão preenchidos
                // TODO: Adicionar a lógica para SALVAR os dados desta tela
                val intent = Intent(this, Tela6FichaActivity::class.java) //
                startActivity(intent)
            } else {
                // Exibe mensagem se algum campo estiver vazio
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
        // --- FIM DA LÓGICA DE VALIDAÇÃO ---

        tvCliqueAquiLerOrdem.setOnClickListener {
            val intent = Intent(this, FecheOlhosActivity::class.java)
            startActivity(intent)
        }

        tvCliqueAquiVerDesenho.setOnClickListener {
            val intent = Intent(this, DesenhoActivity::class.java)
            startActivity(intent)
        }
    }

    // --- FUNÇÃO DE VALIDAÇÃO ADICIONADA ---
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
        val dropdownMap = mapOf(
            actvMemorizacao to R.array.opcoes_nota_0_3,
            actvLinguagemObjetos to R.array.opcoes_nota_0_2,
            actvLinguagemFrase to R.array.opcoes_nota_0_1,
            actvLinguagemEstagios to R.array.opcoes_nota_0_3,
            actvLinguagemLerOrdem to R.array.opcoes_nota_0_1,
            actvLinguagemEscreverFrase to R.array.opcoes_nota_0_1,
            actvLinguagemCopiarDesenho to R.array.opcoes_nota_0_1
        ) //

        dropdownMap.forEach { (autoCompleteTextView, arrayResourceId) ->
            setupAutoCompleteTextView(autoCompleteTextView, arrayResourceId)
        }
    }

    private fun setupDropdownListeners() {
        allDropdowns.forEach { dropdown ->
            dropdown.setOnItemClickListener { _, _, _, _ ->
                calcularEExibirResultado()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchParaAbrirDropdown() {
        allDropdowns.forEach { dropdown ->
            dropdown.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    // Evita fechar e reabrir se já estiver mostrando
                    if (!dropdown.isPopupShowing) {
                        dropdown.showDropDown()
                    }
                    return@setOnTouchListener true // Indica que o evento foi consumido
                }
                return@setOnTouchListener false // Deixa outros eventos (como scroll) passarem
            }
        }
    } //

    private fun calcularEExibirResultado() {
        val notaMemorizacao = actvMemorizacao.text.toString().toIntOrNull() ?: 0
        val notaObjetos = actvLinguagemObjetos.text.toString().toIntOrNull() ?: 0
        val notaFrase = actvLinguagemFrase.text.toString().toIntOrNull() ?: 0
        val notaEstagios = actvLinguagemEstagios.text.toString().toIntOrNull() ?: 0
        val notaLerOrdem = actvLinguagemLerOrdem.text.toString().toIntOrNull() ?: 0
        val notaEscreverFrase = actvLinguagemEscreverFrase.text.toString().toIntOrNull() ?: 0
        val notaCopiarDesenho = actvLinguagemCopiarDesenho.text.toString().toIntOrNull() ?: 0

        val totalScore = notaMemorizacao + notaObjetos + notaFrase + notaEstagios +
                notaLerOrdem + notaEscreverFrase + notaCopiarDesenho

        // TODO: Buscar a escolaridade real do paciente (Tela 1?)
        val escolaridadeMenos4Anos = false // Valor placeholder

        val diagnostico = obterDiagnostico(totalScore, escolaridadeMenos4Anos)

        tvResultadoFinal.text = "$diagnostico, $totalScore pontos." //
    }

    private fun obterDiagnostico(score: Int, escolaridadeMenos4Anos: Boolean): String {
        val pontoDeCorte = if (escolaridadeMenos4Anos) 17 else 24

        return when {
            score > 27 -> "Normal"
            score <= pontoDeCorte -> "Estado cognitivo alterado"
            else -> "Resultado intermediário"
        }
    }

    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    }

    // Função para atualizar a barra de progresso
    private fun updateProgressBar(currentStep: Int, totalSteps: Int) {
        val progress = (currentStep * 100) / totalSteps
        progressBar.progress = progress
    }
}