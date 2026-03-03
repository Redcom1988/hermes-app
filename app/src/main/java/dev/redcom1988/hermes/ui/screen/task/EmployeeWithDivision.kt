package dev.redcom1988.hermes.ui.screen.task

import dev.redcom1988.hermes.domain.account_data.model.Employee

data class EmployeeWithDivision(
    val employee: Employee,
    val divisionName: String
)
