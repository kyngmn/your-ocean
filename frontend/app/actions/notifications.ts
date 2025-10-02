"use server"

import type { MarkAsReadRequestDTO, NotificationDTO, NotificationQueryOptionsDTO } from "@/types/dto"

import { authenticatedFetch } from "./authenticatedFetch"
import { revalidatePath } from "next/cache"

// 미확인 알림 목록 조회
export async function getNotifications(options?: NotificationQueryOptionsDTO) {
  try {
    const params = new URLSearchParams()

    if (options?.page) params.append("page", options.page.toString())
    if (options?.limit) params.append("limit", options.limit.toString())
    if (options?.isRead !== undefined) params.append("isRead", options.isRead.toString())

    const queryString = params.toString()
    const url = `/api/v1/notifications${queryString ? `?${queryString}` : ""}`

    const response = await authenticatedFetch(url)

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch notifications" }
    }

    const notifications: NotificationDTO[] = await response.json()
    return { isSuccess: true, data: notifications }
  } catch (error) {
    console.error("Get notifications error:", error)
    return { isSuccess: false, error: "Failed to fetch notifications" }
  }
}

// 알림 읽음 처리
export async function markNotificationAsRead(notificationIds: MarkAsReadRequestDTO["notificationIds"]) {
  try {
    const response = await authenticatedFetch("/api/v1/notifications/read", {
      method: "POST",
      body: JSON.stringify({ notificationIds })
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to mark notifications as read" }
    }

    const result = await response.json()
    revalidatePath("/notifications")

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Mark notification as read error:", error)
    return { isSuccess: false, error: "Failed to mark notifications as read" }
  }
}

// 미확인 알림 개수 조회
export async function getUnreadNotificationCount() {
  try {
    const response = await authenticatedFetch("/api/v1/notifications/count")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch notification count" }
    }

    const count = await response.json()
    return { isSuccess: true, data: count }
  } catch (error) {
    console.error("Get unread notification count error:", error)
    return { isSuccess: false, error: "Failed to fetch notification count" }
  }
}
