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

    // Listas de CheckBox (lógica em INGLÊS)
    private lateinit var timeCheckBoxes: List<CheckBox>
    private lateinit var locationCheckBoxes: List<CheckBox>
    private lateinit var wordCheckBoxes: List<CheckBox>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Funções do Fragment em INGLÊS
        // Os IDs do binding aqui (ex: binding.cbTempoMes) devem ser em português,
        // de acordo com o XML que você vai criar.
        initializeCheckBoxLists()
        setupDropdowns()
        setupListeners()
        setupObservers()
    }

    private fun initializeCheckBoxLists() {
        // (Os IDs do binding aqui devem ser em português, ex: binding.cbTempoMes)
        timeCheckBoxes = listOf(binding.cbTempoMes, binding.cbTempoDia, binding.cbTempoAno, binding.cbTempoDiaSemana, binding.cbTempoAproximado)
        locationCheckBoxes = listOf(binding.cbLocalEstado, binding.cbLocalCidade, binding.cbLocalBairro, binding.cbLocalLocal, binding.cbLocalPais)
        wordCheckBoxes = listOf(binding.cbPalavraCarro, binding.cbPalavraVaso, binding.cbPalavraTijolo)
    }

    private fun setupDropdowns() {
        val mathScoreOptions = (0..5).map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mathScoreOptions)
        // (ID do binding em português)
        binding.autoCompletePontuacaoCalculo.setAdapter(adapter)
    }

    private fun setupListeners() {
        // Listeners simples para calcular pontuações na UI
        timeCheckBoxes.forEach { it.setOnCheckedChangeListener { _, _ -> calculateTimeScore() } }
        locationCheckBoxes.forEach { it.setOnCheckedChangeListener { _, _ -> calculateLocationScore() } }
        wordCheckBoxes.forEach { it.setOnCheckedChangeListener { _, _ -> calculateWordsScore() } }

        // (IDs do binding em português)
        binding.rgCalculo.setOnCheckedChangeListener { _, checkedId ->
            binding.tvCalculoInstrucaoSim.isVisible = checkedId == R.id.rbCalculoSim
            binding.tvCalculoInstrucaoNao.isVisible = checkedId == R.id.rbCalculoNao
        }

        binding.botaoProximo.setOnClickListener {
            val data = collectDataFromUi()
            viewModel.onStep4NextClicked(data)
        }
    }

    // Funções de cálculo separadas (lógica em INGLÊS)
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
        // Observa VM em INGLÊS
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            if (ficha != null) {
                populateUi(ficha)
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            // IDs do XML em PORTUGUÊS
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

        // Garante que o estado de visibilidade das instruções de cálculo seja restaurado
        binding.tvCalculoInstrucaoSim.isVisible = ficha.calculoRealiza == "Sim"
        binding.tvCalculoInstrucaoNao.isVisible = ficha.calculoRealiza == "Não"
    }

    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value ?: GeriatricFicha()

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
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