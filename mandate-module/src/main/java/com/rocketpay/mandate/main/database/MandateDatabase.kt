package com.rocketpay.mandate.main.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rocketpay.mandate.feature.bankaccount.data.datasource.local.BankAccountDao
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountEntity
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentDao
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentEntity
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDao
import com.rocketpay.mandate.feature.kyc.data.entities.KycEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateDao
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateSubtextDao
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateSubtextEntity
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductOrderDao
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductWalletDao
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletEntity
import com.rocketpay.mandate.feature.property.data.datasource.local.PropertyDao
import com.rocketpay.mandate.feature.property.data.entities.PropertyEntity
import com.rocketpay.mandate.feature.settlements.data.datasource.local.PaymentOrderDao
import com.rocketpay.mandate.feature.settlements.data.entities.PaymentOrderEntity

@Database(
    entities = [
        MandateEntity::class,
        InstallmentEntity::class,
        KycEntity::class,
        BankAccountEntity::class,
        MandateSubtextEntity::class,
        PaymentOrderEntity::class,
        PropertyEntity::class,
        ProductWalletEntity::class,
        ProductOrderEntity::class
    ],
    views = [],
    version = MandateDatabase.VERSION
)
internal abstract class MandateDatabase : RoomDatabase() {

    internal abstract fun mandateDao(): MandateDao
    internal abstract fun installmentDao(): InstallmentDao
    internal abstract fun kycDao(): KycDao
    internal abstract fun bankAccountDao(): BankAccountDao
    internal abstract fun mandateSubtextDao(): MandateSubtextDao
    internal abstract fun paymentOrderDao(): PaymentOrderDao
    internal abstract fun propertyDao(): PropertyDao
    internal abstract fun productWalletDao(): ProductWalletDao
    internal abstract fun productOrderDao(): ProductOrderDao

    companion object {
        private const val NAME = "rocketpay-mandate"
        const val VERSION = 1

        @Volatile
        lateinit var instance: MandateDatabase
        fun initialise(context: Context) {
            if (!::instance.isInitialized) {
                instance = Room.databaseBuilder(context.applicationContext, MandateDatabase::class.java, NAME)
                    .addCallback(DB_CALLBACK)
                    .build()
            }
        }

        private val DB_CALLBACK: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }

        public val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
            }
        }

    }
}
