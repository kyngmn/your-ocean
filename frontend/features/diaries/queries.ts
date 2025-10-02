"use client"

import type { CreateDiaryRequestDTO, DiaryCalendarResponseDTO, DiaryResponseDTO } from "@/types/dto"
import { UseMutationOptions, UseQueryOptions, useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { createDiary, deleteDiary, getDiaryByDate, getDiaryCalendar } from "@/actions/diaries"

import { formatDateToYm } from "@/lib/date"

/**
 * 다이어리 달력 데이터를 조회하는 React Query 훅
 * 특정 년월(YYYY-MM)에 다이어리가 작성된 날짜들을 가져옵니다.
 *
 * @param yearMonth YYYY-MM 형식의 년월 문자열 (예: "2024-01")
 * @returns 다이어리 달력 데이터와 로딩/에러 상태
 * @example
 * const { data, isLoading, error } = useDiaryCalendar("2024-01")
 * console.log(data?.diaryDates) // ["2024-01-01", "2024-01-15", ...]
 */
export function useDiaryCalendar(
  yearMonth: string,
  options?: Partial<UseQueryOptions<DiaryCalendarResponseDTO, Error>>
) {
  return useQuery({
    queryKey: ["diary-calendar", yearMonth],
    queryFn: async (): Promise<DiaryCalendarResponseDTO> => {
      const result = await getDiaryCalendar(yearMonth)

      // Server Action이 직접 응답 데이터를 반환하거나 에러 객체를 반환
      if ("isSuccess" in result && !result.isSuccess) {
        throw new Error(result.message || "Failed to fetch diary calendar")
      }

      // 성공 시 result가 직접 데이터이거나, result.result가 데이터
      return "result" in result ? result.result : result
    },
    enabled: options?.enabled || (!!yearMonth && yearMonth.length === 7), // YYYY-MM 형식 검증
    staleTime: options?.staleTime || 1000 * 60 * 5, // 5분간 캐시 유지
    retry: options?.retry || 3,
    ...options
  })
}

export function useDiaryByDate(date: string, options?: Partial<UseQueryOptions<DiaryResponseDTO, Error>>) {
  return useQuery({
    queryKey: ["diary", date],
    queryFn: async (): Promise<DiaryResponseDTO> => {
      const result = await getDiaryByDate(date)

      // Server Action이 직접 응답 데이터를 반환하거나 에러 객체를 반환
      if ("isSuccess" in result && !result.isSuccess) {
        throw new Error(result.message || "Failed to fetch diary calendar")
      }

      // 성공 시 result가 직접 데이터이거나, result.result가 데이터
      return "result" in result ? result.result : result
    },
    enabled: options?.enabled || (!!date && date.length === 10), // YYYY-MM 형식 검증
    staleTime: options?.staleTime || 1000 * 60 * 5, // 5분간 캐시 유지
    retry: options?.retry || 3,
    ...options
  })
}

export function useCreateDiary(options?: UseMutationOptions<DiaryResponseDTO, Error, CreateDiaryRequestDTO>) {
  const queryClient = useQueryClient()
  const { onSuccess, ...restOptions } = options || {}
  return useMutation({
    mutationFn: (diaryData) => handleServerAction(createDiary(diaryData)),
    onSuccess: (data, variables, context, mutation) => {
      // 일기 작성 후 해당 연월의 useDiaryCalendar 캐시 갱신
      queryClient.invalidateQueries({ queryKey: ["diary-calendar", formatDateToYm(data.diaryDate)] })
      onSuccess?.(data, variables, context, mutation)
    },
    ...restOptions
  })
}

export function useDeleteDiary(options?: UseMutationOptions<void, Error, number>) {
  const queryClient = useQueryClient()
  const { onSuccess, ...restOptions } = options || {}

  return useMutation({
    mutationFn: (id) => handleServerAction(deleteDiary(id)),
    onSuccess: (data, variables, context, mutation) => {
      queryClient.invalidateQueries({ queryKey: ["diary-calendar"] })
      // 개별 날짜에 대한 다이어리는 복잡성으로 인해 굳이 무효화 하지 않음
      onSuccess?.(data, variables, context, mutation)
    },
    ...restOptions
  })
}

// 헬퍼 함수들 - 서버 액션의 결과를 React Query에서 사용하기 쉽게 변환
async function handleServerAction<T>(
  action: Promise<{ isSuccess: boolean; result?: T; message?: string }>
): Promise<T> {
  const result = await action
  if (!result.isSuccess) {
    throw new Error(result.message || "Server action failed")
  }
  if (result.result === undefined) {
    throw new Error("Server action returned undefined data")
  }
  return result.result
}
