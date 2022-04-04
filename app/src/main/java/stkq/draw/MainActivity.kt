package stkq.draw

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.MaterialToolbar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawBoard = findViewById<DrawBoard>(R.id.board)
        val appBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_undo -> {
                    drawBoard.undo()
                    true
                }
                R.id.action_redo -> {
                    drawBoard.redo()
                    true
                }
                R.id.action_clear -> {
                    drawBoard.clear()
                    true
                }
                R.id.action_brush -> {
                    val picker = BrushPicker(drawBoard.brush)
                    picker.onNewBrush = { drawBoard.brush = it }
                    val heightDp = resources.displayMetrics.run { heightPixels / density }
                    Toast.makeText(applicationContext, heightDp.toString(), Toast.LENGTH_LONG)
                    if (heightDp >= 670) {
                        picker.show(supportFragmentManager, "brushPicker")
                    } else {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction
                            .add(android.R.id.content, picker)
                            .addToBackStack("brushPicker")
                            .commit()
                    }
                    true
                }
                else -> false
            }
        }
    }
}