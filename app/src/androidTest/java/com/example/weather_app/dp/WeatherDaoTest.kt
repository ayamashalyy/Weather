package com.example.weather_app.dp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.Model.FavLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var dao: WeatherDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDataBase::class.java
        ).build()
        dao = database.getWeatherDao()

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
        dao.insertLocation(location)

        val result = dao.getAllLocations().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(location)))
    }

    @Test
    fun deleteLocation_location_returnEmptyLocation() = runBlockingTest {
        //given
        val location1 = FavLocation(0.0, 0.0, "test1", 1)
        val location2 = FavLocation(10.0, 5.0, "test2", 2)
        val location3 = FavLocation(6.0, 3.0, "test3", 3)
        dao.insertLocation(location1)
        dao.insertLocation(location2)
        dao.insertLocation(location3)
        dao.deleteLocation(location1)
        //when
        val result = dao.getAllLocations().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(location2, location3)))

    }

    @Test
    fun getAllLocations_getListOfLocations() = runBlocking {
        //given
        val location1 = FavLocation(0.0, 0.0, "test1", 1)
        val location2 = FavLocation(10.0, 5.0, "test2", 2)
        val location3 = FavLocation(6.0, 3.0, "test3", 3)
        dao.insertLocation(location1)
        dao.insertLocation(location2)
        dao.insertLocation(location3)
        // When
        val result = dao.getAllLocations().getOrAwaitValue()
        // Then
        assertThat(result, `is`(listOf(location1, location2, location3)))


    }

    @Test
    fun allAlerts_getListOfAlerts() = runBlocking {
        //given
        val alert1 = AlertModel(1, "1", "1", "2", "alarm", 0.0, 0.0, "cairo")
        val alert2 = AlertModel(2, "2", "3", "4", "notification", 10.0, 8.0, "tanta")
        val alert3 = AlertModel(3, "3", "5", "6", "alarm", 9.0, 4.0, "Alex")
        dao.insertAlert(alert1)
        dao.insertAlert(alert2)
        dao.insertAlert(alert3)
        // When
        val result = dao.allAlerts().getOrAwaitValue()
        // Then
        assertThat(result, `is`(listOf(alert1, alert2, alert3)))


    }

    @Test
    fun insertAlert_alert_returnAlert() = runBlockingTest {
        //given
        val alert = AlertModel(1, "1", "1", "2", "alarm", 0.0, 0.0, "cairo")
        //when
        dao.insertAlert(alert)

        val result = dao.allAlerts().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf( alert)))
    }

    @Test
    fun deleteAlert_alert_returnEmptyLocation() = runBlockingTest {
        //given
        val alert1 = AlertModel(1, "1", "1", "2", "alarm", 0.0, 0.0, "cairo")
        val alert2 = AlertModel(2, "2", "3", "4", "notification", 10.0, 8.0, "tanta")
        val alert3 = AlertModel(3, "3", "5", "6", "alarm", 9.0, 4.0, "Alex")
        dao.insertAlert(alert1)
        dao.insertAlert(alert2)
        dao.insertAlert(alert3)
        dao.deleteAlert(alert1)
        //when
        val result = dao.allAlerts().getOrAwaitValue()
        //then
        assertThat(result, `is`(listOf(alert2, alert3)))

    }


}




