"use server"

import { authenticatedFetch } from "./authenticatedFetch"
import { dev } from "@/lib/dev"
import { revalidatePath } from "next/cache"

// 채팅 히스토리 조회
export async function getChatHistory(page?: number, size?: number) {
  try {
    const params = new URLSearchParams()
    if (page !== undefined) params.append("page", page.toString())
    if (size !== undefined) params.append("size", size.toString())

    const queryString = params.toString()
    const url = `/api/v1/my-chat/history${queryString ? `?${queryString}` : ""}`

    const response = await authenticatedFetch(url)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch chat history" }
    }

    const result = await response.json()
    dev.log("getChatHistory", result)
    return { isSuccess: true, data: result.result || { messages: [] } }
  } catch (error) {
    console.error("Get chat history error:", error)
    return { isSuccess: false, error: "Failed to fetch chat history" }
  }
}

// 안읽은 메시지 조회
export async function getUnreadChatMessages() {
  try {
    const response = await authenticatedFetch("/api/v1/my-chat/unread")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch unread messages" }
    }

    const result = await response.json()

    dev.log("getUnreadChatMessages", result)
    return { isSuccess: true, data: result.result || [] }
  } catch (error) {
    console.error("Get unread messages error:", error)
    return { isSuccess: false, error: "Failed to fetch unread messages" }
  }
}

// AI와 채팅 메시지 전송
export async function sendChatMessage(message: string) {
  try {
    const response = await authenticatedFetch("/api/v1/my-chat/send", {
      method: "POST",
      body: JSON.stringify({ message })
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to send message" }
    }

    const result = await response.json()
    revalidatePath("/chats")
    dev.log("sendChatMessage", result)

    return { isSuccess: true, result }
  } catch (error) {
    console.error("Send message error:", error)
    return { isSuccess: false, error: "Failed to send message" }
  }
}

// 메시지 읽음 처리
export async function markMessagesAsRead(messageIds: number[]) {
  try {
    const response = await authenticatedFetch("/api/v1/my-chat/mark-read", {
      method: "PATCH",
      body: JSON.stringify(messageIds)
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to mark messages as read" }
    }

    const result = await response.json()
    revalidatePath("/chats")

    dev.log("sendChatMessage", result)

    return result
  } catch (error) {
    console.error("Mark messages as read error:", error)
    return { isSuccess: false, error: "Failed to mark messages as read" }
  }
}
