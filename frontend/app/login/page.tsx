"use client"

import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Suspense, useState } from "react"

import { AlertCircleIcon } from "lucide-react"
import { Button } from "@/components/ui/button"
import Image from "next/image"
import Loading from "@/components/common/Loading"
import Typography from "@/components/ui/Typography"
import WaveAnimation from "@/components/common/WaveAnimation"
import { getGoogleLoginUrl } from "../actions/auth"
import { useSearchParams } from "next/navigation"

function LoginContent() {
  const [loading, setLoading] = useState(false)
  const searchParams = useSearchParams()

  const handleGoogleLogin = async () => {
    setLoading(true)
    try {
      const redirectUrl = searchParams.get("redirect")
      const loginUrl = await getGoogleLoginUrl()

      // redirect 파라미터가 있으면 로그인 URL에 추가
      if (redirectUrl) {
        const url = new URL(loginUrl)
        url.searchParams.set("redirect", redirectUrl)
        window.location.href = url.toString()
      } else {
        window.location.href = loginUrl
      }
    } catch (error) {
      console.error("Google login error:", error)
      setLoading(false)
    }
  }

  // const handleKakaoLogin = () => {
  //   console.log("Kakao Login")
  // }

  return (
    <main className="page relative min-h-screen flex flex-col">
      {/* 중앙 컨텐츠 */}
      <div className="flex-1 flex flex-col items-center justify-center space-y-6 px-4 relative z-10">
        <Image
          src="/characters/G.png"
          width={120}
          height={120}
          alt="logo"
          className="drop-shadow-lg"
          priority={false}
        />
        <div style={{ fontFamily: "BabyShark" }} className="text-center">
          <Typography type="h1">
            너의 <span style={{ color: "var(--accent-blue)" }}>OCEAN</span>은
          </Typography>
          <Typography type="h4" className="mt-4">
            자아의 바다를 함께 탐험할 페르소나밍
          </Typography>
        </div>

        <Suspense>
          <AlertMessage />
        </Suspense>

        <div className="w-full flex flex-col space-y-6">
          <Button
            size="long"
            variant="default"
            className="bg-[#f2f2f2] hover:bg-[#001d35]/12 text-black flex items-center justify-center gap-2.5"
            onClick={handleGoogleLogin}
            disabled={loading}
          >
            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" className="block w-5 h-5 max-h-5">
              <path
                fill="#EA4335"
                d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
              ></path>
              <path
                fill="#4285F4"
                d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
              ></path>
              <path
                fill="#FBBC05"
                d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
              ></path>
              <path
                fill="#34A853"
                d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"
              ></path>
              <path fill="none" d="M0 0h48v48H0z"></path>
            </svg>
            <span>구글로 계속하기</span>
            <span className="hidden">구글로 계속하기</span>
          </Button>

          {/* <Button
            size="long"
            variant="default"
            className="bg-[#FDDC3F] text-black flex items-center justify-center gap-3"
            onClick={handleKakaoLogin}
          >
            <Image src="/icons/kakao.png" alt="kakao" width={30} height={30} />
            <span>카카오로 시작하기</span>
          </Button> */}
        </div>
      </div>

      {/* 파도 애니메이션 - 하단 고정 */}
      <div className="absolute bottom-0 left-0 w-full z-0">
        <WaveAnimation height={280} />
      </div>
    </main>
  )
}

export default function LoginPage() {
  return (
    <Suspense fallback={<Loading />}>
      <LoginContent />
    </Suspense>
  )
}

function AlertMessage() {
  const searchParams = useSearchParams()

  return (
    searchParams.get("error") === "oauth_failed" && (
      <Alert variant="destructive">
        <AlertCircleIcon />
        <AlertTitle>oauth_failed</AlertTitle>
        <AlertDescription>
          <p>로그인 오류가 발생했습니다. 잠시 후에 시도해주세요.</p>
        </AlertDescription>
      </Alert>
    )
  )
}
