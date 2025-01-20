package anabianca.pomodorofocusapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast

class PageTarefas : AppCompatActivity() {
    private val colors = listOf(
        "#dbeafe", // lilás
        "#dcfce7", // verde
        "#fce7f3", // Rosa claro
        "#fef3c7",// amarelo
        "#e4e4e7" // cinza
    )
    private var colorIndex = 0 // Índice inicial para alternar cores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_page_tarefas)

        // código do botao Add+
        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val taskContainer = findViewById<LinearLayout>(R.id.taskContainer)
        val doneContainer = findViewById<LinearLayout>(R.id.sectionDone)

        btnAddTask.setOnClickListener {
            val taskView = LayoutInflater.from(this).inflate(R.layout.item_task, taskContainer, false) as LinearLayout

            // Encontra os elementos no layout e altera a cores
            val taskEditText = taskView.findViewById<TextView>(R.id.taskEditText)
            val taskCheckBox = taskView.findViewById<CheckBox>(R.id.taskCheckBox)
            val btnSafe = taskView.findViewById<Button>(R.id.btnSafe)
            val btnDelete = taskView.findViewById<Button>(R.id.btnDelete)

            // criei pra testar se funciona o add+
            //taskText.text = "New Task ${taskContainer.childCount + 1}"
            taskView.setBackgroundColor(Color.parseColor(colors[colorIndex])) //define a cor aleatória
            colorIndex = (colorIndex + 1) % colors.size
            taskContainer.addView(taskView)

            // Mensagem de feedback
            Toast.makeText(this, "Task added! You can edit it.!", Toast.LENGTH_SHORT).show() // mensagem de feedback
            taskEditText.requestFocus()


            // Código do botao save
            btnSafe.setOnClickListener {
                val taskText = taskEditText.text.toString()

                if (taskText.isNotEmpty()) {
                    val taskTextView = TextView(this)
                    taskTextView.text = taskText
                    taskTextView.textSize = 16f
                    taskTextView.setTextColor(Color.parseColor("#333333"))
                    taskTextView.setPadding(8, 8, 8, 8)

                    val parentLayout = taskView as LinearLayout
                    parentLayout.removeView(taskEditText)
                    parentLayout.addView(taskTextView)

                    // desativa o botao de deletar tarefas
                    val btnDelete = taskView.findViewById<Button>(R.id.btnDelete)
                    btnDelete.visibility = View.GONE // Inicialmente invisível

                    // Código de Desativar o botão após salvar
                    btnSafe.isEnabled = false
                    btnSafe.text = "Saved"

                    // Tornar o botão Save e o CheckBox invisíveis após clicar
                    btnSafe.visibility = View.GONE

                    // Exibe uma mensagem de feedback para o usuário
                    Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                }
            }

            // Lógica para mover a tarefa para Done quando marcada como check
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Quando o CheckBox for marcado  a tarefa passa pra Done
                    taskContainer.removeView(taskView)
                    doneContainer.addView(taskView)

                    // botao de deletar fica visivel
                    btnDelete.visibility = View.VISIBLE

                    // Alterar o fundo ou estilo para indicar que foi concluído
                    taskView.setBackgroundColor(Color.parseColor("#c3e88d")) // Alterando a cor para verde

                    // Mensagem de feedback - pode tirar depois se quiser
                    Toast.makeText(this, "Task marked as done!", Toast.LENGTH_SHORT).show()
                } else {
                    doneContainer.removeView(taskView)
                    taskContainer.addView(taskView)
                    btnDelete.visibility = View.GONE
                    taskView.setBackgroundColor(Color.parseColor(colors[colorIndex]))
                }
            }
            btnDelete.setOnClickListener {
                doneContainer.removeView(taskView)
                Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        // Código da seta de voltar da tarefas pra notes
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
            finish() // Fecha a atividade atual

        }
    }
}
