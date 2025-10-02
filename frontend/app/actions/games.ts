"use server"

import type {
  BARTFirstRoundClickRequestDTO,
  BARTRoundFinishRequestDTO,
  BARTRoundRequestDTO,
  CreateGameSessionRequestDTO,
  GNGClickRequestDTO,
  GameResultDTO,
  GameSessionIdDTO,
  UGRoundRequestDTO
} from "@/types/dto"

import { authenticatedFetch } from "./authenticatedFetch"
import { revalidatePath } from "next/cache"
import { dev } from "@/lib/dev"

// ================================================================================================
// 게임 공통
// ================================================================================================

// 게임 세션 생성
export async function createGameSession(gameType: CreateGameSessionRequestDTO["gameType"]) {
  dev.log("게임 세션 생성 요청", JSON.stringify({ gameType }))
  try {
    const response = await authenticatedFetch("/api/v1/game-sessions", {
      method: "POST",
      body: JSON.stringify({ gameType })
    })

    dev.log("게임 세션 생성 응답", response)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to create game session" }
    }

    const sessionId: GameSessionIdDTO = (await response.json()).result.sessionId
    revalidatePath("/games")

    dev.log("게임 세션 아이디", sessionId)

    return { isSuccess: true, data: { sessionId } }
  } catch (error) {
    console.error("Create game session error:", error)
    return { isSuccess: false, error: "Failed to create game session" }
  }
}

// 세션 결과 조회
export async function getGameSessionResult(sessionId: string) {
  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/result`)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch game result" }
    }

    const result: GameResultDTO = await response.json()
    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Get game session result error:", error)
    return { isSuccess: false, error: "Failed to fetch game result" }
  }
}

// 세션 종료
export async function finishGameSession(sessionId: number) {
  dev.log("🔥 finishGameSession 호출됨:", sessionId)
  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/finish`, {
      method: "PATCH"
    })

    dev.log("🔥 finishGameSession 응답:", response)

    if (!response.ok) {
      dev.error("🔥 게임 세션 종료 실패:", response.status, response.statusText)
      return { isSuccess: false, error: "Failed to finish game session" }
    }

    const result = await response.json()
    dev.log("🔥 게임 세션 종료 성공:", result)
    revalidatePath("/games")

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Finish game session error:", error)
    return { isSuccess: false, error: "Failed to finish game session" }
  }
}

// ================================================================================================
// UG 게임 관련
// ================================================================================================

// UG 게임 순서 조회
export async function getUGGameOrder(sessionId: number) {
  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/ug/orders`)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch UG game order" }
    }

    const order = (await response.json()).result

    dev.log("🔥 UG 게임 주문 조회 성공:", order)

    return { isSuccess: true, data: order }
  } catch (error) {
    console.error("Get UG game order error:", error)
    return { isSuccess: false, error: "Failed to fetch UG game order" }
  }
}

// UG 게임 응답 요청
export async function submitUGRound(sessionId: number, roundId: number, responseData: UGRoundRequestDTO) {
  dev.log("🔥 UG 게임 응답 요청", JSON.stringify({ sessionId, roundId, responseData }))

  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/ug/rounds/${roundId}/responses`, {
      method: "POST",
      body: JSON.stringify(responseData)
    })

    // dev.log("🔥 UG 게임 응답:", response)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to submit UG response" }
    }

    // 요청만 보내면 되기 때문에 응답 데이터는 필요 없음
    // const result = await response.json()

    return { isSuccess: true, data: { message: "데이터 잘 받았다네요 우하하" } }
  } catch (error) {
    console.error("Submit UG response error:", error)
    return { isSuccess: false, error: "Failed to submit UG response" }
  }
}

