package com.example.weather_app.dp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceImpTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: AppDataBase
    private lateinit var localDataSource: WeatherLocalDataSourceImp

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDataBase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = WeatherLocalDataSourceImp(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        database.close()

    }

    @Test
    fun insertLocation_location_returnLocation() = runBlockingTest {
        //given
        val location = FavLocation(0.0, 0.0, "Test Location", 1)
        //when
        localDataSource.insertLocation(location)

        val result = localDataSource.getAllStoredLocations().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(location)))
    }

    @Test
    fun deleteLocation_location_returnEmptyLocation() = runBlockingTest {
        //given
        val location1 = FavLocation(0.0, 0.0, "test1", 10)
        val location2 = FavLocation(10.0, 5.0, "test2", 20)
        val location3 = FavLocation(6.0, 3.0, "test3", 30)
        localDataSource.insertLocation(location1)
        localDataSource.insertLocation(location2)
        localDataSource.insertLocation(location3)
        localDataSource.deleteLocation(location1)
        //when
        val result = localDataSource.getAllStoredLocations().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(location2, location3)))

    }

    @Test
    fun getAllStoredLocations_getListOfLocations() = runBlocking {
        //given
        val location1 = FavLocation(0.0, 0.0, "test1", 1)
        val location2 = FavLocation(10.0, 5.0, "test2", 2)
        val location3 = FavLocation(6.0, 3.0, "test3", 3)
        localDataSource.insertLocation(location1)
        localDataSource.insertLocation(location2)
        localDataSource.insertLocation(location3)
        // When
        val result = localDataSource.getAllStoredLocations().getOrAwaitValue()
        // Then
        assertThat(result, `is`(listOf(location1, location2, location3)))


    }

    @Test
    fun getAllAlerts_getListOfAlerts() = runBlocking {
        //given
        val alert1 = AlertModel(10, "1", "1", "2", "alarm", 0.0, 0.0, "cairo")
        val alert2 = AlertModel(20, "2", "3", "4", "notification", 10.0, 8.0, "tanta")
        val alert3 = AlertModel(30, "3", "5", "6", "alarm", 9.0, 4.0, "Alex")
        localDataSource.insertAlert(alert1)
        localDataSource.insertAlert(alert2)
        localDataSource.insertAlert(alert3)
        // When
        val result = localDataSource.getAllAlerts().getOrAwaitValue()
        // Then
        assertThat(result, `is`(listOf(alert1, alert2, alert3)))


    }

    @Test
    fun insertAlert_alert_returnAlert() = runBlockingTest {
        //given
        val alert = AlertModel(1, "1", "1", "2", "alarm", 0.0, 0.0, "cairo")
        //when
        localDataSource.insertAlert(alert)

        val result = localDataSource.getAllAlerts().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(alert)))
    }

    @Test
    fun deleteAlert_alert_returnEmptyLocation() = runBlockingTest {
        //given
        val alert1 = AlertModel(11, "1", "1", "2", "alarm", 0.0, 0.0, "cairo")
        val alert2 = AlertModel(22, "2", "3", "4", "notification", 10.0, 8.0, "tanta")
        val alert3 = AlertModel(33, "3", "5", "6", "alarm", 9.0, 4.0, "Alex")
        localDataSource.insertAlert(alert1)
        localDataSource.insertAlert(alert2)
        localDataSource.insertAlert(alert3)
        localDataSource.deleteAlert(alert1)
        //when
        val result = localDataSource.getAllAlerts().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(alert2, alert3)))

    }


}