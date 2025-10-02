"use client"

import { useState, useEffect } from "react"
import ProgressBar from "@/components/common/ProgressBar"
import Typography from "@/components/ui/Typography"
import { Slider } from "@/components/ui/slider"
import { Button } from "@/components/ui/button"
import Image from "next/image"
import { Badge } from "@/components/ui/badge"
import LottiePlayer from "@/components/common/LottiePlayer"
import { dev } from "@/lib/dev"
import { useMutationFinishGameSession, useMutationGameSession, useMutationUGRound } from "../mutations"
import { GameSessionIdDTO } from "@/types/dto"
import { getUGGameOrder } from "@/app/actions/games"
import Loading from "@/components/common/Loading"
import { GameUgOrder } from "@/types/schema"

// Family 페르소나 타입별 이미지
const FamilyPersonaImages = {
  O: "/characters/O.png",
  C: "/characters/C.png",
  E: "/characters/E.png",
  A: "/characters/A.png",
  N: "/characters/N.png"
}

const TOTAL_ROUNDS = 30

// 게임 데이터 타입
interface GameData {
  successRate: number
  acceptRate: number
}

// 게임 상태 타입
type GameMode = "proposer" | "responder" | "result"

// 라운드 결과 타입
interface RoundResult {
  roundNumber: number
  roleType: number
  personaType: string
  money: string
  totalAmount: number
  userProposal?: number
  userDecision?: boolean
  aiDecision?: boolean
  userEarned: number
  aiEarned: number
  isAccepted: boolean
}

interface TreasureGameProps {
  onGameEnd?: (gameData: GameData) => void
}

