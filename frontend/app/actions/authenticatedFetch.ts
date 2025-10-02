"use server"

import { cookies } from "next/headers"
import { dev } from "@/lib/dev"

// 인증된 API 요청을 위한 헬퍼 함수
export async function authenticatedFetch(url: string, options: RequestInit = {}) {
  dev.log("요청 URL", `${process.env.NEXT_PUBLIC_API_URL}${url}`)

  const cookieStore = await cookies() // cookies는 app 폴더 내부의 server action(또는 route handler)으로만 쓸 수 있음
  const accessToken = cookieStore.get("accessToken")?.value

  if (!accessToken) {
    throw new Error("No access token")
  }

  return await fetch(`${process.env.NEXT_PUBLIC_API_URL}${url}`, {
    ...options,
    headers: {
      ...(options.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
      Authorization: `Bearer ${accessToken}`, // 쿠키가 있음에도 이렇게 해야한다..
      ...options.headers
    }
  })
}
