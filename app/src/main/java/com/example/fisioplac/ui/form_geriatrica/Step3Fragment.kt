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
import com.example.fisioplac.databinding.FragmentStep3Binding

class Step3Fragment : Fragment(), FormStepFragment {

    private var _binding: FragmentStep3Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        setupListeners()
        setupObservers()
    }

    private fun setupDropdowns() {
        val visionHearingOptions = listOf("Normal", "Déficit c/ uso de corretor", "Déficit s/ uso de corretor", "Perda parcial", "Perda total")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, visionHearingOptions)
        binding.autoCompleteVisao.setAdapter(adapter)
        binding.autoCompleteAudicao.setAdapter(adapter)

        val fallCountOptions = listOf("1", "2", "3", "4", "5 ou mais")
        val fallAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, fallCountOptions)
        binding.autoCompleteContagemQuedas.setAdapter(fallAdapter)
    }

    /**
     * Valida se os campos obrigatórios foram preenchidos.
     * Se o RadioGroup estiver marcado como "Sim" ou "Distúrbio",
     * valida se o campo de texto correspondente foi preenchido.
     */
    private fun validateFields(): Boolean {
        var isValid = true

        // Visão e Audição
        if (binding.autoCompleteVisao.text.isNullOrEmpty()) {
            binding.autoCompleteVisao.error = "Obrigatório"
            isValid = false
        }
        if (binding.autoCompleteAudicao.text.isNullOrEmpty()) {
            binding.autoCompleteAudicao.error = "Obrigatório"
            isValid = false
        }

        // RadioGroups e campos condicionais
        // (A lógica aqui é: se o container estiver visível, o campo dentro dele é obrigatório)

        if (binding.llUrinariaData.isVisible && binding.etUrinariaData.text.isNullOrEmpty()) {
            binding.etUrinariaData.error = "Obrigatório"
            isValid = false
        }
        if (binding.llFecalData.isVisible && binding.etFecalData.text.isNullOrEmpty()) {
            binding.etFecalData.error = "Obrigatório"
            isValid = false
        }
        if (binding.llSonoDetalhes.isVisible && binding.etSonoDetalhes.text.isNullOrEmpty()) {
            binding.etSonoDetalhes.error = "Obrigatório"
            isValid = false
        }
        if (binding.llOrteseDetalhes.isVisible && binding.etOrteseDetalhes.text.isNullOrEmpty()) {
            binding.etOrteseDetalhes.error = "Obrigatório"
            isValid = false
        }
        if (binding.llProteseDetalhes.isVisible && binding.etProteseDetalhes.text.isNullOrEmpty()) {
            binding.etProteseDetalhes.error = "Obrigatório"
            isValid = false
        }
        if (binding.llQuedaDetalhes.isVisible && binding.autoCompleteContagemQuedas.text.isNullOrEmpty()) {
            binding.autoCompleteContagemQuedas.error = "Obrigatório"
            isValid = false
        }
        // Para fumante/etilista, o campo aparece se for "Não" (tempo parado)
        if (binding.llFumanteTempoParado.isVisible && binding.etFumanteTempoParado.text.isNullOrEmpty()) {
            binding.etFumanteTempoParado.error = "Obrigatório"
            isValid = false
        }
        if (binding.llEtilistaTempoParado.isVisible && binding.etEtilistaTempoParado.text.isNullOrEmpty()) {
            binding.etEtilistaTempoParado.error = "Obrigatório"
            isValid = false
        }

        // Validação básica dos RadioGroups (deve ter algo selecionado)
        if (binding.rgUrinaria.checkedRadioButtonId == -1) isValid = false
        if (binding.rgFecal.checkedRadioButtonId == -1) isValid = false
        if (binding.rgSono.checkedRadioButtonId == -1) isValid = false
        if (binding.rgOrtese.checkedRadioButtonId == -1) isValid = false
        if (binding.rgProtese.checkedRadioButtonId == -1) isValid = false
        if (binding.rgQueda.checkedRadioButtonId == -1) isValid = false
        if (binding.rgFumante.checkedRadioButtonId == -1) isValid = false
        if (binding.rgEtilista.checkedRadioButtonId == -1) isValid = false


        return isValid
    }

    private fun setupListeners() {
        binding.rgUrinaria.setOnCheckedChangeListener { _, checkedId ->
            binding.llUrinariaData.isVisible = checkedId == R.id.rbUrinariaSim
        }
        binding.rgFecal.setOnCheckedChangeListener { _, checkedId ->
            binding.llFecalData.isVisible = checkedId == R.id.rbFecalSim
        }
        binding.rgSono.setOnCheckedChangeListener { _, checkedId ->
            binding.llSonoDetalhes.isVisible = checkedId == R.id.rbSonoDisturbio
        }
        binding.rgOrtese.setOnCheckedChangeListener { _, checkedId ->
            binding.llOrteseDetalhes.isVisible = checkedId == R.id.rbOrteseSim
        }
        binding.rgProtese.setOnCheckedChangeListener { _, checkedId ->
            binding.llProteseDetalhes.isVisible = checkedId == R.id.rbProteseSim
        }
        binding.rgQueda.setOnCheckedChangeListener { _, checkedId ->
            binding.llQuedaDetalhes.isVisible = checkedId == R.id.rbQuedaSim
        }
        binding.rgFumante.setOnCheckedChangeListener { _, checkedId ->
            binding.llFumanteTempoParado.isVisible = checkedId == R.id.rbFumanteNao
        }
        binding.rgEtilista.setOnCheckedChangeListener { _, checkedId ->
            binding.llEtilistaTempoParado.isVisible = checkedId == R.id.rbEtilistaNao
        }

        binding.botaoProximo.setOnClickListener {
            // Validação APENAS no clique
            if (validateFields()) {
                val data = collectDataFromUi()
                viewModel.onStep3NextClicked(data)
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
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

            // Botão sempre habilitado, exceto carregando
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
        binding.autoCompleteVisao.setText(ficha.visao, false)
        binding.autoCompleteAudicao.setText(ficha.audicao, false)

        when (ficha.incontinenciaUrinaria) {
            "Sim" -> binding.rgUrinaria.check(R.id.rbUrinariaSim)
            "Não" -> binding.rgUrinaria.check(R.id.rbUrinariaNao)
            else -> binding.rgUrinaria.clearCheck()
        }
        binding.etUrinariaData.setText(ficha.incontinenciaUrinariaData)
        binding.llUrinariaData.isVisible = ficha.incontinenciaUrinaria == "Sim"

        when (ficha.incontinenciaFecal) {
            "Sim" -> binding.rgFecal.check(R.id.rbFecalSim)
            "Não" -> binding.rgFecal.check(R.id.rbFecalNao)
            else -> binding.rgFecal.clearCheck()
        }
        binding.etFecalData.setText(ficha.incontinenciaFecalData)
        binding.llFecalData.isVisible = ficha.incontinenciaFecal == "Sim"

        when (ficha.sono) {
            "Normal" -> binding.rgSono.check(R.id.rbSonoNormal)
            "Distúrbios" -> binding.rgSono.check(R.id.rbSonoDisturbio)
            else -> binding.rgSono.clearCheck()
        }
        binding.etSonoDetalhes.setText(ficha.sonoDisturbioDetalhes)
        binding.llSonoDetalhes.isVisible = ficha.sono == "Distúrbios"

        when (ficha.usaOrtese) {
            "Sim" -> binding.rgOrtese.check(R.id.rbOrteseSim)
            "Não" -> binding.rgOrtese.check(R.id.rbOrteseNao)
            else -> binding.rgOrtese.clearCheck()
        }
        binding.etOrteseDetalhes.setText(ficha.orteseDetalhes)
        binding.llOrteseDetalhes.isVisible = ficha.usaOrtese == "Sim"

        when (ficha.usaProtese) {
            "Sim" -> binding.rgProtese.check(R.id.rbProteseSim)
            "Não" -> binding.rgProtese.check(R.id.rbProteseNao)
            else -> binding.rgProtese.clearCheck()
        }
        binding.etProteseDetalhes.setText(ficha.proteseDetalhes)
        binding.llProteseDetalhes.isVisible = ficha.usaProtese == "Sim"

        when (ficha.teveQuedaUltimos12Meses) {
            "Sim" -> binding.rgQueda.check(R.id.rbQuedaSim)
            "Não" -> binding.rgQueda.check(R.id.rbQuedaNao)
            else -> binding.rgQueda.clearCheck()
        }
        binding.autoCompleteContagemQuedas.setText(ficha.contagemQuedas, false)
        binding.llQuedaDetalhes.isVisible = ficha.teveQuedaUltimos12Meses == "Sim"

        when (ficha.eFumante) {
            "Sim" -> binding.rgFumante.check(R.id.rbFumanteSim)
            "Não" -> binding.rgFumante.check(R.id.rbFumanteNao)
            else -> binding.rgFumante.clearCheck()
        }
        binding.etFumanteTempoParado.setText(ficha.parouFumarTempo)
        binding.llFumanteTempoParado.isVisible = ficha.eFumante == "Não"

        when (ficha.eEtilista) {
            "Sim" -> binding.rgEtilista.check(R.id.rbEtilistaSim)
            "Não" -> binding.rgEtilista.check(R.id.rbEtilistaNao)
            else -> binding.rgEtilista.clearCheck()
        }
        binding.etEtilistaTempoParado.setText(ficha.parouBeberTempo)
        binding.llEtilistaTempoParado.isVisible = ficha.eEtilista == "Não"
    }

    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value ?: GeriatricFicha()

        return currentFicha.copy(
            visao = binding.autoCompleteVisao.text.toString(),
            audicao = binding.autoCompleteAudicao.text.toString(),

            incontinenciaUrinaria = when (binding.rgUrinaria.checkedRadioButtonId) {
                R.id.rbUrinariaSim -> "Sim"
                R.id.rbUrinariaNao -> "Não"
                else -> null
            },
            incontinenciaUrinariaData = binding.etUrinariaData.text.toString().takeIf { binding.llUrinariaData.isVisible },

            incontinenciaFecal = when (binding.rgFecal.checkedRadioButtonId) {
                R.id.rbFecalSim -> "Sim"
                R.id.rbFecalNao -> "Não"
                else -> null
            },
            incontinenciaFecalData = binding.etFecalData.text.toString().takeIf { binding.llFecalData.isVisible },

            sono = when (binding.rgSono.checkedRadioButtonId) {
                R.id.rbSonoNormal -> "Normal"
                R.id.rbSonoDisturbio -> "Distúrbios"
                else -> null
            },
            sonoDisturbioDetalhes = binding.etSonoDetalhes.text.toString().takeIf { binding.llSonoDetalhes.isVisible },

            usaOrtese = when (binding.rgOrtese.checkedRadioButtonId) {
                R.id.rbOrteseSim -> "Sim"
                R.id.rbOrteseNao -> "Não"
                else -> null
            },
            orteseDetalhes = binding.etOrteseDetalhes.text.toString().takeIf { binding.llOrteseDetalhes.isVisible },

            usaProtese = when (binding.rgProtese.checkedRadioButtonId) {
                R.id.rbProteseSim -> "Sim"
                R.id.rbProteseNao -> "Não"
                else -> null
            },
            proteseDetalhes = binding.etProteseDetalhes.text.toString().takeIf { binding.llProteseDetalhes.isVisible },

            teveQuedaUltimos12Meses = when (binding.rgQueda.checkedRadioButtonId) {
                R.id.rbQuedaSim -> "Sim"
                R.id.rbQuedaNao -> "Não"
                else -> null
            },
            contagemQuedas = binding.autoCompleteContagemQuedas.text.toString().takeIf { binding.llQuedaDetalhes.isVisible },

            eFumante = when (binding.rgFumante.checkedRadioButtonId) {
                R.id.rbFumanteSim -> "Sim"
                R.id.rbFumanteNao -> "Não"
                else -> null
            },
            parouFumarTempo = binding.etFumanteTempoParado.text.toString().takeIf { binding.llFumanteTempoParado.isVisible },

            eEtilista = when (binding.rgEtilista.checkedRadioButtonId) {
                R.id.rbEtilistaSim -> "Sim"
                R.id.rbEtilistaNao -> "Não"
                else -> null
            },
            parouBeberTempo = binding.etEtilistaTempoParado.text.toString().takeIf { binding.llEtilistaTempoParado.isVisible }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}