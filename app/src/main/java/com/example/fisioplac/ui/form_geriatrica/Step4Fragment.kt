package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fisioplac.R
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.FragmentStep4Binding

class Step4Fragment : Fragment(), FormStepFragment {

    private var _binding: FragmentStep4Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private lateinit var timeCheckBoxes: List<CheckBox>
    private lateinit var locationCheckBoxes: List<CheckBox>
    private lateinit var wordCheckBoxes: List<CheckBox>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeCheckBoxLists()
        setupDropdowns()
        setupListeners()
        setupObservers()
    }

    /**
     * Valida se os campos obrigatórios foram preenchidos.
     * Neste passo, a parte obrigatória é a seção de Cálculo e Atenção.
     */
    private fun validateFields(): Boolean {
        var isValid = true

        // 1. Valida RadioGroup (Sim/Não)
        if (binding.rgCalculo.checkedRadioButtonId == -1) {
            // Exibe erro visual ou Toast específico se preferir
            isValid = false
        }

        // 2. Valida Dropdown de Pontuação
        if (binding.autoCompletePontuacaoCalculo.text.isNullOrEmpty()) {
            binding.autoCompletePontuacaoCalculo.error = "Obrigatório"
            isValid = false
        } else {
            binding.autoCompletePontuacaoCalculo.error = null
        }

        return isValid
    }

    private fun initializeCheckBoxLists() {
        timeCheckBoxes = listOf(binding.cbTempoMes, binding.cbTempoDia, binding.cbTempoAno, binding.cbTempoDiaSemana, binding.cbTempoAproximado)
        locationCheckBoxes = listOf(binding.cbLocalEstado, binding.cbLocalCidade, binding.cbLocalBairro, binding.cbLocalLocal, binding.cbLocalPais)
        wordCheckBoxes = listOf(binding.cbPalavraCarro, binding.cbPalavraVaso, binding.cbPalavraTijolo)
    }

    private fun setupDropdowns() {
        val mathScoreOptions = (0..5).map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mathScoreOptions)
        binding.autoCompletePontuacaoCalculo.setAdapter(adapter)
    }

    private fun setupListeners() {
        timeCheckBoxes.forEach { it.setOnCheckedChangeListener { _, _ -> calculateTimeScore() } }
        locationCheckBoxes.forEach { it.setOnCheckedChangeListener { _, _ -> calculateLocationScore() } }
        wordCheckBoxes.forEach { it.setOnCheckedChangeListener { _, _ -> calculateWordsScore() } }

        binding.rgCalculo.setOnCheckedChangeListener { _, checkedId ->
            binding.tvCalculoInstrucaoSim.isVisible = checkedId == R.id.rbCalculoSim
            binding.tvCalculoInstrucaoNao.isVisible = checkedId == R.id.rbCalculoNao
        }

        // Removemos a limpeza de erro ao selecionar o dropdown
        // binding.autoCompletePontuacaoCalculo.setOnItemClickListener { _, _, _, _ -> binding.autoCompletePontuacaoCalculo.error = null }

        binding.botaoProximo.setOnClickListener {
            // Validação ocorre APENAS no clique
            if (validateFields()) {
                val data = collectDataFromUi()
                viewModel.onStep4NextClicked(data)
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateTimeScore() {
        val timeScore = timeCheckBoxes.count { it.isChecked }
        binding.tvPontuacaoTempo.text = timeScore.toString()
    }

    private fun calculateLocationScore() {
        val locationScore = locationCheckBoxes.count { it.isChecked }
        binding.tvPontuacaoLocal.text = locationScore.toString()
    }

    private fun calculateWordsScore() {
        val wordsScore = wordCheckBoxes.count { it.isChecked }
        binding.tvPontuacaoPalavras.text = wordsScore.toString()
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

    private fun populateUi(ficha: GeriatricFicha) {
        binding.cbTempoMes.isChecked = ficha.orientacaoTempoMes
        binding.cbTempoDia.isChecked = ficha.orientacaoTempoDia
        binding.cbTempoAno.isChecked = ficha.orientacaoTempoAno
        binding.cbTempoDiaSemana.isChecked = ficha.orientacaoTempoDiaSemana
        binding.cbTempoAproximado.isChecked = ficha.orientacaoTempoAproximado
        binding.tvPontuacaoTempo.text = ficha.pontuacaoOrientacaoTempo.toString()

        binding.cbLocalEstado.isChecked = ficha.orientacaoLocalEstado
        binding.cbLocalCidade.isChecked = ficha.orientacaoLocalCidade
        binding.cbLocalBairro.isChecked = ficha.orientacaoLocalBairro
        binding.cbLocalLocal.isChecked = ficha.orientacaoLocalLocal
        binding.cbLocalPais.isChecked = ficha.orientacaoLocalPais
        binding.tvPontuacaoLocal.text = ficha.pontuacaoOrientacaoLocal.toString()

        binding.cbPalavraCarro.isChecked = ficha.registroPalavraCarro
        binding.cbPalavraVaso.isChecked = ficha.registroPalavraVaso
        binding.cbPalavraTijolo.isChecked = ficha.registroPalavraTijolo
        binding.tvPontuacaoPalavras.text = ficha.pontuacaoRegistroPalavras.toString()

        when (ficha.calculoRealiza) {
            "Sim" -> binding.rgCalculo.check(R.id.rbCalculoSim)
            "Não" -> binding.rgCalculo.check(R.id.rbCalculoNao)
            else -> binding.rgCalculo.clearCheck()
        }
        binding.autoCompletePontuacaoCalculo.setText(ficha.pontuacaoCalculo.toString(), false)

        binding.tvCalculoInstrucaoSim.isVisible = ficha.calculoRealiza == "Sim"
        binding.tvCalculoInstrucaoNao.isVisible = ficha.calculoRealiza == "Não"
    }

    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value ?: GeriatricFicha()

        return currentFicha.copy(
            orientacaoTempoMes = binding.cbTempoMes.isChecked,
            orientacaoTempoDia = binding.cbTempoDia.isChecked,
            orientacaoTempoAno = binding.cbTempoAno.isChecked,
            orientacaoTempoDiaSemana = binding.cbTempoDiaSemana.isChecked,
            orientacaoTempoAproximado = binding.cbTempoAproximado.isChecked,
            pontuacaoOrientacaoTempo = binding.tvPontuacaoTempo.text.toString().toIntOrNull() ?: 0,

            orientacaoLocalEstado = binding.cbLocalEstado.isChecked,
            orientacaoLocalCidade = binding.cbLocalCidade.isChecked,
            orientacaoLocalBairro = binding.cbLocalBairro.isChecked,
            orientacaoLocalLocal = binding.cbLocalLocal.isChecked,
            orientacaoLocalPais = binding.cbLocalPais.isChecked,
            pontuacaoOrientacaoLocal = binding.tvPontuacaoLocal.text.toString().toIntOrNull() ?: 0,

            registroPalavraCarro = binding.cbPalavraCarro.isChecked,
            registroPalavraVaso = binding.cbPalavraVaso.isChecked,
            registroPalavraTijolo = binding.cbPalavraTijolo.isChecked,
            pontuacaoRegistroPalavras = binding.tvPontuacaoPalavras.text.toString().toIntOrNull() ?: 0,

            calculoRealiza = when (binding.rgCalculo.checkedRadioButtonId) {
                R.id.rbCalculoSim -> "Sim"
                R.id.rbCalculoNao -> "Não"
                else -> null
            },
            pontuacaoCalculo = binding.autoCompletePontuacaoCalculo.text.toString().toIntOrNull() ?: 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}