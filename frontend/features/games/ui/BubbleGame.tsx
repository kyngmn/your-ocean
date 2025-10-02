"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { Button } from "@/components/ui/button"
import LottiePlayer from "@/components/common/LottiePlayer"
import Typography from "@/components/ui/Typography"
import ProgressBar from "@/components/common/ProgressBar"
import {
  useMutationBARTFirstRoundClick,
  useMutationBARTRoundClick,
  useMutationBARTRoundFinish,
  useMutationFinishGameSession,
  useMutationGameSession
} from "../mutations"
import { GameSessionIdDTO } from "@/types/dto"
import { dev } from "@/lib/dev"

// 상수
const _ROUND_DURATION = 1000 // 1초
const TOTAL_ROUNDS = 30

// ==============================================
// 버블 관련 설정
// ==============================================
// 버블 종류와 색상 매핑
type BubbleType = 1 | 2 | 3
type BubbleColor = "RED" | "BLUE" | "GREEN"

// 버블 종류별 설정
const BUBBLE_CONFIG: Record<BubbleType, { color: BubbleColor; src: string; min: number; max: number }> = {
  1: { color: "RED", src: "/animations/bubble1.lottie", min: 1, max: 20 },
  2: { color: "BLUE", src: "/animations/bubble2.lottie", min: 20, max: 40 },
  3: { color: "GREEN", src: "/animations/bubble3.lottie", min: 40, max: 60 }
}

