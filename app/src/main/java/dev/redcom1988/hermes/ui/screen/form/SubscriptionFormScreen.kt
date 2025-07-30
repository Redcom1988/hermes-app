package dev.redcom1988.hermes.ui.screen.form

import android.text.format.DateFormat.is24HourFormat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.AllIcons
import compose.icons.fontawesomeicons.Brands
import dev.redcom1988.hermes.domain.category.model.Category
import dev.redcom1988.hermes.domain.subscription.model.Subscription
import dev.redcom1988.hermes.ui.components.AppBar
import dev.redcom1988.hermes.ui.components.AppBarActions
import dev.redcom1988.hermes.ui.components.ResultScreen
import dev.redcom1988.hermes.ui.components.preference.Preference
import dev.redcom1988.hermes.ui.components.preference.PreferenceScreen
import dev.redcom1988.hermes.ui.screen.choose_currency.CHOOSE_CURRENCY_KEY
import dev.redcom1988.hermes.ui.screen.choose_currency.ChooseCurrencyScreen
import dev.redcom1988.hermes.ui.screen.choose_logo.CHOOSE_LOGO_KEY
import dev.redcom1988.hermes.ui.screen.choose_logo.ChooseLogoScreen
import dev.redcom1988.hermes.ui.util.PlaceholderTransformation
import dev.redcom1988.hermes.ui.util.splitCamelCase
import dev.redcom1988.hermes.ui.util.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone

sealed interface SubscriptionFormScreenType {
    data object Add: SubscriptionFormScreenType
    data class Edit(val subscription: Subscription): SubscriptionFormScreenType
}

