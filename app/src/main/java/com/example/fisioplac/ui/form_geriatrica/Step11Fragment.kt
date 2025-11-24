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
import com.example.fisioplac.databinding.FragmentStep11Binding // XML que você vai criar

class Step11Fragment : Fragment(), FormStepFragment {

    private var _binding: FragmentStep11Binding? = null
    private val binding get() = _binding!!

    // ViewModel em INGLÊS
    private val viewModel: GeriatricFormViewModel by activityViewModels()

    // Mapas para armazenar as notas base (lógica em INGLÊS)
    private val notasBase = mapOf(
        0 to 5.0, // Sem apoios
        1 to 4.0, // Com 1 apoio
        2 to 3.0, // Com 2 apoios
        3 to 2.0, // Com 3 apoios
        4 to 1.0, // Com 4 apoios
        5 to 0.0  // 4+ apoios ou ajuda externa
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStep11Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        setupListeners()
        setupObservers()
    }

    private fun setupDropdowns() {
        // Pega o array de strings 'opcoes_apoio' (que precisa estar no arrays.xml)
        val apoiosArray = resources.getStringArray(R.array.opcoes_apoio)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, apoiosArray)

        // IDs em PORTUGUÊS
        binding.actvSentarApoios.setAdapter(adapter)
        binding.actvLevantarApoios.setAdapter(adapter)

        // Limpa os campos iniciais
        binding.actvSentarApoios.setText("", false)
        binding.actvLevantarApoios.setText("", false)
    }

    private fun setupListeners() {
        // --- Listener para o dropdown de SENTAR ---
        binding.actvSentarApoios.setOnItemClickListener { parent, _, position, _ ->
            val selection = parent.adapter.getItem(position).toString()
            binding.actvSentarApoios.setText(selection, false)
            calcularNotaSentar(position, binding.cbSentarDesequilibrio.isChecked)
            binding.actvSentarApoios.clearFocus()
        }

        // --- Listener para o checkbox de SENTAR ---
        binding.cbSentarDesequilibrio.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = getPositionFromDropdown(binding.actvSentarApoios)
            if (currentPosition != -1) {
                calcularNotaSentar(currentPosition, isChecked)
            }
        }

        // --- Listener para o dropdown de LEVANTAR ---
        binding.actvLevantarApoios.setOnItemClickListener { parent, _, position, _ ->
            val selection = parent.adapter.getItem(position).toString()
            binding.actvLevantarApoios.setText(selection, false)
            calcularNotaLevantar(position, binding.cbLevantarDesequilibrio.isChecked)
            binding.actvLevantarApoios.clearFocus()
        }

        // --- Listener para o checkbox de LEVANTAR ---
        binding.cbLevantarDesequilibrio.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = getPositionFromDropdown(binding.actvLevantarApoios)
            if (currentPosition != -1) {
                calcularNotaLevantar(currentPosition, isChecked)
            }
        }

        // --- Navegação ---
        binding.botaoProximo.setOnClickListener {
            if (validateFields()) {
                val data = collectDataFromUi()
                viewModel.onStep11NextClicked(data) // VM em INGLÊS
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Touch Listeners para abrir os dropdowns ---
        setupTouchToOpenDropdown(binding.actvSentarApoios)
        setupTouchToOpenDropdown(binding.actvLevantarApoios)
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
        val sentarApoios = ficha.equilibrioSentarApoios
        val levantarApoios = ficha.equilibrioLevantarApoios
        val sentarDesequilibrio = ficha.equilibrioSentarDesequilibrio
        val levantarDesequilibrio = ficha.equilibrioLevantarDesequilibrio

        binding.actvSentarApoios.setText(sentarApoios, false)
        binding.cbSentarDesequilibrio.isChecked = sentarDesequilibrio
        binding.actvLevantarApoios.setText(levantarApoios, false)
        binding.cbLevantarDesequilibrio.isChecked = levantarDesequilibrio

        // Recalcula as notas com base nos dados preenchidos
        val sentarPosition = getPositionFromDropdown(binding.actvSentarApoios)
        if (sentarPosition != -1) {
            calcularNotaSentar(sentarPosition, sentarDesequilibrio)
        } else {
            binding.tvSentarNota.text = "0.0"
        }

        val levantarPosition = getPositionFromDropdown(binding.actvLevantarApoios)
        if (levantarPosition != -1) {
            calcularNotaLevantar(levantarPosition, levantarDesequilibrio)
        } else {
            binding.tvLevantarNota.text = "0.0"
        }
    }

    /**
     * Coleta todos os dados da UI.
     * (Campos do Modelo em PORTUGUÊS)
     */
    override fun collectDataFromUi(): GeriatricFicha {
        val currentFicha = viewModel.formData.value!!

        return currentFicha.copy(
            // Campos do Modelo em PORTUGUÊS, IDs do XML em PORTUGUÊS
            equilibrioSentarApoios = binding.actvSentarApoios.text.toString(),
            equilibrioSentarDesequilibrio = binding.cbSentarDesequilibrio.isChecked,
            equilibrioSentarNota = binding.tvSentarNota.text.toString().toDoubleOrNull() ?: 0.0,

            equilibrioLevantarApoios = binding.actvLevantarApoios.text.toString(),
            equilibrioLevantarDesequilibrio = binding.cbLevantarDesequilibrio.isChecked,
            equilibrioLevantarNota = binding.tvLevantarNota.text.toString().toDoubleOrNull() ?: 0.0
        )
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (binding.actvSentarApoios.text.isNullOrEmpty()) {
            binding.actvSentarApoios.error = "Obrigatório"
            isValid = false
        } else {
            binding.actvSentarApoios.error = null
        }

        if (binding.actvLevantarApoios.text.isNullOrEmpty()) {
            binding.actvLevantarApoios.error = "Obrigatório"
            isValid = false
        } else {
            binding.actvLevantarApoios.error = null
        }
        return isValid
    }

    /**
     * Calcula e atualiza a nota da seção SENTAR.
     */
    private fun calcularNotaSentar(position: Int, desequilibrio: Boolean) {
        val notaBase = notasBase[position] ?: 0.0
        val notaFinal = if (desequilibrio) notaBase - 0.5 else notaBase
        binding.tvSentarNota.text = notaFinal.toString()
    }

    /**
     * Calcula e atualiza a nota da seção LEVANTAR.
     */
    private fun calcularNotaLevantar(position: Int, desequilibrio: Boolean) {
        val notaBase = notasBase[position] ?: 0.0
        val notaFinal = if (desequilibrio) notaBase - 0.5 else notaBase
        binding.tvLevantarNota.text = notaFinal.toString()
    }

    /**
     * Helper para descobrir a posição selecionada no dropdown.
     */
    private fun getPositionFromDropdown(dropdown: AutoCompleteTextView): Int {
        val adapter = dropdown.adapter as? ArrayAdapter<String>
        val currentText = dropdown.text.toString()
        if (currentText.isEmpty()) {
            return -1
        }
        return adapter?.getPosition(currentText) ?: -1
    }

    /**
     * Permite que o dropdown seja aberto com um simples toque.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchToOpenDropdown(dropdown: AutoCompleteTextView) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}