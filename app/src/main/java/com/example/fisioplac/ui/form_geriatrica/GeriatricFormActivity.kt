package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback // <-- IMPORT ADICIONADO
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fisioplac.R
import com.example.fisioplac.databinding.ActivityGeriatricFormBinding

class GeriatricFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeriatricFormBinding
    private val viewModel: GeriatricFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeriatricFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pacienteId = intent.getStringExtra("PACIENTE_ID")
        val pacienteNome = intent.getStringExtra("PACIENTE_NOME")
        if (pacienteId == null) {
            Toast.makeText(this, "Erro: Paciente ID nulo.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.startForm(pacienteId, pacienteNome)

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