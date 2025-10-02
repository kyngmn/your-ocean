"use client"

import { Button } from "@/components/ui/button"
import Typography from "@/components/ui/Typography"
import WaveAnimation from "@/components/common/WaveAnimation"
import { useRouter } from "next/navigation"

export default function TestPage() {
  const router = useRouter()

  const goGuide = () => {
    router.push("/big-five/guide")
  }

  return (
    <>
      <main className="page relative min-h-screen flex flex-col">
        {/* 중앙 컨텐츠 */}
        <div className="flex-1 flex flex-col items-center justify-center space-y-6 px-4 relative z-10">
          <div style={{ fontFamily: "BabyShark" }} className="text-center">
            <Typography type="h1">BIG5 성격검사</Typography>
            <Typography type="h4" className="mt-4">
              나를 알아가기 위한 첫 걸음을 내딛어봐요!
            </Typography>
          </div>
          <Button size="long" variant="default" onClick={goGuide}>
            시작하기
          </Button>
        </div>

        {/* 파도 애니메이션 - 하단 고정 */}
        <div className="absolute bottom-0 left-0 w-full z-0">
          <WaveAnimation height={280} />
        </div>
      </main>
    </>
  )
}
