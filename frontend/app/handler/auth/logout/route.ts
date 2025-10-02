import { NextResponse } from "next/server"
import { TOKEN_KEY } from "@/lib/manager"
import { cookies } from "next/headers"

export async function POST() {
  const cookieStore = await cookies()

  // 쿠키에서 토큰 삭제
  cookieStore.delete(TOKEN_KEY)

  return NextResponse.json({ isSuccess: true })
}
