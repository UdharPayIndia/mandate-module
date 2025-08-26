import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.udharpay.main.database.ApplicationDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Objects

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {
    @get:Rule
    var helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        Objects.requireNonNull(ApplicationDatabase::class.java.canonicalName),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate2To3() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 2)

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        //db.execSQL("INSERT into customers");

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 2 and provide
        // MIGRATION_2_3 as the migration process.
        helper.runMigrationsAndValidate(TEST_DB, 3, true, ApplicationDatabase.MIGRATION_2_3)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate3To4() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 3)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 4, true, ApplicationDatabase.MIGRATION_3_4)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate4To5() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 4)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 5, true, ApplicationDatabase.MIGRATION_4_5)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate5To6() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 5)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 6, true, ApplicationDatabase.MIGRATION_5_6)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate6To7() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 6)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 7, true, ApplicationDatabase.MIGRATION_6_7)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate7To8() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 7)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 8, true, ApplicationDatabase.MIGRATION_7_8)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate8To9() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 8)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 9, true, ApplicationDatabase.MIGRATION_8_9)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate9To10() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 9)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 10, true, ApplicationDatabase.MIGRATION_9_10)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate10To11() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 10)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 11, true, ApplicationDatabase.MIGRATION_10_11)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate11To12() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 11)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 12, true, ApplicationDatabase.MIGRATION_11_12)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate12To13() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 12)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 13, true, ApplicationDatabase.MIGRATION_12_13)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate13To14() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 13)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 14, true, ApplicationDatabase.MIGRATION_13_14)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate14To15() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 14)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 15, true, ApplicationDatabase.MIGRATION_14_15)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate15To16() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 15)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 16, true, ApplicationDatabase.MIGRATION_15_16)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate16To17() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 16)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 17, true, ApplicationDatabase.MIGRATION_16_17)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate17To18() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 17)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 18, true, ApplicationDatabase.MIGRATION_17_18)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate18To19() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 18)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 19, true, ApplicationDatabase.MIGRATION_18_19)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate19To20() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 19)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 20, true, ApplicationDatabase.MIGRATION_19_20)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate20To21() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 20)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 21, true, ApplicationDatabase.MIGRATION_20_21)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate21To22() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 21)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 22, true, ApplicationDatabase.MIGRATION_21_22)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate22To23() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 22)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 23, true, ApplicationDatabase.MIGRATION_22_23)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate23To24() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 23)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 24, true, ApplicationDatabase.MIGRATION_23_24)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate24To25() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 24)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 25, true, ApplicationDatabase.MIGRATION_24_25)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate25To26() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 25)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 26, true, ApplicationDatabase.MIGRATION_25_26)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate26To27() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 26)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 27, true, ApplicationDatabase.MIGRATION_26_27)
    }

    @Test
    @kotlin.Throws(IOException::class)
    fun migrate27To28() {
        val db: SupportSQLiteDatabase = helper.createDatabase(TEST_DB, 27)
        db.close()
        helper.runMigrationsAndValidate(TEST_DB, 28, true, ApplicationDatabase.MIGRATION_27_28)
    }


    companion object {
        private const val TEST_DB = "migration-test"
    }

}