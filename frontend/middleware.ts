import type { NextRequest } from "next/server"
import { NextResponse } from "next/server"

const TOKEN_KEY = "accessToken"

export function middleware(req: NextRequest) {
  const url = req.nextUrl

  // 로그인 등 특정 경로는 제외
  const skip =
    url.pathname.startsWith("/api") ||
    url.pathname.startsWith("/handler") ||
    url.pathname.startsWith("/_next") ||
    url.pathname.startsWith("/login")
  if (skip) {
    const res = NextResponse.next()
    res.headers.set("x-pathname", url.pathname + (url.search || ""))
    return res
  }

  // 토큰 확인
  const accessToken = req.cookies.get(TOKEN_KEY)?.value

  // 인증이 필요한 페이지인지 확인 (보호된 페이지들)
  const protectedPaths = ["/diaries", "/profile", "/friends", "/games", "/communities"]
  const isProtectedPath = protectedPaths.some((path) => url.pathname.startsWith(path))

  if (!accessToken && isProtectedPath) {
    // 쿠키에 리다이렉트 경로 저장하고 로그인 페이지로 리다이렉트
    const response = NextResponse.redirect(new URL("/login", req.url))
    response.cookies.set("authRedirect", url.pathname + (url.search || ""), {
      httpOnly: true,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      maxAge: 60 * 5 // 5분
    })
    return response
  }

  // 정상적인 요청은 헤더 추가하고 통과
  const res = NextResponse.next()
  res.headers.set("x-pathname", url.pathname + (url.search || ""))
  return res
}

// 필요 시 matcher로 범위 지정
export const config = {
  matcher: ["/((?!_next|api|static|favicon.ico).*)"]
}
