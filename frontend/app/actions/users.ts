"use server"

import type {UserDTO, UserPersonaResponseDTO } from "@/types/dto"

import { authenticatedFetch } from "./authenticatedFetch"
import { revalidatePath } from "next/cache"
import { dev } from "@/lib/dev"

// 회원 정보 수정
export async function updateUser(nickname: string, profileImageUrl: File) {
  try {
    // FormData 생성
    const formData = new FormData()
    
    if (nickname) {
      formData.append('nickname', nickname)
    }
    if (profileImageUrl) {
      formData.append('file', profileImageUrl)
    }

    dev.log("🔥 formData:", formData)

    const response = await authenticatedFetch("/api/v1/users", {
      method: "PATCH",
      body: formData
    })
    dev.log("🔥 updateUser 응답:", response)

    if (!response.ok) {
      const err = (await response.json()).message
      return { isSuccess: false, error: err }
    }

    const updatedUser: UserDTO = (await response.json()).result
    revalidatePath("/profile")

    return { isSuccess: true, data: updatedUser }
  } catch (error) {
    console.error("Update user error:", error)
    return { isSuccess: false, error: "Failed to update user" }
  }
}

// 회원 탈퇴
export async function deleteUser() {
  try {
    const response = await authenticatedFetch("/api/v1/users", {
      method: "DELETE"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to delete user" }
    }

    const result = await response.json()
    revalidatePath("/")

    // 회원탈퇴 성공 시 클라이언트 쿠키도 삭제
    if (typeof document !== "undefined") {
      document.cookie = "accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
    }

    return { isSuccess: true, data: result }
  } catch (error) {
    console.error("Delete user error:", error)
    return { isSuccess: false, error: "Failed to delete user" }
  }
}

// 유저 페르소나 목록 조회
export async function getUserPersonas() {
  try {
    const response = await authenticatedFetch("/api/v1/users/personas")

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch user personas" }
    }

    const personas: UserPersonaResponseDTO = (await response.json()).result
    return { isSuccess: true, data: personas }
  } catch (error) {
    console.error("Get user personas error:", error)
    return { isSuccess: false, error: "Failed to fetch user personas" }
  }
}
