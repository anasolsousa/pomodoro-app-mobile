package anabianca.pomodorofocusapp

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Color
import android.view.Gravity

class NoteActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var notesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        // Encontrar o botão pelo ID
        val btnGoToTasks = findViewById<Button>(R.id.btnGoToTasks)

        //  abrir a PageTarefas
        btnGoToTasks.setOnClickListener {
            val intent = Intent(this, PageTarefas::class.java)
            startActivity(intent)
        }


        db = FirebaseFirestore.getInstance()

        val noteEditText: EditText = findViewById(R.id.noteEditText)
        val saveNoteButton: Button = findViewById(R.id.saveNoteButton)
        notesContainer = findViewById(R.id.notesContainer)

        loadNotes()

        val btnVoltar: ImageButton = findViewById(R.id.btnVoltarTraz)
        btnVoltar.setOnClickListener {
            finish() // Usa finish() em vez de startActivity para voltar
        }

        saveNoteButton.setOnClickListener {
            val noteText = noteEditText.text.toString()
            if (noteText.isNotEmpty()) {
                saveNote(noteText)
                noteEditText.text.clear()
            } else {
                Toast.makeText(this, "A nota está vazia!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNote(noteText: String) {
        val note = hashMapOf(
            "text" to noteText,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("notes")
            .add(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Nota salva com sucesso!", Toast.LENGTH_SHORT).show()
                loadNotes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar nota: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNotes() {
        notesContainer.removeAllViews()

        db.collection("notes")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val noteText = document.getString("text") ?: ""
                    addNoteToView(noteText, document.id)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar notas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addNoteToView(noteText: String, noteId: String) {
        // Criar o CardView para a nota
        val cardView = CardView(this).apply {
            radius = 15f
            cardElevation = 5f
            useCompatPadding = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        // Container para o texto e botão
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(25, 25, 25, 25)
            gravity = Gravity.CENTER_VERTICAL
        }

        // TextView para o texto da nota
        val noteTextView = TextView(this).apply {
            text = noteText
            textSize = 16f
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        // Botão de excluir
        val deleteButton = Button(this).apply {
            text = "Excluir"
            setTextColor(Color.WHITE)
            setBackgroundColor(resources.getColor(R.color.laranja, theme))
            textSize = 14f
            setTextColor(Color.WHITE)
            background = resources.getDrawable(R.drawable.delete_button, theme)
            minimumWidth = 0
            minimumHeight = 0
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
            }
            setOnClickListener {
                deleteNote(noteId)
            }
        }

        // Montar a hierarquia de views
        container.addView(noteTextView)
        container.addView(deleteButton)
        cardView.addView(container)
        notesContainer.addView(cardView)
    }

    private fun deleteNote(noteId: String) {
        db.collection("notes").document(noteId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Nota excluída com sucesso!", Toast.LENGTH_SHORT).show()
                loadNotes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao excluir nota: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}