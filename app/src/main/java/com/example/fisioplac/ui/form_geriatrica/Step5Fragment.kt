package com.example.fisioplac.ui.form_geriatrica

import android.annotation.SuppressLint
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
import com.example.fisioplac.ui.dialogs.DesenhoDialogFragment
import com.example.fisioplac.ui.dialogs.FecheOlhosDialogFragment

class Step5Fragment : Fragment(), FormStepFragment {

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

        // IDs do XML em PORTUGUÊS
        allDropdowns = listOf(
            binding.actvMemorizacaoNota, binding.actvLinguagemObjetosNota, binding.actvLinguagemFraseNota,
            binding.actvLinguagemEstagiosNota, binding.actvLinguagemLerOrdemNota, binding.actvLinguagemEscreverFraseNota,
            binding.actvLinguagemCopiarDesenhoNota
        )

        setupDropdownMenus()
        setupListeners()
        setupObservers()
        setupTouchToOpenDropdown()
    }

    /**
     * Valida se todos os campos de dropdown foram preenchidos.
     * Retorna FALSE se algum estiver vazio e marca o campo com erro.
     */
    private fun validateFields(): Boolean {
        var isValid = true
        for (dropdown in allDropdowns) {
            if (dropdown.text.isNullOrEmpty()) {
                dropdown.error = "Obrigatório" // Sinalização visual
                isValid = false
            } else {
                dropdown.error = null // Limpa o erro
            }
        }
        return isValid
    }

    private fun setupListeners() {
        // Navegação
        binding.botaoProximo.setOnClickListener {
            // Validação ocorre APENAS no clique
            if (validateFields()) {
                val data = collectDataFromUi()
                viewModel.onStep5NextClicked(data)
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão Voltar (se existir no layout, adicione aqui)
        // binding.botaoVoltar.setOnClickListener { viewModel.onBackClicked() }

        // Dialogs
        binding.tvCliqueAquiLerOrdem.setOnClickListener {
            FecheOlhosDialogFragment().show(childFragmentManager, "FecheOlhosDialog")
        }

        binding.tvCliqueAquiVerDesenho.setOnClickListener {
            DesenhoDialogFragment().show(childFragmentManager, "DesenhoDialog")
        }

        // Cálculo em tempo real (sem validação bloqueante)
        allDropdowns.forEach { dropdown ->
            dropdown.setOnItemClickListener { _, _, _, _ ->
                calculateAndShowResult()
                // Limpa o erro se o usuário selecionar algo
                dropdown.error = null
            }
        }
    }

    private fun setupObservers() {
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            if (ficha != null) {
                populateUi(ficha)
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            binding.progressBar.isVisible = state.isLoading

            // Botão sempre habilitado, exceto durante loading
            binding.botaoProximo.isEnabled = !state.isLoading
            binding.botaoProximo.text = if (state.isLoading) "Salvando..." else "Avançar"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown()
            }
        })
    }

    // --- O RESTANTE DO CÓDIGO PERMANECE IGUAL ---

    private fun populateUi(ficha: GeriatricFicha) {
        binding.actvMemorizacaoNota.setText(ficha.pontuacaoMemorizacao.toString(), false)
        binding.actvLinguagemObjetosNota.setText(ficha.linguagemNomearObjetos.toString(), false)
        binding.actvLinguagemFraseNota.setText(ficha.linguagemRepetirFrase.toString(), false)
        binding.actvLinguagemEstagiosNota.setText(ficha.linguagemComandoEstagios.toString(), false)
        binding.actvLinguagemLerOrdemNota.setText(ficha.linguagemLerOrdem.toString(), false)
        binding.actvLinguagemEscreverFraseNota.setText(ficha.linguagemEscreverFrase.toString(), false)
        binding.actvLinguagemCopiarDesenhoNota.setText(ficha.linguagemCopiarDesenho.toString(), false)

        if (ficha.diagnosticoEstadoMental.isNotBlank()) {
            binding.tvResultadoFinal.text = "${ficha.diagnosticoEstadoMental}, ${ficha.pontuacaoTotalEstadoMental} pontos."
        } else {
            binding.tvResultadoFinal.text = ""
        }
    }

    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!
        val (totalScore, diagnostico) = calculateAndShowResult()

        return currentFicha.copy(
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

    private fun calculateAndShowResult(): Pair<Int, String> {
        val notaMemorizacao = binding.actvMemorizacaoNota.text.toString().toIntOrNull() ?: 0
        val notaObjetos = binding.actvLinguagemObjetosNota.text.toString().toIntOrNull() ?: 0
        val notaFrase = binding.actvLinguagemFraseNota.text.toString().toIntOrNull() ?: 0
        val notaEstagios = binding.actvLinguagemEstagiosNota.text.toString().toIntOrNull() ?: 0
        val notaLerOrdem = binding.actvLinguagemLerOrdemNota.text.toString().toIntOrNull() ?: 0
        val notaEscreverFrase = binding.actvLinguagemEscreverFraseNota.text.toString().toIntOrNull() ?: 0
        val notaCopiarDesenho = binding.actvLinguagemCopiarDesenhoNota.text.toString().toIntOrNull() ?: 0

        val totalScoreStep5 = notaMemorizacao + notaObjetos + notaFrase + notaEstagios +
                notaLerOrdem + notaEscreverFrase + notaCopiarDesenho

        val fichaAtual = viewModel.formData.value
        val totalScoreStep4 = (fichaAtual?.pontuacaoOrientacaoTempo ?: 0) +
                (fichaAtual?.pontuacaoOrientacaoLocal ?: 0) +
                (fichaAtual?.pontuacaoRegistroPalavras ?: 0) +
                (fichaAtual?.pontuacaoCalculo ?: 0)

        val pontuacaoTotal = totalScoreStep4 + totalScoreStep5

        val escolaridade = viewModel.formData.value?.escolaridade ?: ""
        val escolaridadeMenos4Anos = when(escolaridade) {
            "Analfabeto", "Fundamental I Incompleto" -> true
            else -> false
        }

        val diagnostico = obterDiagnostico(pontuacaoTotal, escolaridadeMenos4Anos)

        binding.tvResultadoFinal.text = "$diagnostico, $pontuacaoTotal pontos."
        return Pair(pontuacaoTotal, diagnostico)
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
        val context = context ?: return
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchToOpenDropdown() {
        allDropdowns.forEach { dropdown ->
            dropdown.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
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
        super.onDestroyView()
        _binding = null
    }
}