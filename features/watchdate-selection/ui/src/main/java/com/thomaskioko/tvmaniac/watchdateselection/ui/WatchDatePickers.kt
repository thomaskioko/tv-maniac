package com.thomaskioko.tvmaniac.watchdateselection.ui

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_cancel
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_ok
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.watchdateselection.WatchDateSelectionTestTags
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
internal fun WatchDatePickerDialog(
    maxSelectableDate: LocalDate,
    onDismiss: () -> Unit,
    onDateChosen: (LocalDate) -> Unit,
) {
    val context = LocalContext.current
    val selectableDates = remember(maxSelectableDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis.toUtcDate() <= maxSelectableDate

            override fun isSelectableYear(year: Int): Boolean = year <= maxSelectableDate.year
        }
    }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = maxSelectableDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
        yearRange = DatePickerDefaults.YearRange.first..maxSelectableDate.year,
        selectableDates = selectableDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { pickerState.selectedDateMillis?.let { onDateChosen(it.toUtcDate()) } },
                enabled = pickerState.selectedDateMillis != null,
                modifier = Modifier.testTag(WatchDateSelectionTestTags.PICKER_CONFIRM),
            ) {
                Text(text = label_ok.resolve(context))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag(WatchDateSelectionTestTags.PICKER_CANCEL),
            ) {
                Text(text = label_cancel.resolve(context))
            }
        },
    ) {
        DatePicker(
            state = pickerState,
            modifier = Modifier.testTag(WatchDateSelectionTestTags.DATE_PICKER),
        )
    }
}

@Composable
internal fun WatchTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeChosen: (LocalTime) -> Unit,
) {
    val context = LocalContext.current
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time }
    val pickerState = rememberTimePickerState(
        initialHour = now.hour,
        initialMinute = now.minute,
    )

    TimePickerDialog(
        onDismissRequest = onDismiss,
        title = {},
        confirmButton = {
            TextButton(
                onClick = { onTimeChosen(LocalTime(pickerState.hour, pickerState.minute)) },
                modifier = Modifier.testTag(WatchDateSelectionTestTags.PICKER_CONFIRM),
            ) {
                Text(text = label_ok.resolve(context))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag(WatchDateSelectionTestTags.PICKER_CANCEL),
            ) {
                Text(text = label_cancel.resolve(context))
            }
        },
    ) {
        TimePicker(
            state = pickerState,
            modifier = Modifier.testTag(WatchDateSelectionTestTags.TIME_PICKER),
        )
    }
}

private fun Long.toUtcDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
