package com.example.fisioplac.ui.form_geriatrica

// --- 1. ADICIONAR ESTAS IMPORTAÇÕES ---
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment // <-- IMPORTANTE
import androidx.lifecycle.lifecycleScope // <-- IMPORTANTE
import com.example.fisioplac.R
import com.example.fisioplac.databinding.ActivityGeriatricaBinding
import com.example.fisioplac.ui.home.HomeActivity
import kotlinx.coroutines.flow.collectLatest // <-- IMPORTANTE
import kotlinx.coroutines.launch // <-- IMPORTANTE

class GeriatricaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeriatricaBinding
    private val viewModel: GeriatricFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeriatricaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val specialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (specialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        viewModel.setSpecialty(specialty)

        setupListeners()
        setupNavigation()
        observeUiState() // <-- Chamada está correta
    }

    /**
     * 2. MUDANÇA: 'observe' (LiveData) -> 'lifecycleScope.launch' (StateFlow)
     * Trocamos a forma de observar o ViewModel para ser compatível com StateFlow.
     */
    private fun observeUiState() {
        // Inicia uma Coroutine no escopo do ciclo de vida da Activity
        lifecycleScope.launch {
            // 'collectLatest' é a forma moderna de "observar"
            viewModel.uiState.collectLatest { state ->
                // Agora 'state' é o seu GeriatricFormUiState
                // Os erros ('isLoading', 'isPatientFound', etc.) vão sumir
                // quando seu ViewModel estiver com o UiState correto.

                // Controla o Loading
                binding.editTextCpf.isEnabled = !state.isLoading

                // Mostra o card se o paciente foi encontrado
                binding.cardPatientInfo.visibility = if (state.isPatientFound) View.VISIBLE else View.GONE

                // Preenche os dados do paciente se eles existirem
                state.patientProfile?.let { profile ->
                    binding.textViewPatientName.text = profile.name
                    binding.textViewBirthdate.text = profile.birthDate
                    binding.textViewAge.text = profile.age
                    binding.textViewSex.text = profile.sex
                }

                // Controla a seleção (cor de fundo) do card
                val cardColor = if (state.isPatientSelected) R.color.verde_claro else R.color.white
                // Usamos 'this@GeriatricaActivity' para o Contexto
                binding.cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this@GeriatricaActivity, cardColor))

                // Controla se o botão principal está habilitado
                binding.btnIniciarFicha.isEnabled = state.canInitiateFicha

                // Mostra mensagens de erro
                state.errorMessage?.let { message ->
                    Toast.makeText(this@GeriatricaActivity, message, Toast.LENGTH_SHORT).show()
                    viewModel.onErrorMessageShown() // Avisa o ViewModel que a msg foi mostrada
                }
            }
        }
    }

    private fun setupListeners() {
        // Botão de Voltar (já está em setupNavigation, mas ok)
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Lógica de busca no CPF
        binding.editTextCpf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onCpfChanged(s.toString())
            }
        })

        // Clique no Card do Paciente
        binding.cardPatientInfo.setOnClickListener {
            viewModel.onPatientCardClicked()
        }

        // Botão "Iniciar Ficha"
        binding.btnIniciarFicha.setOnClickListener {
            // --- 3. MUDANÇA: Navegação para Fragment, não para Activity ---
            val state = viewModel.uiState.value // Pega o estado atual
            val patientId = state.foundPatientId
            val patientProfile = state.patientProfile

            if (patientId != null && patientProfile != null) {

                // Avisa o ViewModel para preparar os dados da Tela 1
                viewModel.onInitiateFicha(patientId, patientProfile)

                // Esconde a UI de CPF e mostra o container do Fragment
                // (ASSUMINDO que seu XML tem 'layout_cpf' e 'fragment_container')
                // Se você não tiver, adicione-os ao seu XML
                // binding.layoutCpf.visibility = View.GONE
                // binding.fragmentContainer.visibility = View.VISIBLE

                // Carrega o Tela1FichaFragment
                navigateTo(Tela1FichaFragment())

            } else {
                Toast.makeText(this, "Erro: ID ou Nome do paciente não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 4. NOVO: Função helper para carregar os Fragments
     * Você PRECISA ter um <FragmentContainerView> no seu XML 'activity_geriatrica.xml'
     * com o id 'fragment_container' para isso funcionar.
     */
    fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // <-- Certifique-se que R.id.fragment_container existe no XML
            .addToBackStack(null) // Permite voltar com o botão "back"
            .commit()
    }

    private fun setupNavigation() {
        binding.bottomNavigationBar.selectedItemId = R.id.nav_new_file

        // Este btnBack é duplicado do setupListeners(), mas deixei
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }
                R.id.nav_new_file -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}