package com.example.weather_app.alert.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_app.Model.AlertModel
import com.example.weather_app.getOrAwaitValue
import com.example.weather_app.model.FakeRepository
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    lateinit var viewModel: AlertViewModel
    lateinit var repo: FakeRepository

    @Before
    fun setUp() {
        repo = FakeRepository()
        viewModel = AlertViewModel(repo)
    }

    @Test
    fun getAlerts_returnAlerts() = runBlockingTest {
        repo.insertAlert(AlertModel(1, "1", "1", "1", "alarm", 0.0, 0.0, "cairo"))
        repo.insertAlert(AlertModel(2, "2", "2", "2", "notification", 10.0, 5.0, "tanta"))
        //when
        viewModel.getAlerts()
        val result = viewModel.alert.getOrAwaitValue()
        //then
        assertThat(result, not(nullValue()))
    }

    @Test
    fun insertAlert_alert_returnAlert() = runBlockingTest {
        // given
        val expectedAlerts = listOf(
            AlertModel(3, "1", "1", "1", "alarm", 0.0, 0.0, "address1"),
            AlertModel(4, "2", "2", "2", "notification", 2.0, 4.0, "address2")
        )
        // when
        expectedAlerts.forEach { viewModel.addAlert(it) }
        val result = viewModel.alert.getOrAwaitValue()

        // then
        assertThat(result, notNullValue())
        assertThat(result, `is`(expectedAlerts))
    }

    @Test
    fun deleteAlert_alert_returnEmptyAlert() = runBlockingTest {
        //given
        repo.insertAlert(AlertModel(1, "1", "1", "1", "alarm", 0.0, 0.0, "cairo"))
        repo.insertAlert(AlertModel(2, "2", "2", "2", "notification", 10.0, 5.0, "tanta"))
        repo.insertAlert(AlertModel(3, "3", "3", "3", "notification", 20.0, 15.0, "tanta"))
        val expectedAlerts = listOf(
            AlertModel(1, "1", "1", "1", "alarm", 0.0, 0.0, "cairo"),
            AlertModel(2, "2", "2", "2", "notification", 10.0, 5.0, "tanta")
        )
        //when
        val alert = AlertModel(3, "3", "3", "3", "notification", 20.0, 15.0, "tanta")
        viewModel.removeAlert(alert)
        //then
        val result = viewModel.alert.getOrAwaitValue()
        assertThat(result, notNullValue())
        assertThat(result, `is`(expectedAlerts))
    }





}