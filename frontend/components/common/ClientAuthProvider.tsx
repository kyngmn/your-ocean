"use client"

import { useAuthStore } from "@/stores/auth-store"
import { UserDTO } from "@/types/dto"
import { useEffect, type ReactNode } from "react"

interface Props {
  children: ReactNode
  accessToken: string
  user: UserDTO
}

export default function ClientAuthProvider({ children, accessToken, user }: Props) {
  const { login, isAuthenticated } = useAuthStore()

  useEffect(() => {
    // 서버에서 받은 유저 데이터를 클라이언트 스토어에 저장
    if (accessToken && user && !isAuthenticated) {
      login(accessToken, user)
    }
  }, [accessToken, user, isAuthenticated])

  return <>{children}</>
}
