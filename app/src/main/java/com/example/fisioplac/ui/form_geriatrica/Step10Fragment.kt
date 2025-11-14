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
import com.example.fisioplac.databinding.FragmentStep10Binding // Você precisará criar fragment_step_10.xml

class Step10Fragment : Fragment() {

    private var _binding: FragmentStep10Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    // Lista para validação (lógica em INGLÊS)
    private lateinit var fieldsToValidate: List<EditText>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep10Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assumindo que os IDs no XML serão em português (ex: etPa0, etFc3, ...)
        fieldsToValidate = listOf(
            binding.etPa0, binding.etPa3, binding.etPa6,
            binding.etFc0, binding.etFc3, binding.etFc6,
            binding.etSat0, binding.etSat3, binding.etSat6,
            binding.etFr0, binding.etFr3, binding.etFr6,
            binding.etBorg0, binding.etBorg3, binding.etBorg6,
            binding.etDistanciaPercorrida,
            binding.etDistanciaPredita
        )

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        // IDs em PORTUGUÊS, VM em INGLÊS
        binding.botaoProximo.setOnClickListener {
            if (validateFields()) {
                val data = collectDataFromUi()
                // Como esta é a última tela (por enquanto), ela chama a função de concluir
                viewModel.onStep10NextClicked(data)
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
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
        binding.etPa0.setText(ficha.tc6mPa0)
        binding.etPa3.setText(ficha.tc6mPa3)
        binding.etPa6.setText(ficha.tc6mPa6)
        binding.etFc0.setText(ficha.tc6mFc0)
        binding.etFc3.setText(ficha.tc6mFc3)
        binding.etFc6.setText(ficha.tc6mFc6)
        binding.etSat0.setText(ficha.tc6mSat0)
        binding.etSat3.setText(ficha.tc6mSat3)
        binding.etSat6.setText(ficha.tc6mSat6)
        binding.etFr0.setText(ficha.tc6mFr0)
        binding.etFr3.setText(ficha.tc6mFr3)
        binding.etFr6.setText(ficha.tc6mFr6)
        binding.etBorg0.setText(ficha.tc6mBorg0)
        binding.etBorg3.setText(ficha.tc6mBorg3)
        binding.etBorg6.setText(ficha.tc6mBorg6)
        binding.etDistanciaPercorrida.setText(ficha.tc6mDistanciaPercorrida)
        binding.etDistanciaPredita.setText(ficha.tc6mDistanciaPredita)
    }

    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
    private fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
            tc6mPa0 = binding.etPa0.text.toString(),
            tc6mPa3 = binding.etPa3.text.toString(),
            tc6mPa6 = binding.etPa6.text.toString(),
            tc6mFc0 = binding.etFc0.text.toString(),
            tc6mFc3 = binding.etFc3.text.toString(),
            tc6mFc6 = binding.etFc6.text.toString(),
            tc6mSat0 = binding.etSat0.text.toString(),
            tc6mSat3 = binding.etSat3.text.toString(),
            tc6mSat6 = binding.etSat6.text.toString(),
            tc6mFr0 = binding.etFr0.text.toString(),
            tc6mFr3 = binding.etFr3.text.toString(),
            tc6mFr6 = binding.etFr6.text.toString(),
            tc6mBorg0 = binding.etBorg0.text.toString(),
            tc6mBorg3 = binding.etBorg3.text.toString(),
            tc6mBorg6 = binding.etBorg6.text.toString(),
            tc6mDistanciaPercorrida = binding.etDistanciaPercorrida.text.toString(),
            tc6mDistanciaPredita = binding.etDistanciaPredita.text.toString()
        )
    }

    /**
     * Valida se todos os campos de EditText na lista 'fieldsToValidate' estão preenchidos.
     */
    private fun validateFields(): Boolean {
        var allFieldsValid = true
        for (field in fieldsToValidate) {
            if (field.text.isBlank()) {
                field.error = "Obrigatório"
                allFieldsValid = false
            } else {
                field.error = null // Limpa o erro
            }
        }
        return allFieldsValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}