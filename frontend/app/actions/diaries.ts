"use server"

import { revalidatePath, revalidateTag } from "next/cache"

import type { CreateDiaryRequestDTO } from "@/types/dto"
import { DiaryAnalysisSchema } from "@/types/schema.zod"
import { authenticatedFetch } from "./authenticatedFetch"
import { dev } from "@/lib/dev"
import z from "zod"

// 다이어리 상세 조회
export async function getDiary(diaryId: number) {
  try {
    const response = await authenticatedFetch(`/api/v1/diaries/${diaryId}`, {
      next: { tags: [`diary-${diaryId.toString()}`] }
    })

    if (!response.ok) {
      return { isSuccess: false, message: "Failed to fetch diary" }
    }

    const result = await response.json()
    return result
  } catch (error) {
    console.error("Get diary error:", error)
    return { isSuccess: false, message: "Failed to fetch diary" }
  }
}

// 다이어리 날짜별 조회
export async function getDiaryByDate(date: string) {
  try {
    const response = await authenticatedFetch(`/api/v1/diaries/date/${date}`)
    return await response.json()
  } catch (error) {
    console.error("Get diary error:", error)
    return { isSuccess: false, message: "Failed to fetch diary" }
  }
}

// 다이어리 작성
export async function createDiary(diaryData: CreateDiaryRequestDTO) {
  try {
    const response = await authenticatedFetch("/api/v1/diaries", {
      method: "POST",
      body: JSON.stringify(diaryData)
    })

    if (!response.ok) {
      return { isSuccess: false, message: "Failed to create diary" }
    }

    const result = await response.json()

    revalidateTag("diary-calendar")
    if (result?.diaryDate) {
      revalidatePath(`/diaries/date/${result?.diaryDate}`)
    }

    return result
  } catch (error) {
    console.error("Create diary error:", error)
    return { isSuccess: false, message: "Failed to create diary" }
  }
}

// 다이어리 삭제
export async function deleteDiary(diaryId: number) {
  try {
    const response = await authenticatedFetch(`/api/v1/diaries/${diaryId}`, {
      method: "DELETE"
    })
    if (!response.ok) {
      return { isSuccess: false, message: "Failed to delete diary" }
    }

    revalidatePath("/diaries")
    revalidateTag(`diary-${diaryId.toString()}`)
    revalidateTag("diary-calendar")

    return await response.json()
  } catch (error) {
    console.error("Delete diary error:", error)
    return { isSuccess: false, message: "Failed to delete diary" }
  }
}

// 월별 다이어리 작성 현황 조회
export async function getDiaryCalendar(yearMonth: string) {
  try {
    const response = await authenticatedFetch(`/api/v1/diaries/calendar?ym=${yearMonth}`, {
      next: { tags: ["diary-calendar"] }
    })

    if (!response.ok) {
      return { isSuccess: false, message: "Failed to fetch diary calendar" }
    }

    const result = await response.json()
    return result
  } catch (error) {
    console.error("Get diary calendar error:", error)
    return { isSuccess: false, message: "Failed to fetch diary calendar" }
  }
}

// 다이어리 분석 결과 조회
export async function getDiaryAnalysis(diaryId: number) {
  try {
    const response = await authenticatedFetch(`/api/v1/diaries/${diaryId}/analysis`)
    if (!response.ok) {
      return { isSuccess: false, message: "Failed to fetch diary analysis" }
    }

    const result = await response.json()
    // TODO 서버 스펠링 고쳐지면 변경
    const oceanMessages = result.result?.oceanMessages?.map(
      (o: z.infer<typeof DiaryAnalysisSchema>["oceanMessages"][number]) => {
        if ((o.personality as never) === "EXTRAVERSION") {
          o.personality = "EXTROVERSION"
        }
        return o
      }
    )

    return { ...result, result: { ...(result?.result && result?.result), oceanMessages } }
  } catch (error) {
    console.error("Get diary analysis error:", error)
    return { isSuccess: false, message: "Failed to fetch diary analysis" }
  }
}

// 게임 카운트 조회
export async function getGameCount() {
  try {
    const response = await authenticatedFetch("/api/v1/users/games/count")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch game count" }
    }

    const count = (await response.json()).result
    return { isSuccess: true, data: count }
  } catch (error) {
    console.error("Get game count error:", error)
    return { isSuccess: false, error: "Failed to fetch game count" }
  }
}

// 오늘의 메시지 조회
export async function getTodayMessage() {
  try {
    const response = await authenticatedFetch("/api/v1/users/daily-message", { next: { revalidate: 6 * 60 * 60 } })
    if (!response.ok) {
      return { isSuccess: false, message: "Failed to fetch today message" }
    }

    const result = (await response.json()).result

    dev.log("🔥result", result)

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Get today message error:", error)
    return { isSuccess: false, message: "Failed to fetch today message" }
  }
}
