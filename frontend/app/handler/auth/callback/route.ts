import { NextResponse } from "next/server"
import { cookies } from "next/headers"
import { dev } from "@/lib/dev"

export async function GET(request: Request) {
  // 여기서 인증 처리 로직 수행
  // 예: 토큰 검증, 사용자 정보 저장 등

  const params = new URL(request.url).searchParams
  const _token = params.get("token") // 건드리면 안됨
  const _new = params.get("new")
  const redirectParam = params.get("redirect") // 초대 링크 등에서 전달된 리다이렉트 URL

  dev.log("🔥 Auth callback - redirectParam:", redirectParam)

  const cookieStore = await cookies()
  if (_token) {
    // 쿠키에 토큰 저장
    cookieStore.set("accessToken", _token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      maxAge: 60 * 60 * 24 * 7 // 7 day
    })
  }

  // 리다이렉트 경로 우선순위: URL 파라미터 > 쿠키 > 기본값
  const authRedirectCookie = cookieStore.get("authRedirect")?.value
  const redirectTo = redirectParam || authRedirectCookie || "/"

  // 보안을 위해 내부 경로만 허용
  const isSafeInternal = redirectTo.startsWith("/") && !redirectTo.startsWith("//")
  const finalPath = isSafeInternal ? redirectTo : "/"

  // 사용된 리다이렉트 쿠키 삭제
  cookieStore.delete("authRedirect")

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || request.url
  let url = new URL(finalPath, baseUrl)

  // 백엔드에서 전달한 new 파라미터를 활용 (new=true면 신규 사용자)
  if (_new === "true") {
    url = new URL("/big-five", baseUrl)
  } else {
    // 기존 사용자는 원래 접근하려던 페이지로 리다이렉트
  }

  return NextResponse.redirect(url)
}
