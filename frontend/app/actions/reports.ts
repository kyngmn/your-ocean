"use server"

import type { ReportDTO } from "@/types/dto"
import { authenticatedFetch } from "./authenticatedFetch"
import { dev } from "@/lib/dev"

// SELF 리포트 상세 조회
export async function getSelfReport() {
  try {
    const response = await authenticatedFetch("/api/v1/reports/self")

    dev.log("🔥 getSelfReport 응답:", response)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch self report" }
    }

    const report: ReportDTO = (await response.json()).result
    
    return { isSuccess: true, data: report }
  } catch (error) {
    dev.log("🔥 getSelfReport 에러:", error)
    console.error("Get self report error:", error)
    return { isSuccess: false, error: "Failed to fetch self report" }
  }
}

// FINAL 리포트 상세 조회
export async function getFinalReport() {
  try {
    const response = await authenticatedFetch("/api/v1/reports/final")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch final report" }
    }

    const report: ReportDTO = (await response.json()).result
    
    return { isSuccess: true, data: report }
  } catch (error) {
    console.error("Get final report error:", error)
    return { isSuccess: false, error: "Failed to fetch final report" }
  }
}

// 사용자의 모든 리포트 조회 (추가 기능)
export async function getUserReports() {
  try {
    const response = await authenticatedFetch("/api/v1/reports")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch user reports" }
    }

    const reports: ReportDTO[] = await response.json()
    return { isSuccess: true, data: reports }
  } catch (error) {
    console.error("Get user reports error:", error)
    return { isSuccess: false, error: "Failed to fetch user reports" }
  }
}
