"use client"

import { Button } from "@/components/ui/button"
import Typography from "@/components/ui/Typography"
import Image from "next/image"
import { useRouter } from "next/navigation"

export default function NotFound() {
  const router = useRouter()
  const goChat = () => {
    router.push("/")
  }

  return (
    <>
      <main className="page">
        <div className="section flex flex-1 flex-col items-center justify-center gap-4">
          <Image src="/image/error.png" alt="404" width={200} height={200} />
          <Typography type="h2">요청하신 페이지를 찾을 수 없습니다.</Typography>
          <Button size="long" className="w-full" onClick={goChat}>
            메인으로 돌아가기
          </Button>
        </div>
      </main>
    </>
  )
}
