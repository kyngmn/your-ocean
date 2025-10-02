import { NextRequest, NextResponse } from "next/server"
import { cookies } from "next/headers"
import { TOKEN_KEY } from "@/lib/manager"
import { dev } from "@/lib/dev"

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url)
  const code = searchParams.get("code")
  const nickname = searchParams.get("nickname")
  const profileImage = searchParams.get("profileImage")

  dev.log("🔥 친구 초대 링크 접근:", { code, url: request.url })

  if (!code) {
    // 토큰이 없으면 에러 페이지로 리다이렉트
    dev.log("🔥 코드가 없어서 에러 페이지로 리다이렉트")
    return NextResponse.redirect(new URL("/error", request.url))
  }

  // 로그인 상태 확인
  const cookieStore = await cookies()
  const accessToken = cookieStore.get(TOKEN_KEY)?.value

  dev.log("🔥 로그인 상태 확인:", {
    hasToken: !!accessToken,
    tokenLength: accessToken?.length,
    userAgent: request.headers.get("user-agent")?.includes("Chrome") ? "Chrome" : "Other",
    isSecure: request.url.includes("https"),
    requestHeaders: Object.fromEntries(request.headers.entries())
  })

  // 리다이렉트 URL 생성
  const handleRedirectUrl = () => {
    const redirectUrl = new URL("/communities/invite?nickname=" + nickname + "&profileImage=" + profileImage, request.url)
    return redirectUrl
  }

  if (!accessToken) {
    // 로그인되지 않은 경우 초대 페이지로 리다이렉트 (middleware가 처리)
    dev.log("🔥 로그인 안됨 - 초대 페이지로 리다이렉트하여 middleware 처리")

    const response = NextResponse.redirect(handleRedirectUrl())
    response.cookies.set("inviteToken", code, {
      httpOnly: false, // 클라이언트에서 읽을 수 있도록
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      maxAge: 60 * 10 // 10분
    })

    return response
  }

  // 로그인된 경우 초대 페이지로 리다이렉트
  dev.log("🔥 로그인된 상태로 초대 페이지로 리다이렉트")

  // 토큰을 쿠키에 저장하고 URL에는 노출시키지 않음
  const response = NextResponse.redirect(handleRedirectUrl())

  response.cookies.set("inviteToken", code, {
    httpOnly: false, // 클라이언트에서 읽을 수 있도록
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    maxAge: 60 * 10 // 10분
  })

  return response
}
