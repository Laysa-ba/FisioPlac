package com.example.fisioplac.ui.form_geriatrica

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fisioplac.R
import com.example.fisioplac.data.model.Medicamento
import com.example.fisioplac.databinding.FragmentStep2Binding

class Step2Fragment : Fragment() {

    private var _binding: FragmentStep2Binding? = null
    private val binding get() = _binding!!

    // 1. Obtém o ViewModel COMPARTILHADO
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private lateinit var medicamentoAdapter: MedicamentoAdapter

    // Esta é a lista que o adapter realmente usa
    private val listaDeMedicamentosAdapter = mutableListOf<Medicamento>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        setupValidationListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        medicamentoAdapter = MedicamentoAdapter(
            listaDeMedicamentosAdapter, // Passa a lista correta para o adapter
            onItemClicked = { position ->
                // Delega o clique para o ViewModel
                viewModel.toggleMedicamentoExpanded(position)
            }
        )
        binding.rvMedicamentos.adapter = medicamentoAdapter
        binding.rvMedicamentos.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * *** FUNÇÃO CORRIGIDA ***
     * Observa o ViewModel para atualizar a UI.
     */
    private fun observeViewModel() {
        // Observa a lista de medicamentos do ViewModel
        viewModel.formData.observe(viewLifecycleOwner) { ficha ->
            // Atualiza a lista do adapter SEMPRE que o ViewModel mudar
            if (listaDeMedicamentosAdapter != ficha.medicamentos) {
                listaDeMedicamentosAdapter.clear()
                listaDeMedicamentosAdapter.addAll(ficha.medicamentos)
                medicamentoAdapter.notifyDataSetChanged() // Notifica o adapter
            }

            // Controla a visibilidade da lista
            val hasMeds = listaDeMedicamentosAdapter.isNotEmpty()
            binding.llHeaderLista.visibility = if (hasMeds) View.VISIBLE else View.GONE
            binding.rvMedicamentos.visibility = if (hasMeds) View.VISIBLE else View.GONE
        }

        // Observa o estado da UI (Loading, Erro)
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // --- CORREÇÃO APLICADA AQUI ---
            // O estado agora é uma 'data class', então acessamos as propriedades
            val isLoading = state.isLoading
            binding.btnConcluir.isEnabled = !isLoading
            binding.btnAdicionar.isEnabled = !isLoading
            binding.btnConcluir.text = if (isLoading) "Salvando..." else "Concluir"

            // Mostra o Toast de erro
            state.errorMessage?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // Reseta o erro
            }
            // --- FIM DA CORREÇÃO ---
        }
    }

    private fun setupClickListeners() {
        binding.btnAdicionar.setOnClickListener {
            // Valida os campos antes de adicionar
            if (validateMedicamentoFields()) {
                val nome = binding.etNomeMedicamento.text.toString().trim()
                val comoUsar = binding.etComoUsar.text.toString().trim()
                val tempoUso = binding.etTempoUso.text.toString().trim()

                // 1. Envia o novo medicamento para o ViewModel
                viewModel.addMedicamento(nome, comoUsar, tempoUso)

                // 2. Limpa os campos e esconde o teclado (Lógica de UI)
                binding.etNomeMedicamento.text?.clear()
                binding.etComoUsar.text?.clear()
                binding.etTempoUso.text?.clear()
                hideKeyboard()
                binding.main.requestFocus()
            }
        }

        binding.btnConcluir.setOnClickListener {
            // Apenas notifica o ViewModel. A lista já está lá.
            viewModel.onConcluirClicked()
        }
    }

    private fun validateMedicamentoFields(): Boolean {
        binding.tilNomeMedicamento.error = null
        binding.tilComoUsar.error = null
        binding.tilTempoUso.error = null

        val nome = binding.etNomeMedicamento.text.toString().trim()
        val comoUsar = binding.etComoUsar.text.toString().trim()
        val tempoUso = binding.etTempoUso.text.toString().trim()
        var isFormValid = true

        if (nome.isEmpty()) {
            binding.tilNomeMedicamento.error = "Campo obrigatório"
            isFormValid = false
        }
        if (comoUsar.isEmpty()) {
            binding.tilComoUsar.error = "Campo obrigatório"
            isFormValid = false
        }
        if (tempoUso.isEmpty()) {
            binding.tilTempoUso.error = "Campo obrigatório"
            isFormValid = false
        }
        return isFormValid
    }

    private fun setupValidationListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilNomeMedicamento.error != null && !binding.etNomeMedicamento.text.isNullOrBlank()) {
                    binding.tilNomeMedicamento.error = null
                }
                if (binding.tilComoUsar.error != null && !binding.etComoUsar.text.isNullOrBlank()) {
                    binding.tilComoUsar.error = null
                }
                if (binding.tilTempoUso.error != null && !binding.etTempoUso.text.isNullOrBlank()) {
                    binding.tilTempoUso.error = null
                }
            }
        }
        binding.etNomeMedicamento.addTextChangedListener(textWatcher)
        binding.etComoUsar.addTextChangedListener(textWatcher)
        binding.etTempoUso.addTextChangedListener(textWatcher)
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}