data class SubscriptionFormScreen(
    val type: SubscriptionFormScreenType = SubscriptionFormScreenType.Add,
) : ResultScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(arguments: Map<String, Any?>) {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SubscriptionFormScreenModel(type.toFormData()) }
        val state = screenModel.state.collectAsState().value
        val categories by screenModel.categories.collectAsState()
        val title = when(type) {
            is SubscriptionFormScreenType.Add -> "Add Subscription"
            is SubscriptionFormScreenType.Edit -> "Edit Subscription"
        }

        with(arguments) {
            val currencyArg = this[CHOOSE_CURRENCY_KEY] as? String
            val logoArg = this[CHOOSE_LOGO_KEY] as? String

            val currency = currencyArg?.let { Currency.getInstance(currencyArg) }
            val logo = FontAwesomeIcons.Brands.AllIcons.map { it.name }.firstOrNull { it == logoArg }

            if (currency != null) {
                screenModel.updateState { it.copy(currency = currency) }
            }
            if (logo != null) {
                screenModel.updateState { it.copy(logo = logo) }
            }
        }

        var showUnsavedChangesDialog by rememberSaveable { mutableStateOf(false) }
        var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
        var showDatePicker by rememberSaveable { mutableStateOf(false) }
        var showTimePicker by rememberSaveable { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.paymentDate,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val localCalendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val today = localCalendar.timeInMillis
                    val candidateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = utcTimeMillis
                        timeZone = TimeZone.getDefault() // convert it to local timezone
                    }
                    val candidateMillis = candidateCalendar.timeInMillis
                    return candidateMillis >= today
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val currentYear = LocalDate.now().year
                    return year >= currentYear
                }
            }
        )
        val timePickerState = when(type) {
            is SubscriptionFormScreenType.Add -> rememberTimePickerState(9, 0)
            is SubscriptionFormScreenType.Edit -> {
                val time = type.subscription.nextPaymentDate.toLocalDateTime()
                rememberTimePickerState(
                    initialHour = time.hour,
                    initialMinute = time.minute,
                )
            }
        }
        var expandedBillingUnit by rememberSaveable { mutableStateOf(false) }
        var expandedBillingSchedule by rememberSaveable { mutableStateOf(false) }

        fun handleBack() {
            if (screenModel.isChanged(type.toFormData())) {
                showUnsavedChangesDialog = true
            } else {
                navigator.pop()
            }
        }

        BackHandler { handleBack() }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },
                title = {
                    Text("Delete Subscription")
                },
                text = {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            ) {
                                append("CAUTION: \n")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                )
                            ) {
                                append(state.name)
                            }
                            append(" will be deleted")
                        }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            screenModel.handleDelete(
                                onSuccess = {
                                    navigator.pop()
                                }
                            )
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showUnsavedChangesDialog) {
            AlertDialog(
                onDismissRequest = {
                    showUnsavedChangesDialog = false
                },
                text = {
                    Text("Discard unsaved changes?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showUnsavedChangesDialog = false
                            navigator.pop()
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUnsavedChangesDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        PreferenceScreen(
            title = title,
            itemsProvider = {
                listOf(
                    getBasicInformationGroup(
                        name = state.name,
                        isActive = state.active,
                        logo = state.logo,
                        onNameChange = { name ->
                            screenModel.updateState { it.copy(name = name) }
                        },
                        onToggleActiveSubscription = {
                            screenModel.updateState { it.copy(active = !state.active) }
                        },
                        onLogoClick = {
                            navigator.push(ChooseLogoScreen(state.logo))
                        },
                        categories = categories,
                        selectedCategories = state.categories.map { it.id.toString() },
                        onConfirmSelectCategories = { selectedCategories ->
                            screenModel.updateState {
                                it.copy(
                                    categories = categories.filter { it.id.toString() in selectedCategories }
                                )
                            }
                        },
                    ),
                    getPricingGroup(
                        price = state.price,
                        currency = state.currency,
                        onPriceChange = { newPrice ->
                            if (newPrice.isEmpty() || newPrice.toDoubleOrNull() != null) {
                                screenModel.updateState { it.copy(price = newPrice) }
                            }
                        },
                        onCurrencyClick = { navigator.push(ChooseCurrencyScreen(state.currency.currencyCode)) }
                    ),
                    getBillingCycleGroup(
                        billingCycle = state.billingCycle,
                        expandedBillingUnit = expandedBillingUnit,
                        expandedBillingSchedule = expandedBillingSchedule,
                        onExpandedBillingUnitChange = { expandedBillingUnit = it },
                        onExpandedBillingScheduleChange = { expandedBillingSchedule = it },
                        onBillingCycleChange = { newBillingCycle ->
                            screenModel.updateState { it.copy(billingCycle = newBillingCycle) }
                        },
                        nextPaymentDate = state.paymentDate,
                        onDateClick = { showDatePicker = true },
                        onTimeClick = { showTimePicker = true },
                    ),
                    getAdditionalInformationGroup(
                        url = state.url,
                        notes = state.notes,
                        onUrlChange = { newUrl ->
                            screenModel.updateState { it.copy(url = newUrl) }
                        },
                        onNotesChange = { newNotes ->
                            screenModel.updateState { it.copy(notes = newNotes) }
                        }
                    ),
                )
            },
            shadowElevation = 8.dp,
            onBackPressed = { handleBack() },
            actions = {
                AppBarActions(
                    actions = when(type) {
                        is SubscriptionFormScreenType.Add -> emptyList()
                        is SubscriptionFormScreenType.Edit -> listOf(
                            AppBar.Action(
                                title = "Delete",
                                icon = Icons.Default.Delete,
                                onClick = { showDeleteDialog = true }
                            )
                        )
                    }
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        enabled = when(type) {
                            is SubscriptionFormScreenType.Add -> state.name.isNotBlank() && state.price.isNotBlank()
                            is SubscriptionFormScreenType.Edit -> {
                                state.name.isNotBlank() &&
                                        state.price.isNotBlank() &&
                                        screenModel.isChanged(type.toFormData())
                            }
                        },
                        onClick = {
                            screenModel.handleSave(
                                onSuccess = {
                                    navigator.pop()
                                }
                            )
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        )

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDate ->
                                // Preserve existing time when updating date
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = state.paymentDate
                                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                                val currentMinute = calendar.get(Calendar.MINUTE)

                                calendar.timeInMillis = selectedDate
                                calendar.set(Calendar.HOUR_OF_DAY, currentHour)
                                calendar.set(Calendar.MINUTE, currentMinute)
                                calendar.set(Calendar.SECOND, 0)
                                calendar.set(Calendar.MILLISECOND, 0)

                                screenModel.updateState {
                                    it.copy(
                                        paymentDate = calendar.timeInMillis
                                    )
                                }
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(datePickerState)
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Update time while preserving date
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = state.paymentDate
                            calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            calendar.set(Calendar.MINUTE, timePickerState.minute)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            screenModel.updateState {
                                it.copy(
                                    paymentDate = calendar.timeInMillis
                                )
                            }
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                TimePicker(timePickerState)
            }
        }
    }

    @Composable
    private fun getBasicInformationGroup(
        name: String,
        isActive: Boolean,
        logo: String?,
        categories: List<Category>,
        selectedCategories: List<String>,
        onNameChange: (String) -> Unit,
        onToggleActiveSubscription: () -> Unit,
        onLogoClick: () -> Unit,
        onConfirmSelectCategories: (List<String>) -> Unit,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Basic Information",
            preferenceItems = listOf(
                Preference.PreferenceItem.CustomPreference {
                    SubscriptionNameSection(
                        name = name,
                        onNameChange = onNameChange,
                    )
                },
                Preference.PreferenceItem.CustomPreference {
                    LogoSection(
                        logo = logo,
                        onLogoClick = onLogoClick,
                    )
                },
                Preference.PreferenceItem.BasicSwitchPreference(
                    value = isActive,
                    title = "Active Subscription",
                    onValueChanged = {
                        onToggleActiveSubscription()
                        true
                    }
                ),
                Preference.PreferenceItem.BasicMultiSelectListPreference(
                    values = selectedCategories,
                    visible = categories.isNotEmpty(),
                    entries = mapOf(
                        *categories.map { it.id.toString() to it.name }.toTypedArray()
                    ),
                    subtitleProvider = { v, e ->
                        val combined = remember(v, e) {
                            v.mapNotNull { e[it] }
                                .joinToString()
                                .takeUnless { it.isBlank() }
                        }
                            ?: "Uncategorized" // TODO copy
                        "%s".format(combined)
                    },
                    title = "Categories",
                    onValueChanged = {
                        onConfirmSelectCategories(it)
                        true
                    }
                ),
            )
        )
    }

    @Composable
    private fun getPricingGroup(
        price: String,
        currency: Currency,
        onPriceChange: (String) -> Unit,
        onCurrencyClick: () -> Unit,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Pricing",
            preferenceItems = listOf(
                Preference.PreferenceItem.CustomPreference {
                    OutlinedTextField(
                        value = price,
                        onValueChange = onPriceChange,
                        label = { Text("Price *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        isError = price.isBlank(),
                        textStyle = LocalTextStyle.current.copy(
                            color = when {
                                price.isEmpty() -> OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor
                                else -> LocalTextStyle.current.color
                            }
                        ),
                        visualTransformation = if (price.isEmpty())
                            PlaceholderTransformation("Enter Price")
                        else VisualTransformation.None
                    )
                },
                Preference.PreferenceItem.CustomPreference {
                    OutlinedTextField(
                        value = "(${currency.currencyCode}) ${currency.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            //For Icons
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        label = { Text("Currency") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .clickable { onCurrencyClick() }
                    )
                }
            )
        )
    }

    @Composable
    private fun getBillingCycleGroup(
        billingCycle: BillingCycle,
        expandedBillingUnit: Boolean,
        expandedBillingSchedule: Boolean,
        onExpandedBillingUnitChange: (Boolean) -> Unit,
        onExpandedBillingScheduleChange: (Boolean) -> Unit,
        onBillingCycleChange: (BillingCycle) -> Unit,
        nextPaymentDate: Long,
        onDateClick: () -> Unit,
        onTimeClick: () -> Unit,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Billing Cycle",
            preferenceItems = listOf(
                Preference.PreferenceItem.CustomPreference {
                    BillingCycleSection(
                        billingCycle = billingCycle,
                        expandedBillingUnit = expandedBillingUnit,
                        expandedBillingSchedule = expandedBillingSchedule,
                        onExpandedBillingUnitChange = onExpandedBillingUnitChange,
                        onExpandedBillingScheduleChange = onExpandedBillingScheduleChange,
                        onBillingCycleChange = onBillingCycleChange
                    )
                },
                Preference.PreferenceItem.CustomPreference {
                    PaymentDateSection(
                        nextPaymentDate = nextPaymentDate,
                        onDateClick = onDateClick,
                        onTimeClick = onTimeClick,
                    )
                }
            )
        )
    }

    @Composable
    private fun getAdditionalInformationGroup(
        url: String,
        notes: String,
        onUrlChange: (String) -> Unit,
        onNotesChange: (String) -> Unit,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = "Additional Information",
            preferenceItems = listOf(
                Preference.PreferenceItem.CustomPreference {
                    OutlinedTextField(
                        value = url,
                        onValueChange = onUrlChange,
                        label = { Text("Website URL (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Link, contentDescription = null)
                        }
                    )
                },
                Preference.PreferenceItem.CustomPreference {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp, bottom = 16.dp),
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                }
            )
        )
    }

}

@Composable
private fun SubscriptionNameSection(
    name: String,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Subscription Name *") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        singleLine = true,
        isError = name.isBlank(),
        textStyle = LocalTextStyle.current.copy(
            color = when {
                name.isEmpty() -> OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor
                else -> LocalTextStyle.current.color
            }
        ),
        visualTransformation = if (name.isEmpty())
            PlaceholderTransformation("Enter Subscription Name")
        else VisualTransformation.None
    )
}

@Composable
private fun LogoSection(
    logo: String?,
    onLogoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLogoClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val image = FontAwesomeIcons.Brands.AllIcons.firstOrNull { it.name == logo }
            if (image != null) {
                Icon(
                    imageVector = image,
                    contentDescription = null,
                )
            } else {
                Text(
                    text = "No Logo",
                    maxLines = 2,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = logo?.splitCamelCase() ?: "Add Logo"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillingCycleSection(
    billingCycle: BillingCycle,
    expandedBillingUnit: Boolean,
    expandedBillingSchedule: Boolean,
    onExpandedBillingUnitChange: (Boolean) -> Unit,
    onExpandedBillingScheduleChange: (Boolean) -> Unit,
    onBillingCycleChange: (BillingCycle) -> Unit
) {
    when(billingCycle) {
        is BillingCycle.Custom -> {
            val billingValue = billingCycle.value
            val billingUnit = billingCycle.unit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = billingValue,
                    onValueChange = { newBillingValue ->
                        if (newBillingValue.isEmpty() || newBillingValue.toIntOrNull() != null) {
                            onBillingCycleChange(
                                BillingCycle.Custom(
                                    value = newBillingValue,
                                    unit = billingUnit,
                                )
                            )
                        }
                    },
                    label = { Text("Every") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                // Billing Unit Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedBillingUnit,
                    onExpandedChange = onExpandedBillingUnitChange,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = Subscription.TimeUnit.entries.find {
                            it == billingUnit
                        }?.label ?: Subscription.TimeUnit.DAYS.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBillingUnit) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBillingUnit,
                        onDismissRequest = { onExpandedBillingUnitChange(false) }
                    ) {
                        Subscription.TimeUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit.label) },
                                onClick = {
                                    onBillingCycleChange(
                                        BillingCycle.Custom(
                                            value = billingValue,
                                            unit = unit,
                                        )
                                    )
                                    onExpandedBillingUnitChange(false)
                                }
                            )
                        }
                    }
                }
            }
        }
        else -> {
            val billingUnit = billingCycle.text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedBillingSchedule,
                    onExpandedChange = onExpandedBillingScheduleChange,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = billingUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBillingSchedule) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBillingSchedule,
                        onDismissRequest = { onExpandedBillingScheduleChange(false) }
                    ) {
                        // for each sealed class
                        val billingCycles = listOf(
                            BillingCycle.Daily(),
                            BillingCycle.Weekly(),
                            BillingCycle.Monthly(),
                            BillingCycle.Annually(),
                            BillingCycle.Custom("1", Subscription.TimeUnit.MONTHS, "Custom")
                        )
                        billingCycles.forEach { billingCycleOption ->
                            DropdownMenuItem(
                                text = { Text(billingCycleOption.text) },
                                onClick = {
                                    onBillingCycleChange(billingCycleOption)
                                    onExpandedBillingScheduleChange(false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentDateSection(
    nextPaymentDate: Long,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat(
        if (is24HourFormat(context)) "HH:mm" else "h:mm a",
        Locale.getDefault()
    )
    val currentDate = Date(nextPaymentDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = dateFormat.format(currentDate),
            onValueChange = {},
            label = { Text("Payment Date") },
            modifier = Modifier
                .weight(1f)
                .clickable { onDateClick() },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors().copy(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = onDateClick) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                }
            }
        )

        OutlinedTextField(
            value = timeFormat.format(currentDate),
            onValueChange = {},
            label = { Text("Payment Time") },
            modifier = Modifier
                .weight(1f)
                .clickable { onTimeClick() },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors().copy(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = onTimeClick) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Select time")
                }
            }
        )
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = {
            content()
        }
    )
}