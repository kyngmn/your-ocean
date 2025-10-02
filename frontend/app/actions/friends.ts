"use server"

import type { ChatMessageDTO, FriendResponseDTO, PaginationParamsDTO } from "@/types/dto"

import { authenticatedFetch } from "./authenticatedFetch"
import { revalidatePath } from "next/cache"
import { dev } from "@/lib/dev"

// 친구 목록 조회
export async function getFriends() {
  try {
    const response = await authenticatedFetch("/api/v1/friends")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch friends" }
    }

    const friends: FriendResponseDTO[] = (await response.json()).result
    return { isSuccess: true, data: friends }
  } catch (error) {
    console.error("Get friends error:", error)
    return { isSuccess: false, error: "Failed to fetch friends" }
  }
}

// 초대 링크 생성
export async function createInviteLink() {
  try {
    const response = await authenticatedFetch("/api/v1/friends/invites", {
      method: "POST"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to create invite link" }
    }

    dev.log("createInviteLink 응답:", response)

    const invite = (await response.json()).result
    return { isSuccess: true, data: invite }
  } catch (error) {
    dev.error("Create invite link error:", error)
    return { isSuccess: false, error: "Failed to create invite link" }
  }
}

// 초대 수락
export async function acceptInvite(token: string) {
  try {
    const response = await authenticatedFetch(`/api/v1/friends/invites/${token}/accept`, {
      method: "POST"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to accept invite" }
    }

    const result = await response.json()

    revalidatePath("/communities")

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Accept invite error:", error)
    return { isSuccess: false, error: "Failed to accept invite" }
  }
}

// 초대 거절
export async function declineInvite(token: string) {
  try {
    const response = await authenticatedFetch(`/api/v1/friends/invites/${token}/decline`, {
      method: "POST"
    })

    if (!response.ok) {
      return { isSuccess: false, message: "Failed to decline invite" }
    }

    const result = await response.json()
    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Decline invite error:", error)
    return { isSuccess: false, message: "Failed to decline invite" }
  }
}

// 친구 끊기
export async function removeFriend(friendId: number) {
  dev.log("removeFriend", friendId)

  try {
    const response = await authenticatedFetch(`/api/v1/friends/${friendId}`, {
      method: "DELETE"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to remove friend" }
    }

    dev.log("removeFriend 응답:", response)

    revalidatePath("/friends")
    revalidatePath("/chat")

    return { isSuccess: true, data: { message: "친구 삭제 완료" } }
  } catch (error) {
    console.error("Remove friend error:", error)
    return { isSuccess: false, error: "Failed to remove friend" }
  }
}

// 친구 채팅방 입장
export async function enterFriendChatRoom(roomId: number) {
  try {
    const response = await authenticatedFetch(`/api/v1/friend-chats/${roomId}`, {
      method: "POST"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to enter chat room" }
    }

    const result = await response.json()
    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Enter friend chat room error:", error)
    return { isSuccess: false, error: "Failed to enter chat room" }
  }
}

// 친구 채팅방에 메시지 전송
export async function sendFriendMessage(roomId: number, message: string) {
  try {
    const response = await authenticatedFetch(`/api/v1/friend-chats/${roomId}/messages`, {
      method: "POST",
      body: JSON.stringify({ message })
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to send message" }
    }

    const sentMessage: ChatMessageDTO = await response.json()
    revalidatePath(`/chat/friend/${roomId}`)

    return { isSuccess: true, data: sentMessage }
  } catch (error) {
    console.error("Send friend message error:", error)
    return { isSuccess: false, error: "Failed to send message" }
  }
}

// 친구 채팅 메시지 목록 조회 (추가 기능)
export async function getFriendChatMessages(roomId: number, options?: PaginationParamsDTO) {
  try {
    const params = new URLSearchParams()

    if (options?.page) params.append("page", options.page.toString())
    if (options?.limit) params.append("limit", options.limit.toString())

    const queryString = params.toString()
    const url = `/api/v1/friend-chats/${roomId}/messages${queryString ? `?${queryString}` : ""}`

    const response = await authenticatedFetch(url)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch chat messages" }
    }

    const messages = await response.json()
    return { isSuccess: true, data: messages }
  } catch (error) {
    console.error("Get friend chat messages error:", error)
    return { isSuccess: false, error: "Failed to fetch chat messages" }
  }
}
