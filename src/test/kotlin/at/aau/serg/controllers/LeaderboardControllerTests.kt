package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin
import org.springframework.http.HttpStatus

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController
    private lateinit var sampleGameResults: List<GameResult>

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
        sampleGameResults = (1..10).map {
            GameResult(it.toLong(), "Player$it", 100 - it * 5, 10.0 + it)
        }
        whenever(mockedService.getGameResults()).thenReturn(sampleGameResults)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val response = controller.getLeaderboard(null)
        val res = response.body!!


        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val response = controller.getLeaderboard(null)
        val res = response.body!!

        verify(mockedService).getGameResults()
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_getLeaderboard_withNullRank_returnsFullBoard() {
        val response = controller.getLeaderboard(null)
        val sortedResults = sampleGameResults.sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(sortedResults, response.body)
        assertEquals(10, response.body?.size)
    }

    @Test
    fun test_getLeaderboard_withValidRank_returnsSlice() {
        // rank 5 should be index 4
        // expected slice is index 1 to 7 (4-3 to 4+3)
        val response = controller.getLeaderboard(5)
        val sortedResults = sampleGameResults.sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))
        val expectedSlice = sortedResults.slice(1..7)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedSlice, response.body)
        assertEquals(7, response.body?.size)
    }

    @Test
    fun test_getLeaderboard_withRankAtStart_returnsSlice() {
        // rank 1 should be index 0
        // expected slice is index 0 to 3 (0-3 clamped to 0, 0+3)
        val response = controller.getLeaderboard(1)
        val sortedResults = sampleGameResults.sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))
        val expectedSlice = sortedResults.slice(0..3)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedSlice, response.body)
        assertEquals(4, response.body?.size)
    }

    @Test
    fun test_getLeaderboard_withRankAtEnd_returnsSlice() {
        // rank 10 should be index 9
        // expected slice is index 6 to 9 (9-3, 9+3 clamped to 9)
        val response = controller.getLeaderboard(10)
        val sortedResults = sampleGameResults.sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))
        val expectedSlice = sortedResults.slice(6..9)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedSlice, response.body)
        assertEquals(4, response.body?.size)
    }

    @Test
    fun test_getLeaderboard_withInvalidRankTooLow_returnsBadRequest() {
        val response = controller.getLeaderboard(0)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun test_getLeaderboard_withInvalidRankTooHigh_returnsBadRequest() {
        val response = controller.getLeaderboard(11)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}