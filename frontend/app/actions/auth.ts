import type { AuthResponseDTO } from "@/types/dto"
import { authenticatedFetch } from "@/app/actions/authenticatedFetch"

// 구글 소셜 로그인 URL 반환
export async function getGoogleLoginUrl() {
  return `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/google`
}

// 카카오 소셜 로그인
export async function kakaoLogin(authCode: string) {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/auth/kakao/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ code: authCode })
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Kakao login failed" }
    }

    const data: AuthResponseDTO = await response.json()

    // 쿠키에 토큰 저장
    // const cookieStore = await cookies()
    // cookieStore.set("accessToken", data.accessToken, {
    //   httpOnly: true,
    //   secure: process.env.NODE_ENV === "production",
    //   sameSite: "lax",
    //   maxAge: 60 * 60 * 24 // 1 day
    // })

    // cookieStore.set("refreshToken", data.refreshToken, {
    //   httpOnly: true,
    //   secure: process.env.NODE_ENV === "production",
    //   sameSite: "lax",
    //   maxAge: 60 * 60 * 24 * 7 // 7 days
    // })

    return data
  } catch (error) {
    console.error("Kakao login error:", error)
    return { isSuccess: false, error: "Login failed" }
  }
}

// 현재 사용자 정보 가져오기
export async function getCurrentUser() {
  try {
    const response = await authenticatedFetch("/api/v1/users", {
      method: "GET"
    })

    if (!response.ok) {
      return { isSuccess: false, error: "Failed to fetch user" }
    }

    return await response.json()
  } catch (error) {
    console.error("Get current user error:", error)
    return { isSuccess: false, error: "Failed to fetch user" }
  }
}