// ================================================================================================
// BART 게임 관련
// ================================================================================================
// BART 첫 번째 라운드 클릭 요청
export async function submitBARTFirstRoundClick(
  sessionId: number,
  roundIndex: number,
  clickData: BARTFirstRoundClickRequestDTO
) {

  dev.log("🔥 BART 첫 번째 라운드 클릭 요청", JSON.stringify({ sessionId, roundIndex, clickData }))

  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/bart/rounds/${roundIndex}/clicks`, {
      method: "POST",
      body: JSON.stringify(clickData)
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to submit BART click" }
    }

    // 요청만 보내면 되기 때문에 응답 데이터는 필요 없음
    // const result = await response.json()

    return { isSuccess: true, data: { message: "데이터 잘 받았다네요 우하하" } }
  } catch (error) {
    console.error("Submit BART click error:", error)
    return { isSuccess: false, error: "Failed to submit BART click" }
  }
}

// BART 라운드 클릭 요청
export async function submitBARTRoundClick(sessionId: number, roundIndex: number, clickData: BARTRoundRequestDTO) {

  dev.log("🔥 BART 라운드 클릭 요청", JSON.stringify({ sessionId, roundIndex, clickData }))

  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/bart/rounds/${roundIndex}/clicks`, {
      method: "POST",
      body: JSON.stringify(clickData)
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to submit BART click" }
    }
    // 요청만 보내면 되기 때문에 응답 데이터는 필요 없음
    // const result = await response.json()

    return { isSuccess: true, data: { message: "데이터 잘 받았다네요 우하하" } }
  } catch (error) {
    console.error("Submit BART click error:", error)
    return { isSuccess: false, error: "Failed to submit BART click" }
  }
}

// BART 라운드 종료 요청
export async function finishBARTRound(sessionId: number, roundIndex: number, roundData: BARTRoundFinishRequestDTO) {

  dev.log("🔥 BART 라운드 종료 요청", JSON.stringify({ sessionId, roundIndex, roundData }))
  dev.log("🔥 요청 데이터 상세:", { 
    sessionId, 
    roundIndex, 
    isPopped: roundData.isPopped,
    dataType: typeof roundData.isPopped 
  })
  
  try {
    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/bart/rounds/${roundIndex}/finish`, {
      method: "POST",
      body: JSON.stringify(roundData)
    })
    
    if (!response.ok) {
      const errorText = await response.text()
      dev.error("🔥 BART 라운드 종료 실패:", response.status, response.statusText, errorText)
      return { isSuccess: false, error: "Failed to finish BART round" }
    }

    // 요청만 보내면 되기 때문에 응답 데이터는 필요 없음
    // const result = await response.json()

    return { isSuccess: true, data: { message: "BART 게임 라운드 종료 요청 성공" } }
  } catch (error) {
    console.error("Finish BART round error:", error)
    return { isSuccess: false, error: "Failed to finish BART round" }
  }
}

// ================================================================================================
// GNG 게임 관련
// ================================================================================================

// GNG 클릭 요청
export async function submitGNGClick(sessionId: number, roundIndex: number, clickData: GNGClickRequestDTO) {
  try {
    dev.log("GNG 클릭 요청", JSON.stringify({ sessionId, roundIndex, clickData }))

    const response = await authenticatedFetch(`/api/v1/game-sessions/${sessionId}/gng/rounds/${roundIndex}/clicks`, {
      method: "POST",
      body: JSON.stringify(clickData)
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to submit GNG click" }
    }

    const result = await response.json()

    dev.log("GNG 클릭 응답", result)

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Submit GNG click error:", error)
    return { isSuccess: false, error: "Failed to submit GNG click" }
  }
}

// ================================================================================================
// 추가 기능
// ================================================================================================

export async function getUserGameSessions() {
  try {
    const response = await authenticatedFetch("/api/v1/game-sessions")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch game sessions" }
    }

    const sessions = await response.json()
    return { isSuccess: true, data: sessions }
  } catch (error) {
    console.error("Get user game sessions error:", error)
    return { isSuccess: false, error: "Failed to fetch game sessions" }
  }
}
