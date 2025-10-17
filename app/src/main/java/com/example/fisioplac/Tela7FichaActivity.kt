package com.example.fisioplac

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.databinding.ActivityTela7FichaBinding
import com.example.fisioplac.databinding.ItemBarthelBinding

class Tela7FichaActivity : AppCompatActivity() {

    private data class BarthelOption(val description: String, val score: Int)
    private lateinit var binding: ActivityTela7FichaBinding
    private val currentScores = mutableMapOf<String, Int>()
    private val completedItems = mutableSetOf<String>()
    private val categories = listOf(
        "Alimentação", "Banho", "Atividades diárias", "Vestir-se", "Intestino",
        "Sistema urinário", "Uso do banheiro", "Transferência (Cama-Cadeira)",
        "Mobilidade em superfícies planas", "Escadas"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTela7FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fichaProgressBar.max = categories.size

        categories.forEach { category ->
            currentScores[category] = 0
        }

        setupBarthelItem(binding.itemAlimentacao, "Alimentação", listOf(BarthelOption("Incapaz", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10)))
        setupBarthelItem(binding.itemBanho, "Banho", listOf(BarthelOption("Dependente", 0), BarthelOption("Independente", 5)))
        setupBarthelItem(binding.itemAtividadesDiarias, "Atividades diárias", listOf(BarthelOption("Precisa de ajuda", 0), BarthelOption("Independente", 5)))
        setupBarthelItem(binding.itemVestir, "Vestir-se", listOf(BarthelOption("Dependente", 0), BarthelOption("Precisa de ajuda, mas realiza parte", 5), BarthelOption("Independente", 10)))
        setupBarthelItem(binding.itemIntestino, "Intestino", listOf(BarthelOption("Incontinente", 0), BarthelOption("Acidente ocasional", 5), BarthelOption("Continente", 10)))
        setupBarthelItem(binding.itemUrinario, "Sistema urinário", listOf(BarthelOption("Incontinente", 0), BarthelOption("Acidente ocasional", 5), BarthelOption("Continente", 10)))
        setupBarthelItem(binding.itemUsoBanheiro, "Uso do banheiro", listOf(BarthelOption("Dependente", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10)))
        setupBarthelItem(binding.itemTransferencia, "Transferência (Cama-Cadeira)", listOf(BarthelOption("Incapacitado", 0), BarthelOption("Muita ajuda (1 ou 2 pessoas)", 5), BarthelOption("Pouca ajuda (verbal ou fisica)", 10), BarthelOption("Independente", 15)))
        setupBarthelItem(binding.itemMobilidade, "Mobilidade em superfícies planas", listOf(BarthelOption("Imóvel ou < 50m", 0), BarthelOption("Independente em cadeira de rodas > 50m", 5), BarthelOption("Anda com ajuda de 1 pessoa", 10), BarthelOption("Independente (pode usar auxílio)", 15)))
        setupBarthelItem(binding.itemEscadas, "Escadas", listOf(BarthelOption("Incapaz", 0), BarthelOption("Precisa de ajuda", 5), BarthelOption("Independente", 10)))

        updateTotalScore()
        updateProgressBar()
    }

    private fun setupBarthelItem(itemBinding: ItemBarthelBinding, hint: String, options: List<BarthelOption>) {
        itemBinding.menu.hint = hint
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options.map { it.description })
        itemBinding.autoCompleteTextView.setAdapter(adapter)

        itemBinding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedOption = options[position]
            // Esta linha depende do ID 'etScore' no arquivo item_barthel.xml
            itemBinding.etScore.setText(selectedOption.score.toString())
            currentScores[hint] = selectedOption.score
            completedItems.add(hint)
            updateTotalScore()
            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        binding.fichaProgressBar.progress = completedItems.size
    }

    private fun updateTotalScore() {
        val totalScore = currentScores.values.sum()
        val dependencyLevel = when (totalScore) {
            in 0..25 -> "Dependência total"
            in 26..50 -> "Dependência severa"
            in 51.. 75-> "Dependência moderada"
            in 76..99 -> "Dependência leve"
            100 -> "Totalmente independente"
            else -> "Nenhuma seleção"
        }
        binding.tvResultado.text = "RESULTADO: $dependencyLevel, $totalScore pontos."
    }
}