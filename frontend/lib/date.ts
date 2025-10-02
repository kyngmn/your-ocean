/**
 * @fileoverview 날짜 관련 유틸리티 함수들
 * dayjs를 사용하여 한국 시간대(KST) 기준으로 날짜를 처리합니다.
 */

import "dayjs/locale/ko"

import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
import timezone from "dayjs/plugin/timezone"
import utc from "dayjs/plugin/utc"

dayjs.extend(utc)
dayjs.extend(relativeTime)
dayjs.extend(timezone)

/** 한국 시간대 설정 */
const KST = "Asia/Seoul"

/**
 * 오늘 날짜를 YYYY-MM-DD 형식으로 반환합니다.
 *
 * @returns 오늘 날짜 (예: "2024-01-15")
 * @example
 * const today = getTodayDate()
 * console.log(today) // "2024-01-15"
 */
export function getTodayDate(): string {
  return dayjs().tz(KST).format("YYYY-MM-DD")
}
/**
 * 오늘 날짜를 YYYY-MM 형식으로 반환합니다.
 *
 * @returns 오늘 연도와 월 (예: "2025-02")
 * @example
 * const thisYearMonth = getThisYearMonth()
 * console.log(thisYearMonth) // "2025-02"
 */
export function getThisYearMonth(): string {
  return dayjs().tz(KST).format("YYYY-MM")
}
/**
 * 날짜를 년도 표시 여부에 따라 포맷팅합니다.
 * 올해 날짜면 MM-DD 형식, 다른 해면 YYYY-MM-DD 형식으로 반환합니다.
 *
 * @param _date 포맷팅할 날짜
 * @returns 포맷팅된 날짜 문자열 (올해: "01-15", 다른 해: "2023-01-15")
 * @example
 * formatDateToYmd(new Date()) // "01-15" (올해인 경우)
 * formatDateToYmd("2023-01-15") // "2023-01-15" (다른 해인 경우)
 * formatDateToYmd("") // ""
 */
export function formatDateToYmd(_date: Date | string, yearEllipsis: boolean = false): string {
  if (!_date) return ""

  const date = dayjs(_date).tz(KST)
  if (yearEllipsis) {
    const thisYear = dayjs().tz(KST).year()
    if (date.year() === thisYear) {
      return date.format("MM-DD")
    }
  }

  return date.format("YYYY-MM-DD")
}
/**
 * 날짜를 년도 표시 여부에 따라 YYY-MM-DD 형식으로 포맷팅합니다.
 *
 * @param _date 포맷팅할 날짜
 * @returns 포맷팅된 날짜 문자열 ("2023-01-15")
 * @example
 * formatDateToYmd("2023-01-15") // "2023-01-15"
 * formatDateToYmd("") // ""
 */
export function formatDateToYm(_date: Date | string): string {
  if (!_date) return ""
  const date = dayjs(_date).tz(KST)
  return date.format("YYYY-MM")
}
/**
 * 날짜를 한국어 형식으로 포맷팅합니다.
 *
 * @param _date 포맷팅할 날짜
 * @returns 한국어 형식의 날짜 문자열 (예: "2024년 1월 15일")
 * @example
 * formatDate(new Date()) // "2024년 1월 15일"
 * formatDate("2024-01-15") // "2024년 1월 15일"
 * formatDate("") // ""
 */
export function formatDate(_date: Date | string): string {
  if (!_date) return ""

  return dayjs(_date).tz(KST).format("YYYY년 M월 D일")
}
/**
 * YYYY-MM-DD 형식의 날짜를 KST 00:00:00 기준으로 UTC ISO 문자열로 변환합니다.
 *
 * @param ymd YYYY-MM-DD 형식의 날짜 문자열
 * @returns UTC ISO 문자열 또는 undefined
 * @example
 * getIsoUtc("2024-01-15") // "2024-01-14T15:00:00.000Z" (KST 2024-01-15 00:00:00)
 * getIsoUtc("") // undefined
 */
export function getIsoUtc(ymd: string): string | undefined {
  return ymd ? dayjs.tz(`${ymd}T00:00:00`, KST).utc().toISOString() : undefined
}
export function getFromNow(_date: Date | string): string {
  if (!_date) return ""
  return dayjs(_date).fromNow()
}
