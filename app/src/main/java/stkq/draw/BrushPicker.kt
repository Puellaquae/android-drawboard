package stkq.draw

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class BrushPicker(private val oldBrush: DrawBoard.BrushStyle) : DialogFragment() {
    var onNewBrush: ((brush: DrawBoard.BrushStyle) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val brushPicker = inflater.inflate(R.layout.brush_picker_fullscreen, container, false)
        val appBar = brushPicker.findViewById<MaterialToolbar>(R.id.topAppBar)
        enterTransition = androidx.transition.AutoTransition()
        exitTransition = androidx.transition.AutoTransition()
        appBar.setNavigationOnClickListener {
            dismiss()
        }
        addListener(brushPicker)
        val preview = brushPicker.findViewById<BrushPreview>(R.id.preview)
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_pick -> {
                    onNewBrush?.let { it(preview.brushStyle) }
                    dismiss()
                    true
                }
                else -> false
            }
        }
        return brushPicker
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val brushPicker = inflater.inflate(R.layout.brush_picker, null)
            val preview = brushPicker.findViewById<BrushPreview>(R.id.preview)
            val dialog = MaterialAlertDialogBuilder(it)
                .setTitle(R.string.brush_picker)
                .setView(brushPicker)
                .setNeutralButton(R.string.cancel) { _, _ ->
                    // Do Nothing
                }.setPositiveButton(R.string.ok) { _, _ ->
                    onNewBrush?.let { it(preview.brushStyle) }
                }.create()
            addListener(brushPicker)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addListener(brushPicker: View) {
        val preview = brushPicker.findViewById<BrushPreview>(R.id.preview)
        val sliderRed = brushPicker.findViewById<Slider>(R.id.slider_red)
        val sliderGreen = brushPicker.findViewById<Slider>(R.id.slider_green)
        val sliderBlue = brushPicker.findViewById<Slider>(R.id.slider_blue)
        val sliderWidth = brushPicker.findViewById<Slider>(R.id.slider_width)
        val switchTouch = brushPicker.findViewById<SwitchMaterial>(R.id.switch_3d)
        preview.brushStyle = oldBrush
        sliderRed.value = oldBrush.red.toFloat()
        sliderGreen.value = oldBrush.green.toFloat()
        sliderBlue.value = oldBrush.blue.toFloat()
        sliderWidth.value = oldBrush.strokeWidth
        switchTouch.isChecked = oldBrush.forceTouch
        sliderRed.addOnChangeListener { _, value, _ ->
            preview.red = value.toInt()
        }
        sliderGreen.addOnChangeListener { _, value, _ ->
            preview.green = value.toInt()
        }
        sliderBlue.addOnChangeListener { _, value, _ ->
            preview.blue = value.toInt()
        }
        sliderWidth.addOnChangeListener { _, value, _ ->
            preview.width = value
        }
        switchTouch.setOnClickListener {
            preview.touch = switchTouch.isChecked
            if (preview.touch && !preview.brushStyle.pressureMeasured) {
                Toast.makeText(context, R.string.pressure_test, Toast.LENGTH_LONG).show()
            }
        }
    }
}