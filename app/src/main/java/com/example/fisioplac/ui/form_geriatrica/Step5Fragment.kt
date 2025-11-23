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

            binding.botaoProximo.isEnabled = !state.isLoading
            binding.botaoProximo.text = if (state.isLoading) "Salvando..." else "Avançar"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown()
            }
        })
    }

    /**
     * CORREÇÃO AQUI: Lógica para exibir vazio se o valor for -1 ou 0 inicial.
     */
    private fun populateUi(ficha: GeriatricFicha) {
        // Se não tem diagnóstico, é ficha nova: 0 deve ser mostrado como vazio
        val isFichaNova = ficha.diagnosticoEstadoMental.isEmpty()

        fun formatScore(value: Int): String {
            // Se é -1 (vazio salvo) ou 0 numa ficha nova, mostra vazio
            if (value == -1 || (isFichaNova && value == 0)) return ""
            return value.toString()
        }

        binding.actvMemorizacaoNota.setText(formatScore(ficha.pontuacaoMemorizacao), false)
        binding.actvLinguagemObjetosNota.setText(formatScore(ficha.linguagemNomearObjetos), false)
        binding.actvLinguagemFraseNota.setText(formatScore(ficha.linguagemRepetirFrase), false)
        binding.actvLinguagemEstagiosNota.setText(formatScore(ficha.linguagemComandoEstagios), false)
        binding.actvLinguagemLerOrdemNota.setText(formatScore(ficha.linguagemLerOrdem), false)
        binding.actvLinguagemEscreverFraseNota.setText(formatScore(ficha.linguagemEscreverFrase), false)
        binding.actvLinguagemCopiarDesenhoNota.setText(formatScore(ficha.linguagemCopiarDesenho), false)

        if (ficha.diagnosticoEstadoMental.isNotBlank()) {
            binding.tvResultadoFinal.text = "${ficha.diagnosticoEstadoMental}, ${ficha.pontuacaoTotalEstadoMental} pontos."
        } else {
            binding.tvResultadoFinal.text = ""
        }
    }

    /**
     * CORREÇÃO AQUI: Salva -1 se o campo estiver vazio.
     */
    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!
        val (totalScore, diagnostico) = calculateAndShowResult()

        fun parse(view: AutoCompleteTextView): Int {
            // Se estiver vazio, retorna -1
            return view.text.toString().toIntOrNull() ?: -1
        }

        return currentFicha.copy(
            pontuacaoMemorizacao = parse(binding.actvMemorizacaoNota),
            linguagemNomearObjetos = parse(binding.actvLinguagemObjetosNota),
            linguagemRepetirFrase = parse(binding.actvLinguagemFraseNota),
            linguagemComandoEstagios = parse(binding.actvLinguagemEstagiosNota),
            linguagemLerOrdem = parse(binding.actvLinguagemLerOrdemNota),
            linguagemEscreverFrase = parse(binding.actvLinguagemEscreverFraseNota),
            linguagemCopiarDesenho = parse(binding.actvLinguagemCopiarDesenhoNota),
            pontuacaoTotalEstadoMental = totalScore,
            diagnosticoEstadoMental = diagnostico
        )
    }

    private fun validateFields(): Boolean {
        var isValid = true
        for (dropdown in allDropdowns) {
            if (dropdown.text.isNullOrEmpty()) {
                dropdown.error = "Obrigatório"
                isValid = false
            } else {
                dropdown.error = null
            }
        }
        return isValid
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
        // Para cálculo, vazio conta como 0
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