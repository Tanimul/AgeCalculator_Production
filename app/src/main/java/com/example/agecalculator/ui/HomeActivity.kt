package com.example.agecalculator.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.agecalculator.AppBaseActivity
import com.example.agecalculator.R
import com.example.agecalculator.databinding.ActivityHomeBinding
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.PeriodFormatterBuilder
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class HomeActivity : AppBaseActivity() {
    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding
    private var dateOfBirth: Long = 0L
    private var dayOfToDay: Long = 0L
    private val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    private val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("M", Locale.getDefault())
    private val  dayOfWeek = SimpleDateFormat("EEEE")
    private lateinit var toDaysCalendar: Calendar
    private lateinit var birthDateCalender: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: HomeActivity.")

        setToolbarWithoutBackButton(binding.toolbarLayout.toolbar)
        title = ""

        // write permission to access the storage
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )


        toDaysCalendar = Calendar.getInstance()
        birthDateCalender = Calendar.getInstance()

        defaultDate()

        binding.btDateOfBirth.setOnClickListener {
            dateBirthPicker()
        }
        binding.btToDay.setOnClickListener {
            dateToDaysPicker()
        }

        binding.ivShare.setOnClickListener {
            // implemented below
            val bitmap = getScreenShotFromView(binding.layoutConstraint03)
            if (bitmap != null) {
                /// saveMediaToStorage(bitmap)
                Log.d(TAG, "Bitmap: $bitmap")

                val imageBitMap = Intent(Intent.ACTION_SEND)

                imageBitMap.type = "image/*"
                imageBitMap.putExtra(
                    Intent.EXTRA_STREAM,
                    getImageUri(this, bitmap)
                )
                try {
                    startActivity(Intent.createChooser(imageBitMap, "Age Calculation ..."))
                } catch (ex: ActivityNotFoundException) {
                    ex.printStackTrace()
                }
            }


        }

    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun getScreenShotFromView(layoutConst2: ConstraintLayout): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            screenshot = Bitmap.createBitmap(
                layoutConst2.measuredWidth,
                layoutConst2.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            layoutConst2.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot

    }

    private fun defaultDate() {

        dayOfToDay = toDaysCalendar.timeInMillis
        binding.btToDay.text = dateFormat.format(toDaysCalendar.time)
        Log.d(TAG, "defaultDate -> Birth Day: " + dateFormat.format(dayOfToDay))

        birthDateCalender.set(
            birthDateCalender.get(Calendar.YEAR) - 10,
            birthDateCalender.get(Calendar.MONTH),
            birthDateCalender.get(Calendar.DAY_OF_MONTH)
        )
        dateOfBirth = birthDateCalender.timeInMillis
        binding.btDateOfBirth.text = dateFormat.format(birthDateCalender.time)
        Log.d(TAG, "defaultDate -> ToDays Day: " + dateFormat.format(dateOfBirth))

        getSummaryCalculation()
        getAgeCalculation()
        getNextBirthday()
    }

    private fun getSummaryCalculation() {
        Log.d(TAG, "SummaryCalculation-> " + dateFormat.format(dateOfBirth))
        Log.d(TAG, "SummaryCalculation-> " + dateFormat.format(dayOfToDay))

        val resDays: Long = TimeUnit.MILLISECONDS.toDays(dayOfToDay - dateOfBirth)
        val resMinutes: Long = TimeUnit.MILLISECONDS.toMinutes(dayOfToDay - dateOfBirth)
        val resSeconds = TimeUnit.MILLISECONDS.toSeconds(dayOfToDay - dateOfBirth)
        val resHours: Long = TimeUnit.MILLISECONDS.toHours(dayOfToDay - dateOfBirth)
        val resWeek: Long = TimeUnit.MILLISECONDS.toDays(dayOfToDay - dateOfBirth) / 7
        val resYear: Long = TimeUnit.MILLISECONDS.toDays(dayOfToDay - dateOfBirth) / 365

        val period = Period(dateOfBirth, dayOfToDay, PeriodType.months())
        Log.d(TAG, "getSummaryCalculation: $period")
        binding.tvMonthsNumber.text = period.months.toString()

        binding.tvYearsNumber.text = resYear.toString()
        binding.tvWeeksNumber.text = resWeek.toString()
        binding.tvDaysNumber.text = resDays.toString()
        binding.tvHoursNumber.text = resHours.toString()
        binding.tvMinutesNumber.text = resMinutes.toString()
        binding.tvSecondsNumber.text = resSeconds.toString()
    }

    private fun getAgeCalculation() {
        Log.d(TAG, "ageCalculation-> " + dateFormat.format(dateOfBirth))
        Log.d(TAG, "ageCalculation-> " + dateFormat.format(dayOfToDay))
        val period = Period(dateOfBirth, dayOfToDay, PeriodType.yearMonthDay())
        binding.tvAgeNumber.text = period.years.toString()
        val ageMonthDayText =
            period.months.toString() + " months | " + period.days.toString() + " days"
        binding.tvAgeMonthDay.text = ageMonthDayText

        Log.d(TAG, "ageCalculation: $period")
    }

    private fun getNextBirthday() {

        val today = LocalDate(
            yearFormat.format(dayOfToDay).toInt(),
            monthFormat.format(dayOfToDay).toInt(),
            dayFormat.format(dayOfToDay).toInt()
        )
        val birthDate = LocalDate(
            yearFormat.format(dateOfBirth).toInt(),
            monthFormat.format(dateOfBirth).toInt(),
            dayFormat.format(dateOfBirth).toInt()
        )

        val age: Int = Period(birthDate, today).years

        val nextBirthday: LocalDate = birthDate.plusYears(age + 1)
        val dayOfWeek = LocalDate(nextBirthday).dayOfWeek().getAsText(Locale.ENGLISH)
        Log.d(TAG, "getNextBirthday: $dayOfWeek")
        binding.tvNextBirthdayDay.text = dayOfWeek


        val monthsAndDays = PeriodType.yearMonthDay().withYearsRemoved()
        val leftToBirthday = Period(today, nextBirthday, monthsAndDays)
        Log.d(TAG, "getNextBirthday: $leftToBirthday")
        val periodFormatter =
            when (leftToBirthday.days) {

                0 -> PeriodFormatterBuilder()
                    .appendMonths().appendSuffix(" months ")
                    .appendSuffix("| 0 days")
                    .toFormatter()
                else -> PeriodFormatterBuilder()
                    .appendMonths().appendSuffix(" months ")
                    .appendSuffix("| ")
                    .appendDays().appendSuffix(" days ")
                    .toFormatter()
            }

        Log.d(TAG, "getNextBirthday: " + periodFormatter.print(leftToBirthday))

        binding.tvMonthDayRem.text = periodFormatter.print(leftToBirthday)
        Log.d(TAG, "ageCalculation: $periodFormatter.print(leftToBirthday)")


    }


    private fun dateBirthPicker() {
        val dialogView = View.inflate(this, R.layout.layout_datepicker, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        datePicker.updateDate(
            birthDateCalender.get(Calendar.YEAR),
            birthDateCalender.get(Calendar.MONTH),
            birthDateCalender.get(
                Calendar.DAY_OF_MONTH
            )
        )

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setTitle("Choose Date")
        builder.setNegativeButton("cancel", null)
        builder.setPositiveButton("set") { _, _ ->
            birthDateCalender.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            Log.d(TAG, "dateBirthPicker: " + datePicker.year)
            binding.btDateOfBirth.text = dateFormat.format(birthDateCalender.time)
            dateOfBirth = birthDateCalender.timeInMillis
            Log.d(TAG, "dateBirthPicker: $dateOfBirth")
            Log.d(TAG, "dateBirthPicker: " + dateFormat.format(dateOfBirth))
            getSummaryCalculation()
            getAgeCalculation()
            getNextBirthday()
        }
        builder.show()

    }

    private fun dateToDaysPicker() {
        val dialogView = View.inflate(this, R.layout.layout_datepicker, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        datePicker.updateDate(
            toDaysCalendar.get(Calendar.YEAR),
            toDaysCalendar.get(Calendar.MONTH),
            toDaysCalendar.get(
                Calendar.DAY_OF_MONTH
            )
        )

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setTitle("Choose Date")
        builder.setNegativeButton("cancel", null)
        builder.setPositiveButton("set") { _, _ ->
            toDaysCalendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            binding.btToDay.text = dateFormat.format(toDaysCalendar.time)
            dayOfToDay = toDaysCalendar.timeInMillis
            getSummaryCalculation()
            getAgeCalculation()
            getNextBirthday()
        }
        builder.show()

    }
}