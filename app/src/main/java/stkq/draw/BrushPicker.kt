package stkq.draw

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class BrushPicker(val oldBrush: DrawBoard.BrushStyle) : DialogFragment() {
    var onNewBrush: ((brush: DrawBoard.BrushStyle) -> Unit)? = null
        set(value) {
            field = value
        }

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
                .setNeutralButton(R.string.cancel) { dialog, which ->
                    // Do Nothing
                }.setPositiveButton(R.string.ok) { dialog, which ->
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
        preview.red = oldBrush.red
        sliderRed.value = oldBrush.red.toFloat()
        preview.green = oldBrush.green
        sliderGreen.value = oldBrush.green.toFloat()
        preview.blue = oldBrush.blue
        sliderBlue.value = oldBrush.blue.toFloat()
        preview.width = oldBrush.strokeWidth
        sliderWidth.value = oldBrush.strokeWidth
        preview.touch = oldBrush.forceTouch
        switchTouch.isChecked = oldBrush.forceTouch
        sliderRed.addOnChangeListener { slider, value, fromUser ->
            preview.red = value.toInt()
        }
        sliderGreen.addOnChangeListener { slider, value, fromUser ->
            preview.green = value.toInt()
        }
        sliderBlue.addOnChangeListener { slider, value, fromUser ->
            preview.blue = value.toInt()
        }
        sliderWidth.addOnChangeListener { slider, value, fromUser ->
            preview.width = value
        }
        switchTouch.setOnClickListener {
            preview.touch = switchTouch.isChecked
        }
    }
}