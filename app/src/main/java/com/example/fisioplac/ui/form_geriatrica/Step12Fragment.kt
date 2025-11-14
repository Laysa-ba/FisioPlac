package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.databinding.FragmentStep12Binding
import com.example.fisioplac.ui.dialogs.ConfirmSaveDialogFragment // 1. IMPORTAR O NOVO DIALOG

class Step12Fragment : Fragment() {

    private var _binding: FragmentStep12Binding? = null
    private val binding get() = _binding!!

    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private lateinit var fieldsToValidate: List<EditText>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep12Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldsToValidate = listOf(
            binding.etDiagnostico,
            binding.etObjetivos
        )

        setupListeners()
        setupObservers()

        // 2. ADICIONAR O "OUVINTE" DO POP-UP
        // Escuta a resposta do ConfirmSaveDialogFragment
        childFragmentManager.setFragmentResultListener("confirmSaveRequest", viewLifecycleOwner) { _, bundle ->
            val result = bundle.getBoolean("result")
            if (result) {
                // Se o usuário clicou "Sim, salvar", nós coletamos os dados
                // e chamamos o ViewModel para salvar.
                val data = collectDataFromUi()
                viewModel.onStep12NextClicked(data)
            }
            // Se for falso (clicou "Cancelar"), não faz nada.
        }
    }

    private fun setupListeners() {
        // 3. ATUALIZAR O CLIQUE DO BOTÃO
        binding.botaoConcluir.setOnClickListener {
            if (validateFields()) {
                // Em vez de salvar, AGORA NÓS MOSTRAMOS O POP-UP
                ConfirmSaveDialogFragment().show(childFragmentManager, "ConfirmSaveDialog")
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
            binding.botaoConcluir.isEnabled = !state.isLoading
            binding.botaoConcluir.text = if (state.isLoading) "Salvando..." else "Finalizar Ficha"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown()
            }
        })
    }

    private fun populateUi(ficha: GeriatricFicha) {
        binding.etDiagnostico.setText(ficha.diagnosticoFisioterapeutico)
        binding.etObjetivos.setText(ficha.objetivosTratamento)
    }

    private fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!

        return currentFicha.copy(
            diagnosticoFisioterapeutico = binding.etDiagnostico.text.toString().trim(),
            objetivosTratamento = binding.etObjetivos.text.toString().trim()
        )
    }

    private fun validateFields(): Boolean {
        var allFieldsValid = true
        for (field in fieldsToValidate) {
            if (field.text.isBlank()) {
                field.error = "Obrigatório"
                allFieldsValid = false
            } else {
                field.error = null
            }
        }
        return allFieldsValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}