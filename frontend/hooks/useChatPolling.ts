"use client"

import { useCallback, useEffect, useMemo, useRef, useState } from "react"
import { useChatHistory, useMarkMessagesAsRead, useUnreadChatMessages } from "@/features/chats/queries"

import { useQueryClient } from "@tanstack/react-query"

interface UseChatPollingOptions {
  interval?: number // 폴링 간격 (ms), 기본값: 3000ms (3초)
  enabled?: boolean // 폴링 활성화 여부
}

export function useChatPolling(options: UseChatPollingOptions = {}) {
  const { interval = 3000, enabled = true } = options
  const queryClient = useQueryClient()
  const [hasNewMessages, setHasNewMessages] = useState(false)
  const previousUnreadCountRef = useRef<number>(0)

  // 채팅 히스토리 조회 (초기 로드)
  const { data: historyData, isLoading: isLoadingHistory } = useChatHistory()

  // 읽지 않은 메시지 폴링 (읽음 처리 자동 수행)
  const { data: unreadData, isLoading: isLoadingUnread } = useUnreadChatMessages({
    interval,
    enabled
  })

  // 읽음 처리 뮤테이션 (필요 시 수동 호출용)
  const markAsReadMutation = useMarkMessagesAsRead()

  // 서버 데이터만 관리 (히스토리 + 새 메시지)
  const serverMessages = useMemo(() => {
    const historyMessages = historyData?.isSuccess ? historyData.data.messages || [] : []
    const unreadMessages = unreadData?.isSuccess ? unreadData.data || [] : []

    // 기존 히스토리의 최대 ID 찾기
    const maxHistoryId = historyMessages.length > 0 ? Math.max(...historyMessages.map((msg: any) => msg.id)) : 0

    // 읽지 않은 메시지 중 히스토리에 없는 새로운 메시지만 필터링
    const newUnreadMessages = unreadMessages.filter((msg: any) => msg.id > maxHistoryId)

    // 히스토리 + 새로운 읽지 않은 메시지 합치기 (시간순 정렬)
    const combined = [...historyMessages, ...newUnreadMessages].sort(
      (a: any, b: any) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    )

    return combined
  }, [historyData, unreadData])

  // 새 메시지 감지
  useEffect(() => {
    if (unreadData?.isSuccess && unreadData?.data) {
      const currentUnreadCount = unreadData.data.length
      if (currentUnreadCount > previousUnreadCountRef.current) {
        setHasNewMessages(true)
      }
      previousUnreadCountRef.current = currentUnreadCount
    }
  }, [unreadData])

  // 읽음 처리는 unread API 호출 시 자동으로 처리되므로 단순화
  const markAsRead = useCallback(() => {
    setHasNewMessages(false)
  }, [])

  // 폴링 시작/중지 함수 (useCallback으로 메모이제이션)
  const startPolling = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ["chat", "unread"] })
    queryClient.invalidateQueries({ queryKey: ["chat", "history"] })
  }, [queryClient])

  const stopPolling = useCallback(() => {
    queryClient.cancelQueries({ queryKey: ["chat", "unread"] })
  }, [queryClient])


  return {
    serverMessages, // 서버에서 온 메시지들만 (히스토리 + 새 메시지)
    hasNewMessages,
    isLoading: isLoadingHistory || isLoadingUnread,
    markAsRead,
    startPolling,
    stopPolling
  }
}
