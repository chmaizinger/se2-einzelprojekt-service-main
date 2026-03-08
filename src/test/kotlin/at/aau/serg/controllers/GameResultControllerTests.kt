package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenever
import kotlin.test.assertEquals

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult() {
        val gameResult = GameResult(1, "player1", 100, 10.0)
        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val result = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(gameResult, result)
    }

    @Test
    fun test_getAllGameResults() {
        val gameResults = listOf(GameResult(1, "player1", 100, 10.0))
        whenever(mockedService.getGameResults()).thenReturn(gameResults)

        val result = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(gameResults, result)
    }

    @Test
    fun test_addGameResult() {
        val gameResult = GameResult(1, "player1", 100, 10.0)

        controller.addGameResult(gameResult)

        verify(mockedService).addGameResult(gameResult)
    }

    @Test
    fun test_deleteGameResult() {
        controller.deleteGameResult(1)

        verify(mockedService).deleteGameResult(1)
    }
}
