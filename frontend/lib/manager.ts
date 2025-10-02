export const TOKEN_KEY = "accessToken"

/**
 * 토큰 관리 유틸리티
 */
export const tokenManager = {
  /**
   * 토큰 저장
   */
  setToken(token: string): void {
    if (typeof window !== "undefined") {
      localStorage.setItem(TOKEN_KEY, token)
    }
  },

  /**
   * 토큰 조회
   */
  getToken(): string | null {
    if (typeof window !== "undefined") {
      return localStorage.getItem(TOKEN_KEY)
    }
    return null
  },

  /**
   * 토큰 제거
   */
  removeToken(): void {
    if (typeof window !== "undefined") {
      localStorage.removeItem(TOKEN_KEY)
    }
  },

  /**
   * 토큰 존재 여부 확인
   */
  hasToken(): boolean {
    return !!this.getToken()
  }
}
