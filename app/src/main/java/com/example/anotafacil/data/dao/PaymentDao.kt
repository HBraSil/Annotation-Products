package com.example.anotafacil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anotafacil.data.entity.PaymentEntity
import com.example.anotafacil.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query("SELECT * FROM payment WHERE customerId = :customerId")
    fun getPayments(customerId: Uuid?): Flow<List<PaymentEntity>>


    @Query("""
    SELECT *
    FROM payment
    WHERE id = :paymentId
""")
    suspend fun getPaymentById(paymentId: Uuid): PaymentEntity?


    @Query("""
        SELECT *
        FROM payment
        WHERE syncStatus = :status
    """)
    suspend fun getPaymentsBySyncStatus(
        status: SyncStatus
    ): List<PaymentEntity>

    @Query("""
        UPDATE payment
        SET syncStatus = :status
        WHERE id = :paymentId
    """)
    suspend fun updatePaymentSyncStatus(
        paymentId: Uuid,
        status: SyncStatus
    ): Int
}