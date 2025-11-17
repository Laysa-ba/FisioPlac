package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fisioplac.R
import com.example.fisioplac.databinding.ActivityGeriatricFormBinding
import com.example.fisioplac.UserSession

class GeriatricFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeriatricFormBinding
    private val viewModel: GeriatricFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeriatricFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- INÍCIO DO BLOCO DE CÓDIGO CORRIGIDO ---

        // 1. Busca o ID do paciente (obrigatório)
        val pacienteId = intent.getStringExtra("PACIENTE_ID")
        if (pacienteId == null) {
            Toast.makeText(this, "Erro: Paciente ID nulo.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Busca o nome do Doutor/Estagiário logado
        val nomeDoutor = UserSession.doctorName ?: "Doutor(a)"

        // 3. Busca TODOS os dados do paciente vindos do intent
        // (Estes dados foram enviados pela GeriatricaActivity)
        val pacienteNome = intent.getStringExtra("PACIENTE_NOME")
        val pacienteNascimento = intent.getStringExtra("PACIENTE_NASCIMENTO")
        val pacienteSexo = intent.getStringExtra("PACIENTE_SEXO")
        val pacienteEstadoCivil = intent.getStringExtra("PACIENTE_ESTADO_CIVIL")
        val pacienteTelefone = intent.getStringExtra("PACIENTE_TELEFONE")
        val pacienteEscolaridade = intent.getStringExtra("PACIENTE_ESCOLARIDADE")
        val pacienteRenda = intent.getStringExtra("PACIENTE_RENDA")
        val pacienteLocalResidencia = intent.getStringExtra("PACIENTE_LOCAL_RESIDENCIA")
        val pacienteMoraCom = intent.getStringExtra("PACIENTE_MORA_COM")

        // 4. Cria o objeto PacienteInfo
        val infoDoPaciente = PacienteInfo(
            nome = pacienteNome,
            dataNascimento = pacienteNascimento,
            sexo = pacienteSexo,
            estadoCivil = pacienteEstadoCivil,
            telefone = pacienteTelefone,
            escolaridade = pacienteEscolaridade,
            renda = pacienteRenda,
            localResidencia = pacienteLocalResidencia,
            moraCom = pacienteMoraCom
        )

        // 5. CHAMA A FUNÇÃO CORRETA do ViewModel
        // A linha antiga "viewModel.startForm(pacienteId, pacienteNome)" foi substituída
        viewModel.startForm(
            pacienteId = pacienteId,
            pacienteInfo = infoDoPaciente,
            nomeDoutor = nomeDoutor
        )

        // --- FIM DO BLOCO DE CÓDIGO CORRIGIDO ---

        // Assumindo 12 steps no total
        binding.fichaProgressBar.max = 12

        // Lógica para a SETA NO TOPO (isto já estava correto)
        binding.backArrow.setOnClickListener {
            // 1. Encontra o fragmento que está visível
            val currentFragment = supportFragmentManager.findFragmentById(R.id.id_fragment_container)

            // 2. Verifica se esse fragmento é um 'FormStepFragment' (se ele sabe salvar dados)
            if (currentFragment is FormStepFragment) {

                // Exceção: Não salvar dados no Step 9 (que é só de passagem)
                if (currentFragment !is Step9Fragment) {
                    // 3. Se for, coleta os dados dele
                    val data = currentFragment.collectDataFromUi()
                    // 4. Envia os dados para o ViewModel (para salvar o estado)
                    viewModel.updateFormData(data)
                }
            }

            // 5. Agora, manda o ViewModel navegar para trás
            viewModel.onBackClicked()
        }

        // *** LÓGICA PARA O BOTÃO "VOLTAR" DO SISTEMA (Android) ***
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Executa a mesma lógica da seta no topo
                val currentFragment = supportFragmentManager.findFragmentById(R.id.id_fragment_container)
                if (currentFragment is FormStepFragment) {
                    if (currentFragment !is Step9Fragment) {
                        val data = currentFragment.collectDataFromUi()
                        viewModel.updateFormData(data)
                    }
                }
                viewModel.onBackClicked()
            }
        })
        // *** FIM DO NOVO BLOCO ***

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentStep.value?.let { step ->
            navigateToStep(step)
        }
    }

    private fun observeViewModel() {
        viewModel.currentStep.observe(this) { step ->
            binding.fichaProgressBar.progress = step
            navigateToStep(step)
        }

        viewModel.closeForm.observe(this) { shouldClose ->
            if (shouldClose) {
                finish()
            }
        }

        // Observa o sucesso do salvamento
        viewModel.formSaveSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Ficha salva com sucesso!", Toast.LENGTH_LONG).show()
                finish() // Fecha a Activity do formulário
            }
        }
    }

    private fun navigateToStep(step: Int) {
        val fragment: Fragment = when (step) {
            1 -> Step1Fragment()
            2 -> Step2Fragment()
            3 -> Step3Fragment()
            4 -> Step4Fragment()
            5 -> Step5Fragment()
            6 -> Step6Fragment()
            7 -> Step7Fragment()
            8 -> Step8Fragment()
            9 -> Step9Fragment()
            10 -> Step10Fragment()
            11 -> Step11Fragment()
            12 -> Step12Fragment()
            else -> Step1Fragment()
        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.id_fragment_container)
        if (currentFragment == null || currentFragment::class.java != fragment::class.java) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, fragment)
                .commit()
        }
    }
}