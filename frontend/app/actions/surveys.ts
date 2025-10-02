"use server"

import type {
  CompleteSurveyRequestDTO,
  SurveyQuestionDTO,
  SurveyRequestDTO,
  SurveyResponseDTO,
  SurveyResponseLogRequestDTO
} from "@/types/dto"

import { authenticatedFetch } from "./authenticatedFetch"
import { revalidatePath } from "next/cache"

// 설문 문항 조회
export async function getSurveys(page: SurveyRequestDTO ) {
  try {
    const response = await authenticatedFetch(`/api/v1/surveys?page=${page.page}`, {
      method: "GET"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch surveys" }
    }

    const surveys: SurveyQuestionDTO[] = (await response.json()).result.surveys
    return { isSuccess: true, data: surveys }
  } catch (error) {
    console.error("Get surveys error:", error)
    return { isSuccess: false, error: "Failed to fetch surveys" }
  }
}

// 설문 응답 로그 (중간 저장)
export async function logSurveyResponse(logData: SurveyResponseLogRequestDTO) {
  try {
    const response = await authenticatedFetch("/api/v1/survey-responses/logs", {
      method: "POST",
      body: JSON.stringify({
        ...logData,
        occurredAt: new Date().toISOString()
      })
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to log survey response" }
    }

    const log = await response.json()
    return { isSuccess: true, data: log }
  } catch (error) {
    console.error("Log survey response error:", error)
    return { isSuccess: false, error: "Failed to log survey response" }
  }
}

// 설문 완료 (최종 제출)
export async function completeSurvey(responses: CompleteSurveyRequestDTO["responses"]) {

  try {
    const response = await authenticatedFetch("/api/v1/survey-responses/complete", {
      method: "POST",
      body: JSON.stringify({ responses: responses })
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to complete survey" }
    }

    const result = await response.json()
    revalidatePath("/survey")
    revalidatePath("/reports")

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Complete survey error:", error)
    return { isSuccess: false, error: "Failed to complete survey" }
  }
}

// 사용자 설문 응답 조회 (추가 기능)
export async function getUserSurveyResponses() {
  try {
    const response = await authenticatedFetch("/api/v1/survey-responses")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch survey responses" }
    }

    const responses: SurveyResponseDTO[] = await response.json()
    return { isSuccess: true, data: responses }
  } catch (error) {
    console.error("Get user survey responses error:", error)
    return { isSuccess: false, error: "Failed to fetch survey responses" }
  }
}