// ==============================================
// 게임 컴포넌트
// ==============================================
export default function TreasureGame({ onGameEnd }: TreasureGameProps) {
  const [currentRoundNumber, setCurrentRoundNumber] = useState(1)
  const [gameMode, setGameMode] = useState<GameMode>("proposer")
  const [_gameData, setGameData] = useState<GameData>({
    successRate: 0,
    acceptRate: 0
  })
  const [sessionId, setSessionId] = useState<number | null>(null)

  // 게임 진행 상태
  const [proposalRate, setProposalRate] = useState(1)
  const [roundResults, setRoundResults] = useState<RoundResult[]>([])
  const [negotiationSuccessCount, setNegotiationSuccessCount] = useState(0)
  const [currentRoundTotalAmount, setCurrentRoundTotalAmount] = useState<number>(0)
  const [currentRoundPersonaImage, setCurrentRoundPersonaImage] = useState<string>("")
  const [gameOrder, setGameOrder] = useState<GameUgOrder[]>([])

  // UG 라운드 결과 보내기
  const { mutate: mutateUGRound } = useMutationUGRound({
    onSuccess: (data) => {
      dev.log("🔥 UG 라운드 결과 보내기 성공:", data)
    },
    onError: (error) => {
      dev.error("🔥 UG 라운드 결과 보내기 실패:", error)
    }
  })

  // 게임 세션 생성
  const { mutate: mutateGameSession } = useMutationGameSession({
    onSuccess: async (data: unknown) => {
      dev.log("🔥 게임 세션 생성 성공:", data)
      const sessionData = data as GameSessionIdDTO
      setSessionId(sessionData.sessionId)

      // 세션 ID를 받은 후 게임 순서 및 정보 조회
      if (sessionData.sessionId) {
        const gameOrder = await getUGGameOrder(sessionData.sessionId)
        dev.log("🔥 게임 순서 및 정보 조회 성공:", gameOrder)
        setGameOrder(gameOrder.data)
      }
    },
    onError: (error) => {
      dev.error("🔥 게임 세션 생성 실패:", error)
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

  // 처음 게임 시작 시 게임 세션 생성
  useEffect(() => {
    mutateGameSession("UG")
  }, [mutateGameSession])

  // 라운드가 변경될 때마다 totalAmount와 이미지 설정
  useEffect(() => {
    // 게임 순서가 로드되지 않았으면 리턴
    if (!gameOrder || gameOrder.length === 0) return
    const currentRoundData = gameOrder[currentRoundNumber - 1]
    if (!currentRoundData) return

    const totalAmount = generateTotalAmount(currentRoundData.money)

    // 이미지 생성 함수 (useEffect 내부에서 정의)
    const getPersonaImage = (personaType: string): string => {
      if (personaType === "FRIEND") return "/characters/friend.png"
      if (personaType === "STRANGER") return "/characters/random.png"
      if (personaType === "FAMILY") {
        const familyPersona = getRandomFamilyPersona()
        return FamilyPersonaImages[familyPersona]
      }
      return "/characters/random.png" // 기본값
    }

    const personaImage = getPersonaImage(currentRoundData.personaType)
    setCurrentRoundTotalAmount(totalAmount)
    setCurrentRoundPersonaImage(personaImage)
  }, [gameOrder, currentRoundNumber])

  // 게임 순서가 로드되지 않았으면 로딩 표시
  if (!gameOrder || gameOrder.length === 0) return <Loading />

  // 현재 라운드 데이터
  const currentRoundData = gameOrder[currentRoundNumber - 1]

  // ================================
  // 유틸 함수
  // ================================
  // Family 사진 랜덤 선택
  const getRandomFamilyPersona = (): keyof typeof FamilyPersonaImages => {
    const personas = Object.keys(FamilyPersonaImages) as Array<keyof typeof FamilyPersonaImages>
    return personas[Math.floor(Math.random() * personas.length)]
  }

  // 페르소나 타입을 한글로 변환
  const getPersonaTypeKorean = (personaType: string): string => {
    switch (personaType) {
      case "FRIEND":
        return "친구"
      case "STRANGER":
        return "처음 보는 사람"
      case "FAMILY":
        return "가족"
      default:
        return personaType
    }
  }

  // 금액 크기에 따른 totalAmount 생성
  const generateTotalAmount = (moneySize: string): number => {
    if (moneySize === "SMALL") {
      // 1만원 ~ 10만원, 1만원 단위로 랜덤 선택
      const minSteps = 1 // 1만원
      const maxSteps = 10 // 10만원
      const randomSteps = Math.floor(Math.random() * (maxSteps - minSteps + 1)) + minSteps
      return randomSteps * 10000
    } else if (moneySize === "LARGE") {
      // 1억 ~ 10억, 1000만원 단위로 랜덤 선택
      const minSteps = 10 // 1억 (10 * 1000만원)
      const maxSteps = 100 // 10억 (100 * 1000만원)
      const randomSteps = Math.floor(Math.random() * (maxSteps - minSteps + 1)) + minSteps
      return randomSteps * 10000000
    }
    return 10000 // 기본값
  }

  // 금액을 한국어 형식으로 표시
  const formatKoreanAmount = (amount: number): string => {
    if (amount < 1000000) {
      // 만원 단위 (1만원 ~ 99만원)
      const man = Math.floor(amount / 10000)
      return `${man}만원`
    } else if (amount < 100000000) {
      // 천만원 단위 (1천만원 ~ 9천만원)
      const cheonman = Math.floor(amount / 10000000)
      return `${cheonman}천만원`
    } else {
      // 억 단위 (1억 이상)
      const eok = Math.floor(amount / 100000000)
      const remainder = amount % 100000000
      const cheonman = Math.floor(remainder / 10000000)

      if (cheonman === 0) {
        return `${eok}억`
      } else {
        return `${eok}억 ${cheonman}천`
      }
    }
  }

  // 게임 모드 텍스트
  const getModeText = () => {
    if (gameMode === "result") return `${currentRoundNumber}라운드 결과`
    if (currentRoundData.roleType === 1) return "제안자 모드"
    if (currentRoundData.roleType === 2) return "응답자 모드"
    if (currentRoundData.roleType === 3) return "제안자 모드"
    return ""
  }

  // 상대방 수락 확률 계산 (roleType=1일 때)
  const calculateAcceptanceProbability = (proposalRate: number): number => {
    // proposalRate: 사용자가 가져갈 몫 (1~9)
    // opponentRate: 상대방이 가져갈 몫 (9~1)
    const opponentRate = 10 - proposalRate

    // 상대방이 가져갈 몫이 많을수록 수락 확률 증가
    // opponentRate 9 (상대방이 9, 사용자가 1) → 90% 확률
    // opponentRate 1 (상대방이 1, 사용자가 9) → 10% 확률
    const baseProbability = 0.1 // 최소 확률
    const maxProbability = 0.9 // 최대 확률
    const probability = baseProbability + ((opponentRate - 1) * (maxProbability - baseProbability)) / 8

    return Math.max(baseProbability, Math.min(maxProbability, probability))
  }

  // 상대방 결정 시뮬레이션
  const simulateAIDecision = (proposalRate: number, roleType: number): boolean => {
    if (roleType === 3) return true // 무조건 수락
    if (roleType === 1) {
      const probability = calculateAcceptanceProbability(proposalRate)
      dev.log("probability" + probability)
      return Math.random() < probability
    }
    return false
  }

  // ================================
  // 게임 처리
  // ================================

  // 제안자 모드 처리
  const handleProposal = () => {
    const aiAccepted = simulateAIDecision(proposalRate, currentRoundData.roleType)
    const totalAmount = currentRoundTotalAmount

    const result: RoundResult = {
      roundNumber: currentRoundNumber,
      roleType: currentRoundData.roleType,
      personaType: currentRoundData.personaType,
      money: currentRoundData.money,
      totalAmount: totalAmount,
      userProposal: proposalRate,
      aiDecision: aiAccepted,
      userEarned: aiAccepted ? Math.floor(totalAmount * (proposalRate / 10)) : 0,
      aiEarned: aiAccepted ? Math.floor(totalAmount * ((10 - proposalRate) / 10)) : 0,
      isAccepted: aiAccepted
    }

    // 라운드 결과 보내기
    if (sessionId && currentRoundNumber && gameOrder[currentRoundNumber - 1].id) {
      mutateUGRound({
        sessionId,
        roundId: currentRoundNumber,
        roundData: {
          orderId: Number(gameOrder[currentRoundNumber - 1].id),
          totalAmount: totalAmount,
          isAccepted: aiAccepted,
          proposalRate: proposalRate
        }
      })
    }

    setRoundResults((prev) => [...prev, result])
    if (aiAccepted) {
      setNegotiationSuccessCount((prev) => prev + 1)
    }

    setGameMode("result")
  }

  // 응답자 모드 처리
  const handleResponse = (accepted: boolean) => {
    const aiProposal = currentRoundData.rate || 1
    const totalAmount = currentRoundTotalAmount

    const result: RoundResult = {
      roundNumber: currentRoundNumber,
      roleType: currentRoundData.roleType,
      personaType: currentRoundData.personaType,
      money: currentRoundData.money,
      totalAmount: totalAmount,
      userDecision: accepted,
      userEarned: accepted ? Math.floor(totalAmount * (aiProposal / 10)) : 0,
      aiEarned: accepted ? Math.floor(totalAmount * ((10 - aiProposal) / 10)) : 0,
      isAccepted: accepted
    }

    // 라운드 결과 보내기
    if (sessionId && currentRoundNumber && gameOrder[currentRoundNumber - 1].id) {
      mutateUGRound({
        sessionId,
        roundId: currentRoundNumber,
        roundData: {
          orderId: Number(gameOrder[currentRoundNumber - 1].id),
          totalAmount: totalAmount,
          isAccepted: accepted,
          proposalRate: aiProposal
        }
      })
    }

    setRoundResults((prev) => [...prev, result])
    if (accepted) {
      setNegotiationSuccessCount((prev) => prev + 1)
    }

    setGameMode("result")
  }

  // 다음 라운드로 진행
  const nextRound = () => {
    if (currentRoundNumber < TOTAL_ROUNDS) {
      setCurrentRoundNumber((prev) => prev + 1)
      setGameMode(currentRoundData.roleType === 2 ? "responder" : "proposer")
      setProposalRate(1)
    } else {
      // 게임 종료
      dev.log("🔥 게임 종료 - API 요청 시작:", sessionId)
      mutateFinishGameSession(sessionId as number)

      calculateFinalStats()
    }
  }

  // 최종 통계 계산
  const calculateFinalStats = () => {
    const totalProposals = roundResults.filter((result) => result.roleType === 1 || result.roleType === 3).length
    const acceptedProposals = roundResults.filter(
      (result) => (result.roleType === 1 || result.roleType === 3) && result.isAccepted
    ).length
    const userAcceptedOffers = roundResults.filter((result) => result.roleType === 2 && result.isAccepted).length
    const totalOffers = roundResults.filter((result) => result.roleType === 2).length

    const finalStats: GameData = {
      successRate: totalProposals > 0 ? (acceptedProposals / totalProposals) * 100 : 0,
      acceptRate: totalOffers > 0 ? (userAcceptedOffers / totalOffers) * 100 : 0
    }

    setGameData(finalStats)
    onGameEnd?.(finalStats)
  }

  // 게임 모드에 따른 렌더링
  const renderGameContent = () => {
    // ================================
    // 결과 모드
    // ================================
    if (gameMode === "result") {
      const lastResult = roundResults[roundResults.length - 1]
      return (
        <>
          <div className="flex-1 flex flex-col justify-center space-y-6 p-8 w-full">
            <div className="text-center space-y-4 w-full">
              <LottiePlayer
                src={lastResult.isAccepted ? "/animations/success.lottie" : "/animations/fail.lottie"}
                loop={true}
                autoplay={true}
              />
              <Typography type="h3">{lastResult.isAccepted ? "제안 수락!" : "제안 거절!"}</Typography>
              <div className="bg-gray-100 rounded-lg p-6 space-y-3">
                <div className="flex justify-between">
                  <span>발견된 총 금액:</span>
                  <span className="font-semibold">{formatKoreanAmount(lastResult.totalAmount)}</span>
                </div>
                <div className="flex justify-between">
                  <span>내가 가져간 금액:</span>
                  <span className="font-semibold">{formatKoreanAmount(lastResult.userEarned)}</span>
                </div>
                <div className="flex justify-between">
                  <span>상대방이 가져간 금액:</span>
                  <span className="font-semibold">{formatKoreanAmount(lastResult.aiEarned)}</span>
                </div>
              </div>
            </div>
          </div>
          <div className="px-8">
            <Button onClick={nextRound} size="lg" className="w-full">
              {currentRoundNumber < TOTAL_ROUNDS ? "다음 라운드" : "게임 종료"}
            </Button>
          </div>
        </>
      )
    }

    // ================================
    // 제안자 모드
    // ================================
    if (gameMode === "proposer") {
      const totalAmount = currentRoundTotalAmount

      return (
        <>
          <div className="flex-1 flex flex-col justify-center space-y-8 w-full">
            <div className="text-center space-y-4 w-full">
              <Typography type="h3">
                <span className="text-accent-blue">{getPersonaTypeKorean(currentRoundData.personaType)}</span>에게
                얼마를 제안할까?
              </Typography>
              <div>
                <Typography type="h3">발견된 금액: {formatKoreanAmount(totalAmount)}</Typography>
              </div>
            </div>
            {/* 페르소나 이미지 */}
            <div className="flex justify-center">
              {currentRoundPersonaImage && (
                <Image
                  src={currentRoundPersonaImage}
                  alt={`${getPersonaTypeKorean(currentRoundData.personaType)} character`}
                  width={200}
                  height={200}
                  className="animate-fade-in"
                />
              )}
            </div>

            <div className="rounded-lg border border-gray-200 px-8 space-y-4 w-full max-w-none">
              <div className="text-center">
                <div className="mt-4">
                  <div className="flex justify-between mb-4">
                    <div>
                      <Typography type="p">나</Typography>
                      <Typography type="p">{proposalRate}</Typography>
                    </div>
                    <div>
                      <Typography type="p">{getPersonaTypeKorean(currentRoundData.personaType)}</Typography>
                      <Typography type="p">{10 - proposalRate}</Typography>
                    </div>
                  </div>
                  <Slider
                    value={[proposalRate]}
                    onValueChange={(value) => setProposalRate(value[0])}
                    min={1}
                    max={9}
                    step={1}
                    className="w-full max-w-none"
                  />
                </div>
              </div>
              <div className="text-center py-2">
              {currentRoundData.roleType === 1 && (
                <Typography type="p" className="text-error">
                  상대방이 거절할 수 있어요!
                </Typography>
              )}
              {currentRoundData.roleType === 3 && (
                <Typography type="p" className="text-accent-blue">
                  상대방이 무조건 수락해요!
                </Typography>
              )}
              </div>
            </div>
          </div>
          <div className="flex justify-center w-full my-8">
            <Button onClick={handleProposal} size="lg" className="flex-1">
              제안하기
            </Button>
          </div>
        </>
      )
    }

    // ================================
    // 응답자 모드
    // ================================
    if (gameMode === "responder") {
      const aiProposal = currentRoundData.rate || 1
      const totalAmount = currentRoundTotalAmount

      return (
        <>
          <div className="flex-1 flex flex-col justify-center space-y-8 w-full">
            <div className="text-center space-y-4 w-full">
              <Typography type="h3">
                <span className="text-accent-blue">{getPersonaTypeKorean(currentRoundData.personaType)}</span> 쪽에서
                제안한 금액이에요
              </Typography>
              <div>
                <Typography type="h3">발견된 금액: {formatKoreanAmount(totalAmount)}</Typography>
              </div>

              {/* 페르소나 이미지 */}
              <div className="flex justify-center">
                <Image
                  src={currentRoundPersonaImage}
                  alt={`${getPersonaTypeKorean(currentRoundData.personaType)} character`}
                  width={200}
                  height={200}
                  className="animate-fade-in"
                />
              </div>

              <div className="rounded-lg border border-gray-200 p-4 space-y-2 w-full max-w-none">
                <div className="flex justify-start">
                  <Badge variant="outline" className="text-sm p-2 rounded-full">
                    {getPersonaTypeKorean(currentRoundData.personaType)}
                  </Badge>
                </div>
                <Typography type="p" className="text-lg">
                  내가 {10 - aiProposal}정도 가져갈테니까, 네가 {aiProposal}정도 어때?
                </Typography>
              </div>
            </div>
          </div>
          <div className="flex justify-center gap-4 w-full my-8">
            <Button onClick={() => handleResponse(true)} variant="default" size="lg" className="flex-1">
              수락
            </Button>
            <Button onClick={() => handleResponse(false)} variant="outline" size="lg" className="flex-1">
              거절
            </Button>
          </div>
        </>
      )
    }

    return null
  }

  return (
    <div className="flex flex-col min-h-[calc(100vh-var(--header-height))] w-full p-8">
      {/* 헤더 영역 - 상단 고정 */}
      <div className="flex-shrink-0 p-4">
        <div className="text-center space-y-2">
          <Typography type="h3">{getModeText()}</Typography>
          <ProgressBar
            type="twoLine"
            value={(currentRoundNumber / TOTAL_ROUNDS) * 100}
            entireRound={TOTAL_ROUNDS}
            currentRound={currentRoundNumber}
          />
          <Badge variant="outline" className="text-sm p-2 rounded-full">
            협상 성공: {negotiationSuccessCount}회
          </Badge>
        </div>
      </div>

      {/* 게임 영역 */}
      {renderGameContent()}
    </div>
  )
}
