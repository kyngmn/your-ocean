"use client"

import CustomTextarea from "@/components/common/CustomTextarea"
import { toast } from "sonner"
import { useSendChatMessage } from "@/features/chats/queries"
import { useChatOptimistic } from "../context/ChatOptimisticContext"
import { useState } from "react"

export default function ChatMessageForm() {
  const [message, setMessage] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { addOptimisticMessage, removeOptimisticMessage } = useChatOptimistic()

  // 메시지 전송 mutation
  const sendMessageMutation = useSendChatMessage()

  // 전송 처리
  const handleSend = () => {
    // 유효성 검사
    if (!message.trim()) {
      toast.error("내용을 입력해주세요.")
      return
    }

    const messageText = message.trim()

    // 1. 사용자 메시지를 즉시 UI에 추가 (Optimistic Update)
    addOptimisticMessage(messageText)

    // 2. 입력창 즉시 초기화
    setMessage("")
    setIsSubmitting(true)

    // 3. 서버에 메시지 전송
    sendMessageMutation.mutate(messageText, {
      onSuccess: (result) => {
        if (result.isSuccess) {
          toast.success("메시지가 전송되었습니다.")
          // optimistic 메시지 제거 (서버에서 새 메시지가 폴링으로 들어올 것임)
          removeOptimisticMessage(messageText)
        } else {
          toast.error(result.error || "메시지 전송에 실패했습니다.")
          // 실패 시에도 optimistic 메시지 제거
          removeOptimisticMessage(messageText)
        }
        setIsSubmitting(false)
      },
      onError: (error) => {
        toast.error("메시지 전송에 실패했습니다.")
        console.error("메시지 전송 실패:", error)
        // 실패 시 optimistic 메시지 제거
        removeOptimisticMessage(messageText)
        setIsSubmitting(false)
      }
    })
  }

  const isPending = sendMessageMutation.isPending || isSubmitting

  // Shift + Enter로 전송
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  return (
    <CustomTextarea
      type="chat"
      value={message}
      onChange={(e) => setMessage(e.target.value)}
      onKeyDown={handleKeyDown}
      placeholder="오늘의 메시지를 작성해보세요! (Shift + Enter로 전송)"
      disabled={isPending}
      onClick={handleSend}
      loading={isPending}
    />
  )
}
