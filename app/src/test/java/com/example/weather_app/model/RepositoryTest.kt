package com.example.weather_app.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weather_app.Model.City
import com.example.weather_app.Model.Coord
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.Model.WeatherRepositoryImp
import com.example.weather_app.Model.WeatherResponse
import com.example.weather_app.dp.FakeLocal
import com.example.weather_app.network.FakeRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()


    private val local = mutableListOf(
        FavLocation(0.0, 0.0, "t1", 1),
        FavLocation(10.0, 10.0, "t2", 2),
        FavLocation(80.0, 5.0, "t3", 3),
    )
    private val localCurrentWeather = mutableListOf(
        WeatherResponse(
            listOf(), City(
                2, "tanta", Coord(2.0, 3.0), "Egypt", 2, 3, 4, 9
            )
        )
    )

    private val remote =
        WeatherResponse(listOf(), City(20, "cairo", Coord(12.0, 15.0), "Egypt", 1000, 20, 16, 81))

    private lateinit var fakeLocal: FakeLocal
    private lateinit var fakeRemote: FakeRemote
    private lateinit var repository: WeatherRepositoryImp
    private lateinit var weather: WeatherResponse

    @Before
    fun setUp() {
        fakeLocal = FakeLocal(local, localCurrentWeather)
        fakeRemote = FakeRemote(remote)
        repository = WeatherRepositoryImp.getInstance(fakeRemote, fakeLocal)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun makeNetworkCall_latitudeLongitudeUnitsLanguage_weatherResponse() = runBlockingTest {
        val coroutineScope = CoroutineScope(coroutineContext)
        coroutineScope.launch {
            repository.getWeather(0.0, 0.0, "", "").collect {
                weather = it
                assertThat(weather, `is`(remote))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteLocation_location_returnEmptyLocation() = runBlockingTest {
        //given
        val location =  FavLocation(0.0, 0.0, "t1", 1)
        //when
        repository.deleteLocation(location)
        repository.getStoredLocations().collect { val result = it.size
            //then
            assertThat(result, `is`(2))
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertLocation_location_returnListOfLocation() = runBlockingTest {
        //given
        val list = FavLocation(0.0, 0.0, "t1", 10)

        //when
        repository.insertLocation(list)

        repository.getStoredLocations().collect { val result = it.size
            //then
            Assert.assertThat(result, `is`(4))
        }

    }

    @Test
    fun getStoredLocations_returnLocations() = runBlockingTest {
        //when
        var result = listOf<FavLocation>()
        repository.getStoredLocations().collect { result = it }
        //then
        assertThat(result, `is`(local))
    }
    @Test
    fun getAllStoredCurrentWeather_returnCurrentWeather() = runBlockingTest {
        //when
        var result = listOf<WeatherResponse>()
        repository.getStoredCurrentWeather().collect { result = it }
        //then
        assertThat(result, `is`(localCurrentWeather))
    }
}






