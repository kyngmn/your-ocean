import { createJSONStorage, devtools, persist } from "zustand/middleware"

import { UserDTO } from "@/types/dto"
import { create } from "zustand"
import { tokenManager } from "@/lib/manager"

interface AuthState {
  user: UserDTO | null
  isAuthenticated: boolean
  isLoading: boolean

  // Actions
  login: (accessToken: string, user: UserDTO) => void
  logout: () => Promise<{ isSuccess: boolean }>
  updateUser: (userData: Partial<UserDTO>) => void
  setLoading: (loading: boolean) => void
  initializeAuth: () => void
  checkAuth: () => boolean
}

export const useAuthStore = create<AuthState>()(
  devtools(
    persist(
      (set, get) => ({
        user: null,
        isAuthenticated: false,
        isLoading: false, // 기본값을 false로 설정

        login: (token: string, user: UserDTO) => {
          // localStorage에 저장
          tokenManager.setToken(token)

          // Zustand 상태 업데이트
          set({
            user,
            isAuthenticated: true,
            isLoading: false
          })
        },

        logout: async (): Promise<{ isSuccess: boolean }> => {
          // // Zustand 상태 초기화
          // set({
          //   user: null,
          //   isAuthenticated: false,
          //   isLoading: false
          // })

          // localStorage 즉시 클리어
          useAuthStore.persist.clearStorage()
          tokenManager.removeToken()

          // 서버에서 쿠키 삭제를 위해 로그아웃 엔드포인트 호출
          try {
            // TODO
            const res = await fetch("/handler/auth/logout", {
              method: "POST",
              credentials: "include"
            })
            return res.json()
          } catch (error) {
            console.error("Logout API call failed:", error)
            return { isSuccess: false }
          }
        },

        // 사용자 정보 업데이트
        updateUser: (userData: Partial<UserDTO>) => {
          const currentUser = get().user
          if (currentUser) {
            const updatedUser = { ...currentUser, ...userData }
            // Zustand 상태 업데이트
            set({ user: updatedUser })
          }
        },

        setLoading: (loading: boolean) => {
          set({ isLoading: loading })
        },

        // TODO
        initializeAuth: () => {
          const state = get()

          // 이미 초기화되었거나 인증 상태가 확실한 경우 스킵
          if (state.user !== null || state.isAuthenticated) {
            console.log("Auth already initialized, skipping")
            return
          }

          console.log("Starting auth initialization...")

          // 초기화 시작 시 로딩 상태로 설정
          set({ isLoading: true })

          try {
            // 토큰이 있는지 확인
            const hasToken = tokenManager.hasToken()

            if (!hasToken) {
              console.log("No token found, setting unauthenticated state")
              set({
                user: null,
                isAuthenticated: false,
                isLoading: false
              })
              return
            }

            // 토큰이 있으면 만료 확인
            if (isTokenExpired()) {
              console.log("Token expired during initialization")
              // 만료된 토큰 정리
              // logout();
              set({
                user: null,
                isAuthenticated: false,
                isLoading: false
              })
              return
            }

            // localStorage에서 유저 정보 복원
            // const user = authManager.getCurrentUser();
            // const isAuthenticated = authManager.isAuthenticated();

            // console.log('Auth initialization result:', {
            //   user,
            //   isAuthenticated,
            // });

            // set({
            //   user,
            //   isAuthenticated,
            //   isLoading: false,
            // });
          } catch (error) {
            console.error("Auth initialization failed:", error)
            set({
              user: null,
              isAuthenticated: false,
              isLoading: false
            })
          }
        },

        // TODO
        checkAuth: () => {
          const { isAuthenticated } = get()

          const token = tokenManager.getToken()
          if (!token) return true

          // Mock 토큰인 경우 만료되지 않은 것으로 처리
          if (token.startsWith("mock_access_token_")) {
            return false
          }

          try {
            // JWT의 payload 부분을 디코딩
            const payload = JSON.parse(atob(token.split(".")[1]))
            const exp = payload.exp

            if (!exp) return false // exp가 없으면 만료되지 않는 것으로 간주

            // 현재 시간과 비교 (exp는 초 단위, Date.now()는 밀리초 단위)
            return Date.now() >= exp * 1000
          } catch {
            // 디코딩 실패하면 만료된 것으로 간주
            get().logout()
          }

          return isAuthenticated
        }
      }),
      {
        name: "auth-storage", // localStorage key
        storage: createJSONStorage(() => localStorage),
        partialize: (state) => ({
          // persist할 상태만 선택 (토큰은 별도 localStorage에 저장)
          user: state.user,
          isAuthenticated: state.isAuthenticated
        })
      }
    ),
    {
      name: "auth-storage" // devtools name
    }
  )
)

// 초기화 함수 - 앱 시작 시 호출
export const isTokenExpired = (): boolean => {
  const token = tokenManager.getToken()
  if (!token) return true

  try {
    // JWT의 payload 부분을 디코딩
    const payload = JSON.parse(atob(token.split(".")[1]))
    const exp = payload.exp

    if (!exp) return false // exp가 없으면 만료되지 않는 것으로 간주

    // 현재 시간과 비교 (exp는 초 단위, Date.now()는 밀리초 단위)
    return Date.now() >= exp * 1000
  } catch {
    // 디코딩 실패하면 만료된 것으로 간주
    return true
  }
}
