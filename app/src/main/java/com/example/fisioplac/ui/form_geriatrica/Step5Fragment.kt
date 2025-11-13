package com.example.fisioplac.ui.form_geriatrica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fisioplac.R
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.FragmentStep5Binding
// *** CORREÇÃO: Importa os novos DialogFragments ***
import com.example.fisioplac.ui.dialogs.DesenhoDialogFragment
import com.example.fisioplac.ui.dialogs.FecheOlhosDialogFragment

class Step5Fragment : Fragment() {

    // ... código existente (onCreateView, onViewCreated, setupDropdownMenus) ...
    private var _binding: FragmentStep5Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private lateinit var allDropdowns: List<AutoCompleteTextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assumindo que os IDs no seu XML serão em português
        // (ex: actvMemorizacaoNota, actvLinguagemObjetosNota, ...)
        allDropdowns = listOf(
            binding.actvMemorizacaoNota, binding.actvLinguagemObjetosNota, binding.actvLinguagemFraseNota,
            binding.actvLinguagemEstagiosNota, binding.actvLinguagemLerOrdemNota, binding.actvLinguagemEscreverFraseNota,
            binding.actvLinguagemCopiarDesenhoNota
        )

        setupDropdownMenus()
        setupListeners()
        setupObservers()
        setupTouchToOpenDropdown() // Adicionado do seu código original
    }

    private fun setupDropdownMenus() {
        val dropdownMap = mapOf(
            binding.actvMemorizacaoNota to R.array.opcoes_nota_0_3,
            binding.actvLinguagemObjetosNota to R.array.opcoes_nota_0_2,
            binding.actvLinguagemFraseNota to R.array.opcoes_nota_0_1,
            binding.actvLinguagemEstagiosNota to R.array.opcoes_nota_0_3,
            binding.actvLinguagemLerOrdemNota to R.array.opcoes_nota_0_1,
            binding.actvLinguagemEscreverFraseNota to R.array.opcoes_nota_0_1,
            binding.actvLinguagemCopiarDesenhoNota to R.array.opcoes_nota_0_1
        )

        dropdownMap.forEach { (autoCompleteTextView, arrayResourceId) ->
            setupAutoCompleteTextView(autoCompleteTextView, arrayResourceId)
        }
    }


    private fun setupListeners() {
        // Navegação (ViewModel em INGLÊS, IDs em PORTUGUÊS)
// ... código existente (botaoConcluir, botaoVoltar) ...
        binding.botaoProximo.setOnClickListener {
            if (validateFields()) {
                val data = collectDataFromUi()
                viewModel.onStep5NextClicked(data)
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        // *** CORREÇÃO: Chama os DialogFragments ***
        binding.tvCliqueAquiLerOrdem.setOnClickListener {
            FecheOlhosDialogFragment().show(childFragmentManager, "FecheOlhosDialog")
        }

        binding.tvCliqueAquiVerDesenho.setOnClickListener {
            DesenhoDialogFragment().show(childFragmentManager, "DesenhoDialog")
        }

        // Atualiza o resultado em tempo real
// ... código existente (allDropdowns.forEach) ...
        allDropdowns.forEach { dropdown ->
            dropdown.setOnItemClickListener { _, _, _, _ ->
                calculateAndShowResult()
            }
        }
    }

    private fun setupObservers() {
// ... código existente (viewModel.formData.observe) ...
        // Observa VM em INGLÊS
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            if (ficha != null) {
                populateUi(ficha)
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
// ... código existente (state handling) ...
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
// ... código existente (populateUi) ...
    private fun populateUi(ficha: GeriatricFicha) {
        // IDs em PORTUGUÊS, Campos do Modelo em PORTUGUÊS
        binding.actvMemorizacaoNota.setText(ficha.pontuacaoMemorizacao.toString(), false)
        binding.actvLinguagemObjetosNota.setText(ficha.linguagemNomearObjetos.toString(), false)
        binding.actvLinguagemFraseNota.setText(ficha.linguagemRepetirFrase.toString(), false)
        binding.actvLinguagemEstagiosNota.setText(ficha.linguagemComandoEstagios.toString(), false)
        binding.actvLinguagemLerOrdemNota.setText(ficha.linguagemLerOrdem.toString(), false)
        binding.actvLinguagemEscreverFraseNota.setText(ficha.linguagemEscreverFrase.toString(), false)
        binding.actvLinguagemCopiarDesenhoNota.setText(ficha.linguagemCopiarDesenho.toString(), false)

        // Atualiza o texto do resultado ao carregar, apenas se não for vazio
        if (ficha.diagnosticoEstadoMental.isNotBlank()) {
            binding.tvResultadoFinal.text = "${ficha.diagnosticoEstadoMental}, ${ficha.pontuacaoTotalEstadoMental} pontos."
        } else {
            binding.tvResultadoFinal.text = "" // Limpa se for uma ficha nova
        }
    }


    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
// ... código existente (collectDataFromUi) ...
    private fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!

        val (totalScore, diagnostico) = calculateAndShowResult()

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
            pontuacaoMemorizacao = binding.actvMemorizacaoNota.text.toString().toIntOrNull() ?: 0,
            linguagemNomearObjetos = binding.actvLinguagemObjetosNota.text.toString().toIntOrNull() ?: 0,
            linguagemRepetirFrase = binding.actvLinguagemFraseNota.text.toString().toIntOrNull() ?: 0,
            linguagemComandoEstagios = binding.actvLinguagemEstagiosNota.text.toString().toIntOrNull() ?: 0,
            linguagemLerOrdem = binding.actvLinguagemLerOrdemNota.text.toString().toIntOrNull() ?: 0,
            linguagemEscreverFrase = binding.actvLinguagemEscreverFraseNota.text.toString().toIntOrNull() ?: 0,
            linguagemCopiarDesenho = binding.actvLinguagemCopiarDesenhoNota.text.toString().toIntOrNull() ?: 0,
            pontuacaoTotalEstadoMental = totalScore,
            diagnosticoEstadoMental = diagnostico
        )
    }


    /**
     * Valida se todos os campos de dropdown foram preenchidos.
     */
// ... código existente (validateFields) ...
    private fun validateFields(): Boolean {
        for (dropdown in allDropdowns) {
            if (dropdown.text.isNullOrEmpty()) {
                dropdown.error = "Obrigatório" // Adiciona um feedback visual
                return false
            } else {
                dropdown.error = null // Limpa o erro
            }
        }
        return true
    }


    /**
     * Calcula a pontuação e o diagnóstico e exibe na UI.
     * Retorna o par (Pontuação, Diagnóstico) para ser usado no 'collectDataFromUi'.
     */
// ... código existente (calculateAndShowResult) ...
    private fun calculateAndShowResult(): Pair<Int, String> {
        val notaMemorizacao = binding.actvMemorizacaoNota.text.toString().toIntOrNull() ?: 0
        val notaObjetos = binding.actvLinguagemObjetosNota.text.toString().toIntOrNull() ?: 0
        val notaFrase = binding.actvLinguagemFraseNota.text.toString().toIntOrNull() ?: 0
        val notaEstagios = binding.actvLinguagemEstagiosNota.text.toString().toIntOrNull() ?: 0
        val notaLerOrdem = binding.actvLinguagemLerOrdemNota.text.toString().toIntOrNull() ?: 0
        val notaEscreverFrase = binding.actvLinguagemEscreverFraseNota.text.toString().toIntOrNull() ?: 0
        val notaCopiarDesenho = binding.actvLinguagemCopiarDesenhoNota.text.toString().toIntOrNull() ?: 0

        // Soma as pontuações do Step 5
        val totalScoreStep5 = notaMemorizacao + notaObjetos + notaFrase + notaEstagios +
                notaLerOrdem + notaEscreverFrase + notaCopiarDesenho

        // Pega as pontuações do Step 4 (que já estão no ViewModel)
        val fichaAtual = viewModel.formData.value
        val totalScoreStep4 = (fichaAtual?.pontuacaoOrientacaoTempo ?: 0) +
                (fichaAtual?.pontuacaoOrientacaoLocal ?: 0) +
                (fichaAtual?.pontuacaoRegistroPalavras ?: 0) +
                (fichaAtual?.pontuacaoCalculo ?: 0)

        // A pontuação total é a soma dos dois steps
        val pontuacaoTotal = totalScoreStep4 + totalScoreStep5

        // Pega a escolaridade do Step 1, que está no ViewModel
        val escolaridade = viewModel.formData.value?.escolaridade ?: ""

        // Lógica de escolaridade (ajuste conforme as opções exatas do Step 1)
        val escolaridadeMenos4Anos = when(escolaridade) {
            "Analfabeto", "Fundamental I Incompleto" -> true // Ajuste estas strings se necessário
            else -> false
        }

        val diagnostico = obterDiagnostico(pontuacaoTotal, escolaridadeMenos4Anos)

        binding.tvResultadoFinal.text = "$diagnostico, $pontuacaoTotal pontos."
        return Pair(pontuacaoTotal, diagnostico)
    }


    /**
     * Determina o diagnóstico com base na pontuação e escolaridade.
     */
// ... código existente (obterDiagnostico) ...
    private fun obterDiagnostico(score: Int, escolaridadeMenos4Anos: Boolean): String {
        // Ponto de corte (17 para baixa escolaridade, 24 para alta)
        val pontoDeCorte = if (escolaridadeMenos4Anos) 17 else 24

        return when {
            score > 27 -> "Normal"
            score <= pontoDeCorte -> "Estado cognitivo alterado"
            else -> "Resultado intermediário" // Entre 24 e 27
        }
    }


    /**
     * Configura o adapter para um AutoCompleteTextView.
     */
// ... código existente (setupAutoCompleteTextView) ...
    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        // Verifica se o contexto é nulo antes de usá-lo
        val context = context ?: return
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    }


    /**
     * Permite que o dropdown seja aberto com um simples toque, não apenas no ícone.
     * *** CORREÇÃO: Erro de digitação @DSuppressLint -> @SuppressLint ***
     */
// ... código existente (setupTouchToOpenDropdown) ...
    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchToOpenDropdown() {
        allDropdowns.forEach { dropdown ->
            dropdown.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    // Verifica se o contexto é nulo
                    if (context == null) return@setOnTouchListener false

                    if (!dropdown.isPopupShowing) {
                        dropdown.showDropDown()
                    }
                    return@setOnTouchListener true
                }
                return@setOnTouchListener false
            }
        }
    }


    override fun onDestroyView() {
// ... código existente (onDestroyView) ...
        super.onDestroyView()
        _binding = null
    }
}