"use client"

import { useCallback, useEffect, useState } from "react"
import ProgressBar from "@/components/common/ProgressBar"
import Typography from "@/components/ui/Typography"
import LottiePlayer from "@/components/common/LottiePlayer"
import Image from "next/image"
import { useMutationFinishGameSession, useMutationGameSession, useMutationGNGRound } from "@/features/games/mutations"
import { dev } from "@/lib/dev"
import { GameType } from "@/types/enums"
import { GameSessionIdDTO } from "@/types/dto"

// 상수
const SHARK_SRC = "/characters/shark.png"
const SEAPLANT_SRC = "/animations/seaplant.lottie"
const ROUND_DURATION = 1000 // 1초
const TOTAL_ROUNDS = 50
const GAME_TYPE = "GNG" as GameType

// 게임 데이터 타입
interface GameData {
  successCount: number
  failCount: number
  noGoCount: number
  averageReactionTime: number
}

// 게임 컴포넌트 속성
interface SharkGameProps {
  onGameEnd?: (gameData: GameData) => void
}

// 게임 상태
type GameStatus = "waiting" | "showing" | "result"

// 라운드 상태
type RoundState = {
  type: "GO" | "NOGO"
  startTime: number
  status: GameStatus
  reactionTime?: number
}

// ==============================================
// 게임 컴포넌트
// ==============================================
export default function SharkGame({ onGameEnd }: SharkGameProps) {
  const [currentRoundNumber, setCurrentRoundNumber] = useState(1)
  const [round, setRound] = useState<RoundState | null>(null)
  const [gameData, setGameData] = useState<GameData>({
    successCount: 0,
    failCount: 0,
    noGoCount: 0,
    averageReactionTime: 0
  })
  const [reactionTimes, setReactionTimes] = useState<number[]>([])
  const [countdown, setCountdown] = useState<number | null>(null)
  const [gameStarted, setGameStarted] = useState(false)
  const [sessionId, setSessionId] = useState<number | null>(null)

  // 랜덤 타입 생성 (50% 확률)
  const getRandomType = (): "GO" | "NOGO" => {
    return Math.random() < 0.5 ? "GO" : "NOGO"
  }

  // 카운트다운 시작
  const startCountdown = useCallback(() => {
    setCountdown(3)
  }, [])

  // 게임 세션 생성 (성공/실패 시 카운트다운 시작)
  const { mutate: mutateGameSession } = useMutationGameSession({
    onSuccess: (data: unknown) => {
      dev.log("🔥 게임 세션 생성 성공:", data)
      const sessionData = data as GameSessionIdDTO
      setSessionId(sessionData.sessionId)
      startCountdown()
    },
    onError: (error) => {
      dev.error("🔥 게임 세션 생성 실패:", error)
      // startCountdown()
    }
  })

  // GNG 라운드 요청
  const { mutate: mutateGNGRound } = useMutationGNGRound({
    onSuccess: (data) => {
      dev.log("🔥 GNG 라운드 요청 성공:", data)
    },
    onError: (error) => {
      dev.error("🔥 GNG 라운드 요청 실패:", error)
    }
  })

  // 게임 세션 종료
  const { mutate: mutateFinishGameSession } = useMutationFinishGameSession({
    onSuccess: (data) => {
      dev.log("🔥 게임 세션 종료 성공:", data)
    },
    onError: (error) => {
      dev.error("🔥 게임 세션 종료 실패:", error)
    }
  })

  // 게임 세션 생성 후 카운트다운 시작
  const initializeGame = useCallback(() => {
    dev.log("🔥 게임 초기화 시작")
    mutateGameSession(GAME_TYPE)
  }, [mutateGameSession])

  // 새로운 라운드 시작
  const startNewRound = useCallback(() => {
    const type = getRandomType()
    const startTime = Date.now()

    setRound({
      type,
      startTime,
      status: "showing"
    })
  }, [])

  // 스페이스바 눌렀을 때 처리
  const handleSpacePress = useCallback(() => {
    if (!round || round.status !== "showing") return

    const reactionTime = Date.now() - round.startTime
    const isSuccess = round.type === "GO" // GO일 때만 성공
    const respondedAt = new Date().toISOString()
    const stimulusStartedAt = new Date(round.startTime).toISOString()

    // API 요청 보내기
    dev.log("🔥 API 요청 시도:", {
      sessionId,
      roundIndex: currentRoundNumber,
      clickData: {
        stimulusType: round.type,
        stimulusStartedAt,
        respondedAt,
        isSucceeded: isSuccess
      }
    })

    if (sessionId) {
      mutateGNGRound({
        sessionId,
        roundIndex: currentRoundNumber,
        clickData: {
          stimulusType: round.type,
          stimulusStartedAt,
          respondedAt,
          isSucceeded: isSuccess
        }
      })
    } else {
      dev.error("🔥 sessionId가 없어서 API 요청을 보낼 수 없습니다")
    }

    setRound({
      ...round,
      status: "result",
      reactionTime
    })

    // 게임 데이터 업데이트
    setGameData((prev) => ({
      ...prev,
      successCount: prev.successCount + (isSuccess ? 1 : 0),
      failCount: prev.failCount + (isSuccess ? 0 : 1)
    }))

    if (isSuccess) {
      setReactionTimes((prev) => [...prev, reactionTime])
    }

    // 1초 후 다음 라운드
    setTimeout(() => {
      if (currentRoundNumber < TOTAL_ROUNDS) {
        setCurrentRoundNumber((prev) => prev + 1)
        setRound(null)
      } else {
        // 게임 종료
        const finalGameData = {
          ...gameData,
          successCount: gameData.successCount + (isSuccess ? 1 : 0),
          failCount: gameData.failCount + (isSuccess ? 0 : 1),
          averageReactionTime:
            reactionTimes.length > 0 ? reactionTimes.reduce((a, b) => a + b, 0) / reactionTimes.length : 0
        }

        dev.log("🔥 게임 종료 - API 요청 시작:", sessionId)
        mutateFinishGameSession(sessionId as number)
        
        onGameEnd?.(finalGameData)
      }
    }, 1000)
  }, [round, currentRoundNumber, gameData, reactionTimes, onGameEnd, sessionId, mutateGNGRound])

  // 반응하지 않았을 때 처리
  const handleNoReaction = useCallback(() => {
    if (!round || round.status !== "showing") return

    const isSuccess = round.type === "NOGO" // NOGO일 때만 성공 (누르지 않아야 함)
    const respondedAt = new Date().toISOString()
    const stimulusStartedAt = new Date(round.startTime).toISOString()

    // API 요청 보내기 (반응하지 않은 경우)
    if (sessionId) {
      dev.log("🔥 API 요청 시작 (No Reaction):", {
        sessionId,
        roundIndex: currentRoundNumber,
        clickData: {
          stimulusType: round.type,
          stimulusStartedAt,
          respondedAt,
          isSucceeded: isSuccess
        }
      })
      mutateGNGRound({
        sessionId,
        roundIndex: currentRoundNumber,
        clickData: {
          stimulusType: round.type,
          stimulusStartedAt,
          respondedAt,
          isSucceeded: isSuccess
        }
      })
    } else {
      dev.error("🔥 sessionId가 없어서 API 요청을 보낼 수 없습니다 (No Reaction)")
    }

    setRound({
      ...round,
      status: "result"
    })

    // 게임 데이터 업데이트
    setGameData((prev) => ({
      ...prev,
      successCount: prev.successCount + (isSuccess ? 1 : 0),
      noGoCount: prev.noGoCount + (isSuccess ? 0 : 1)
    }))

    // 1초 후 다음 라운드
    setTimeout(() => {
      if (currentRoundNumber < TOTAL_ROUNDS) {
        setCurrentRoundNumber((prev) => prev + 1)
        setRound(null)
      } else {
        // 게임 종료
        const finalGameData = {
          ...gameData,
          successCount: gameData.successCount + (isSuccess ? 1 : 0),
          noGoCount: gameData.noGoCount + (isSuccess ? 0 : 1),
          averageReactionTime:
            reactionTimes.length > 0 ? reactionTimes.reduce((a, b) => a + b, 0) / reactionTimes.length : 0
        }

        // dev.log("🔥 게임 종료 - API 요청 시작 (No Reaction):", sessionId)
        // mutateFinishGameSession(sessionId as number)

        onGameEnd?.(finalGameData)
      }
    }, 1000)
  }, [round, currentRoundNumber, gameData, reactionTimes, onGameEnd, sessionId, mutateGNGRound])

  // 컴포넌트 마운트 시 게임 초기화
  useEffect(() => {
    initializeGame()
  }, [initializeGame])

  // sessionId 변경 감지
  useEffect(() => {
    dev.log("🔥 sessionId 상태 변경:", sessionId, typeof sessionId)
  }, [sessionId])

  // 카운트다운 관리
  useEffect(() => {
    if (countdown === null) return

    const timer = setTimeout(() => {
      if (countdown > 1) {
        setCountdown(countdown - 1)
      } else {
        setCountdown(null)
        setGameStarted(true)
      }
    }, 1000)

    return () => clearTimeout(timer)
  }, [countdown])

  // 라운드 시작
  useEffect(() => {
    if (!round && gameStarted) {
      startNewRound()
    }
  }, [round, gameStarted, startNewRound])

  // 자동 결과 처리 (1초 후)
  useEffect(() => {
    if (round && round.status === "showing") {
      const timer = setTimeout(() => {
        handleNoReaction()
      }, ROUND_DURATION)

      return () => clearTimeout(timer)
    }
  }, [round, handleNoReaction])

  // 스페이스바 키 이벤트 처리
  useEffect(() => {
    const handleKeyPress = (event: KeyboardEvent) => {
      if (event.code === "Space" && round && round.status === "showing") {
        event.preventDefault()
        handleSpacePress()
      }
    }

    window.addEventListener("keydown", handleKeyPress)
    return () => window.removeEventListener("keydown", handleKeyPress)
  }, [round, handleSpacePress])

  // 터치/클릭 이벤트 처리 (모바일 지원)
  const handleTouchClick = useCallback(() => {
    if (round && round.status === "showing") {
      handleSpacePress()
    }
  }, [round, handleSpacePress])

  // 게임 결과 메시지
  const getResultMessage = () => {
    if (!round || round.status !== "result") return null

    const isSuccess =
      (round.type === "NOGO" && !round.reactionTime) || (round.type === "GO" && round.reactionTime)

    return (
      <div className="animate-fade-in flex flex-col items-center justify-center">
        <Typography type="h3" className={`${isSuccess ? "text-accent-blue" : "text-error-dark"} animate-bounce`}>
          {isSuccess ? "성공!" : "실패!"}
        </Typography>
        {round.reactionTime && (
          <Typography type="p" className="text-gray-600">
            반응 시간: {round.reactionTime}ms
          </Typography>
        )}
      </div>
    )
  }

  return (
    <div className="flex flex-col min-h-[calc(100vh-var(--header-height))] w-full p-8">
      {/* 헤더 영역 - 상단 고정 */}
      <div className="flex-shrink-0 p-4">
        <div className="text-center space-y-2">
          <Typography type="h3">상어를 피하자!</Typography>
          <ProgressBar
            type="twoLine"
            value={(currentRoundNumber / TOTAL_ROUNDS) * 100}
            entireRound={TOTAL_ROUNDS}
            currentRound={currentRoundNumber}
          />
        </div>
      </div>

      {/* 게임 영역 */}
      <div className="flex-1 flex flex-col items-center justify-center py-8">
        {/* 카운트다운 표시 */}
        {countdown !== null && (
          <div className="flex flex-col items-center justify-center">
            <Typography type="h1" className="text-6xl font-bold animate-bounce">
              {countdown}
            </Typography>
            <Typography type="p" className="text-gray-600 mt-4">
              게임 시작 준비 중
            </Typography>
          </div>
        )}

        {/* 게임 결과 메시지 */}
        {getResultMessage()}

        {/* 게임 화면 */}
        {round && round.status === "showing" && gameStarted && (
          <div
            className="relative flex items-center justify-center w-64 h-64 cursor-pointer touch-manipulation"
            onClick={handleTouchClick}
            onTouchStart={handleTouchClick}
          >
            {round.type === "NOGO" ? (
              <Image src={SHARK_SRC} alt="상어" width={256} height={256} className="w-full h-full object-contain" />
            ) : (
              <LottiePlayer src={SEAPLANT_SRC} autoplay={true} loop={true} className="w-full h-full" />
            )}
          </div>
        )}
      </div>

      {/* 하단 안내 */}
      <div className="flex-shrink-0 p-4 text-center">
        {countdown !== null ? (
          <Typography type="p" className="text-gray-600">
            준비하세요! 곧 게임이 시작됩니다
          </Typography>
        ) : (
          <>
            <Typography type="p" className="text-sm text-gray-500 mt-2">
              화면을 터치하거나 스페이스바를 눌러 반응하세요
            </Typography>
          </>
        )}
      </div>
    </div>
  )
}
