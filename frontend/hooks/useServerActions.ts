// React Query와 Server Actions을 조합한 커스텀 훅들
import {
  useQuery,
  useMutation,
  useQueryClient,
  type UseQueryOptions,
  type UseMutationOptions
} from "@tanstack/react-query"
import { getNotifications, markNotificationAsRead, getUnreadNotificationCount } from "@/app/actions/notifications"
import { getFriends, createInviteLink, acceptInvite, declineInvite } from "@/app/actions/friends"
import { getSurveys, logSurveyResponse, completeSurvey, getUserSurveyResponses } from "@/app/actions/surveys"
import { getSelfReport, getFinalReport, getUserReports } from "@/app/actions/reports"
import { getGameSessionResult, finishGameSession } from "@/app/actions/games"
import type {
  UserDTO,
  NotificationDTO,
  NotificationQueryOptionsDTO,
  MarkAsReadRequestDTO,
  CreateInviteResponseDTO,
  SurveyResponseLogRequestDTO,
  CompleteSurveyRequestDTO,
  SurveyResponseDTO,
  ReportDTO,
  GameResultDTO,
  SurveyRequestDTO,
  FriendResponseDTO
} from "@/types/dto"
import { getCurrentUser } from "@/app/actions/auth"

// 헬퍼 함수들 - 서버 액션의 결과를 React Query에서 사용하기 쉽게 변환
export async function handleServerAction<T>(
  action: Promise<{ isSuccess: boolean; data?: T; error?: string }>
): Promise<T> {
  const result = await action
  if (!result.isSuccess) {
    throw new Error(result.error || "Server action failed")
  }
  if (result.data === undefined) {
    throw new Error("Server action returned undefined data")
  }
  return result.data
}

// =============================================================================
// User 관련 훅들
// =============================================================================

export function useCurrentUser(options?: Partial<UseQueryOptions<UserDTO>>) {
  return useQuery<UserDTO>({
    queryKey: ["user", "current"],
    queryFn: () => handleServerAction(getCurrentUser()),
    ...options
  })
}

// export function useUserPersonas(options?: Partial<UseQueryOptions<UserPersonaDTO[]>>) {
//   return useQuery<UserPersonaDTO[]>({
//     queryKey: ["user", "personas"],
//     queryFn: () => handleServerAction(getUserPersonas()),
//     ...options
//   })
// }

// export function useUpdateUser(options?: UseMutationOptions<UserDTO, Error, UpdateUserRequestDTO>) {
//   const queryClient = useQueryClient()
//   const { onSuccess, ...restOptions } = options || {}

//   return useMutation<UserDTO, Error, UpdateUserRequestDTO>({
//     mutationFn: (userData) => handleServerAction(updateUser(userData)),
//     onSuccess: (data, variables, context, mutation) => {
//       queryClient.invalidateQueries({ queryKey: ["user"] })
//       onSuccess?.(data, variables, context, mutation)
//     },
//     ...restOptions
//   })
// }

// =============================================================================
// Notification 관련 훅들
// =============================================================================

export function useNotifications(
  queryOptions?: NotificationQueryOptionsDTO,
  options?: Partial<UseQueryOptions<NotificationDTO[]>>
) {
  return useQuery<NotificationDTO[]>({
    queryKey: ["notifications", queryOptions],
    queryFn: () => handleServerAction(getNotifications(queryOptions)),
    ...options
  })
}

export function useUnreadNotificationCount(options?: Partial<UseQueryOptions<{ count: number }>>) {
  return useQuery<{ count: number }>({
    queryKey: ["notifications", "unread-count"],
    queryFn: () => handleServerAction(getUnreadNotificationCount()),
    ...options
  })
}

export function useMarkNotificationAsRead(
  options?: UseMutationOptions<any, Error, MarkAsReadRequestDTO["notificationIds"]>
) {
  const queryClient = useQueryClient()
  const { onSuccess, ...restOptions } = options || {}

  return useMutation<any, Error, MarkAsReadRequestDTO["notificationIds"]>({
    mutationFn: (notificationIds) => handleServerAction(markNotificationAsRead(notificationIds)),
    onSuccess: (data, variables, context, mutation) => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] })
      onSuccess?.(data, variables, context, mutation)
    },
    ...restOptions
  })
}

// =============================================================================
// Friend 관련 훅들
// =============================================================================

export function useFriends(options?: Partial<UseQueryOptions<FriendResponseDTO[]>>) {
  return useQuery<FriendResponseDTO[]>({
    queryKey: ["friends"],
    queryFn: () => handleServerAction(getFriends()),
    ...options
  })
}

export function useCreateInviteLink(options?: UseMutationOptions<CreateInviteResponseDTO, Error, void>) {
  return useMutation<CreateInviteResponseDTO, Error, void>({
    mutationFn: () => handleServerAction(createInviteLink()),
    ...options
  })
}