// 각 버블 종류별 범위 내에서 max_pump 설정
function randomIntInclusive(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

// 버블 종류 랜덤 설정
function randomBubbleType(): BubbleType {
  const roll = randomIntInclusive(1, 3) as BubbleType
  return roll
}

// ==============================================
// 라운드 관련 설정
// ==============================================
// 라운드 상태
type RoundState = {
  bubbleType: BubbleType
  color: BubbleColor
  poppingPoint: number
  currentPump: number
  clickIndex: number
  status: "playing" | "popped" | "stopped"
}

// 새로운 라운드 생성
function createNewRound(): RoundState {
  const bubbleType = randomBubbleType()
  const config = BUBBLE_CONFIG[bubbleType]
  const poppingPoint = randomIntInclusive(config.min, config.max)

  return {
    bubbleType,
    color: config.color,
    poppingPoint,
    currentPump: 0,
    clickIndex: 0,
    status: "playing"
  }
}

// ==============================================
// 게임 결과 데이터
// ==============================================
// 게임 데이터 타입
interface GameData {
  successBalloons: number
  failBalloons: number
  rewardAmount: number
  missedReward: number
}

interface BubbleGameProps {
  onGameEnd?: (gameData: GameData) => void
}

// ==============================================
// 게임 컴포넌트
// ==============================================
export default function BubbleGame({ onGameEnd }: BubbleGameProps) {
  const [sessionId, setSessionId] = useState<number | null>(null)
  const [totalScore, setTotalScore] = useState(0)
  const [round, setRound] = useState<RoundState | null>(null)
  const [currentRoundNumber, setCurrentRoundNumber] = useState(1)
  const [isAutoAdvancing, setIsAutoAdvancing] = useState(false)
  const [isFirstClick, setIsFirstClick] = useState(true)
  const [canStop, setCanStop] = useState(false)

  // 게임 데이터 수집
  const [gameData, setGameData] = useState<GameData>({
    successBalloons: 0,
    failBalloons: 0,
    rewardAmount: 0,
    missedReward: 0
  })

  // ==============================================
  // mutation 관련
  // ==============================================
  // 게임 세션 생성
  const { mutate: mutateGameSession } = useMutationGameSession({
    onSuccess: (data: unknown) => {
      dev.log("🔥 게임 세션 생성 성공:", data)
      const sessionData = data as GameSessionIdDTO
      setSessionId(sessionData.sessionId)
    },
    onError: (error) => {
      dev.error("🔥 게임 세션 생성 실패:", error)
    }
  })

  // 첫 클릭 요청
  const { mutate: mutateFirstClick } = useMutationBARTFirstRoundClick({
    onSuccess: (data: unknown) => {
      dev.log("🔥 첫 클릭 성공:", data)
    },
    onError: (error) => {
      dev.error("🔥 첫 클릭 실패:", error)
    }
  })

  // 일반 클릭 요청
  const { mutate: mutateRoundClick } = useMutationBARTRoundClick({
    onSuccess: (data: unknown) => {
      dev.log("🔥 클릭 성공:", data)
    },
    onError: (error) => {
      dev.error("🔥 클릭 실패:", error)
    }
  })

  // 라운드 종료 요청
  const { mutate: mutateRoundFinish } = useMutationBARTRoundFinish({
    onSuccess: (data: unknown) => {
      dev.log("🔥 라운드 종료 성공:", data)
    },
    onError: (error) => {
      dev.error("🔥 라운드 종료 실패:", error)
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

  // ==============================================
  // useEffect 로 게임 및 라운드 시작 관리
  // ==============================================
  // 게임 시작 - 세션 생성
  useEffect(() => {
    if (!sessionId) {
      mutateGameSession("BART")
    }
  }, [sessionId, mutateGameSession])

  // 새로운 라운드 시작
  const startNewRound = useCallback(() => {
    const newRound = createNewRound()
    setRound(newRound)
    setIsFirstClick(true)
    setCanStop(false) // 새로운 라운드에서는 그만 버튼 비활성화
  }, [])

  // 라운드 시작
  useEffect(() => {
    if (sessionId && !round) {
      startNewRound()
    }
  }, [sessionId, round, startNewRound])

  // ==============================================
  // 게임 진행
  // 1. 바람 넣기
  // 2. 그만! 버튼 누르기
  // 3. 게임 종료
  // ==============================================
  // 바람 넣기
  const handlePump = useCallback(() => {
    if (!round || round.status !== "playing" || !sessionId) return

    const next = round.currentPump + 1
    const newClickIndex = round.clickIndex + 1
    const clickedAt = new Date().toISOString()

    // 각 라운드의 첫 클릭인지 확인하여 적절한 API 호출
    if (isFirstClick) {
      // 각 라운드의 첫 클릭 데이터
      const firstClickData = {
        color: round.color,
        poppingPoint: round.poppingPoint,
        clickIndex: newClickIndex,
        clickedAt
      }

      mutateFirstClick({
        sessionId,
        roundIndex: currentRoundNumber,
        clickData: firstClickData
      })
      setIsFirstClick(false)
      setCanStop(true) // 첫 클릭 후 그만 버튼 활성화
    } else {
      // 일반 클릭 데이터
      const normalClickData = {
        clickIndex: newClickIndex,
        clickedAt
      }

      mutateRoundClick({
        sessionId,
        roundIndex: currentRoundNumber,
        clickData: normalClickData
      })
    }

    if (next >= round.poppingPoint) {
      // 터짐 - 라운드 종료 API 호출
      setRound({ ...round, currentPump: next, clickIndex: newClickIndex, status: "popped" })

      // mutateRoundFinish({
      //   sessionId,
      //   roundIndex: currentRoundNumber,
      //   clickData: { isPopped: true }
      // })

      // 실패한 버블 카운트 증가
      setGameData((prev) => ({
        ...prev,
        failBalloons: prev.failBalloons + 1,
        missedReward: prev.missedReward + round.poppingPoint
      }))

      // 1초 후 자동 진행
      setIsAutoAdvancing(true)
      setTimeout(() => {
        if (currentRoundNumber < 30) {
          setCurrentRoundNumber((prev) => prev + 1)
          startNewRound()
        } else {
          // 게임 종료 시 최종 데이터 전달
          const finalGameData = {
            ...gameData,
            failBalloons: gameData.failBalloons + 1,
            missedReward: gameData.missedReward + round.poppingPoint,
            rewardAmount: totalScore
          }
          
          dev.log("🔥 게임 종료 (터짐) - API 요청 시작:", sessionId)
          mutateFinishGameSession(sessionId as number)
          
          onGameEnd?.(finalGameData)
        }
        setIsAutoAdvancing(false)
      }, 1000)
    } else {
      setRound({ ...round, currentPump: next, clickIndex: newClickIndex })
    }
  }, [
    round,
    sessionId,
    isFirstClick,
    currentRoundNumber,
    mutateFirstClick,
    mutateRoundClick,
    mutateRoundFinish,
    startNewRound,
    totalScore,
    onGameEnd,
    gameData
  ])

  // 그만!
  const handleStop = useCallback(() => {
    if (!round || round.status !== "playing" || !sessionId) return

    setRound({ ...round, status: "stopped" })

    // 라운드 종료 API 호출 (터지지 않음)
    mutateRoundFinish({
      sessionId,
      roundIndex: currentRoundNumber,
      clickData: { isPopped: false }
    })

    // 점수 계산
    const roundScore = round.currentPump
    setTotalScore((s) => s + roundScore)

    // 성공한 버블 카운트 증가
    setGameData((prev) => ({
      ...prev,
      successBalloons: prev.successBalloons + 1,
      rewardAmount: prev.rewardAmount + roundScore
    }))

    // 1초 후 자동 진행
    setIsAutoAdvancing(true)
    setTimeout(() => {
      if (currentRoundNumber < 30) {
        setCurrentRoundNumber((prev) => prev + 1)
        startNewRound()
      } else {
        // 게임 종료 시 최종 데이터 전달
        const finalGameData = {
          ...gameData,
          successBalloons: gameData.successBalloons + 1,
          rewardAmount: gameData.rewardAmount + roundScore
        }

        dev.log("🔥 게임 종료 (그만) - API 요청 시작:", sessionId)
        mutateFinishGameSession(sessionId as number)
        
        onGameEnd?.(finalGameData)
      }
      setIsAutoAdvancing(false)
    }, 1000)
  }, [round, sessionId, currentRoundNumber, mutateRoundFinish, startNewRound, onGameEnd, gameData])

  // 라운드 종료
  const handleNext = useCallback(() => {
    if (currentRoundNumber < 30) {
      setCurrentRoundNumber((prev) => prev + 1)
      startNewRound()
    } else {
      // Game finished - 최종 데이터 전달
      const finalGameData = {
        ...gameData,
        rewardAmount: totalScore
      }

      dev.log("🔥 게임 종료 - API 요청 시작:", sessionId)
      mutateFinishGameSession(sessionId as number)

      onGameEnd?.(finalGameData)
    }
  }, [startNewRound, currentRoundNumber, totalScore, onGameEnd, gameData, sessionId, mutateFinishGameSession])

  const sizePx = useMemo(() => {
    // Visual growth: base 140px + 4px per pump
    const base = 140
    const growthPerPump = 4
    const pumps = round?.currentPump ?? 0
    return base + pumps * growthPerPump
  }, [round?.currentPump])

  const bubbleSrc = round ? BUBBLE_CONFIG[round.bubbleType].src : BUBBLE_CONFIG[1].src

  return (
    <div className="flex flex-col min-h-[calc(100vh-var(--header-height))] w-full p-8">
      {/* 헤더 영역 - 상단 고정 */}
      <div className="flex-shrink-0 p-4">
        <div className="text-center space-y-2">
          <Typography type="h3">
            총 점수: <span className="text-accent-blue">{totalScore}</span>
          </Typography>
          <ProgressBar
            type="twoLine"
            value={(currentRoundNumber / TOTAL_ROUNDS) * 100}
            entireRound={TOTAL_ROUNDS}
            currentRound={currentRoundNumber}
          />
        </div>
      </div>

      {/* 게임 영역 - 남은 공간 모두 사용, 중앙 정렬 */}
      <div className="flex flex-1  flex-col items-center justify-center py-auto">
        {isAutoAdvancing && (
          <div className="animate-fade-in">
            <Typography
              type="h3"
              className={`${round?.status === "popped" ? "text-error-dark" : "black"} animate-bounce`}
            >
              {round?.status === "popped" ? "터졌어요!" : `${round?.currentPump}점을 얻었어요!`}
            </Typography>
          </div>
        )}

        <div className="relative flex items-center justify-center" style={{ width: sizePx, height: sizePx }}>
          {/* Lottie bubble */}
          {round?.status === "playing" && (
            <div className="w-full h-full">
              <LottiePlayer src={bubbleSrc} autoplay={true} loop={true} className="w-full h-full" />
            </div>
          )}
          {/* Pop overlay */}
          {round?.status === "popped" && (
            <div className="absolute inset-0 flex items-center justify-center text-red-600 font-bold bg-white/60 rounded">
              <LottiePlayer src="/animations/bubble_burst2.lottie" autoplay={true} className="w-full h-full" />
            </div>
          )}
        </div>
      </div>

      {/* 버튼 영역 - 하단 고정 */}
      {round && round.status === "playing" && (
        <div className="text-center space-y-2">
          <Typography type="h3">현재 점수: {round?.currentPump}</Typography>
        </div>
      )}
      <div className="flex flex-shrink-0 py-8">
        {round && round.status === "playing" && (
          <div className="flex w-full gap-2">
            <Button className="flex-1" size="lg" onClick={handlePump}>
              바람 넣기
            </Button>
              <Button className="flex-1" size="lg" variant="secondary" onClick={handleStop} disabled={!canStop}>
                그만!
              </Button>
          </div>
        )}

        {round && round.status !== "playing" && !isAutoAdvancing && currentRoundNumber >= 30 && (
          <div className="flex flex-col items-center w-full gap-3">
            <Button size="lg" className="w-full" onClick={handleNext}>
              게임 종료
            </Button>
          </div>
        )}
      </div>
    </div>
  )
}
