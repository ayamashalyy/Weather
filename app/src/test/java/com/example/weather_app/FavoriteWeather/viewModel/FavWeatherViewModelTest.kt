package com.example.weather_app.FavoriteWeather.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_app.Model.FavLocation
import com.example.weather_app.getOrAwaitValue
import com.example.weather_app.model.FakeRepository
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavWeatherViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    lateinit var viewModel: FavWeatherViewModel
    lateinit var repo: FakeRepository

    @Before
    fun setUp() {
        repo = FakeRepository()
        viewModel = FavWeatherViewModel(repo)
    }

    @Test
    fun getLocalLocations_returnStoredLocations() = runBlockingTest {
        // given
        repo.insertLocation(favLocation = FavLocation(0.0,0.0,"cairo",1))
        repo.insertLocation(favLocation = FavLocation(10.0,4.0,"tanta",2))
        // when
        viewModel.getLocalLocations()
        val result = viewModel.weather.getOrAwaitValue()
        //then
        assertThat(result, not(nullValue()))
    }

    @Test
    fun deleteLocations_location_returnEmptyLocation() = runBlockingTest {
        //given
        repo.insertLocation(favLocation = FavLocation(0.0,0.0,"cairo",1))
        repo.insertLocation(favLocation = FavLocation(10.0,4.0,"tanta",2))
        repo.insertLocation(favLocation = FavLocation(20.0,20.0,"cairo",3))

        val expectedLocations = listOf(
            FavLocation(0.0,0.0,"cairo",1),
            FavLocation(10.0,4.0,"tanta",2)
        )
        //when
        val location =FavLocation(20.0,20.0,"cairo",3)
        viewModel.deleteLocations(location)
        //then
        val result = viewModel.weather.getOrAwaitValue()
        assertThat(result, notNullValue())
        assertThat(result,`is`(expectedLocations))
    }

}