package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fisioplac.databinding.FragmentStep9Binding // XML que você vai criar

class Step9Fragment : Fragment() {

    private var _binding: FragmentStep9Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep9Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        // Navegação (VM em INGLÊS, IDs em PORTUGUÊS)
        // O XML deste step terá 'botaoAvancar' em vez de 'botaoConcluir'
        // pois ele navega para o próximo, não salva.
        binding.botaoProximo.setOnClickListener {
            // Este step não salva dados, apenas avança
            viewModel.onStep9NextClicked()
        }
    }

    private fun setupObservers() {
        // VM em INGLÊS
        viewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            // IDs em PORTUGUÊS
            binding.progressBar.isVisible = state.isLoading
            binding.botaoProximo.isEnabled = !state.isLoading
            binding.botaoProximo.text = if (state.isLoading) "..." else "Avançar"

            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // VM em INGLÊS
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}