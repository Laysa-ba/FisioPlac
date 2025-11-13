package com.example.fisioplac.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.fisioplac.R
import com.example.fisioplac.databinding.DialogDesenhoBinding

class DesenhoDialogFragment : DialogFragment() {

    private var _binding: DialogDesenhoBinding? = null
    private val binding get() = _binding!!

    // *** PASSO 1: Remove a moldura padrão do diálogo ***
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o estilo para STYLE_NO_FRAME, usando o tema padrão (0)
        setStyle(DialogFragment.STYLE_NO_FRAME, 0)
    }

    // *** PASSO 2: Faz o DialogFragment preencher o ecrã ***
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Define o layout para ocupar o ecrã inteiro
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            // *** CORREÇÃO: Adiciona a flag para desenhar sobre a barra de estado ***
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            // Opcional: Remove a sombra/dim extra
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDesenhoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remove o fundo transparente, já que agora é ecrã inteiro
        dialog?.window?.setBackgroundDrawableResource(android.R.color.white)

        // *** IMPORTANTE ***
        // Coloque a sua imagem do desenho em 'res/drawable/'
        // e nomeie-a 'desenho_para_copiar'
        binding.imgDesenho.setImageResource(R.drawable.desenho_para_copiar)

        // O binding agora encontra 'btnFechar' (do XML 'dialog_desenho.xml')
        binding.btnFechar.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}