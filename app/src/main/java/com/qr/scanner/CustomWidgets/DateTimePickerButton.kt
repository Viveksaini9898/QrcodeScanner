package com.qr.scanner.CustomWidgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color.blue
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.qr.scanner.R
import kotlinx.android.synthetic.main.layout_date_time_picker_button.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateTimePickerButton : FrameLayout {
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_date_time_picker_button, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.DateTimePickerButton).apply {
            showHint(this)
            recycle()
        }

        view.setOnClickListener {
            showDateTimePickerDialog()
        }

        showDateTime()
    }

    var dateTime: Long = System.currentTimeMillis()
        set(value) {
            field = value
            showDateTime()
        }

    private fun showHint(attributes: TypedArray) {
        view.text_view_hint.text = attributes.getString(R.styleable.DateTimePickerButton_hint).orEmpty()
    }

    private fun showDateTimePickerDialog() {
        SingleDateAndTimePickerDialog.Builder(context)
            .backgroundColor(context.resources.getColor(R.color.white))
            .title(view.text_view_hint.text.toString())
            .mainColor(context.resources.getColor(R.color.app_default))
            .listener { newDateTime ->
                dateTime = newDateTime.time
                showDateTime()
            }
            .display()
    }

    private fun showDateTime() {
        view.text_view_date_time.text = dateFormatter.formatOrNull(dateTime).orEmpty()
    }

    private fun DateFormat.formatOrNull(time: Long?): String? {
        return try {
            format(Date(time!!))
        } catch (ex: Exception) {
            null
        }
    }
}