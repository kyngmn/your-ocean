"use client"

import "dayjs/locale/ko"

import { useEffect, useRef } from "react"

import ChatMessage from "@/components/common/ChatMessage"
import dayjs from "dayjs"
import { getFromNow } from "@/lib/date"
import { motion } from "framer-motion"
import relativeTime from "dayjs/plugin/relativeTime"
import { useChatOptimistic } from "../context/ChatOptimisticContext"
import { useChatPolling } from "@/hooks/useChatPolling"

dayjs.extend(relativeTime)
dayjs.locale("ko")

export default function ChatsView() {
  const scrollEndRef = useRef<HTMLDivElement>(null)
  const { serverMessages, hasNewMessages, markAsRead } = useChatPolling()
  const { optimisticMessages } = useChatOptimistic()

  // 서버 메시지 + optimistic 메시지 합치기
  const chatHistory = [...serverMessages, ...optimisticMessages].sort(
    (a: any, b: any) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
  )
  useEffect(() => {
    scrollEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }, [chatHistory])

  // 컴포넌트가 마운트되거나 채팅을 볼 때 읽음 처리
  useEffect(() => {
    if (hasNewMessages) {
      markAsRead()
    }
  }, [hasNewMessages, markAsRead])

  // 페이지를 벗어날 때 읽음 처리
  useEffect(() => {
    return () => {
      if (hasNewMessages) {
        markAsRead()
      }
    }
  }, [hasNewMessages, markAsRead])

  return (
    <motion.div
      className="flex-1 flex flex-col gap-6 p-4 justify-end"
      initial="hidden"
      animate="show"
      variants={{
        hidden: {},
        show: {
          transition: {
            staggerChildren: 0.1 // 0.1초씩 지연해서 하나씩 나타남
          }
        }
      }}
    >
      {chatHistory?.map((item: any, index: number) => {
        const { id, message, createdAt, senderKind, senderActorId } = item
        return (
          <motion.div
            key={id || index}
            variants={{
              hidden: {
                opacity: 0,
                y: 20,
                scale: 0.95
              },
              show: {
                opacity: 1,
                y: 0,
                scale: 1,
                transition: {
                  type: "spring",
                  damping: 20,
                  stiffness: 300
                }
              }
            }}
          >
            <ChatMessage
              senderKind={senderKind}
              senderActorId={senderActorId}
              message={message}
              createdAt={createdAt}
            />
          </motion.div>
        )
      })}
      <div ref={scrollEndRef} />
    </motion.div>
  )
}
