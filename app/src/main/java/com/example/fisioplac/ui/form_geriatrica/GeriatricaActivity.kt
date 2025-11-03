package com.example.fisioplac.ui.form_geriatrica // 1. PACOTE ATUALIZADO

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels // Importação do ViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fisioplac.R // Importação do R
import com.example.fisioplac.Tela8FichaActivity // Importação da Tela8
import com.example.fisioplac.ui.home.HomeActivity // Importação da Home
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

// NOTA: Esta Activity ainda usa findViewById. O ideal seria usar ViewBinding,
// mas para isso, precisaríamos do seu arquivo activity_geriatrica.xml
// para adicionar um <ProgressBar> e garantir os IDs.

class GeriatricaActivity : AppCompatActivity() {

    // 2. Remove as instâncias do Firebase
    // private lateinit var auth: FirebaseAuth
    // private lateinit var db: FirebaseFirestore

    // 3. Obtém a instância do ViewModel
    private val viewModel: GeriatricaViewModel by viewModels()

    // Componentes da UI (permanecem)
    private lateinit var cpfEditText: EditText
    private lateinit var cardPatientInfo: MaterialCardView
    private lateinit var textViewPatientName: TextView
    private lateinit var textViewBirthdate: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewSex: TextView
    private lateinit var iniciarFichaButton: Button

    // Remove variáveis de estado (agora estão no ViewModel)
    // private var foundPatientId: String? = null
    // private var selectedSpecialty: String? = null
    // private var isPatientSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geriatrica)

        // 1. Inicializa os componentes da UI
        initializeViews()

        // 2. Recebe a especialidade e envia para o ViewModel
        val specialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (specialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        viewModel.setSpecialty(specialty)

        // 3. Configura os Listeners
        setupListeners()

        // 4. Configura a navegação
        setupNavigation()

        // 5. OBSERVA o ViewModel
        observeUiState()
    }

    private fun initializeViews() {
        cpfEditText = findViewById(R.id.edit_text_cpf)
        cardPatientInfo = findViewById(R.id.card_patient_info)
        textViewPatientName = findViewById(R.id.text_view_patient_name)
        textViewBirthdate = findViewById(R.id.text_view_birthdate)
        textViewAge = findViewById(R.id.text_view_age)
        textViewSex = findViewById(R.id.text_view_sex)
        iniciarFichaButton = findViewById(R.id.btn_iniciar_ficha)
        // Adicione um ProgressBar ao seu XML e inicialize-o aqui
        // progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupListeners() {
        // O TextWatcher agora só DELEGA para o ViewModel
        cpfEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.onCpfChanged(s.toString())
            }
        })

        // O clique no card só DELEGA para o ViewModel
        cardPatientInfo.setOnClickListener {
            viewModel.onPatientCardClicked()
        }

        // O clique no botão de iniciar ficha
        iniciarFichaButton.setOnClickListener {
            // Pega o ID do paciente do estado atual do ViewModel
            val patientId = viewModel.uiState.value?.foundPatientId
            val patientName = textViewPatientName.text.toString()

            val intent = Intent(this, Tela8FichaActivity::class.java)
            intent.putExtra("PACIENTE_ID", patientId)
            intent.putExtra("PACIENTE_NOME", patientName)
            startActivity(intent)
        }
    }

    /**
     * Esta é a função principal da View. Ela "reage" às mudanças
     * de estado vindas do ViewModel.
     */
    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->

            // Controla o Loading (idealmente, um ProgressBar)
            // progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            cpfEditText.isEnabled = !state.isLoading

            // Mostra o card se o paciente foi encontrado
            cardPatientInfo.visibility = if (state.isPatientFound) View.VISIBLE else View.GONE

            // Preenche os dados do paciente se eles existirem
            state.patientProfile?.let { profile ->
                textViewPatientName.text = profile.name
                textViewBirthdate.text = profile.birthDate
                textViewAge.text = profile.age
                textViewSex.text = profile.sex
            }

            // Controla a seleção (cor de fundo) do card
            val cardColor = if (state.isPatientSelected) R.color.verde_claro else R.color.white
            cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, cardColor))

            // Controla se o botão principal está habilitado
            iniciarFichaButton.isEnabled = state.canInitiateFicha

            // Mostra mensagens de erro
            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.onErrorMessageShown() // Avisa o ViewModel que a msg foi mostrada
            }
        }
    }

    // A lógica de busca (searchPatient, resetPatientSelection, populatePatientCard)
    // foi COMPLETAMENTE REMOVIDA DA ACTIVITY.

    private fun setupNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        val backButton: ImageButton = findViewById(R.id.btn_back)

        bottomNav.selectedItemId = R.id.nav_new_file

        backButton.setOnClickListener {
            finish()
        }

        bottomNav.setOnItemSelectedListener { item ->
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