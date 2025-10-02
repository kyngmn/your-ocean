"use client"

import { createContext, useContext, useState, ReactNode } from "react"

interface OptimisticMessage {
  id: string
  message: string
  createdAt: string
  senderKind: "USER"
  senderActorId: null
  isRead: false
}

interface ChatOptimisticContextType {
  optimisticMessages: OptimisticMessage[]
  addOptimisticMessage: (messageText: string) => string
  removeOptimisticMessage: (messageText: string) => void
}

const ChatOptimisticContext = createContext<ChatOptimisticContextType | null>(null)

export const useChatOptimistic = () => {
  const context = useContext(ChatOptimisticContext)
  if (!context) {
    throw new Error("useChatOptimistic must be used within ChatOptimisticProvider")
  }
  return context
}

interface ChatOptimisticProviderProps {
  children: ReactNode
}

export function ChatOptimisticProvider({ children }: ChatOptimisticProviderProps) {
  const [optimisticMessages, setOptimisticMessages] = useState<OptimisticMessage[]>([])

  // optimistic 메시지 추가
  const addOptimisticMessage = (messageText: string) => {
    const optimisticMessage: OptimisticMessage = {
      id: `temp_${Date.now()}`,
      message: messageText,
      createdAt: new Date().toISOString(),
      senderKind: "USER",
      senderActorId: null,
      isRead: false
    }
    setOptimisticMessages(prev => [...prev, optimisticMessage])
    return optimisticMessage.id
  }

  // 메시지 전송 성공 시 해당 optimistic 메시지 제거
  const removeOptimisticMessage = (messageText: string) => {
    setOptimisticMessages(prev =>
      prev.filter(msg => msg.message !== messageText)
    )
  }

  return (
    <ChatOptimisticContext.Provider
      value={{
        optimisticMessages,
        addOptimisticMessage,
        removeOptimisticMessage
      }}
    >
      {children}
    </ChatOptimisticContext.Provider>
  )
}