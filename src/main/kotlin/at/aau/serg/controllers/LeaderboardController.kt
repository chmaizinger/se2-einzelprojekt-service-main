package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import kotlin.math.max
import kotlin.math.min

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): ResponseEntity<List<GameResult>> {
        val leaderboard = gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        if (rank == null) {
            return ResponseEntity.ok(leaderboard)
        }

        if (rank < 1 || rank > leaderboard.size) {
            return ResponseEntity.badRequest().build()
        }

        val rankIndex = rank - 1
        val startIndex = max(0, rankIndex - 3)
        val endIndex = min(leaderboard.size - 1, rankIndex + 3)

        val result = leaderboard.slice(startIndex..endIndex)
        return ResponseEntity.ok(result)
    }
}