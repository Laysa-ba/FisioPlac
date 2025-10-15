package com.example.fisioplac

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.databinding.ActivityTela1FichaBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Tela1FichaActivity : AppCompatActivity() {

    // Declaração da variável de binding para acessar as views do XML de forma segura
    private lateinit var binding: ActivityTela1FichaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando View Binding e define como o conteúdo da tela
        binding = ActivityTela1FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura todos os menus suspensos (AutoCompleteTextView)
        setupDropdownMenus()

        // Configura os listeners de clique para os botões e outros componentes
        setupClickListeners()

        // Configura a lógica para o RadioGroup de atividades físicas
        setupRadioGroupLogic()

        // Define a data atual no campo de data
        setCurrentDate()
    }

    /**
     * Configura todos os AutoCompleteTextViews com seus respectivos adaptadores e listas de opções.
     */
    private fun setupDropdownMenus() {
        // Mapeia cada AutoCompleteTextView para o seu array de strings correspondente
        val dropdownMap = mapOf(
            binding.actvEstadoCivil to R.array.opcoes_estado_civil,
            binding.actvEscolaridade to R.array.opcoes_escolaridade,
            binding.actvLocalResidencia to R.array.opcoes_local_residencia,
            binding.actvMoraCom to R.array.opcoes_mora_com,
            binding.actvDiasSemana to R.array.opcoes_dias_semana,
            binding.actvAtividadeSocial to R.array.opcoes_atividade_social,
            binding.actvDoencas to R.array.opcoes_doencas
        )

        // Itera sobre o mapa e configura cada um
        dropdownMap.forEach { (autoCompleteTextView, arrayResourceId) ->
            setupAutoCompleteTextView(autoCompleteTextView, arrayResourceId)
        }
    }

    /**
     * Função auxiliar para configurar um único AutoCompleteTextView.
     * @param view O componente AutoCompleteTextView a ser configurado.
     * @param arrayResourceId O ID do recurso do array de strings a ser usado.
     */
    private fun setupAutoCompleteTextView(view: AutoCompleteTextView, arrayResourceId: Int) {
        val items = resources.getStringArray(arrayResourceId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        view.setAdapter(adapter)
    }

    /**
     * Configura os listeners de clique para os componentes interativos da tela.
     */
    private fun setupClickListeners() {
        // Seta de voltar: finaliza a activity atual
        binding.backArrow.setOnClickListener {
            finish()
        }

        // Botão Avançar: valida os campos e exibe uma mensagem
        binding.btnAvancar.setOnClickListener {
            if (validateFields()) {
                Toast.makeText(this, "Ficha preenchida, avançando...", Toast.LENGTH_SHORT).show()
                // Aqui você adicionaria a lógica para ir para a próxima tela
                // Ex: val intent = Intent(this, ProximaTelaActivity::class.java)
                // startActivity(intent)
            } else {
                Toast.makeText(this, "Aviso: Todos os dados são obrigatórios!", Toast.LENGTH_LONG).show()
            }
        }

        // Campos de data: abrem um seletor de data ao serem clicados
        binding.etData.setOnClickListener { showDatePickerDialog(binding.etData) }
        binding.etNascimento.setOnClickListener { showDatePickerDialog(binding.etNascimento) }
    }

    /**
     * Controla a visibilidade do campo "dias na semana" com base na seleção
     * do RadioGroup de atividades físicas.
     */
    private fun setupRadioGroupLogic() {
        // Esconde o campo de dias por padrão, pois a opção "Não" já vem marcada
        binding.diasSemanaContainer.visibility = View.GONE

        binding.rgAtividadesFisicas.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_sim_atividade) {
                // Se "Sim" for selecionado, mostra o campo
                binding.diasSemanaContainer.visibility = View.VISIBLE
            } else {
                // Se "Não" for selecionado, esconde o campo e limpa o texto
                binding.diasSemanaContainer.visibility = View.GONE
                binding.actvDiasSemana.text.clear()
            }
        }
    }


    /**
     * Exibe um DatePickerDialog para facilitar a seleção de datas.
     * @param editText O campo de texto onde a data selecionada será exibida.
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Formata a data e a define no EditText
                val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    /**
     * Define a data atual no campo de data da ficha.
     */
    private fun setCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.etData.setText(currentDate)
    }

    /**
     * Valida se os campos de texto obrigatórios foram preenchidos.
     * @return true se todos os campos estiverem preenchidos, false caso contrário.
     */
    private fun validateFields(): Boolean {
        // Lista de todos os EditTexts que são obrigatórios
        val fieldsToValidate = listOf(
            binding.etData, binding.etEstagiario, binding.etNome,
            binding.etNascimento, binding.etIdade, binding.etTelefone,
            binding.etRenda
            // Adicione outros campos se necessário. Ex: binding.etQueixaPrincipal
        )

        var allFieldsValid = true
        for (field in fieldsToValidate) {
            if (field.text.toString().trim().isEmpty()) {
                field.error = "Campo obrigatório"
                allFieldsValid = false
            } else {
                field.error = null // Limpa o erro se o campo for preenchido
            }
        }
        return allFieldsValid
    }
}