"use client"

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { getChatHistory, getUnreadChatMessages, sendChatMessage, markMessagesAsRead } from "@/app/actions/my-chat"

// 채팅 히스토리 조회 쿼리
export function useChatHistory(page?: number, size?: number) {
  return useQuery({
    queryKey: ["chat", "history", page, size],
    queryFn: () => getChatHistory(page, size),
    staleTime: 1000 * 60 * 5, // 5분간 fresh 상태 유지
  })
}

// 읽지 않은 메시지 조회 쿼리
export function useUnreadChatMessages(options?: {
  interval?: number
  enabled?: boolean
}) {
  const { interval = 3000, enabled = true } = options || {}

  return useQuery({
    queryKey: ["chat", "unread"],
    queryFn: getUnreadChatMessages,
    refetchInterval: enabled ? interval : false,
    refetchIntervalInBackground: true,
    enabled
  })
}

// 메시지 전송 뮤테이션
export function useSendChatMessage() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: sendChatMessage,
    onSuccess: () => {
      // 히스토리와 읽지 않은 메시지 목록 새로고침
      queryClient.invalidateQueries({ queryKey: ["chat", "history"] })
      queryClient.invalidateQueries({ queryKey: ["chat", "unread"] })
    }
  })
}

// 메시지 읽음 처리 뮤테이션
export function useMarkMessagesAsRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: markMessagesAsRead,
    onSuccess: () => {
      // 히스토리와 읽지 않은 메시지 목록 새로고침
      queryClient.invalidateQueries({ queryKey: ["chat", "history"] })
      queryClient.invalidateQueries({ queryKey: ["chat", "unread"] })
    }
  })
}