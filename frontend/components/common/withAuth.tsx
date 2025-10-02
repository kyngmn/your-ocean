import ClientAuthProvider from "./ClientAuthProvider"
import { TOKEN_KEY } from "@/lib/manager"
import { cookies } from "next/headers"
import { getCurrentUser } from "@/app/actions/auth"
import { redirect } from "next/navigation"

export function withAuth<P extends object>(Component: React.ComponentType<P>) {
  return async function AuthenticatedComponent(props: P) {
    // const headersList = await headers()
    // // middleware가 넣어준 현재 경로(+쿼리)
    // const rawPath = headersList.get("x-pathname") || "/"
    // // 오픈 리다이렉트 방지: 내부 경로만 허용
    // const isSafeInternal = rawPath.startsWith("/")
    // const currentPath = isSafeInternal ? rawPath : "/"

    const cookieStore = await cookies()
    const accessToken = cookieStore.get(TOKEN_KEY)?.value
    if (!accessToken) {
      redirect("/login")
    }

    try {
      const userData = await getCurrentUser()
      // dev.log("🔥 UserDTO:", userData)
      if (userData?.isSuccess && userData?.result) {
        return (
          <ClientAuthProvider accessToken={accessToken} user={userData.result}>
            <Component {...props} />
          </ClientAuthProvider>
        )
      } else {
        redirect("/login")
      }
    } catch (error) {
      console.error("Auth error:", error)
      redirect("/login")
    }
  }
}