export function useAcceptInvite(options?: UseMutationOptions<any, Error, string>) {
  const queryClient = useQueryClient()
  const { onSuccess, ...restOptions } = options || {}

  return useMutation<any, Error, string>({
    mutationFn: (token) => handleServerAction(acceptInvite(token)),
    onSuccess: (data, variables, context, mutation) => {
      queryClient.invalidateQueries({ queryKey: ["friends"] })
      onSuccess?.(data, variables, context, mutation)
    },
    ...restOptions
  })
}

export function useDeclineInvite(options?: UseMutationOptions<any, Error, string>) {
  return useMutation<any, Error, string>({
    mutationFn: (token) => handleServerAction(declineInvite(token)),
    ...options
  })
}

// export function useRemoveFriend(options?: UseMutationOptions<void, Error, number>) {
//   const queryClient = useQueryClient()
//   const { onSuccess, ...restOptions } = options || {}

//   return useMutation<void, Error, number>({
//     mutationFn: (friendId) => handleServerAction(removeFriend(friendId)),
//     onSuccess: (data, variables, context, mutation) => {
//       queryClient.invalidateQueries({ queryKey: ["friends"] })
//       onSuccess?.(data, variables, context, mutation)
//     },
//     ...restOptions
//   })
// }

// =============================================================================
// Survey 관련 훅들
// =============================================================================

// 설문 문항 조회
export const useSurveys = (page: SurveyRequestDTO) => {
  return useQuery({
    queryKey: ["surveys", page.page],
    queryFn: () => handleServerAction(getSurveys(page)),
    staleTime: 5 * 60 * 1000 // 5분
  })
}

export function useLogSurveyResponse(options?: UseMutationOptions<any, Error, SurveyResponseLogRequestDTO>) {
  return useMutation<any, Error, SurveyResponseLogRequestDTO>({
    mutationFn: (logData) => handleServerAction(logSurveyResponse(logData)),
    ...options
  })
}

export function useCompleteSurvey(options?: UseMutationOptions<any, Error, CompleteSurveyRequestDTO["responses"]>) {
  const queryClient = useQueryClient()
  const { onSuccess, ...restOptions } = options || {}

  return useMutation<any, Error, CompleteSurveyRequestDTO["responses"]>({
    mutationFn: (responses) => handleServerAction(completeSurvey(responses)),
    onSuccess: (data, variables, context, mutation) => {
      queryClient.invalidateQueries({ queryKey: ["user"] })
      queryClient.invalidateQueries({ queryKey: ["reports"] })
      onSuccess?.(data, variables, context, mutation)
    },
    ...restOptions
  })
}

export function useUserSurveyResponses(options?: Partial<UseQueryOptions<SurveyResponseDTO[]>>) {
  return useQuery<SurveyResponseDTO[]>({
    queryKey: ["survey-responses"],
    queryFn: () => handleServerAction(getUserSurveyResponses()),
    ...options
  })
}

// =============================================================================
// Report 관련 훅들
// =============================================================================

export function useSelfReport(options?: Partial<UseQueryOptions<ReportDTO>>) {
  return useQuery<ReportDTO>({
    queryKey: ["reports", "self"],
    queryFn: () => handleServerAction(getSelfReport()),
    ...options
  })
}

export function useFinalReport(options?: Partial<UseQueryOptions<ReportDTO>>) {
  return useQuery<ReportDTO>({
    queryKey: ["reports", "final"],
    queryFn: () => handleServerAction(getFinalReport()),
    ...options
  })
}

export function useUserReports(options?: Partial<UseQueryOptions<ReportDTO[]>>) {
  return useQuery<ReportDTO[]>({
    queryKey: ["reports"],
    queryFn: () => handleServerAction(getUserReports()),
    ...options
  })
}

// =============================================================================
// Game 관련 훅들
// =============================================================================

// export function useCreateGameSession(
//   options?: UseMutationOptions<GameSessionDTO, Error, CreateGameSessionRequestDTO["gameType"]>
// ) {
//   return useMutation<GameSessionDTO, Error, CreateGameSessionRequestDTO["gameType"]>({
//     mutationFn: (gameType) => handleServerAction(createGameSession(gameType)),
//     ...options
//   })
// }

export function useGameSessionResult(sessionId: string, options?: Partial<UseQueryOptions<GameResultDTO>>) {
  return useQuery<GameResultDTO>({
    queryKey: ["game-sessions", sessionId, "result"],
    queryFn: () => handleServerAction(getGameSessionResult(sessionId)),
    enabled: !!sessionId,
    ...options
  })
}

export function useFinishGameSession(options?: UseMutationOptions<any, Error, number>) {
  const queryClient = useQueryClient()
  const { onSuccess, ...restOptions } = options || {}

  return useMutation<any, Error, number>({
    mutationFn: (sessionId) => handleServerAction(finishGameSession(sessionId)),
    onSuccess: (data, variables, context, mutation) => {
      queryClient.invalidateQueries({ queryKey: ["game-sessions", variables] })
      onSuccess?.(data, variables, context, mutation)
    },
    ...restOptions
  })
}
