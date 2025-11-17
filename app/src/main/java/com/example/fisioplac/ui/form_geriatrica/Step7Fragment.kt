package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fisioplac.R
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.FragmentStep7Binding // XML que você vai criar
import com.example.fisioplac.databinding.ItemBarthelBinding // Layout de item que você precisa ter

class Step7Fragment : Fragment(),FormStepFragment {

    private var _binding: FragmentStep7Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    // Mapeia o HINT (categoria) para a pontuação atual
    private val currentScores = mutableMapOf<String, Int>()
    private val categories = listOf(
        "Alimentação", "Banho", "Atividades diárias", "Vestir-se", "Intestino",
        "Sistema urinário", "Uso do banheiro", "Transferência (Cama-Cadeira)",
        "Mobilidade em superfícies planas", "Escadas"
    )

    // Opções do Barthel (lógica em INGLÊS)
    private data class BarthelOption(val description: String, val score: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep7Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeScores()
        setupBarthelItems()
        setupListeners()
        setupObservers()
    }

    private fun initializeScores() {
        categories.forEach { category ->
            currentScores[category] = 0
        }
    }

    private fun setupBarthelItems() {
        // IDs do XML em PORTUGUÊS (ex: binding.itemAlimentacao)
        setupBarthelItem(binding.itemAlimentacao, "Alimentação", listOf(BarthelOption("Incapaz", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10)))
        setupBarthelItem(binding.itemBanho, "Banho", listOf(BarthelOption("Dependente", 0), BarthelOption("Independente", 5)))
        setupBarthelItem(binding.itemAtividadesDiarias, "Atividades diárias", listOf(BarthelOption("Precisa de ajuda", 0), BarthelOption("Independente", 5)))
        setupBarthelItem(binding.itemVestir, "Vestir-se", listOf(BarthelOption("Dependente", 0), BarthelOption("Precisa de ajuda, mas realiza parte", 5), BarthelOption("Independente", 10)))
        setupBarthelItem(binding.itemIntestino, "Intestino", listOf(BarthelOption("Incontinente", 0), BarthelOption("Acidente ocasional", 5), BarthelOption("Continente", 10)))
        setupBarthelItem(binding.itemUrinario, "Sistema urinário", listOf(BarthelOption("Incontinente", 0), BarthelOption("Acidente ocasional", 5), BarthelOption("Continente", 10)))
        setupBarthelItem(binding.itemUsoBanheiro, "Uso do banheiro", listOf(BarthelOption("Dependente", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10)))
        setupBarthelItem(binding.itemTransferencia, "Transferência (Cama-Cadeira)", listOf(BarthelOption("Incapacitado", 0), BarthelOption("Muita ajuda (1 ou 2 pessoas)", 5), BarthelOption("Pouca ajuda (verbal ou fisica)", 10), BarthelOption("Independente", 15)))
        setupBarthelItem(binding.itemMobilidade, "Mobilidade em superfícies planas", listOf(BarthelOption("Imóvel ou < 50m", 0), BarthelOption("Independente em cadeira de rodas > 50m", 5), BarthelOption("Anda com ajuda de 1 pessoa", 10), BarthelOption("Independente (pode usar auxílio)", 15)))
        setupBarthelItem(binding.itemEscadas, "Escadas", listOf(BarthelOption("Incapaz", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10)))
    }

    /**
     * Configura um item Barthel individual (o <include> no XML)
     */
    private fun setupBarthelItem(itemBinding: ItemBarthelBinding, hint: String, options: List<BarthelOption>) {
        // IDs do XML em PORTUGUÊS (assumindo item_barthel.xml)
        itemBinding.menu.hint = hint
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options.map { it.description })
        itemBinding.autoCompleteTextView.setAdapter(adapter)

