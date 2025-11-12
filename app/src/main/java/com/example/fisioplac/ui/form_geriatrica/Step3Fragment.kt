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
import com.example.fisioplac.databinding.FragmentStep3Binding // Usa o binding do XML traduzido

class Step3Fragment : Fragment() {

    // Usa o binding para o layout com IDs em português
    private var _binding: FragmentStep3Binding? = null
    private val binding get() = _binding!!

    // 1. Usa o ViewModel compartilhado (com nomes de função em INGLÊS)
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Funções do Fragment em INGLÊS
        setupDropdowns()
        setupListeners()
        setupObservers()
    }

    private fun setupDropdowns() {
        val visionHearingOptions = listOf("Normal", "Déficit c/ uso de corretor", "Déficit s/ uso de corretor", "Perda parcial", "Perda total")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, visionHearingOptions)
        // IDs do XML em PORTUGUÊS
        binding.autoCompleteVisao.setAdapter(adapter)
        binding.autoCompleteAudicao.setAdapter(adapter)

        val fallCountOptions = listOf("1", "2", "3", "4", "5 ou mais")
        val fallAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, fallCountOptions)
        // IDs do XML em PORTUGUÊS
        binding.autoCompleteContagemQuedas.setAdapter(fallAdapter)
    }

    private fun setupListeners() {
        // --- Lógica de visibilidade (usando IDs em PORTUGUÊS) ---
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

        // --- Botões de Navegação (IDs em PORTUGUÊS, Funções do VM em INGLÊS) ---
        binding.botaoProximo.setOnClickListener {
            val data = collectDataFromUi()
            viewModel.onStep3NextClicked(data) // VM em INGLÊS
        }

        binding.botaoVoltar.setOnClickListener {
            viewModel.onBackClicked() // VM em INGLÊS
        }
    }

    private fun setupObservers() {
        // Observa o LiveData do VM em INGLÊS
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            if (ficha != null) {
                populateUi(ficha)
            }
        }

        // Observa o LiveData do VM em INGLÊS
        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            // IDs do XML em PORTUGUÊS
            binding.progressBar.isVisible = state.isLoading
            binding.botaoProximo.isEnabled = !state.isLoading
            binding.botaoVoltar.isEnabled = !state.isLoading
            binding.botaoProximo.text = if (state.isLoading) "Salvando..." else "Concluir"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // VM em INGLÊS
            }
        })
    }

    /**
     * Preenche a UI com os dados do ViewModel.
     * (Campos do Modelo em PORTUGUÊS)
     * *** LÓGICA ATUALIZADA PARA String? E clearCheck() ***
     */
    private fun populateUi(ficha: GeriatricFicha) {
        // IDs em PORTUGUÊS, Campos do Modelo em PORTUGUÊS
        binding.autoCompleteVisao.setText(ficha.visao, false)
        binding.autoCompleteAudicao.setText(ficha.audicao, false)

        // --- CORREÇÃO: Usa 'when' para lidar com 'null' e 'clearCheck' ---
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

    private fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value ?: GeriatricFicha()

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
            visao = binding.autoCompleteVisao.text.toString(),
            audicao = binding.autoCompleteAudicao.text.toString(),

            // --- CORREÇÃO: Salva 'null' se nada for selecionado ---
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