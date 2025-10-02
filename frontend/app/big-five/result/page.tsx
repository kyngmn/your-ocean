"use client"

import { Button } from "@/components/ui/button"
import Typography from "@/components/ui/Typography"
import Image from "next/image"
import { useRouter } from "next/navigation"

export default function ResultPage() {
  const router = useRouter()

  const goReport = () => {
    router.push("/profile/report")
  }

  return (
    <>
      <main className="page relative min-h-screen flex flex-col">
        {/* 중앙 컨텐츠 */}
        <div className="flex-1 flex flex-col items-center justify-center space-y-6 px-4 relative z-10">
          <Image src="/characters/random.png" alt="random" width={300} height={300} className="animate-head-shake-loop" />
          <div style={{ fontFamily: "BabyShark" }} className="text-center">
            <Typography type="h1">모든 검사를 완료 했어요!</Typography>
            <Typography type="h4" className="mt-4">지금 바로 결과를 확인해보러 갈까요?</Typography>
          </div>
            <Button size="long" variant="default" onClick={goReport}>
              이동하기
            </Button>
        </div>
      </main>
    </>
  )
}
