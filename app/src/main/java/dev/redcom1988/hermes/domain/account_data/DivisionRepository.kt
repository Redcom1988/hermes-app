package dev.redcom1988.hermes.domain.account_data

interface DivisionRepository {

    suspend fun getDivisionWorkHoursByName(divisionName: String): Int?
    suspend fun getDivisionById(divisionId: Int): String?


}