        itemBinding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedOption = options[position]
            // ID 'etScore' dentro do 'item_barthel.xml'
            itemBinding.etScore.setText(selectedOption.score.toString())
            currentScores[hint] = selectedOption.score
            updateTotalScore()
        }
    }

    private fun setupListeners() {
        // Navegação (VM em INGLÊS, IDs em PORTUGUÊS)
        binding.botaoProximo.setOnClickListener {
            val data = collectDataFromUi()
            viewModel.onStep7NextClicked(data) // VM em INGLÊS
        }
    }

    private fun setupObservers() {
        // VM em INGLÊS
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            if (ficha != null) {
                populateUi(ficha)
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            // IDs em PORTUGUÊS
            binding.progressBar.isVisible = state.isLoading
            binding.botaoProximo.isEnabled = !state.isLoading
            binding.botaoProximo.text = if (state.isLoading) "Salvando..." else "Avançar"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // VM em INGLÊS
            }
        })
    }

    /**
     * Preenche a UI com os dados do ViewModel.
     * (Campos do Modelo em PORTUGUÊS)
     */
    private fun populateUi(ficha: GeriatricFicha) {
        // IDs em PORTUGUÊS, Campos do Modelo em PORTUGUÊS
        setBarthelItem(binding.itemAlimentacao, ficha.barthelAlimentacao)
        setBarthelItem(binding.itemBanho, ficha.barthelBanho)
        setBarthelItem(binding.itemAtividadesDiarias, ficha.barthelAtividadesDiarias)
        setBarthelItem(binding.itemVestir, ficha.barthelVestir)
        setBarthelItem(binding.itemIntestino, ficha.barthelIntestino)
        setBarthelItem(binding.itemUrinario, ficha.barthelUrinario)
        setBarthelItem(binding.itemUsoBanheiro, ficha.barthelUsoBanheiro)
        setBarthelItem(binding.itemTransferencia, ficha.barthelTransferencia)
        setBarthelItem(binding.itemMobilidade, ficha.barthelMobilidade)
        setBarthelItem(binding.itemEscadas, ficha.barthelEscadas)

        updateTotalScore() // Calcula a pontuação e o diagnóstico com base nos dados preenchidos
    }

    /**
     * Preenche um item Barthel específico (lógica auxiliar para populateUi)
     */
    private fun setBarthelItem(itemBinding: ItemBarthelBinding, selectedDescription: String?) {
        if (selectedDescription.isNullOrEmpty()) {
            itemBinding.autoCompleteTextView.setText("", false)
            itemBinding.etScore.setText("0")
            currentScores[itemBinding.menu.hint.toString()] = 0
            return
        }

        val adapter = itemBinding.autoCompleteTextView.adapter
        var score = 0
        if (adapter != null) { // Adiciona verificação de nulidade
            for (i in 0 until adapter.count) {
                val description = adapter.getItem(i).toString()
                if (description == selectedDescription) {
                    // Encontra a opção correspondente (recria a lógica do BarthelOption)
                    val hint = itemBinding.menu.hint.toString()
                    score = getScoreForDescription(hint, description)
                    break
                }
            }
        }

        itemBinding.autoCompleteTextView.setText(selectedDescription, false)
        itemBinding.etScore.setText(score.toString())
        currentScores[itemBinding.menu.hint.toString()] = score
    }

    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!
        val (totalScore, dependencyLevel) = updateTotalScore() // Pega os valores calculados

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
            barthelAlimentacao = binding.itemAlimentacao.autoCompleteTextView.text.toString(),
            barthelBanho = binding.itemBanho.autoCompleteTextView.text.toString(),
            barthelAtividadesDiarias = binding.itemAtividadesDiarias.autoCompleteTextView.text.toString(),
            barthelVestir = binding.itemVestir.autoCompleteTextView.text.toString(),
            barthelIntestino = binding.itemIntestino.autoCompleteTextView.text.toString(),
            barthelUrinario = binding.itemUrinario.autoCompleteTextView.text.toString(),
            barthelUsoBanheiro = binding.itemUsoBanheiro.autoCompleteTextView.text.toString(),
            barthelTransferencia = binding.itemTransferencia.autoCompleteTextView.text.toString(),
            barthelMobilidade = binding.itemMobilidade.autoCompleteTextView.text.toString(),
            barthelEscadas = binding.itemEscadas.autoCompleteTextView.text.toString(),
            barthelPontuacaoTotal = totalScore,
            barthelNivelDependencia = dependencyLevel
        )
    }

    /**
     * Calcula a pontuação total e o nível de dependência, atualiza a UI.
     * Retorna o Par (Pontuação, Nível) para ser usado no collectDataFromUi.
     */
    private fun updateTotalScore(): Pair<Int, String> {
        val totalScore = currentScores.values.sum()
        val dependencyLevel = when (totalScore) {
            in 0..25 -> "Dependência total"
            in 26..50 -> "Dependência severa"
            in 51..75 -> "Dependência moderada"
            in 76..99 -> "Dependência leve"
            100 -> "Totalmente independente"
            else -> "Nenhuma seleção" // Caso de 0 ou < 0
        }
        binding.tvResultado.text = "RESULTADO: $dependencyLevel, $totalScore pontos."
        return Pair(totalScore, dependencyLevel)
    }

    /**
     * Função auxiliar para encontrar a pontuação de uma descrição (necessária para o populateUi)
     */
    private fun getScoreForDescription(hint: String, description: String): Int {
        val options = when (hint) {
            "Alimentação" -> listOf(BarthelOption("Incapaz", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10))
            "Banho" -> listOf(BarthelOption("Dependente", 0), BarthelOption("Independente", 5))
            "Atividades diárias" -> listOf(BarthelOption("Precisa de ajuda", 0), BarthelOption("Independente", 5))
            "Vestir-se" -> listOf(BarthelOption("Dependente", 0), BarthelOption("Precisa de ajuda, mas realiza parte", 5), BarthelOption("Independente", 10))
            "Intestino" -> listOf(BarthelOption("Incontinente", 0), BarthelOption("Acidente ocasional", 5), BarthelOption("Continente", 10))
            "Sistema urinário" -> listOf(BarthelOption("Incontinente", 0), BarthelOption("Acidente ocasional", 5), BarthelOption("Continente", 10))
            "Uso do banheiro" -> listOf(BarthelOption("Dependente", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10))
            "Transferência (Cama-Cadeira)" -> listOf(BarthelOption("Incapacitado", 0), BarthelOption("Muita ajuda (1 ou 2 pessoas)", 5), BarthelOption("Pouca ajuda (verbal ou fisica)", 10), BarthelOption("Independente", 15))
            "Mobilidade em superfícies planas" -> listOf(BarthelOption("Imóvel ou < 50m", 0), BarthelOption("Independente em cadeira de rodas > 50m", 5), BarthelOption("Anda com ajuda de 1 pessoa", 10), BarthelOption("Independente (pode usar auxílio)", 15))
            "Escadas" -> listOf(BarthelOption("Incapaz", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10))
            else -> emptyList()
        }
        return options.find { it.description == description }?.score ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}