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
import com.example.fisioplac.databinding.FragmentStep6Binding // XML que você vai criar

class Step6Fragment : Fragment() {

    private var _binding: FragmentStep6Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    private lateinit var allDropdowns: List<AutoCompleteTextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep6Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // IDs do XML em PORTUGUÊS
        allDropdowns = listOf(
            binding.actvOmbroD, binding.actvOmbroE, binding.actvCotoveloD, binding.actvCotoveloE,
            binding.actvPunhoD, binding.actvPunhoE, binding.actvQuadrilD, binding.actvQuadrilE,
            binding.actvJoelhoD, binding.actvJoelhoE, binding.actvTornozeloD, binding.actvTornozeloE
        )

        setupDropdownMenus()
        setupListeners()
        setupObservers()
        setupTouchToOpenDropdown()
    }

    private fun setupDropdownMenus() {
        // Usa o array 'opcoes_nota_0_5' que adicionamos ao arrays.xml
        val arrayResourceId = R.array.opcoes_nota_0_5
        allDropdowns.forEach { dropdown ->
            setupAutoCompleteTextView(dropdown, arrayResourceId)
        }
    }

    private fun setupListeners() {
        // Navegação (VM em INGLÊS, IDs em PORTUGUÊS)
        binding.botaoProximo.setOnClickListener {
            if (validateFields()) {
                val data = collectDataFromUi()
                viewModel.onStep6NextClicked(data) // VM em INGLÊS
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
        binding.actvOmbroD.setText(ficha.forcaOmbroD, false)
        binding.actvOmbroE.setText(ficha.forcaOmbroE, false)
        binding.actvCotoveloD.setText(ficha.forcaCotoveloD, false)
        binding.actvCotoveloE.setText(ficha.forcaCotoveloE, false)
        binding.actvPunhoD.setText(ficha.forcaPunhoD, false)
        binding.actvPunhoE.setText(ficha.forcaPunhoE, false)
        binding.actvQuadrilD.setText(ficha.forcaQuadrilD, false)
        binding.actvQuadrilE.setText(ficha.forcaQuadrilE, false)
        binding.actvJoelhoD.setText(ficha.forcaJoelhoD, false)
        binding.actvJoelhoE.setText(ficha.forcaJoelhoE, false)
        binding.actvTornozeloD.setText(ficha.forcaTornozeloD, false)
        binding.actvTornozeloE.setText(ficha.forcaTornozeloE, false)
    }

    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
    private fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
            forcaOmbroD = binding.actvOmbroD.text.toString(),
            forcaOmbroE = binding.actvOmbroE.text.toString(),
            forcaCotoveloD = binding.actvCotoveloD.text.toString(),
            forcaCotoveloE = binding.actvCotoveloE.text.toString(),
            forcaPunhoD = binding.actvPunhoD.text.toString(),
            forcaPunhoE = binding.actvPunhoE.text.toString(),
            forcaQuadrilD = binding.actvQuadrilD.text.toString(),
            forcaQuadrilE = binding.actvQuadrilE.text.toString(),
            forcaJoelhoD = binding.actvJoelhoD.text.toString(),
            forcaJoelhoE = binding.actvJoelhoE.text.toString(),
            forcaTornozeloD = binding.actvTornozeloD.text.toString(),
            forcaTornozeloE = binding.actvTornozeloE.text.toString()
        )
    }

    /**
     * Valida se todos os campos de dropdown foram preenchidos.
     */
    private fun validateFields(): Boolean {
        for (dropdown in allDropdowns) {
            if (dropdown.text.isNullOrEmpty()) {
                dropdown.error = "Obrigatório"
                return false
            } else {
                dropdown.error = null
            }
        }
        return true
    }

    /**
     * Configura o adapter para um AutoCompleteTextView.
     */
    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        val context = context ?: return
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    }

    /**
     * Permite que o dropdown seja aberto com um simples toque.
     */
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